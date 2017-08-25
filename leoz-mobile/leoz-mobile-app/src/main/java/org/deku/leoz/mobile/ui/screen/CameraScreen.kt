package org.deku.leoz.mobile.ui.screen

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.flurgle.camerakit.CameraKit
import com.flurgle.camerakit.CameraListener
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.tnt.innight.mobile.Sounds
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_camera.*
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.view.ActionItem
import org.jetbrains.anko.imageBitmap
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import sx.rx.subscribeOn
import java.util.concurrent.ExecutorService

/**
 * Generic camera screen
 * Created by phpr on 03.08.2017.
 */
open class CameraScreen(target: Fragment? = null) : BaseCameraScreen<Any>(target) { }