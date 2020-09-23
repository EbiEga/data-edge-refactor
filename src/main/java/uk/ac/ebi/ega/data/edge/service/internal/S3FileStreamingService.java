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
package uk.ac.ebi.ega.data.edge.service.internal;

import org.springframework.http.HttpRange;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import uk.ac.ebi.ega.data.edge.configuration.properties.S3Properties;
import uk.ac.ebi.ega.data.edge.controllers.FluxResponseProvider;
import uk.ac.ebi.ega.data.edge.controllers.S3StreamingResponse;
import uk.ac.ebi.ega.data.edge.service.FileStreamingService;

import java.util.concurrent.CompletableFuture;

public class S3FileStreamingService implements FileStreamingService {

    private S3AsyncClient s3client;

    private S3Properties s3Properties;

    public S3FileStreamingService(S3AsyncClient s3client, S3Properties s3Properties) {
        this.s3client = s3client;
        this.s3Properties = s3Properties;
    }

    @Override
    public CompletableFuture<Long> getFileSize(String id) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(id)
                .build();
        return s3client.headObject(request).thenApply(HeadObjectResponse::contentLength);
    }

    @Override
    public CompletableFuture<S3StreamingResponse> streamFile(String id, HttpRange range) {
        // TODO connect to the file metadata and get the proper files :)
        GetObjectRequest.Builder builder = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(id);
        if (range != null) {
            builder = builder.range("bytes=" + range.toString());
        }
        return s3client.getObject(builder.build(), new FluxResponseProvider(range));
    }

}
