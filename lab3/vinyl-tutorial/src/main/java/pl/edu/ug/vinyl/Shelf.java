package pl.edu.ug.vinyl;

import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

// Zwykła klasa bez adnotacji Springa; obiekt opisuje beans.xml
public class Shelf implements ApplicationRunner {

    private final String owner;
    // Lista płyt: Shelf nie wie, skąd pochodzi
    private final List<Album> albums;

    // Kolejność parametrów odpowiada kolejności elementów <constructor-arg> w XML
    public Shelf(String owner, List<Album> albums) {
        this.owner = owner;
        this.albums = albums;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Record shelf of " + owner + ": " + albums.size() + " albums"); // → Record shelf of Alice: 2 albums
        // Wypisuje jedną linię na płytę; wynik stoi pod poleceniem uruchomienia
        albums.forEach(a -> System.out.println("  " + a.year() + "  " + a.artist() + " - " + a.title()));
    }
}
