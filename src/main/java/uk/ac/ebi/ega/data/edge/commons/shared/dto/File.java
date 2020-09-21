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
package uk.ac.ebi.ega.data.edge.commons.shared.dto;


import java.util.HashSet;
import java.util.Set;

/**
 * Data transfer object class for files in the data API.
 *
 * @param fileId Stable ID of this file
 * @param datasetId ID of the dataset that gives permission to access this
 *     fileId
 * @param displayFileName filename to display inside the system
 * @param fileName original filename when the file was ingested
 * @param fileSize size of this file
 * @param unencryptedChecksum checksum of the unencrypted target file
 * @param unencryptedChecksumType checksum algorithm
 * @param fileStatus file status description
 *
 */

public class File {

    private String fileId;
    private Set<String> datasetIds;
    private String displayFileName;
    private String displayFilePath;
    private String fileName;
    private long fileSize;
    private String unencryptedChecksum;
    private String unencryptedChecksumType;
    private String fileStatus;

    public File(String fileId, Set<String> datasetIds, String displayFileName, String displayFilePath, String fileName, long fileSize, String unencryptedChecksum, String unencryptedChecksumType, String fileStatus) {
        this.fileId = fileId;
        this.datasetIds = datasetIds;
        this.displayFileName = displayFileName;
        this.displayFilePath = displayFilePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.unencryptedChecksum = unencryptedChecksum;
        this.unencryptedChecksumType = unencryptedChecksumType;
        this.fileStatus = fileStatus;
    }

    public File() {
        datasetIds = new HashSet<>();
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Set<String> getDatasetIds() {
        return datasetIds;
    }

    public void setDatasetIds(Set<String> datasetIds) {
        this.datasetIds = datasetIds;
    }

    public String getDisplayFileName() {
        return displayFileName;
    }

    public void setDisplayFileName(String displayFileName) {
        this.displayFileName = displayFileName;
    }

    public String getDisplayFilePath() {
        return displayFilePath;
    }

    public void setDisplayFilePath(String displayFilePath) {
        this.displayFilePath = displayFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUnencryptedChecksum() {
        return unencryptedChecksum;
    }

    public void setUnencryptedChecksum(String unencryptedChecksum) {
        this.unencryptedChecksum = unencryptedChecksum;
    }

    public String getUnencryptedChecksumType() {
        return unencryptedChecksumType;
    }

    public void setUnencryptedChecksumType(String unencryptedChecksumType) {
        this.unencryptedChecksumType = unencryptedChecksumType;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public void addDataset(String datasetId) {
        this.datasetIds.add(datasetId);
    }
}
