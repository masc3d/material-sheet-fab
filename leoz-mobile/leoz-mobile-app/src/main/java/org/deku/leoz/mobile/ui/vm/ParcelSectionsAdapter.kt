package org.deku.leoz.mobile.ui.vm

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.BuildConfig
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.utils.Log
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmHolder
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleSectionableVmItem
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
    private val sections = mutableListOf<ParcelSectionViewModel>()

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
     * Extension method to create flexible item from view model
     */
    private fun ParcelSectionViewModel.createSectionItem(): FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel> {
        return FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = this,
                isExpandableOnClick = false
        ).also {
            it.isSelectable = true
            it.isExpanded = false
        }
    }

    /**
     * Add parcel section
     * @param sectionViewModel Parcel section view model
     */
    fun addParcelSection(
            sectionViewModel: ParcelSectionViewModel) {

        // Don't add if section exists already
        if (this.sections.contains(sectionViewModel))
            return

        this.sections.add(sectionViewModel)

        sectionViewModel.items
                .observeOnMainThread()
                .subscribe { parcels ->
                    // Need to collapse before updating subitems to prevent weird glitches, eg expanded items remaining visible
                    this.collapseAll()

                    // Check if flexible item for this section exists
                    var sectionItem = this.currentItems.firstOrNull {
                        it.viewModel == sectionViewModel
                    }

                    if (sectionItem != null) {
                        if (!sectionViewModel.showIfEmpty && parcels.isEmpty()) {
                            this.removeItem(this.getGlobalPositionOf(sectionItem))
                            sectionItem = null
                        }
                    } else {
                        if (sectionViewModel.showIfEmpty || !parcels.isEmpty()) {
                            sectionItem = sectionViewModel.createSectionItem()
                            this.addItem(sectionItem)
                        }
                    }

                    if (sectionItem != null) {
                        sectionItem.subItems = parcels.map { parcel ->
                            val item = FlexibleSectionableVmItem(
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

                        this.updateItem(sectionItem)
                    }
                }

        if (sectionViewModel.showIfEmpty || sectionViewModel.items.blockingFirst().isNotEmpty())
            this.addItem(sectionViewModel.createSectionItem())

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

        this.sections.remove(sectionViewModel)
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