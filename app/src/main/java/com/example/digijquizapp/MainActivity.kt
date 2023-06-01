package com.example.digijquizapp

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.digijquizapp.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import com.example.digijquizapp.Adapters.CategoryAdapter
import com.example.digijquizapp.Models.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var categoryImage: CircleImageView
    private lateinit var inputCategoryName: EditText
    private lateinit var uploadCategory: Button
    private lateinit var fetchImage: View

    lateinit var list: ArrayList<CategoryModel>
    lateinit var adapter: CategoryAdapter

    private lateinit var dialog: Dialog

    private lateinit var imageUri: Uri

    private var categoryNumber: Int = 0

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        list = ArrayList()

        dialog = Dialog(this)
        dialog.setContentView(R.layout.item_add_category_dialog)

        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading")
        progressDialog.setMessage("Please wait")

        uploadCategory = dialog.findViewById(R.id.btnUpload)
        inputCategoryName = dialog.findViewById(R.id.inputCategoryName)
        categoryImage = dialog.findViewById(R.id.categoryImage)
        fetchImage = dialog.findViewById(R.id.fetchImage)

        val layoutManager = GridLayoutManager(this, 2)
        binding.recyCategory.layoutManager = layoutManager

        adapter = CategoryAdapter(this, list)
        binding.recyCategory.adapter = adapter

        // Retrieving the category key
        database.reference.child("category_number")
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    categoryNumber = dataSnapshot.getValue(Int::class.java)!!
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to get category number", Toast.LENGTH_SHORT).show()
            }
        })

        // Retrieving categories from the database
        database.reference.child("categories").addValueEventListener( object :ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear()

                    for (categorySnapshot in dataSnapshot.children) {
                        list.add(
                            CategoryModel(
                                categorySnapshot.child("categoryName").value.toString(),
                                categorySnapshot.child("categoryImage").value.toString(),
                                categorySnapshot.key,
                                categorySnapshot.child("setNumbers").value.toString().toInt()
                            )
                        )

                    }

                    adapter.notifyDataSetChanged()

                } else {
                    Toast.makeText(this@MainActivity, "Category does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        // Add category button
        binding.addCategory.setOnClickListener{
            dialog.show()
        }

        // Place for the selected image
        fetchImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // When you click upload button
        uploadCategory.setOnClickListener {
            val name: String = inputCategoryName.text.toString()
            if (imageUri == null){
                Toast.makeText(this@MainActivity, "Please upload category image", Toast.LENGTH_SHORT).show()
            }
            else if (name.isEmpty()){
                inputCategoryName.error = "Enter category name"
            }
            else {
                progressDialog.show()
                uploadData()
            }
        }
    }

    // Upload category data
    private fun uploadData() {
        val reference = storage.reference.child("category").child(System.currentTimeMillis().toString())

        reference.putFile(imageUri).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener {
                val categoryModel = CategoryModel()
                categoryModel.categoryName = inputCategoryName.text.toString()
                categoryModel.setNumbers = 0
                categoryModel.categoryImage = it.toString()

                // Saving categories
                database.reference.child("categories").child("category"+categoryNumber++)
                    .setValue(categoryModel)
                    .addOnSuccessListener {
                        // The save was successful
                        Toast.makeText(this@MainActivity, "data upload", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                        // An error occurred while saving
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }

                // Saving the category number value
                database.reference.child("category_number")
                    .setValue(categoryNumber)
                    .addOnSuccessListener {
                        // The save was successful
                        Toast.makeText(this@MainActivity, "Category number saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // An error occurred while saving
                        Toast.makeText(this@MainActivity, "Failed to save category number", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Convert image to link
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && data != null) {
            imageUri = data.data!!
            categoryImage.setImageURI(imageUri)
        }
    }
}