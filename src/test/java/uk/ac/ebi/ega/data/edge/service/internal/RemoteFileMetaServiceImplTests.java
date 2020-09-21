package uk.ac.ebi.ega.data.edge.service.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ebi.ega.data.edge.commons.shared.dto.File;
import uk.ac.ebi.ega.data.edge.commons.shared.dto.FileDataset;
import uk.ac.ebi.ega.data.edge.service.FileMetaService;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
public class RemoteFileMetaServiceImplTests {
    public static final File TEST_FILE = new File("test-file",
            new HashSet<String>(),
            "my-test-file.bam",
            "test-path/my-test-file.bam",
            "my-test-file.bam",
            123,
            "woop",
            "poow",
            "TESTING");
    private static MockWebServer mockWebServer;

    @Autowired
    private FileMetaService fileMetaService;

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

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
        public WebClient fileMetaServiceWebClient() {
            String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
            return WebClient.create(baseUrl);
        }
    }

    @Test
    public void getFile_whenFileExists_returnsFile() throws JsonProcessingException, InterruptedException {

        // Set up the first request for the file metadata
        mockWebServer.enqueue(new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(new File[]{
                        TEST_FILE
                }))
        );

        // Set up the second request for the datasets
        mockWebServer.enqueue(new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(objectMapper.writeValueAsString(new FileDataset[]{
                        new FileDataset("test-file", "test-dataset")
                }))
        );

        // Make the request
        File result = fileMetaService.getFile("test-file");

        // Assert that the first request was for the file metadata
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/file/test-file", request.getPath());

        // Assert that the second request was for the datasets
        request = mockWebServer.takeRequest();
        assertEquals("/file/test-file/datasets", request.getPath());

        // Assert that the result has all the data expected
        assertEquals(TEST_FILE.getFileId(), result.getFileId());
        assertTrue(result.getDatasetIds().contains("test-dataset"));
        assertEquals(TEST_FILE.getDisplayFileName(), result.getDisplayFileName());
        assertEquals(TEST_FILE.getDisplayFilePath(), result.getDisplayFilePath());
    }
}
