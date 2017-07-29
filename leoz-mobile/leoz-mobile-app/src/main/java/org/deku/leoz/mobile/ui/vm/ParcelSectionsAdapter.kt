package org.deku.leoz.mobile.ui.vm

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.BuildConfig
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem
import sx.rx.ObservableRxProperty

/**
 * Parcel sections adapter
 * Created by masc on 28.07.17.
 */
class ParcelSectionsAdapter
    :
        FlexibleAdapter<
                FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>
                >(listOf(), null, true) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Sections in their original order */
    private val sections = mutableListOf<FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>>()

    val selectedSectionProperty = ObservableRxProperty<ParcelSectionViewModel?>(null)
    var selectedSection by selectedSectionProperty

    init {
        if (BuildConfig.DEBUG) {
            // Only in DBEUG, as flexible adapter logging is broken when classes in stacktrace are obfuscated
            FlexibleAdapter.enableLogs(Log.Level.VERBOSE)
        }

        // TODO: unreliable. need to override flexibleadapter for proper reactive event
        this.addListener(object : FlexibleAdapter.OnItemClickListener {

            override fun onItemClick(position: Int): Boolean {
                log.trace("ON ITEM CLICK")
                val adapter = this@ParcelSectionsAdapter
                val item: Any? = adapter.getItem(position)

                if (item != null &&
                        item is FlexibleExpandableVmItem<*, *>) {

                    this@ParcelSectionsAdapter.selectedSection = item.viewModel as ParcelSectionViewModel
                }

                return true
            }
        })

        this.mode = FlexibleAdapter.MODE_SINGLE

        this.setStickyHeaders(true)
        this.showAllHeaders()
        this.setAutoCollapseOnExpand(true)
        this.collapseAll()

        this.selectedSectionProperty
                .subscribe { section ->
                    if (section.value != null) {
                        val item = headerItems
                                .map { it as FlexibleExpandableVmItem<*, *> }
                                .first { it.viewModel == section.value }

                        this.selectParcelSection(item)
                    }
                }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        log.trace("ADAPTER ATTACHED")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        // TODO detach is never called. verify if this may be a leak
        super.onDetachedFromRecyclerView(recyclerView)
        log.trace("ADAPTER DETACHED")
    }

    fun addParcelSection(
            sectionViewModel: ParcelSectionViewModel) {

        val sectionItem = FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = sectionViewModel,
                isExpandableOnClick = false
        )

        sectionViewModel.parcels
                .observeOnMainThread()
                .subscribe {
                    log.trace("UPDATING ITEMS [${it.count()}]")

                    // Need to collapse on complete sublist update to prevent weird glitches
                    this.collapseAll()

                    sectionItem.subItems = it.map {
                        val item = FlexibleVmSectionableItem(
                                viewRes = R.layout.item_parcel,
                                variableId = BR.parcel,
                                viewModel = ParcelViewModel(it)
                        )

                        item.isEnabled = true
                        item.isDraggable = false
                        item.isSwipeable = false
                        item.isSelectable = false
                        item.header = sectionItem

                        item
                    }

                    this.updateItem(sectionItem)
                }

        sectionItem.isSelectable = true
        sectionItem.isExpanded = false

        this.sections.add(sectionItem)
        this.addItem(sectionItem)

        this.collapseAll()
    }

    private fun selectParcelSection(item: FlexibleExpandableVmItem<*, *>) {
        val adapter = this

        val position = this.getGlobalPositionOf(item)

        // When re-establishing selection when item is selected, flexible adapter may behave erratically. thus checking
        if (!adapter.selectedPositions.contains(position)) {
            adapter.clearSelection()
            adapter.addSelection(position)
        }

        log.info("EXPANDED ${adapter.isExpanded(position)}")
        if (adapter.isExpanded(position)) {
            adapter.collapse(position)
        } else {
            // TODO: after expanding and fast scrolling to bottom, item click event doesn't fire unless the list is nudged a second time. glitchy, needs investigation
            adapter.collapseAll()
            adapter.moveItem(adapter.getGlobalPositionOf(item), 0)
            adapter.recyclerView.post {
                adapter.expand(0)
            }
        }
    }
}