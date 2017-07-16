package org.deku.leoz.mobile.model

import android.databinding.Bindable
import android.databinding.Observable
import android.os.Parcelable
import io.requery.*
import org.deku.leoz.mobile.BR
import sx.android.databinding.BaseRxObservable

/**
 * Requery entity for testing
 * Created by masc on 16.07.17.
 */
@Entity
@Table(name = "test_address")
abstract class BaseTestAddress(
        @Key @Generated @JvmField var id: Int = 0,
        @Bindable @JvmField var line1: String = "",
        @Bindable @JvmField var line2: String = "",
        @Bindable @JvmField var line3: String = "",
        @Bindable @JvmField var street: String = "",
        @Bindable @JvmField var streetNo: String = "",
        @Bindable @JvmField var zipCode: String = "",
        @Bindable @JvmField var city: String = "",
        @Bindable @JvmField var latitude: Double = 0.0,
        @Bindable @JvmField var longitude: Double = 0.0,
        @Bindable @JvmField var phone: String = ""
) : BaseRxObservable(), Persistable, Parcelable, Observable {

    val line1Field by lazy { ObservableRxField(BR.line1, { this.line1 }) }
}
