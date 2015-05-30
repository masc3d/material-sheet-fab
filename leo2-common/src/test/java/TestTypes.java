import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by masc on 30.05.15.
 */
@Ignore
public class TestTypes {

    @Test
    public void testDate() {
        LocalDate.parse("2015-01-01");
    }

    @Test
    public void testTime() {
        System.out.println(LocalTime.parse("10:33:42").format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
    }
}
