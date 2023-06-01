package com.example.digijquizapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.digijquizapp.Models.QuestionModel
import com.example.digijquizapp.R
import com.example.digijquizapp.databinding.ActivityQuestionBinding
import com.example.digijquizapp.databinding.ItemQuestionsBinding

// Adapter for QuestionActivity
class QuestionsAdapter (var context: Context, var list: ArrayList<QuestionModel>, var categoryName: String, var listener: DeleteListener) : RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var binding: ItemQuestionsBinding = ItemQuestionsBinding.bind(itemView)
    }

    interface DeleteListener {
        fun onLongClick(position: Int, id: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_questions, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: QuestionModel = list[position]

        holder.binding.question.text = model.question

        // Handling a long click on a question
        holder.itemView.setOnClickListener {
            listener.onLongClick(position, list[position].key.toString())
        }
    }
}