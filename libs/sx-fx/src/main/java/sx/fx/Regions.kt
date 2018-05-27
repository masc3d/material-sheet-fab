package sx.fx

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.Property
import javafx.scene.layout.Region
import javafx.util.Duration

/**
 * Created by masc on 27/09/2016.
 */
fun <T> Region.animate(value: T, duration: Duration, vararg property: Property<T>) {
    val tl = Timeline()

    // Convert to key values
    val kvs = property.map {
        KeyValue<T>(it, value)
    }.toTypedArray()

    val kf = KeyFrame(duration, *kvs)
    tl.keyFrames.add(kf)
    tl.play()
}