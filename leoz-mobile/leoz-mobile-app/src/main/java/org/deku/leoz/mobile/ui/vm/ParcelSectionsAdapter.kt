package org.deku.leoz.mobile.ui.vm

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import org.deku.leoz.mobile.BR
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.FlexibleExpandableVmItem
import sx.android.ui.flexibleadapter.FlexibleVmSectionableItem

/**
 * Created by masc on 28.07.17.
 */
class ParcelSectionsAdapter
    :
        FlexibleAdapter<
                FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>
                >(listOf(), null, true) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val _headerItems = mutableListOf<FlexibleExpandableVmItem<
            ParcelSectionViewModel, ParcelViewModel>>()

    init {
        FlexibleAdapter.enableLogs(Log.Level.VERBOSE)

        // TODO: unreliable. need to override flexibleadapter for proper reactive event
        this.addListener(object : FlexibleAdapter.OnItemClickListener {
            private var previousHeaderItem: Any? = null

            override fun onItemClick(position: Int): Boolean {
                val adapter = this@ParcelSectionsAdapter
                val item: Any? = adapter.getItem(position)

                if (item != null && _headerItems.contains(item)) {
                    val changed = (item != this.previousHeaderItem)

                    // Select & collapse
                    adapter.toggleSelection(position)

                    if (changed) {
                        adapter.collapseAll()
                    } else {
                        if (adapter.isExpanded(position)) {
                            adapter.collapse(position)
                        } else {
                            adapter.expand(position)
                        }
                    }

                    this.previousHeaderItem = item
                }

                return true
            }
        })

        this.mode = FlexibleAdapter.MODE_SINGLE

        this.setStickyHeaders(true)
        this.showAllHeaders()
//        this.setAutoCollapseOnExpand(true)
        this.collapseAll()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        log.trace("ADAPTER ATTACHED")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        log.trace("ADAPTER DETACHED")
    }

    fun addParcelSection(
            header: ParcelSectionViewModel) {

        val headerItem = FlexibleExpandableVmItem<ParcelSectionViewModel, ParcelViewModel>(
                viewRes = R.layout.item_parcel_header,
                variableId = BR.header,
                viewModel = header,
                isExpandableOnClick = false
        )

        header.parcels
                .observeOnMainThread()
                .subscribe {
                    log.trace("UPDATING ITEMS [${it.count()}]")
                    // Need to collapse on complete sublist update to prevent weird glitches
                    this.collapseAll()

                    headerItem.subItems = it.map {
                        val item = FlexibleVmSectionableItem(
                                viewRes = R.layout.item_parcel,
                                variableId = BR.parcel,
                                viewModel = ParcelViewModel(it)
                        )

                        item.isEnabled = true
                        item.isDraggable = false
                        item.isSwipeable = false
                        item.isSelectable = false
                        item.header = headerItem

                        item
                    }

                    this.updateItem(headerItem)
                }

        headerItem.isSelectable = true

        // Store expandable/header item in our own list, as expanding will
        _headerItems.add(headerItem)
        this.addItem(headerItem)

        this.collapseAll()
    }
}