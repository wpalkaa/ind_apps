package eu.wpalka.lab2zad2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Solution {

    record Booking(String room, LocalDateTime start, LocalDateTime end) {};

    public static Duration occupied(List<Booking> bookings) {
        Duration totalDuration = Duration.ZERO;

        LocalDateTime prevStart = bookings.get(0).start;
        LocalDateTime prevEnd = bookings.get(0).end;

        for( int i = 1; i < bookings.size(); i++ ) {
            LocalDateTime start = bookings.get(i).start;
            LocalDateTime end = bookings.get(i).end;

        if (!start.isAfter(prevEnd)) {
            if (end.isAfter(prevEnd)) {
                prevEnd = end;
            }
        } else {
            totalDuration = totalDuration.plus(Duration.between(prevStart, prevEnd));
            prevStart = start;
            prevEnd = end;
        }
        }
        
        totalDuration = totalDuration.plus(Duration.between(prevStart, prevEnd));
        return totalDuration;
    };

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = in.lines().filter(line -> !line.isBlank()).toList();
        
        List<Booking> bookings = new ArrayList<>();
        for( String line : lines ) {
            String[] parts = line.split(";");
            String room = parts[0];
            LocalDateTime start = LocalDateTime.parse(parts[1]);
            LocalDateTime end = LocalDateTime.parse(parts[2]);

            bookings.add(new Booking(room, start, end));
        };

        Map<String, List<Booking>> bookingsByRoom = bookings.stream()
            .sorted(Comparator.comparing(Booking::start))
            .collect(Collectors.groupingBy(Booking::room, TreeMap::new, Collectors.toList()));

        bookingsByRoom.forEach( (room, roomBookings) -> {
            Duration dur = occupied(roomBookings);
            long hours = dur.toHours();
            int minutes = dur.toMinutesPart();
            System.out.println(room + " " + hours + " h " + minutes + " min");
        });
    }
}
