import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private final int DISK_SIZE = 4096;

    public static long block(long size) {
        if( size <= 0 ) {
            return 0;
        }
        return (size + DISK_SIZE-1) / DISK_SIZE;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();

        String line;
        long totalBlocks = 0;
        long totalFiles = 0;
        long totalBytes = 0;


        while ((line = in.readLine()) != null) {
            line = line.trim();

            if( line.isEmpty() ) {
                continue;
            }

            lines.add(line);
            long size = Long.parseLong(line);

            totalBlocks += block(size);
            totalFiles += 1;
            totalBytes += size;
        }

        System.out.println("Wyjście");
        System.out.println("files: " + totalFiles);
        System.out.println("bytes: " + totalBytes);
        System.out.println("blocks: " + totalBlocks);
        System.out.println("MiB: " +  (double) totalBytes/1024/1024    );
    }
}
