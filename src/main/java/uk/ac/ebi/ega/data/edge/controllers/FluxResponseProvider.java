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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRange;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class FluxResponseProvider implements AsyncResponseTransformer<GetObjectResponse, S3StreamingResponse> {

    private final static Logger logger = LoggerFactory.getLogger(FluxResponseProvider.class);

    private S3StreamingResponse response;

    private final HttpRange range;

    public FluxResponseProvider(HttpRange range) {
        this.range = range;
    }

    @Override
    public CompletableFuture<S3StreamingResponse> prepare() {
        response = new S3StreamingResponse(range);
        return response.getCompletableFuture();
    }

    @Override
    public void onResponse(GetObjectResponse getObjectResponse) {
        this.response.setResponse(getObjectResponse);
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> sdkPublisher) {
        response.setFlux(Flux.from(sdkPublisher).log().map(byteBuffer -> {
            logger.info("do something! {}", byteBuffer.array());
            return byteBuffer;
        }));
        response.getCompletableFuture().complete(response);
    }

    @Override
    public void exceptionOccurred(Throwable throwable) {
        response.getCompletableFuture().completeExceptionally(throwable);
    }

    public HttpRange getRange() {
        return range;
    }

}
