package com.nautsch.htmxbook.contactmanagement

import org.jooq.DSLContext
import org.jooq.generated.Tables.CONTACT
import org.jooq.generated.tables.records.ContactRecord
import org.springframework.stereotype.Repository
import java.util.*


@Repository
class ContactRepository(
    private val dsl: DSLContext
) {

    init {
        dsl.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = "John Doe"
            email = "john.doe@mail.local"
            phone = "+1234567890"
        }.store()
        dsl.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = "Melly Blubber"
            email = "melly.blubber@mail.local"
            phone = "+1234567891"
        }.store()
        dsl.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = "Dolly Fluff"
            email = "dolly.fluff@mail.local"
            phone = "+1234567892"
        }.store()
    }
    fun fetchAll(): List<Contact> {
        return dsl.selectFrom(CONTACT)
            .fetch()
            .map { it.toModel() }
    }

    fun findById(uuid: UUID): Contact? {
        return dsl.selectFrom(CONTACT)
            .where(CONTACT.ID.eq(uuid))  // bitte passen Sie diese Zeile Ihren tatsächlichen Tabellennamen und Feldnamen an
            .fetchOne()
            ?.toModel()
    }

    fun isExisting(email:String): Boolean {
        return dsl.fetchExists(dsl.selectFrom(CONTACT).where(CONTACT.EMAIL.eq(email)))
    }

    fun save(contact: Contact) {
        contact.toRecord().update()
    }
    fun save(contact: ContactUnsaved) {
        contact.toRecord().insert()
    }

    fun delete(id: UUID) {
        dsl.deleteFrom(CONTACT)
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
        return dsl.newRecord(CONTACT).apply {
            id = this@toRecord.id
            name = this@toRecord.name
            email = this@toRecord.email
            phone = this@toRecord.phone
        }
    }
    private fun ContactUnsaved.toRecord(): ContactRecord {
        return dsl.newRecord(CONTACT).apply {
            id = UUID.randomUUID()
            name = this@toRecord.name
            email = this@toRecord.email
            phone = this@toRecord.phone
        }
    }
}

