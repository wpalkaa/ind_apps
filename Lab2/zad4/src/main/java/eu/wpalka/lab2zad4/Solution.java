package eu.wpalka.lab2zad4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = in.lines().filter(line -> !line.isBlank()).toList();
    
        Map<String, TreeSet<String>> result = lines.stream()
            .flatMap(line -> {
                String[] parts = line.split(":", 2);
                String id = parts[0].trim();
                String tagsPart = parts[1];

                return Arrays.stream(tagsPart.split(",", -1))
                    .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                    .filter(tag -> !tag.isEmpty())
                    .map(tag -> Map.entry(tag, id));
            })
            .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    TreeMap::new,
                    Collectors.mapping(
                            Map.Entry::getValue,
                            Collectors.toCollection(TreeSet::new)
                    )
            ));

            if (result.isEmpty()) {
                System.out.println("no tags");
            } else {
                result.forEach((tag, ids) -> 
                        System.out.println(tag + ": " + String.join(" ", ids)));
            }
    }
}