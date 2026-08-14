package com.example.data.model

data class DogBreedInfo(
    val isDog: Boolean = true,
    val breedName: String,
    val confidencePercentage: Int,
    val countryOfOrigin: String,
    val sizeCategory: String,
    val weightRange: String,
    val lifespan: String,
    val description: String,
    val temperament: List<String>,
    val funFacts: List<String>,
    val notADogReason: String? = null
)
