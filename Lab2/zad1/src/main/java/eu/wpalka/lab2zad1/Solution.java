import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {
    private enum Zone {
        RECEIVING,
        STORAGE,
        PICKING,
        SHIPPING,
    };
    private record Item(Zone zone, int quantity) {};

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = in.lines().filter(line -> !line.isBlank()).toList();
        
        List<Item> items = new ArrayList<>();
        int unknown = 0;

        for(String line : lines) {
            String[] parts = line.split(";");
            try {
                Zone zone = Zone.valueOf(parts[1]);
                int quantity;

                try {
                    quantity = Integer.parseInt(parts[2]);
    
                } catch(NumberFormatException e) {
                    throw new IllegalArgumentException("Niem a liczby: " + e);
                };

                items.add(new Item(zone, quantity));

            } catch(IllegalArgumentException e) {
                unknown++;
            }
        }

        Map<Zone, Long> zoneTotalAmount = items.stream()
            .collect(Collectors.groupingBy(Item::zone,
                Collectors.summingLong(Item::quantity)
            ));
        
        for( Zone zone : Zone.values() ) {
            System.out.println(zone + " " + zoneTotalAmount.getOrDefault(zone, 0L) );
        }

        System.out.println("unknown: " + unknown);
    }
}