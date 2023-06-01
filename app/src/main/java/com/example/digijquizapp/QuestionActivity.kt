package com.example.digijquizapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digijquizapp.Adapters.QuestionsAdapter
import com.example.digijquizapp.Models.QuestionModel
import com.example.digijquizapp.databinding.ActivityQuestionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Management activity_question
class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var list: ArrayList<QuestionModel>
    private lateinit var adapter: QuestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        database = FirebaseDatabase.getInstance()

        list = ArrayList()

        val setNumbers: Int = intent.getIntExtra("setNumbers", 0)
        val categoryName: String = intent.getStringExtra("categoryName")!!

        val layoutManager = LinearLayoutManager(this)
        binding.recyQuestions.layoutManager = layoutManager

        adapter = QuestionsAdapter(this,
            list,
            categoryName,
            object : QuestionsAdapter.DeleteListener {
                // Implementation of the interface for removing questions
                override fun onLongClick(position: Int, id: String) {

                    val builder: AlertDialog.Builder =  AlertDialog.Builder(this@QuestionActivity)
                    builder.setTitle("Delete question")
                    builder.setMessage("Are you sure, you want to delete question")

                    builder.setPositiveButton("Yes" ) {
                            dialogInterface: DialogInterface?, i: Int ->
                                    // Deleting a question from the database
                                    database.reference.child("Sets").child(categoryName)
                                    .child("questions").child(id).removeValue()
                                    .addOnSuccessListener {

                                        Toast.makeText(this@QuestionActivity, "question deleted", Toast.LENGTH_SHORT).show()

                                    }
                    }

                    builder.setNegativeButton("No") {
                            dialogInterface: DialogInterface?, i: Int ->
                                dialogInterface!!.dismiss()
                    }

                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()

                }
            })
        binding.recyQuestions.adapter = adapter

        // Getting questions from the database
        database.reference.child("Sets").child(categoryName).child("questions")
            .orderByChild("setNumbers").equalTo(setNumbers.toDouble())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle the data changes and retrieve the queried results
                    if (dataSnapshot.exists()) {
                        list.clear()

                        for (questionSnapshot in dataSnapshot.children) {
                            val model: QuestionModel? = questionSnapshot.getValue(QuestionModel::class.java)
                            model!!.key = questionSnapshot.key
                            list.add(model)
                        }

                        adapter.notifyDataSetChanged()
                    }
                    else {
                        Toast.makeText(this@QuestionActivity, "question is`t exist", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during the database operation
                    Toast.makeText(this@QuestionActivity, databaseError.message, Toast.LENGTH_SHORT).show()
                }
            })

        // Handling button clicks to add questions
        binding.addQuestion.setOnClickListener {
            val intent = Intent(this@QuestionActivity, AddQuestionActivity::class.java)
            intent.putExtra("category", categoryName)
            intent.putExtra("setNumbers", setNumbers)
            startActivity(intent)
        }
    }
}