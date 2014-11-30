package sx.util;

/**
 * Created by masc on 15.08.14.
 */
public class ExceptionUtils {
    public static String stackTraceToString(StackTraceElement[] st) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : st) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
