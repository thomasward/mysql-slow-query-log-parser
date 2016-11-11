import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    public static void main(String [] args) {

        final String inputFileName = "C:\\work\\mysql-slow.log";
        final String outputFileName = "C:\\work\\output.csv";

        try (Stream<String> lineStream = Files.lines(Paths.get(inputFileName));
             PrintWriter writer = new PrintWriter(outputFileName, "UTF-8")) {
            print("Reading file at " + inputFileName);
            print("Writing file at " + outputFileName);

            final LinesOfQueriesBuilder linesOfQueriesBuilder = new LinesOfQueriesBuilder();
            lineStream.filter(filterFromFirstQueryPredicate()).forEachOrdered(line -> linesOfQueriesBuilder.consume(line));

            linesOfQueriesBuilder
                    .getLinesOfQueries()
                    .parallelStream().map(lines -> {
                        return new Query(
                                lines.get(0),
                                lines.get(1),
                                lines.get(2),
                                String.join("\n", lines.subList(3, lines.size())));
                    })
                    .forEachOrdered(query -> {
                        final String csvLine = String.format("%d,%s", query.getTimeStamp(), query.getQueryTime());
                        writer.println(csvLine);
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class LinesOfQueriesBuilder {
        final List<List<String>> linesOfQueries = new ArrayList<>();
        List<String> linesOfQuery = null;

        public void consume(String line) {
            if (isFirstLineofQuery(line)) {
                if (linesOfQuery != null) {
                    linesOfQueries.add(linesOfQuery);
                }
                linesOfQuery = new ArrayList<>();
            }
            linesOfQuery.add(line);
        }

        public List<List<String>> getLinesOfQueries() {
            return linesOfQueries;
        }
    }

    private static Predicate<String> filterFromFirstQueryPredicate() {
        return new Predicate<String>() {
            boolean hasFoundFirstQuery = false;
            @Override
            public boolean test(String s) {
                if (hasFoundFirstQuery) {
                    return true;
                } else if (isFirstLineofQuery(s)) {
                    hasFoundFirstQuery = true;
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    private static boolean isFirstLineofQuery(String s) {
        return s.startsWith("# Time");
    }

    private static void print(String message) {
        System.out.println(message);
    }
}
