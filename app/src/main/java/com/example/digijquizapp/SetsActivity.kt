package com.example.digijquizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.digijquizapp.Adapters.GridAdapter
import com.example.digijquizapp.databinding.ActivitySetsBinding
import com.google.firebase.database.FirebaseDatabase

// Management activity_sets
class SetsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetsBinding
    private lateinit var database: FirebaseDatabase

    private lateinit var adapter: GridAdapter

    private var a: Int = 1
    private var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        key = intent.getStringExtra("key")

        adapter = GridAdapter(intent.getIntExtra("sets", 0),
            intent.getStringExtra("category"),
            key!!,
            object: GridAdapter.GridListener {
                override fun addSets() {
                    database.reference.child("categories").child(key!!)
                        .child("setNumbers").setValue(intent.getIntExtra("sets", 0) + a++)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                adapter.sets++
                                adapter.notifyDataSetChanged()
                            } else {
                                Toast.makeText(this@SetsActivity, it.exception!!.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        )

        binding.gridView.adapter = adapter
    }
}