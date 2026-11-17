package pl.edu.ug.cmms.importer;

public record RejectedRow(
    int lineNumber,
    String reason,
    String rawLine
) {
    public int lineIndex() {
        return lineNumber;
    }
}
