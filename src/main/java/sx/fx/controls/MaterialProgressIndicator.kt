package sx.fx.controls

import javafx.animation.*
import javafx.beans.InvalidationListener
import javafx.beans.property.*
import javafx.scene.Node
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.shape.Arc
import javafx.scene.shape.Circle
import javafx.scene.shape.StrokeLineCap
import javafx.util.Duration

/**
 * Created by masc on 13/10/2016.
 * Created by hansolo on 08.04.16.
 */
class MaterialProgressIndicator : Region() {
    private val dashOffset = SimpleDoubleProperty(0.0)
    private val dashArray_0 = SimpleDoubleProperty(1.0)
    private var size: Double = 0.toDouble()
    private var indeterminatePane: StackPane? = null
    private var progressPane: Pane? = null
    private var circle: Circle? = null
    private var arc: Arc? = null
    private val timeline: Timeline
    private var indeterminatePaneRotation: RotateTransition? = null
    private val listener: InvalidationListener
    private val progress: DoubleProperty
    private val indeterminate: BooleanProperty
    private val roundLineCap: BooleanProperty
    private var isRunning: Boolean = false

    init {
        styleClass.add("circular-progress")
        progress = object : DoublePropertyBase(0.0) {
            public override fun invalidated() {
                if (get() < 0) {
                    startIndeterminate()
                } else {
                    stopIndeterminate()
                    set(clamp(0.0, 1.0, get()))
                    redraw()
                }
            }

            override fun getBean(): Any {
                return this@MaterialProgressIndicator
            }

            override fun getName(): String {
                return "progress"
            }
        }
        indeterminate = object : BooleanPropertyBase(false) {
            override fun getBean(): Any {
                return this@MaterialProgressIndicator
            }

            override fun getName(): String {
                return "indeterminate"
            }
        }
        roundLineCap = object : BooleanPropertyBase(false) {
            public override fun invalidated() {
                if (get()) {
                    circle!!.strokeLineCap = StrokeLineCap.ROUND
                    arc!!.strokeLineCap = StrokeLineCap.ROUND
                } else {
                    circle!!.strokeLineCap = StrokeLineCap.SQUARE
                    arc!!.strokeLineCap = StrokeLineCap.SQUARE
                }
            }

            override fun getBean(): Any {
                return this@MaterialProgressIndicator
            }

            override fun getName(): String {
                return "roundLineCap"
            }
        }
        isRunning = false
        timeline = Timeline()
        listener = InvalidationListener { _ ->
            circle!!.strokeDashOffset = dashOffset.get()
            circle!!.strokeDashArray.setAll(dashArray_0.value, 200.0)
        }
        init()
        initGraphics()
        registerListeners()
    }


    // ******************** Initialization ************************************
    private fun init() {
        if (java.lang.Double.compare(prefWidth, 0.0) <= 0 || java.lang.Double.compare(prefHeight, 0.0) <= 0 ||
                java.lang.Double.compare(width, 0.0) <= 0 || java.lang.Double.compare(height, 0.0) <= 0) {
            if (prefWidth > 0 && prefHeight > 0) {
                setPrefSize(prefWidth, prefHeight)
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
            }
        }

        if (java.lang.Double.compare(minWidth, 0.0) <= 0 || java.lang.Double.compare(minHeight, 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT)
        }

        if (java.lang.Double.compare(maxWidth, 0.0) <= 0 || java.lang.Double.compare(maxHeight, 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT)
        }
    }

    private fun initGraphics() {
        val center = PREFERRED_WIDTH * 0.5
        val radius = PREFERRED_WIDTH * 0.45
        circle = Circle()
        circle!!.centerX = center
        circle!!.centerY = center
        circle!!.radius = radius
        circle!!.styleClass.add("indicator")
        circle!!.strokeLineCap = if (isRoundLineCap) StrokeLineCap.ROUND else StrokeLineCap.SQUARE
        circle!!.strokeWidth = 2.0 * PREFERRED_WIDTH * 0.10526316
        circle!!.strokeDashOffset = dashOffset.get()
        circle!!.strokeDashArray.setAll(dashArray_0.value, 200.0)

        arc = Arc(center, center, radius, radius, 90.0, -360.0 * getProgress())
        arc!!.strokeLineCap = if (isRoundLineCap) StrokeLineCap.ROUND else StrokeLineCap.SQUARE
        arc!!.strokeWidth = PREFERRED_WIDTH * 0.1
        arc!!.styleClass.add("indicator")

        indeterminatePane = StackPane(circle)
        indeterminatePane!!.isVisible = false

        progressPane = Pane(arc)
        progressPane!!.isVisible = java.lang.Double.compare(getProgress(), 0.0) != 0

        children.setAll(progressPane, indeterminatePane)

        // Setup timeline animation
        val kvDashOffset_0 = KeyValue(dashOffset, 0, Interpolator.EASE_BOTH)
        val kvDashOffset_50 = KeyValue(dashOffset, -32, Interpolator.EASE_BOTH)
        val kvDashOffset_100 = KeyValue(dashOffset, -64, Interpolator.EASE_BOTH)

        val kvDashArray_0_0 = KeyValue(dashArray_0, 5, Interpolator.EASE_BOTH)
        val kvDashArray_0_50 = KeyValue(dashArray_0, 89, Interpolator.EASE_BOTH)
        val kvDashArray_0_100 = KeyValue(dashArray_0, 89, Interpolator.EASE_BOTH)

        val kvRotate_0 = KeyValue(circle!!.rotateProperty(), -10, Interpolator.LINEAR)
        val kvRotate_100 = KeyValue(circle!!.rotateProperty(), 370, Interpolator.LINEAR)

        val kf0 = KeyFrame(Duration.ZERO, kvDashOffset_0, kvDashArray_0_0, kvRotate_0)
        val kf1 = KeyFrame(Duration.millis(1000.0), kvDashOffset_50, kvDashArray_0_50)
        val kf2 = KeyFrame(Duration.millis(1500.0), kvDashOffset_100, kvDashArray_0_100, kvRotate_100)

        timeline.cycleCount = Animation.INDEFINITE
        timeline.keyFrames.setAll(kf0, kf1, kf2)

        // Setup additional pane rotation
        indeterminatePaneRotation = RotateTransition()
        indeterminatePaneRotation!!.node = indeterminatePane
        indeterminatePaneRotation!!.fromAngle = 0.0
        indeterminatePaneRotation!!.toAngle = -360.0
        indeterminatePaneRotation!!.interpolator = Interpolator.LINEAR
        indeterminatePaneRotation!!.cycleCount = Timeline.INDEFINITE
        indeterminatePaneRotation!!.duration = Duration(4500.0)
    }

    private fun registerListeners() {
        widthProperty().addListener { _ -> resize() }
        heightProperty().addListener { _ -> resize() }
        progress.addListener { _ -> redraw() }
        dashOffset.addListener(listener)
    }


    // ******************** Methods *******************************************
    fun getProgress(): Double {
        return progress.get()
    }

    fun setProgress(PROGRESS: Double) {
        progress.set(PROGRESS)
    }

    fun progressProperty(): DoubleProperty {
        return progress
    }

    private fun startIndeterminate() {
        if (isRunning) return
        manageNode(indeterminatePane!!, true)
        manageNode(progressPane!!, false)
        timeline.play()
        indeterminatePaneRotation!!.play()
        isRunning = true
        indeterminate.set(true)
    }

    private fun stopIndeterminate() {
        if (!isRunning) return
        timeline.stop()
        indeterminatePaneRotation!!.stop()
        indeterminatePane!!.rotate = 0.0
        manageNode(progressPane!!, true)
        manageNode(indeterminatePane!!, false)
        isRunning = false
        indeterminate.set(false)
    }

    val isIndeterminate: Boolean
        get() = java.lang.Double.compare(ProgressIndicator.INDETERMINATE_PROGRESS, getProgress()) == 0

    fun indeterminateProperty(): ReadOnlyBooleanProperty {
        return indeterminate
    }

    var isRoundLineCap: Boolean
        get() = roundLineCap.get()
        set(BOOLEAN) = roundLineCap.set(BOOLEAN)

    fun roundLineCapProperty(): BooleanProperty {
        return roundLineCap
    }

    private fun manageNode(NODE: Node, MANAGED: Boolean) {
        if (MANAGED) {
            NODE.isManaged = true
            NODE.isVisible = true
        } else {
            NODE.isVisible = false
            NODE.isManaged = false
        }
    }


    // ******************** Resizing ******************************************
    private fun resize() {
        val width = width - insets.left - insets.right
        val height = height - insets.top - insets.bottom
        size = if (width < height) width else height

        if (width > 0 && height > 0) {
            indeterminatePane!!.setMaxSize(size, size)
            indeterminatePane!!.setPrefSize(size, size)
            indeterminatePane!!.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5)

            progressPane!!.setMaxSize(size, size)
            progressPane!!.setPrefSize(size, size)
            progressPane!!.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5)

            val center = size * 0.5
            val radius = size * 0.45

            arc!!.centerX = center
            arc!!.centerY = center
            arc!!.radiusX = radius
            arc!!.radiusY = radius
            arc!!.strokeWidth = size * 0.10526316

            val factor = size / 24
            circle!!.scaleX = factor
            circle!!.scaleY = factor
        }
    }

    private fun redraw() {
        val progress = getProgress()
        progressPane!!.isVisible = java.lang.Double.compare(progress, 0.0) > 0
        arc!!.length = -360.0 * progress
    }

    companion object {
        private val PREFERRED_WIDTH = 24.0
        private val PREFERRED_HEIGHT = 24.0
        private val MINIMUM_WIDTH = 12.0
        private val MINIMUM_HEIGHT = 12.0
        private val MAXIMUM_WIDTH = 1024.0
        private val MAXIMUM_HEIGHT = 1024.0

        private fun <T : Number> clamp(MIN: T, MAX: T, VALUE: T): T {
            if (VALUE.toDouble() < MIN.toDouble()) return MIN
            if (VALUE.toDouble() > MAX.toDouble()) return MAX
            return VALUE
        }
    }
}// ******************** Constructors **************************************
