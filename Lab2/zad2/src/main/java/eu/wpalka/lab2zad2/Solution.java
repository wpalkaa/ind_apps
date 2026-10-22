package eu.wpalka.lab2zad2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {
    record Transaction(String client, Long amount) {};

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = in.lines().filter(line -> !line.isBlank()).toList();
    
        List<Transaction> transactions = new ArrayList<>();
        for( int i = 1; i < lines.size(); i++ ) {
            String[] parts = lines.get(i).split(";");
            String client = parts[0];
            try{
                Long amount = Long.parseLong(parts[1]);
                Transaction t = new Transaction(client, amount);
                transactions.add(t);
            } catch( IllegalArgumentException e ) {
                throw new IllegalArgumentException("Kwota nie jest Long");
            }
        }

        int k = Integer.parseInt(lines.get(0));
        Map<String, Long> clientsAccount = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::client, Collectors.summingLong(Transaction::amount)));
        
        List<Map.Entry<String, Long>> topClients = clientsAccount.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .sorted(Map.Entry.
                <String, Long>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey())
            )
            .limit(k)
            .toList();

            if (topClients.isEmpty()) {
            System.out.println("none");
        } else {
            topClients.forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));
        }
    }
}
