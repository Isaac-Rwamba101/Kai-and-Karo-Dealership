package com.kaiandkaro.dealership.models

data class Vehicle(
    val id: String = "",
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val type: String = "",
    val fuel: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val isSold: Boolean = false,
    val documentUrl: String = "",
    val documentName: String = "",
    val sellerId: String = ""
)
