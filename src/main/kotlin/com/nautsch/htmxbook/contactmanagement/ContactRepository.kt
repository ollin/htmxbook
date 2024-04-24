package com.nautsch.htmxbook.contactmanagement

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.fakerConfig
import org.jooq.DSLContext
import org.jooq.generated.Tables.CONTACT
import org.jooq.generated.tables.records.ContactRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*


@Repository
class ContactRepository(
    private val dsl: DSLContext
) {

    init {
        val config = fakerConfig { locale = "de-CH" }
        val faker = Faker(config)
        for (i in 1..99) {
            val actualName = faker.name.name()

            val actualEmail = faker.internet
                .safeEmail("${actualName.lowercase()}"
                    .replace(" ", ".")
                    .replace("-", ".")
                )

            dsl.newRecord(CONTACT).apply {
                id = UUID.randomUUID()
                name = actualName
                email = actualEmail
                phone = faker.phoneNumber.phoneNumber()
            }.store()
        }
    }
    fun fetchAll(): List<Contact> {
        return dsl.selectFrom(CONTACT)
            .fetch()
            .map { it.toModel() }
    }
    fun fetchAll(pageable: Pageable): Page<Contact> {
        val total = dsl.fetchCount(CONTACT)
        val contacts = dsl.selectFrom(CONTACT)
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .fetch()
            .map { it.toModel() }

        return PageImpl(contacts, pageable, total.toLong())
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

    fun fetch(pageable: Pageable, query: String): Page<Contact> {
        val total = dsl.fetchCount(CONTACT)
        val contacts = dsl.selectFrom(CONTACT)
            .where(
                CONTACT.NAME.likeIgnoreCase("%$query%")
            )
            .or(
                CONTACT.EMAIL.likeIgnoreCase("%$query%")
            )
            .or(
                CONTACT.PHONE.likeIgnoreCase("%$query%")
            )
            .fetch()
            .map { it ->
                it.toModel() }

        return PageImpl(contacts, pageable, total.toLong())
    }

    fun count(): Int {
        return dsl.fetchCount(CONTACT)
    }

    fun delete(id: List<UUID>) {
        dsl.deleteFrom(CONTACT)
            .where(CONTACT.ID.`in`(id))
            .execute()

    }
}

