package sx.android.ui.materialdialogs

import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem

/**
 * Material dialogs adapter extensions
 * Created by masc on 19.10.17.
 */

/**
 * Add all simple list items
 * @param items Items
 */
fun MaterialSimpleListAdapter.addAll(items: Iterable<MaterialSimpleListItem>) =
    items.forEach { this.add(it) }
