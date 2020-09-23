/*
 *
 * Copyright 2020 EMBL - European Bioinformatics Institute
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
 *
 */
package uk.ac.ebi.ega.data.edge.controllers;

import org.springframework.http.HttpRange;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import uk.ac.ebi.ega.data.edge.controllers.exceptions.FileCouldNotBeAccessed;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class S3StreamingResponse {

    private final CompletableFuture<S3StreamingResponse> completableFuture = new CompletableFuture<>();

    private GetObjectResponse response;

    private Flux<ByteBuffer> flux;

    private final HttpRange range;

    public S3StreamingResponse(HttpRange range) {
        this.range = range;
    }

    public void assertSuccessful() {
        SdkHttpResponse sdkResponse = response.sdkHttpResponse();
        if (sdkResponse == null) {
            throw new FileCouldNotBeAccessed(500, "Could not get an sdkResponse");
        }else if(!sdkResponse.isSuccessful()){
            throw new FileCouldNotBeAccessed(sdkResponse.statusCode(),sdkResponse.statusText());
        }
    }

    public CompletableFuture<S3StreamingResponse> getCompletableFuture() {
        return completableFuture;
    }

    public GetObjectResponse getResponse() {
        return response;
    }

    public void setResponse(GetObjectResponse response) {
        this.response = response;
    }

    public Flux<ByteBuffer> getFlux() {
        return flux;
    }

    public void setFlux(Flux<ByteBuffer> flux) {
        this.flux = flux;
    }

    public String contentType() {
        return response.contentType();
    }

    public long contentLength() {
        return response.contentLength();
    }

    public String getFileName() {
        // TODO
        return "test.cip";
    }

    public HttpRange getRange() {
        return range;
    }

}
