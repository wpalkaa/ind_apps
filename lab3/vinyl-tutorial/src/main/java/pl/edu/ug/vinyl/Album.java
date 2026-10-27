package pl.edu.ug.vinyl;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

// W albums.xml rok jest atrybutem (<album year="1959">), więc year oznaczamy jako atrybut;
// title i artist Jackson czyta z elementów o tej samej nazwie
public record Album(String title, String artist, @JacksonXmlProperty(isAttribute = true) int year) {
}
