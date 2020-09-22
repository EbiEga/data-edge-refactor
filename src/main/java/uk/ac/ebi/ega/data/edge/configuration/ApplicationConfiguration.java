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
package uk.ac.ebi.ega.data.edge.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import uk.ac.ebi.ega.data.edge.configuration.properties.S3Properties;
import uk.ac.ebi.ega.data.edge.service.FileMetaService;
import uk.ac.ebi.ega.data.edge.service.FileStreamingService;
import uk.ac.ebi.ega.data.edge.service.KeyService;
import uk.ac.ebi.ega.data.edge.service.internal.KeyServiceImpl;
import uk.ac.ebi.ega.data.edge.service.internal.RemoteFileMetaServiceImpl;
import uk.ac.ebi.ega.data.edge.service.internal.S3FileStreamingService;

import java.net.URI;
import java.time.Duration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public KeyService keyService(WebClient.Builder webClientBuilder) {
        return new KeyServiceImpl(webClientBuilder.build());
    }

    @Bean
    public FileMetaService fileMetaService(WebClient.Builder webClientBuilder) {
        return new RemoteFileMetaServiceImpl(webClientBuilder.build());
    }

    @Bean
    public FileStreamingService fileStreamingService(S3AsyncClient s3AsyncClient, S3Properties s3Properties) {
        return new S3FileStreamingService(s3AsyncClient, s3Properties);
    }

    @Bean
    public S3AsyncClient s3AsyncClient(S3Properties s3Properties) {

        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();

        S3AsyncClientBuilder b = S3AsyncClient.builder()
                .httpClient(httpClient)
                .credentialsProvider(() -> AwsBasicCredentials.create(s3Properties.getAccessKeyId(), s3Properties.getSecretAccessKey()))

                .serviceConfiguration(serviceConfiguration);

        if (s3Properties.getRegion() != null && !s3Properties.getRegion().isEmpty()) {
            b = b.region(Region.of(s3Properties.getRegion()));
        }

        if (s3Properties.getEndpoint() != null && !s3Properties.getEndpoint().isEmpty()) {
            b = b.endpointOverride(URI.create(s3Properties.getEndpoint()));
        }

        return b.build();
    }

    @Bean
    @ConfigurationProperties(prefix = "data.edge.s3")
    public S3Properties s3Properties() {
        return new S3Properties();
    }

}
