package pl.edu.ug.cmms;

public record RejectedRow(
    int lineIndex,
    String reason,
    String rawLine
) {
}
