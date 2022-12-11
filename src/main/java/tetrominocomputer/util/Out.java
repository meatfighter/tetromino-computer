package tetrominocomputer.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

public class Out {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
       
    public static void timeTask(final String startMessage, final Runnable task) {
        try {
            timeTask(startMessage, () -> {
                task.run();
                return null;
            });
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void timeTask(final String startMessage, final Callable<Void> task) throws Exception {
        final Instant start = Instant.now();
        try {            
            System.out.println(startMessage);
            System.out.println();
            task.call();
        } finally {
            System.out.format("Total time: %s%n", Duration.between(start, Instant.now()).toString().substring(2)
                    .replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase());
            System.out.format("Finished at: %s%n", formatter.format(ZonedDateTime.now()));
            System.out.println();
        }            
    }
}
