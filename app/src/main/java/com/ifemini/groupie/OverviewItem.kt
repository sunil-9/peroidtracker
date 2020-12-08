package com.ifemini.groupie

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.cycle_overview_item.*
import com.ifemini.R

class OverviewItem(val string: String): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.cycle_overview__item.text = string
    }

    override fun getLayout(): Int {
        return R.layout.cycle_overview_item
    }
}