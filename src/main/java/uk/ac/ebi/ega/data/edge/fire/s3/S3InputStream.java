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
package uk.ac.ebi.ega.data.edge.fire.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

public class S3InputStream extends InputStream {

    private static final int CHUNK_SIZE = 100 * 1024 * 1024;

    private static Logger logger = LoggerFactory.getLogger(S3InputStream.class);

    private final ExecutorService downloadProducerexecutor;

    private final S3Client s3Client;

    private final String bucket;

    private final String path;

    private final long contentLength;

    private final long lastBytePosition;

    private long position;

    private long lastDownloadedByte;

    private ArrayBlockingQueue<CompletableFuture<InputStream>> bufferQueue;

    private InputStream currentBuffer;

    public S3InputStream(S3Client s3Client, String bucket, String path) throws IOException {
        this.downloadProducerexecutor = Executors.newSingleThreadExecutor();
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.path = path;
        this.position = 0;
        this.lastDownloadedByte = -1;
        this.bufferQueue = new ArrayBlockingQueue<>(3);
        contentLength = getSizeOfS3Object();
        lastBytePosition = contentLength - 1;

        downloadProducerexecutor.submit(this::produceDownloads);
        loadCurrentBufferIfNull();
    }

    private void produceDownloads() {
        while (preloadNextChunk()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized boolean preloadNextChunk() {
        if (lastDownloadedByte < lastBytePosition) {
            long startPosition = lastDownloadedByte + 1;
            long endPosition = Math.min(lastDownloadedByte + CHUNK_SIZE, lastBytePosition);
            try {
                bufferQueue.put(executeAndRetry(() -> downloadChunk(startPosition, endPosition)));
                lastDownloadedByte = endPosition;
            } catch (InterruptedException e) {
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
            return true;
        }
        return false;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        if (position == contentLength) {
            return -1;
        }

        int read = currentBuffer.read(bytes, offset, length);
        if (read == -1) {
            try {
                currentBuffer = bufferQueue.poll().get();
                read = currentBuffer.read(bytes, offset, length);
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Error, interrupted while reading data", e);
            }
        }

        position += read;
        return read;

    }

    private void loadCurrentBufferIfNull() throws IOException {
        if (currentBuffer == null) {
            try {
                currentBuffer = bufferQueue.take().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Error, interrupted while reading data", e);
            }
        }
    }

    @Override
    public int read() throws IOException {
        if (position == contentLength) {
            return -1;
        }

        int read = currentBuffer.read();
        if (read == -1) {
            try {
                currentBuffer = bufferQueue.poll().get();
                read = currentBuffer.read();
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Error, interrupted while reading data", e);
            }
        }

        position++;
        return read;
    }

    private <T> CompletableFuture<T> executeAndRetry(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier)
                .thenApply(CompletableFuture::completedFuture)
                .exceptionally(throwable -> retry(supplier, throwable, 0))
                .thenCompose(Function.identity());
    }

    private <T> CompletableFuture<T> retry(Supplier<T> supplier, Throwable throwable, int currentTry) {
        if (currentTry >= 4) {
            final CompletableFuture<T> failure = new CompletableFuture<>();
            failure.completeExceptionally(throwable);
            return failure;
        }
        return CompletableFuture.supplyAsync(supplier)
                .thenApply(CompletableFuture::completedFuture)
                .exceptionally(t -> {
                    throwable.addSuppressed(t);
                    return retry(supplier, throwable, currentTry + 1);
                })
                .thenCompose(Function.identity());
    }

    private InputStream downloadChunk(long start, long end) {
        logger.info("Preloading chunk, range: {} - {}", start, end);
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .bucket(bucket)
                .key(path)
                .range("bytes=" + start + "-" + end)
                .build();
        return s3Client.getObjectAsBytes(objectRequest).asInputStream();
    }

    public long getContentLength() {
        return contentLength;
    }

    private long getSizeOfS3Object() {
        return s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build()).contentLength();
    }

}