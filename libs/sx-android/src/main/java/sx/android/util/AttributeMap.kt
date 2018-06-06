package sx.android.util

import android.util.AttributeSet

/**
 * Attribute set wrapper for convenient lookups
 */
class AttributeMap(
        attributeSet: AttributeSet
) : AttributeSet by attributeSet {

    /**
     * Attribute map entry
     */
    data class Entry(
            val index: Int,
            val name: String,
            val nameResource: Int
    )

    /**
     * Attributes by name resource
     */
    val byNameResource by lazy {
        (0..this.attributeCount - 1).map {
            Entry(
                    index = it,
                    name = this.getAttributeName(it),
                    nameResource = this.getAttributeNameResource(it)
            )
        }
                .associateBy { it.nameResource }
    }

    fun getResource(nameRes: Int): Int? {
        return this.byNameResource.get(nameRes)?.let {
            this.getAttributeResourceValue(it.index, 0).let { if (it == 0) null else it }
        }
    }
}

fun AttributeSet.toAttributeMap(): AttributeMap = AttributeMap(this)


