
package com.kaiandkaro.dealership.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "buyer" 
)
