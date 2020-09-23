package uk.ac.ebi.ega.data.edge.rest;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
public class MainControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient client;

    @Test
    public void canGetVersionWithoutAuthentication() throws URISyntaxException {
        client.get().uri(new URI("http://localhost:" + port + "/version"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("1.0.0");
    }

    @Test
    public void cannotGetSecretWithoutAuthentication() throws URISyntaxException {
        client.get().uri(new URI("http://localhost:" + port + "/secret"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void canGetSecretWithTokenFromAuthServer() throws URISyntaxException {

        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setUsername("ega-test-data@ebi.ac.uk");
        details.setPassword("egarocks");

        // Totally stolen from pyega3
        details.setClientId("f20cd2d3-682a-4568-a53e-4262ef54c8f4");
        details.setClientSecret("AMenuDLjVdVo4BSwi0QD54LL6NeVDEZRzEQUJ7hJOM3g4imDZBHHX0hNfKHPeQIGkskhtCmqAJtt_jm7EKq-rWw");

        details.setGrantType("password");
        details.setScope(List.of("openid"));
        details.setAccessTokenUri("https://ega.ebi.ac.uk:8443/ega-openid-connect-server/token");

        DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
        ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
        OAuth2AccessToken accessToken = provider.obtainAccessToken(details, clientContext.getAccessTokenRequest());

        client.get().uri(new URI("http://localhost:" + port + "/secret"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getValue())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("omgsosecret ega-test-data@ebi.ac.uk");
    }
}
