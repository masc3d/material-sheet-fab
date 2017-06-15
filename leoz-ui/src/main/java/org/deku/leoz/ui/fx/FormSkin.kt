/*
 * Copyright (c) 2013, dooApp <contact@dooapp.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of dooApp nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.deku.leoz.ui.fx

import com.dooapp.fxform.FXForm
import com.dooapp.fxform.model.Element
import com.dooapp.fxform.view.FXFormSkin
import com.dooapp.fxform.view.NodeCreationException
import com.dooapp.fxform.view.control.AutoHidableLabel
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Separator
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import java.util.*

/**
 * Modified skin based on FormFX inline skin
 */
class FormSkin(fxForm: FXForm<Any>) : FXFormSkin(fxForm) {

    private fun createTitleNode(): Node {
        val label = AutoHidableLabel()
        label.styleClass.add("form-title")
        label.textProperty().bind(this.skinnable.titleProperty())
        return label
    }

    protected var gridPane: GridPane? = null
    protected var row = 0
    private val categoryNode = HashMap<String, Node>()

    @Throws(NodeCreationException::class)
    override fun createRootNode(): Node {
        val titleBox = VBox()
        titleBox.children.add(createTitleNode())
        val contentBox = VBox()
        contentBox.padding = Insets(5.0, 5.0, 5.0, 5.0)
        contentBox.styleClass.add("form-content-box")
        titleBox.children.add(contentBox)
        contentBox.spacing = 5.0
        gridPane = GridPane()
        //gridPane.setHgap(2.0);
        //gridPane.setVgap(2.0);
        contentBox.children.addAll(createClassLevelConstraintNode(), gridPane)
        return titleBox
    }

    override fun createElementNodes(element: Element<Any>): FXFormSkin.ElementNodes {
        val editor = createEditor(element)
        val label = createLabel(element)
        val constraint = createConstraint(element)
        val tooltip = createTooltip(element)
        GridPane.setHgrow(editor.node, Priority.SOMETIMES)
        GridPane.setHalignment(label.node, HPos.RIGHT)
        gridPane!!.addRow(row++, label.node, editor.node)
        gridPane!!.add(constraint.node, 1, row++)
        gridPane!!.add(tooltip.node, 1, row++)
        return FXFormSkin.ElementNodes(label, editor, tooltip, constraint)
    }

    override fun deleteElementNodes(elementNodes: FXFormSkin.ElementNodes) {
        removeRow(GridPane.getRowIndex(elementNodes.editor.node)!!)
        removeRow(GridPane.getRowIndex(elementNodes.tooltip.node)!!)
        removeRow(GridPane.getRowIndex(elementNodes.constraint.node)!!)
    }

    override fun addCategory(category: String?) {
        if (categoryMap.keys.size > 1 && category != null) {
            val node = createCategoryNode()
            categoryNode.put(category, node)
            GridPane.setColumnSpan(node, 2)
            gridPane?.add(node, 0, row++)
            row++
        }
    }

    protected fun createCategoryNode(): Node {
        return Separator()
    }

    override fun removeCategory(category: String?) {
        if (categoryNode.containsKey(category)) {
            removeRow(GridPane.getRowIndex(categoryNode[category])!!)
            categoryNode.remove(category)
        }
    }


    /**
     * Remove a row by moving all nodes under this row one row up.

     * @param row
     */
    protected fun removeRow(row: Int) {
        // copy children to another list since we are going to iterate on it and modify the children list
        val children = LinkedList(gridPane!!.children)
        for (node in children) {
            val nodeRow = GridPane.getRowIndex(node)!!
            if (nodeRow == row) {
                gridPane!!.children.remove(node)
            } else if (nodeRow > row) {
                GridPane.setRowIndex(node, nodeRow - 1)
            }
        }
        this.row--
    }


    override fun toString(): String {
        return "Inline skin"
    }

}
