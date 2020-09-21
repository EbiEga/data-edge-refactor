package uk.ac.ebi.ega.data.edge.commons.exception;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException(String fileId) {
        super(String.format("File not found : %s", fileId));
    }
}
