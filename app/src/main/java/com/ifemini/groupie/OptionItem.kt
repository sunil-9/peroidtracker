package com.ifemini.groupie

import com.ifemini.R
import com.ifemini.Symptom
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.options_activity_item.*

class OptionItem(val symptom: Symptom): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.option_item_name.text = symptom.name


        viewHolder.option_switch.isChecked = symptom.active

        viewHolder.option_item.setOnClickListener {
            viewHolder.option_switch.toggle()
            symptom.toggleActive()
        }
    }

    override fun getLayout(): Int {
        return R.layout.options_activity_item
    }
}