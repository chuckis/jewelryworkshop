package com.example.jewelryworkshop.domain.usecase

import com.example.jewelryworkshop.domain.Customer
import com.example.jewelryworkshop.domain.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ======================== ADD CUSTOMER ========================
// Contains business logic: validation, normalization, business rules
data class AddCustomerParams(
    val firstName: String,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

class AddCustomerUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(params: AddCustomerParams): Long {
        validateCustomerData(params)

        val customer = Customer(
            firstName = params.firstName.trim(),
            lastName = params.lastName?.trim(),
            phoneNumber = params.phoneNumber?.trim(),
            email = params.email?.trim()?.lowercase(),
            address = params.address?.trim(),
            notes = params.notes?.trim()
        )

        return repository.addCustomer(customer)
    }

    private fun validateCustomerData(params: AddCustomerParams) {
        require(params.firstName.isNotBlank()) { "First name cannot be empty" }
        params.email?.let { email ->
            require(isValidEmail(email)) { "Invalid email format" }
        }
        params.phoneNumber?.let { phone ->
            require(isValidPhoneNumber(phone)) { "Invalid phone number format" }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.replace(Regex("[^\\d]"), "").length >= 10
    }
}

// ======================== UPDATE CUSTOMER ========================
// Contains business logic: validation, preserving creation date, normalization
data class UpdateCustomerParams(
    val customerId: Long,
    val firstName: String,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)

class UpdateCustomerUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(params: UpdateCustomerParams) {
        validateCustomerData(params)

        // Get existing customer to preserve creation date and other metadata
        val existingCustomer = repository.getCustomer(Customer(id = params.customerId, firstName = ""))

        val updatedCustomer = existingCustomer.copy(
            firstName = params.firstName.trim(),
            lastName = params.lastName?.trim(),
            phoneNumber = params.phoneNumber?.trim(),
            email = params.email?.trim()?.lowercase(),
            address = params.address?.trim(),
            notes = params.notes?.trim()
        )

        repository.updateCustomer(updatedCustomer)
    }

    private fun validateCustomerData(params: UpdateCustomerParams) {
        require(params.customerId > 0) { "Invalid customer ID" }
        require(params.firstName.isNotBlank()) { "First name cannot be empty" }
        params.email?.let { email ->
            require(isValidEmail(email)) { "Invalid email format" }
        }
        params.phoneNumber?.let { phone ->
            require(isValidPhoneNumber(phone)) { "Invalid phone number format" }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.replace(Regex("[^\\d]"), "").length >= 10
    }
}

// ======================== SEARCH CUSTOMERS ========================
// Contains business logic: search algorithm, filtering logic
data class SearchCustomersParams(
    val query: String
)

class SearchCustomersUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(params: SearchCustomersParams): Flow<List<Customer>> {
        require(params.query.isNotBlank()) { "Search query cannot be empty" }

        return repository.allCustomers().map { customers ->
            customers.filter { customer ->
                customer.firstName.contains(params.query, ignoreCase = true) ||
                        customer.lastName?.contains(params.query, ignoreCase = true) == true ||
                        customer.email?.contains(params.query, ignoreCase = true) == true ||
                        customer.phoneNumber?.contains(params.query, ignoreCase = true) == true
            }
        }
    }
}

// ======================== VALIDATE CUSTOMER ========================
// Pure business logic: validation rules without persistence
data class ValidateCustomerParams(
    val firstName: String,
    val email: String? = null,
    val phoneNumber: String? = null
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

class ValidateCustomerUseCase {
    suspend operator fun invoke(params: ValidateCustomerParams): ValidationResult {
        val errors = mutableListOf<String>()

        if (params.firstName.isBlank()) {
            errors.add("First name is required")
        }

        params.email?.let { email ->
            if (!isValidEmail(email)) {
                errors.add("Email format is invalid")
            }
        }

        params.phoneNumber?.let { phone ->
            if (!isValidPhoneNumber(phone)) {
                errors.add("Phone number format is invalid")
            }
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.replace(Regex("[^\\d]"), "").length >= 10
    }
}

// ======================== FIND CUSTOMERS BY EMAIL ========================
// Contains business logic: email normalization, duplicate detection
data class FindCustomersByEmailParams(
    val email: String
)

class FindCustomersByEmailUseCase(
    private val repository: CustomerRepository
) {
    suspend operator fun invoke(params: FindCustomersByEmailParams): Flow<List<Customer>> {
        require(params.email.isNotBlank()) { "Email cannot be empty" }
        require(isValidEmail(params.email)) { "Invalid email format" }

        val normalizedEmail = params.email.trim().lowercase()

        return repository.allCustomers().map { customers ->
            customers.filter { it.email?.lowercase() == normalizedEmail }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

// ======================== USAGE EXAMPLES ========================
/*
ViewModel usage:

class CustomerViewModel(
    private val repository: CustomerRepository,
    private val addCustomerUseCase: AddCustomerUseCase,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    private val searchCustomersUseCase: SearchCustomersUseCase,
    private val validateCustomerUseCase: ValidateCustomerUseCase
) : ViewModel() {

    // Simple read - direct repository call
    suspend fun getCustomer(id: Long): Customer {
        return repository.getCustomer(Customer(id = id, firstName = ""))
    }

    // Simple delete - direct repository call
    suspend fun deleteCustomer(customer: Customer) {
        repository.deleteCustomer(customer)
    }

    // Get all customers - direct repository call
    fun getAllCustomers(): Flow<List<Customer>> {
        return repository.allCustomers()
    }

    // Complex operations - UseCase calls
    suspend fun addCustomer(params: AddCustomerParams): Long {
        return addCustomerUseCase(params)
    }

    suspend fun updateCustomer(params: UpdateCustomerParams) {
        updateCustomerUseCase(params)
    }

    fun searchCustomers(query: String): Flow<List<Customer>> {
        return searchCustomersUseCase(SearchCustomersParams(query))
    }

    suspend fun validateCustomer(params: ValidateCustomerParams): ValidationResult {
        return validateCustomerUseCase(params)
    }
}

// DI setup (Dagger/Hilt example):
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddCustomerUseCase(repository: CustomerRepository): AddCustomerUseCase {
        return AddCustomerUseCase(repository)
    }

    @Provides
    fun provideUpdateCustomerUseCase(repository: CustomerRepository): UpdateCustomerUseCase {
        return UpdateCustomerUseCase(repository)
    }

    @Provides
    fun provideSearchCustomersUseCase(repository: CustomerRepository): SearchCustomersUseCase {
        return SearchCustomersUseCase(repository)
    }

    @Provides
    fun provideValidateCustomerUseCase(): ValidateCustomerUseCase {
        return ValidateCustomerUseCase()
    }
}
*/