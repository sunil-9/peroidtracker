package com.ifemini.groupie

import com.ifemini.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.option_activity_header_item.*


class OptionHeader(val string: String): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.option_item_header_name.text = string
    }

    override fun getLayout(): Int {
        return R.layout.option_activity_header_item
    }
}