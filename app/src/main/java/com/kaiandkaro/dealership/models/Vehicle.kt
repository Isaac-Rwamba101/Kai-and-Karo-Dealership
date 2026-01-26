package com.kaiandkaro.dealership.models

data class Vehicle(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val isSold: Boolean = false
)
