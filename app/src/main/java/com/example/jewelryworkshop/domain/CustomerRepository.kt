package com.example.jewelryworkshop.domain

import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun addCustomer(customer: Customer): Long
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)
    suspend fun getCustomer(customer: Customer): Customer
    suspend fun allCustomers(): Flow<List<Customer>>
}
