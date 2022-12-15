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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.format("Total time: %s%n", Duration.between(start, Instant.now()).toString().substring(2)
                    .replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase());
            System.out.format("Finished at: %s%n%n", formatter.format(ZonedDateTime.now()));
        }));

        System.out.println();
        System.out.println(startMessage);
        System.out.println();
        task.call();
    }
    
    public static String toBinaryString(final int value, final int digits) {
        final String binStr = Integer.toBinaryString(value);
        if (binStr.length() >= digits) {
            return binStr;
        }
        final StringBuilder sb = new StringBuilder();
        do {
            sb.append('0');
        } while (sb.length() + binStr.length() < digits);
        sb.append(binStr);
        return sb.toString();
    }
    
    public static synchronized void println() {
        System.out.println();
    }
    
    public static synchronized void println(final String message) {
        System.out.println(message);
    }
    
    public static synchronized void format(final String message, final Object... args) {
        System.out.format(message, args);
    } 
    
    public static synchronized void printlnError() {
        System.err.println();
    }    
    
    public static synchronized void printlnError(final String message) {
        System.err.println(message);
    }
    
    public static synchronized void formatError(final String message, final Object... args) {
        System.err.format(message, args);
    } 
    
    public static synchronized void printStackTrace(final Throwable e) {
        e.printStackTrace();
    }
}
