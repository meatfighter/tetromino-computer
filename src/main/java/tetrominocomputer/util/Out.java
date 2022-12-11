package tetrominocomputer.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Out {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
    
    public static String since(final Instant start) {
        return Duration.between(start, Instant.now()).toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }    
    
    public static String now() {        
        return formatter.format(ZonedDateTime.now());
    }
}
