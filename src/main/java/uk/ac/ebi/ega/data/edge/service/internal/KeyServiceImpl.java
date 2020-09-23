/*
 * Copyright 2020 ELIXIR EGA
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
package uk.ac.ebi.ega.data.edge.service.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.ac.ebi.ega.data.edge.commons.exception.FileNotFoundException;
import uk.ac.ebi.ega.data.edge.service.KeyService;
import org.springframework.stereotype.Service;

public class KeyServiceImpl implements KeyService{

    private WebClient keyService;

    public KeyServiceImpl(WebClient keyService) {
        this.keyService = keyService;
    }
    
    @Override
    public String getEncryptionAlgorithm(String fileId) {
        return keyService.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("keys", "encryptionalgorithm", fileId).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, response -> Mono.error(new FileNotFoundException(fileId)))
                .bodyToMono(String.class)
                .block();
    } 
}