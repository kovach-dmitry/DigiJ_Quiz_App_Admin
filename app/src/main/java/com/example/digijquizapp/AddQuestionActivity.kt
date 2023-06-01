package com.example.digijquizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.example.digijquizapp.Models.QuestionModel
import com.example.digijquizapp.databinding.ActivityAddQuestionBinding
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates

class AddQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionBinding
    private var setN: Int = -1
    private lateinit var categoryName: String

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setN = intent.getIntExtra("setNumbers", -1)
        categoryName = intent.getStringExtra("category")!!

        database = FirebaseDatabase.getInstance()

        if (setN == -1) {
            finish()
            return
        }

        // Handling the upload of new questions
        binding.btnUploadQuestion.setOnClickListener {
            var correct: Int = -1

            for (i in 0 until binding.optionConteiner.childCount) {
                val answer: EditText = binding.answerContainer.getChildAt(i) as EditText

                if (answer.text.toString().isEmpty()) {
                    answer.error = "Required"
                    return@setOnClickListener
                }

                val radioButton: RadioButton = binding.optionConteiner.getChildAt(i) as RadioButton

                if (radioButton.isChecked) {
                    correct = i
                    break
                }
            }

            if (correct == -1) {
                Toast.makeText(this@AddQuestionActivity, "Please mark the correct option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val model = QuestionModel()

            model.question = binding.inputQuestion.text.toString()
            model.optionA = (binding.answerContainer.getChildAt(0) as EditText).text.toString()
            model.optionB = (binding.answerContainer.getChildAt(1) as EditText).text.toString()
            model.optionC = (binding.answerContainer.getChildAt(2) as EditText).text.toString()
            model.optionD = (binding.answerContainer.getChildAt(3) as EditText).text.toString()
            model.correctAnswer = (binding.answerContainer.getChildAt(correct) as EditText).text.toString()
            model.setNumbers = setN

            // Adding a new question to the database
            database.reference.child("Sets").child(categoryName).child("questions")
                .push()
                .setValue(model)
                .addOnCompleteListener {
                    Toast.makeText(this@AddQuestionActivity, "question added", Toast.LENGTH_SHORT).show()
                }

        }

    }
}