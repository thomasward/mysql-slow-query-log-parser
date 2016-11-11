import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an individual query in a query log.
 */
public class Query {
    private static final Pattern queryTimePattern = Pattern.compile("Query_time: ([\\d\\.]+) ");
    private static final Pattern timeStampPattern = Pattern.compile("SET timestamp=(\\d+);");

    private final String time;
    private final String userAtHost;
    private final String queryTime;
    private final String query;
    private final long timeStamp;

    public Query(String time, String userAtHost, String queryTime, String query) {
        this.time = time;
        this.userAtHost = userAtHost;


        Matcher queryTimeMatcher = queryTimePattern.matcher(queryTime);
        queryTimeMatcher.find();
        this.queryTime = queryTimeMatcher.group(1);

        this.query = query;

        Matcher timeStampMatcher = timeStampPattern.matcher(query);
        timeStampMatcher.find();
        this.timeStamp = Long.valueOf(timeStampMatcher.group(1));
    }

    public String getTime() {
        return time;
    }

    public String getUserAtHost() {
        return userAtHost;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public String getQuery() {
        return query;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
