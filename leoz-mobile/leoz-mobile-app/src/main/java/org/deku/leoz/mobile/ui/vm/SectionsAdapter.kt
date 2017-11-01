package org.deku.leoz.mobile.ui.vm

import android.support.v7.widget.RecyclerView
import eu.davidea.flexibleadapter.BuildConfig
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import sx.android.rx.observeOnMainThread
import sx.android.ui.flexibleadapter.VmHeaderItem
import sx.android.ui.flexibleadapter.SimpleVmItem
import sx.rx.CompositeDisposableSupplier
import sx.rx.ObservableRxProperty
import sx.rx.bind

/**
 * Sections adapter
 * Created by masc on 28.07.17.
 */
class SectionsAdapter
    :
        FlexibleAdapter<VmHeaderItem<*, *>>
        (listOf(), null, true),
        CompositeDisposableSupplier {

    override val compositeDisposable = CompositeDisposable()

    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Sections in their original order */
    private val sections = mutableListOf<Any>()

    val selectedSectionProperty = ObservableRxProperty<SectionViewModel<*>?>(null)
    var selectedSection by selectedSectionProperty

    private val itemClickEventSubject by lazy { PublishSubject.create<Any>() }
    val itemClickEvent by lazy { itemClickEventSubject.hide() }

    init {
        if (BuildConfig.DEBUG) {
            // Only in DBEUG, as flexible adapter logging is broken when classes in stacktrace are obfuscated
            FlexibleAdapter.enableLogs(Log.Level.VERBOSE)
        }

        // TODO: unreliable. need to override flexibleadapter for proper reactive event
        this.addListener(object : FlexibleAdapter.OnItemClickListener {

            override fun onItemClick(position: Int): Boolean {
                val adapter = this@SectionsAdapter
                val item: Any? = adapter.getItem(position)

                if (item != null) {
                    this@SectionsAdapter.itemClickEventSubject.onNext(item)

                    if (item is VmHeaderItem<*, *> && item.viewModel is SectionViewModel<*>) {

                        val section = item.viewModel as SectionViewModel<*>

                        if (item.isSelectable && !isSectionSelected(section)) {
                            this@SectionsAdapter.selectedSection = section
                            adapter.expand(item)
                            adapter.recyclerView.scrollToPosition(0)
                        } else {
                            if (adapter.isSelected(position) || !item.isSelectable) {
                                if (adapter.isExpanded(position)) {
                                    adapter.collapse(position)
                                } else {
                                    // TODO: after expanding and fast scrolling to bottom, item click event doesn't fire unless the list is nudged a second time. glitchy, needs investigation
                                    adapter.collapseAll()
                                    adapter.expand(position)
                                    adapter.recyclerView.scrollToPosition(0)
                                }
                            } else {
                                log.trace("SELECT")
                                select(section)
                            }
                        }
                    }
                }

                return true
            }
        })

        this.mode = Mode.SINGLE

        this.setStickyHeaders(true)
        this.showAllHeaders()
        this.isAutoCollapseOnExpand = true
        this.collapseAll()

        this.selectedSectionProperty
                .subscribe {
                    val section = it.value
                    if (section != null && !this.isSectionSelected(section)) {
                        this.select(section)
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
     * Add section
     * @param sectionViewModel Parcel section view model
     */
    fun <T, S : SectionViewModel<T>> addSection(
            sectionVmItemProvider: () -> VmHeaderItem<SectionViewModel<T>, *>,
            vmItemProvider: (item: T) -> SimpleVmItem<*>) {

        fun createSectionItem(): VmHeaderItem<SectionViewModel<T>, *> {
            return sectionVmItemProvider.invoke()
                    .also {
                        it.isExpanded = false
                        it.isExpandableOnClick = false
                    }
        }

        val sectionItem = createSectionItem()
        val sectionViewModel = sectionItem.viewModel

        // Don't add if section exists already
        if (this.sections.contains(sectionViewModel))
            return

        this.sections.add(sectionViewModel)

        sectionViewModel.items
                .observeOnMainThread()
                .subscribe { items ->
                    // Need to collapse before updating subitems to prevent weird glitches, eg expanded items remaining visible
                    this.collapseAll()

                    // Check if flexible item for this section exists
                    var sectionItem = this.currentItems.firstOrNull {
                        it.viewModel == sectionViewModel
                    }

                    if (sectionItem != null) {
                        if (!sectionViewModel.showIfEmpty && items.isEmpty()) {
                            this.removeItem(this.getGlobalPositionOf(sectionItem))
                            sectionItem = null
                        }
                    } else {
                        if (sectionViewModel.showIfEmpty || !items.isEmpty()) {
                            // The section could have been removed prior to item change
                            if (this.sections.contains(sectionViewModel)) {
                                sectionItem = createSectionItem()
                                this.addItem(sectionItem)
                            }
                        }
                    }

                    if (sectionItem != null) {
                        sectionItem.subItems = items.map {
                            val item = vmItemProvider.invoke(it)

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
                .bind(this)

        if (sectionViewModel.showIfEmpty || sectionViewModel.items.blockingFirst().isNotEmpty())
            this.addItem(createSectionItem())

        this.collapseAll()
    }

    /**
     * Return flexible item for specific section
     * @param section Section
     */
    fun itemOf(section: SectionViewModel<*>): VmHeaderItem<*, *>? {
        return this.headerItems.firstOrNull {
            it is VmHeaderItem<*, *> && it.viewModel == section
        } as? VmHeaderItem<*, *>
    }

    /**
     * Determine position of section
     * @param section Section
     */
    fun positionOf(section: SectionViewModel<*>): Int {
        return this.itemOf(section)
                ?.let {
                    this.getGlobalPositionOf(it)
                }
                ?: -1
    }

    /**
     * Determine if section is currently selected
     * @param section Section
     */
    fun isSectionSelected(section: SectionViewModel<*>): Boolean =
            this.isSelected(positionOf(section))

    /**
     * Remove section
     * @param section Section view model
     */
    fun removeSection(section: SectionViewModel<*>) {
        val item = this.itemOf(section)

        if (this.isSectionSelected(section))
            this.selectedSection = null

        if (item != null) {
            this.removeItemWithDelay(item, 200, true)
        }

        this.sections.remove(section)

    }

    /**
     * Select section
     */
    private fun select(section: SectionViewModel<*>) {
        val adapter = this

        val item = itemOf(section)

        if (item == null)
            return

        var position = this.getGlobalPositionOf(item)

        if (!adapter.selectedPositions.contains(position)) {
            adapter.clearSelection()
            adapter.addSelection(position)
        }

        adapter.collapseAll()

        // Move selected section to top
        position = adapter.getGlobalPositionOf(item)
        adapter.moveItem(position, 0)

        // Restore other section positions to original order
        run {
            var index = 1
            this.sections
                    .filter { it != section }
                    .forEach {
                        positionOf(it as SectionViewModel<*>)
                                .takeIf { it >= 0 }
                                ?.also {
                                    adapter.moveItem(
                                            it,
                                            index++
                                    )
                                }
                    }
        }

        if (section.expandOnSelection) {
            adapter.expand(0)
        }

        adapter.recyclerView.scrollToPosition(0)
    }
}