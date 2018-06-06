package sx.rx

import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import sx.junit.StandardTest
import java.util.concurrent.Callable

/**
 * Created by masc on 12.02.18.
 */
@Category(StandardTest::class)
class ObservableTest {

    @Test
    fun testCallableToObservable() {
        Assert.assertEquals(
                null,
                Callable<Int?> {
                    null
                }
                        .toObservable()
                        .blockingFirst(null)
        )

        Assert.assertEquals(
                1,
                Callable<Int?> {
                    1
                }
                        .toObservable()
                        .blockingFirst()
        )
    }
}