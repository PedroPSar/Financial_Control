package com.development.pega.financialcontrol.viewholder

import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.ItemListener
import com.development.pega.financialcontrol.model.Income
import kotlinx.android.synthetic.main.income_recycler_view_row.view.*
import kotlin.properties.Delegates

class IncomesViewHolder(itemView: View, private val mItemListener: ItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    lateinit var btnMenu: ImageView
    private var incomeId = 0

    fun bind(income: Income) {
        val txtDate = "${income.day}/${income.month}/${income.year}"
        val txtValue = AppControl.Text.convertFloatToCurrencyText(income.value)
        val txtName = income.name
        incomeId = income.id

        btnMenu = itemView.findViewById(R.id.img_btn_item_menu)
        itemView.findViewById<TextView>(R.id.tv_txt_name).text = txtName
        itemView.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        itemView.findViewById<TextView>(R.id.tv_txt_value).text = txtValue

        itemView.img_btn_item_menu.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.img_btn_item_menu) {
            showItemMenu()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.edit_item -> {
                mItemListener.onEdit(incomeId)
                true
            }
            R.id.delete_item -> {
                true
            }
            else -> false
        }
    }

    private fun showItemMenu() {
        Log.d("teste", "clicked")
        val popUpMenu = PopupMenu(itemView.context, btnMenu)
        popUpMenu.menuInflater.inflate(R.menu.recycler_item_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener(this)
        popUpMenu.show()
    }

}