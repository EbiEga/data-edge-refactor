/*
 * Copyright 2016 ELIXIR EGA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.ega.data.edge.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

@Configuration
public class OAuth2ResourceConfig {

    @Bean
    public OpaqueTokenIntrospector tokenIntrospector(final @Value("${auth.server.url}") String checkTokenUrl,
                                                     final @Value("${auth.server.clientId}") String clientId,
                                                     final @Value("${auth.server.clientsecret}") String clientSecret) {
        return new NimbusOpaqueTokenIntrospector(checkTokenUrl, clientId, clientSecret);
    }
}

