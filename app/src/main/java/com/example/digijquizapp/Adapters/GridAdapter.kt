package com.example.digijquizapp.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.digijquizapp.QuestionActivity

import com.example.digijquizapp.R
import com.example.digijquizapp.SetsActivity

// Adapter for SetsActivity
class GridAdapter (var sets: Int = 0, private var category: String?, private var key: String, private var gridListener: GridListener) : BaseAdapter() {

    override fun getCount() = sets + 1

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup?): View {

        val view: View = if (convertView == null) {
            LayoutInflater.from(viewGroup!!.context).inflate(R.layout.item_sets, viewGroup, false)
        } else {
            convertView
        }

        // Check for the add sets button and for the created set
        if (i == 0) {
            view.findViewById<TextView>(R.id.setName).text = "+"
        } else {
            view.findViewById<TextView>(R.id.setName).text = i.toString()
        }

        view.setOnClickListener {
            if (i == 0) {
                // Adding a set if it is an add button
                gridListener.addSets()
            } else {
                // Go to the window for creating questions for the set
                val intent = Intent(viewGroup!!.context, QuestionActivity::class.java)
                intent.putExtra("setNumbers", i)
                intent.putExtra("categoryName", category)
                viewGroup.context.startActivity(intent)
            }
        }

        return view
    }

    interface GridListener {
        fun addSets()
    }
}