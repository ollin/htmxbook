package com.nautsch.htmxbook.contactmanagement

import org.jooq.DSLContext
import org.jooq.generated.Tables.CONTACT
import org.jooq.generated.tables.records.ContactRecord
import org.springframework.stereotype.Repository
import java.util.*


@Repository
class ContactRepository(
    private val create: DSLContext
) {

    init {
        create.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = "John Doe"
            email = "john.doe@mail.local"
            phone = "+1234567890"
        }.store()
        create.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = "Melly Blubber"
            email = "melly.blubber@mail.local"
            phone = "+1234567891"
        }.store()
        create.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = "Dolly Fluff"
            email = "dolly.fluff@mail.local"
            phone = "+1234567892"
        }.store()
    }
    fun fetchAll(): List<Contact> {
        return create.selectFrom(CONTACT)
            .fetch()
            .map { it.toModel() }
    }

    fun save(contact: Contact) {
        contact.toRecord().store()
    }
    fun save(contact: ContactUnsaved) {
        contact.toRecord().store()
    }

    fun delete(id: UUID) {
        create.deleteFrom(CONTACT)
            .where(CONTACT.ID.eq(id))
            .execute()
    }

    private fun ContactRecord.toModel() = Contact(
        id = this.id,
        name = name,
        email = email,
        phone = phone
    )

    private fun Contact.toRecord(): ContactRecord {
        val record = ContactRecord()

        record.id = this.id
        record.name = this.name
        record.email = this.email
        record.phone = this.phone

        return record
    }
    private fun ContactUnsaved.toRecord(): ContactRecord {
        val record = ContactRecord()

        record.name = this.name
        record.email = this.email
        record.phone = this.phone

        return record
    }
}

