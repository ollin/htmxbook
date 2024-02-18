package com.nautsch.htmxbook.contactmanagement

import java.util.*

data class Contact(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String,
)