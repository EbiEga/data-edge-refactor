package uk.ac.ebi.ega.data.edge.commons.shared.dto;


public class Dataset {
    private String datasetId;
    private String description;
    private String dacStableId;
    private String doubleSignature;

    public Dataset(String datasetId, String description, String dacStableId, String doubleSignature) {
        this.datasetId = datasetId;
        this.description = description;
        this.dacStableId = dacStableId;
        this.doubleSignature = doubleSignature;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDacStableId() {
        return dacStableId;
    }

    public void setDacStableId(String dacStableId) {
        this.dacStableId = dacStableId;
    }

    public String getDoubleSignature() {
        return doubleSignature;
    }

    public void setDoubleSignature(String doubleSignature) {
        this.doubleSignature = doubleSignature;
    }
}
