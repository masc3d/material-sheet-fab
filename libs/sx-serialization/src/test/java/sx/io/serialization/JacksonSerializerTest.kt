package sx.io.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.experimental.categories.Category
import sx.junit.StandardTest

/**
 * Created by masc on 13/09/16.
 */
@Category(StandardTest::class)
class JacksonSerializerTest
    : SerializerTest(
        serializer = JacksonSerializer(
                objectMapper = {
                    val mapper = ObjectMapper()
                    mapper.enable(SerializationFeature.INDENT_OUTPUT)
                    mapper
                }()
        )) {
}