package uk.ac.ebi.ega.data.edge.commons.exception;

public class DatasetNotFoundException extends NotFoundException {
    public DatasetNotFoundException(String datasetId) {
        super(String.format("Dataset not found : %s", datasetId));
    }
}
