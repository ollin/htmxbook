package com.nautsch.htmxbook.contactmanagement

import java.util.*

data class Contact(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String,

    ) {
    companion object {
        fun createNew(name: String, email: String, phone: String): Contact {
            return Contact(
                id = UUID.randomUUID(),
                name = name,
                email = email,
                phone = phone,
            )
        }
    }
}

data class ContactUnsaved(
    val name: String,
    val email: String,
    val phone: String,
)