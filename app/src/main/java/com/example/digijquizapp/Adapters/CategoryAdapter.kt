package com.example.digijquizapp.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.digijquizapp.Models.CategoryModel
import com.example.digijquizapp.R
import com.example.digijquizapp.SetsActivity
import com.example.digijquizapp.databinding.ItemCategoryBinding
import com.squareup.picasso.Picasso

// Adapter for MainActivity
class CategoryAdapter(var context: Context, var list: ArrayList<CategoryModel>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemCategoryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: CategoryModel = list[position]

        holder.binding.categoryName.text = model.categoryName

        // A library for downloading and caching images for Android
        Picasso.get().load(model.categoryImage)
            .placeholder(R.drawable.placeholder)
            .into(holder.binding.categoryImages)

        holder.itemView.setOnClickListener {

            val intent = Intent(context, SetsActivity::class.java)
            intent.putExtra("category", model.categoryName)
            intent.putExtra("sets", model.setNumbers)
            intent.putExtra("key", model.key)


            context.startActivity(intent)
        }
    }

}