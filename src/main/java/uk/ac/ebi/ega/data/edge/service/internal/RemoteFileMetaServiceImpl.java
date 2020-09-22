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
package uk.ac.ebi.ega.data.edge.service.internal;

import org.springframework.cache.annotation.Cacheable;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.ac.ebi.ega.data.edge.commons.exception.DatasetNotFoundException;
import uk.ac.ebi.ega.data.edge.commons.exception.FileNotFoundException;
import uk.ac.ebi.ega.data.edge.commons.exception.NotFoundException;
import uk.ac.ebi.ega.data.edge.commons.shared.dto.Dataset;
import uk.ac.ebi.ega.data.edge.commons.shared.dto.File;
import uk.ac.ebi.ega.data.edge.commons.shared.dto.FileDataset;
import uk.ac.ebi.ega.data.edge.service.FileMetaService;

public class RemoteFileMetaServiceImpl implements FileMetaService {
    private final WebClient fileDatabaseClient;

    public RemoteFileMetaServiceImpl(WebClient fileDatabaseClient) {
        this.fileDatabaseClient = fileDatabaseClient;
    }

    /**
     * Returns a {@link File} descriptor for the requested file.
     *
     * @param fileId The stable ID of the file to request.
     * @return The requested file descriptor.
     */
    @Override
    @Cacheable(cacheNames = "fileFile")
    public File getFile(String fileId) throws NotFoundException {

        File file = fileDatabaseClient.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("file", fileId).build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, response -> Mono.error(new FileNotFoundException(fileId)))
                .bodyToFlux(File.class)
                .single()
                .block();

        Flux<FileDataset> datasets = fileDatabaseClient.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("file", fileId, "datasets").build())
                .retrieve()
                .bodyToFlux(FileDataset.class);

        for (FileDataset fileDataset : datasets.toIterable()) {
            file.addDataset(fileDataset.getDatasetId());
        }

        return file;
    }

    @Override
    @Cacheable(cacheNames = "datasetFile")
    public Dataset getDataset(String datasetId) throws NotFoundException, HttpClientErrorException {
        return fileDatabaseClient.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("datasets", datasetId).build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, response -> Mono.error(new DatasetNotFoundException(datasetId)))
                .bodyToMono(Dataset.class)
                .block();
    }

    /**
     * Returns the list of files for a given dataset from the file database
     * service.
     *
     * @param datasetId Stable ID of the dataset to request
     * @return List of files for the given dataset
     */
    @Override
    @Cacheable(cacheNames = "fileDatasetFile")
    public Iterable<File> getDatasetFiles(String datasetId) throws NotFoundException, HttpClientErrorException {

        return fileDatabaseClient.get()
                .uri(uriBuilder -> uriBuilder.pathSegment("datasets", datasetId, "files").build())
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND, response -> Mono.error(new DatasetNotFoundException(datasetId)))
                .bodyToFlux(File.class)
                .toIterable();
    }
}
