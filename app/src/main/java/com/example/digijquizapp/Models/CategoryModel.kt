package com.example.digijquizapp.Models

class CategoryModel(var categoryName: String? = null, var categoryImage: String? = null, var key: String? = null, var setNumbers: Int = 0) {

    constructor(categoryName: String?, categoryImage: String?, key: String?, setNumbers: String?) : this(categoryName, categoryImage, key) {
        this.setNumbers = setNumbers?.toIntOrNull() ?: 0
    }
}