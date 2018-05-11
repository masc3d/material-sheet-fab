package org.deku.leoz.mobile.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import com.tinsuke.icekick.extension.serialState
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_aidc_camera.*
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.aidc.AidcReader
import sx.android.aidc.CameraAidcReader
import sx.android.aidc.CompositeAidcReader
import sx.android.rx.observeOnMainThreadUntilEvent
import sx.android.ui.view.setBackgroundTintRes
import sx.android.ui.view.setIconTintRes

/**
 * Aidc camera fragment
 * Created by masc on 14.04.15.
 */
class AidcCameraFragment : Fragment<Any>() {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    /**
     * Global aidc reader
     */
    private val aidcReader: AidcReader by Kodein.global.lazy.instance()

    /**
     * Camera aidc reader
     */
    private val cameraReader: CameraAidcReader by Kodein.global.lazy.instance()

    /**
     * List of non-camera aidc readers
     */
    private val nonCameraReaders: List<AidcReader> by lazy {
        val aidcReader = this.aidcReader
        if (aidcReader is CompositeAidcReader) {
            aidcReader.readers
                    .filter { !(it is CameraAidcReader) }
        } else listOf()
    }

    private var isPinnedEventSubject = PublishSubject.create<Boolean>()
    /**
     * Indicates if aidc fragment is pinned
     */
    var isPinned by serialState(false, afterChange = { _, v ->
        this.isPinnedEventSubject.onNext(v)
    })
        private set


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            // Inflate the layout for this fragment
            inflater.inflate(R.layout.fragment_aidc_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.fab_aidc_camera_torch.setOnClickListener {
            this.cameraReader.torch = !this.cameraReader.torch
        }

        this.fab_aidc_camera_pin.setOnClickListener {
            this.isPinned = !this.isPinned
        }
    }

    override fun onResume() {
        log.trace("RESUME")
        super.onResume()

        this.cameraReader.torchProperty
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.fab_aidc_camera_torch.setIconTintRes(
                            if (it.value)
                                R.color.colorAccent
                            else
                                android.R.color.black)
                }

        this.isPinnedEventSubject
                .observeOnMainThreadUntilEvent(this, FragmentEvent.PAUSE)
                .subscribe {
                    this.fab_aidc_camera_pin.also { fab ->
                        when (it) {
                            true -> {
                                fab.setBackgroundTintRes(R.color.colorPrimary)
                                fab.setIconTintRes(android.R.color.white)
                            }
                            false -> {
                                fab.setBackgroundTintRes(R.color.colorDarkGrey)
                                fab.setIconTintRes(android.R.color.black)
                            }
                        }
                    }
                }

        // Disable all but the camera reader
        this.nonCameraReaders.forEach { it.enabled = false }

        // Enable camera reader and add viewfinder
        this.cameraReader.enabled = true
        this.uxContainer.addView(this.cameraReader.view)
    }

    override fun onPause() {
        log.trace("PAUSE")

        // Release camera and remove view finder
        this.cameraReader.enabled = false
        this.uxContainer.removeAllViews()

        // Re-enable other readers
        this.nonCameraReaders.forEach { it.enabled = true }

        super.onPause()
    }

}
