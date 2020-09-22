package uk.ac.ebi.ega.data.edge.service.internal;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.ac.ebi.ega.data.edge.commons.exception.FileNotFoundException;
import uk.ac.ebi.ega.data.edge.service.KeyService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class KeyServiceImplTests {

    private static MockWebServer mockWebServer;

    @Autowired
    private KeyService keyService;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public WebClient keyServiceWebClient() {
            String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
            return WebClient.create(baseUrl);
        }
    }

    @Test
    void getEncryptionAlgorithm_validFileId_returnsEncryptionAlgorithmName() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
            .setBody("test-algorithm")
            .addHeader("Content-Type", "application/json"));

        assertEquals("test-algorithm", keyService.getEncryptionAlgorithm("test-file"));

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/keys/encryptionalgorithm/test-file", request.getPath());
    }

    @Test
    void getEncryptionAlgorithm_fileThatDoesNotExist_throwsFileNotFoundException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));
        assertThrows(FileNotFoundException.class, () -> {
            keyService.getEncryptionAlgorithm("test-file");
        });
    }

    @Test
    void getEncryptionAlgorithm_otherError_throwsWebClientResponseException() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(WebClientResponseException.class, () -> {
            keyService.getEncryptionAlgorithm("test-file");
        });
    }
}
