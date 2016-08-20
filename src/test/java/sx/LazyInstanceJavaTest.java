package sx;

import kotlin.jvm.functions.Function0;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by masc on 20/08/16.
 */
public class LazyInstanceJavaTest {
    class TestClass {
    }

    @Test
    public void testInitNull() {
        LazyInstance<TestClass> li = new LazyInstance<TestClass>(new Function0<TestClass>() {
            @Override
            public TestClass invoke() {
                return null;
            }
        });

        Assert.assertNull(li.get());
    }
}
