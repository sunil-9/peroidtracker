package com.ifemini.groupie

import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.day_view_list_item.*
import com.ifemini.Symptom
import com.ifemini.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder


class ChildItem(val symptom: Symptom, var state: Boolean, var onClick: () -> Unit): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.day_item.text = symptom.name
        viewHolder.item_checkbox.isChecked = state

        viewHolder.list_item.setOnClickListener {
            viewHolder.item_checkbox.toggle()
            state = !state
            onClick.invoke()
        }
    }

    override fun getLayout(): Int {
        return R.layout.day_view_list_item
    }
}