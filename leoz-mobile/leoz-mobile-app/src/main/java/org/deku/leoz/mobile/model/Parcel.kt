package org.deku.leoz.mobile.model

data class Parcel(
        val id: String,
        var state: Parcel.State = Parcel.State.PENDING,
        val labelReference: String,
        val status: MutableList<Order.Status> = mutableListOf(),
        val length: Float = 0.0F,
        val height: Float = 0.0F,
        val width: Float = 0.0F,
        val weight: Float = 0.0F
) {
    enum class State{
        PENDING, LOADED, MISSING, DONE, FAILED
    }
}