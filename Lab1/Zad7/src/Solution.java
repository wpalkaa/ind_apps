import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = in.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            lines.add(line);
        }

        String[] header = lines.get(0).split(";", -1);

        int columnCount = header.length;
        int rowCount = lines.size();
        int[] columnWidths = new int[columnCount];

        for (String row : lines) {
            String[] columns = row.split(";", -1);
            for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                if (columns[columnIndex].length() > columnWidths[columnIndex]) {
                    columnWidths[columnIndex] = columns[columnIndex].length();
                }
            }
        }

        String[] formattedRows = new String[rowCount];
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            StringBuilder formattedRow = new StringBuilder();
            String[] columns = lines.get(rowIndex).split(";", -1);
            for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                if (columnIndex > 0) {
                    formattedRow.append(" | ");
                }

                String column = columns[columnIndex];
                formattedRow.append(column);
                if (columnIndex < columns.length - 1) {
                    formattedRow.append(" ".repeat(columnWidths[columnIndex] - column.length()));
                }
            }

            formattedRows[rowIndex] = formattedRow.toString();
        }

        StringBuilder separatorRow = new StringBuilder();
        for( int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            separatorRow.append("-".repeat(columnWidths[columnIndex]));
            if (columnIndex < columnCount - 1) {
                separatorRow.append("-+-");
            }
        }

        System.out.println(formattedRows[0]);
        System.out.println(separatorRow.toString());

        for (int i = 1; i < formattedRows.length; i++) {
            System.out.println(formattedRows[i]);
        }
    }
}
