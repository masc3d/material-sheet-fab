package sx.rs

import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.StandardTest
import sx.log.slf4j.trace

/**
 * Created by masc on 13.02.18.
 */
@Category(StandardTest::class)
class FlaskTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testFlaskBooleanExpression() {
        log.trace {
            FlaskFilter(FlaskBooleanExpression(
                    or = arrayOf("1", "2", "3").map {
                        FlaskPredicate(
                                name = "field",
                                op = FlaskOperator.LIKE,
                                value = it
                        )
                    })).toJson()
        }
    }
}