package org.deku.leoz.mobile.ui.vm

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.BuildConfig
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.Log
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmHolder
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleVmHolder
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem
import sx.rx.ObservableRxProperty

/**
 * Parcel sections adapter
 * Created by masc on 28.07.17.
 */
class ParcelSectionsAdapter
    :
        FlexibleAdapter<
                FlexibleExpandableVmItem<
                        ParcelSectionViewModel,
                        ParcelViewModel
                        >
                >
        (listOf(), null, true) {

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

    /**
     * Add parcel section
     * @param sectionViewModel Parcel section view model
     */
    fun addParcelSection(
            sectionViewModel: ParcelSectionViewModel) {

        // Don't add if section exists already
        if (this.currentItems.any {
            it.viewModel == sectionViewModel
        })
            return

        val sectionItem = FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = sectionViewModel,
                isExpandableOnClick = false
        )

        sectionViewModel.parcels
                .observeOnMainThread()
                .subscribe { parcels ->
                    log.trace("UPDATING ITEMS [${parcels.count()}]")

                    sectionItem.subItems = parcels.map { parcel ->
                        val item = FlexibleVmSectionableItem(
                                viewRes = R.layout.item_parcel,
                                variableId = BR.parcel,
                                viewModel = ParcelViewModel(parcel)
                        )

                        item.isEnabled = true
                        item.isDraggable = false
                        item.isSwipeable = false
                        item.isSelectable = false
                        item.header = sectionItem

                        item
                    }

                    if (!sectionViewModel.showIfEmpty) {
                        val position = this.getGlobalPositionOf(sectionItem)
                        if (parcels.isEmpty()) {
                            if (position >= 0)
                                this.removeItem(position)
                        } else {
                            if (position < 0)
                                this.addItem(sectionItem)
                        }
                    }

                    // Need to collapse on complete sublist update to prevent weird glitches
                    this.collapseAll()
                }

        sectionItem.isSelectable = true
        sectionItem.isExpanded = false

        this.sections.add(sectionItem)

        if (sectionViewModel.showIfEmpty || sectionViewModel.parcels.blockingFirst().isNotEmpty())
            this.addItem(sectionItem)

        this.collapseAll()
    }

    /**
     * Remove parcel section
     * @param sectionViewModel Parcel section view model
     */
    fun removeParcelSection(sectionViewModel: ParcelSectionViewModel) {
        val item = this.currentItems.firstOrNull {
            it.viewModel == sectionViewModel
        }

        if (item != null) {
            this.removeItem(this.getGlobalPositionOf(item))
        }
    }

    /**
     * Select parcel section
     */
    private fun selectParcelSection(item: IFlexible<FlexibleExpandableVmHolder>) {
        val adapter = this

        val position = this.getGlobalPositionOf(item)

        // When re-establishing selection when item is selected, flexible adapter may behave erratically. thus checking

        val firstSelection = adapter.selectedItemCount == 0

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

            val expand = Runnable {
                adapter.expand(adapter.getGlobalPositionOf(item))
            }

            if (firstSelection)
            // Add delay on first selection to avoid drawing glitches
                adapter.recyclerView.postDelayed(expand, 200)
            else
                adapter.recyclerView.post(expand)
        }
    }
}