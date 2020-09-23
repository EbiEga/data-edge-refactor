package uk.ac.ebi.ega.data.edge.rest;

import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class MainController {
    @RequestMapping("version")
    @ResponseBody
    public Mono<String> getVersion() {
        return Mono.just("1.0.0");
    }

    @RequestMapping("secret")
    @ResponseBody
    public Mono<String> getSecret(BearerTokenAuthentication principal) {
        return Mono.just("omgsosecret " + principal.getTokenAttributes().get("user_id"));
    }
}
