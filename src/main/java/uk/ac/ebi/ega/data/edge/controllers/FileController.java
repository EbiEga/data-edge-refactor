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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.ac.ebi.ega.data.edge.service.FileMetaService;
import uk.ac.ebi.ega.data.edge.service.FileStreamingService;

import java.nio.ByteBuffer;
import java.util.List;

@RestController
public class FileController {

    private FileStreamingService fileStreamingService;

    private FileMetaService fileMetaService;

    public FileController(FileStreamingService fileStreamingService, FileMetaService fileMetaService) {
        this.fileStreamingService = fileStreamingService;
        this.fileMetaService = fileMetaService;
    }

    @RequestMapping(method = RequestMethod.HEAD, path = "/files/{id}")
    Mono<ResponseEntity> getFileHeader(@PathVariable String id) {
        return Mono.fromCallable(() -> fileMetaService.getFile(id))
                .map(file -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .contentLength(file.getFileSize())
                        .build()
                );
    }

    @GetMapping(path = "/files/{id}")
    Mono<ResponseEntity<Flux<ByteBuffer>>> getFile(@PathVariable String id, @RequestHeader HttpHeaders httpHeaders) {
        final List<HttpRange> ranges = httpHeaders.getRange();
        HttpRange range = null;
        if (!ranges.isEmpty()) {
            range = ranges.get(0);
        }

        return Mono.fromFuture(fileStreamingService.streamFile(id, range))
                .map(response -> buildReponseEntity(response));
    }

    private ResponseEntity<Flux<ByteBuffer>> buildReponseEntity(S3StreamingResponse response) {
        response.assertSuccessful();

        HttpStatus httpStatus;
        if (response.getRange() == null) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.PARTIAL_CONTENT;
        }

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.contentLength()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.getFileName() + "\"");
        if (response.getRange() != null) {
            builder.header(HttpHeaders.CONTENT_RANGE, "bytes " + response.getRange().toString());
        }
        return builder.body(response.getFlux());
    }

}
