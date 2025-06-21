package com.example.jewelryworkshop.domain

import java.time.LocalDateTime

data class Customer(
    val id: Long = 0L,
    val firstName: String,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    val fullName: String
        get() = "$firstName $lastName"
}