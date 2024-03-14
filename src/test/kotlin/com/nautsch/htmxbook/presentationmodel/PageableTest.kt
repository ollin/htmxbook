package com.nautsch.htmxbook.presentationmodel

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest


class Pagination(private val page: Page<*>){

    val isPaginationEnabled: Boolean = page.totalPages > 1
    val isGotoFirstPageEnabled: Boolean = page.isFirst.not()
    val isGotoLastPageEnabled: Boolean = page.isLast.not()
}

class PageableTest: BehaviorSpec({

    given("1 name") {
        val names: List<String> = ((1..1).map { "entry-$it" }).toList()
        val total = names.size

        and("a page size of 10") {
            val pageSize = 10

            and("having the first page") {
                val pageable = PageRequest.of(0, 10)
                val page = PageImpl(names.subList(0, 1), pageable, total.toLong())

                `when`("creating Pagination"){
                    val pagination = Pagination(page)

                    then("isPaginationEnabled is false"){
                        pagination.isPaginationEnabled shouldBe false
                    }

                    then("isGotoFirstPageEnabled is false"){
                        pagination.isGotoFirstPageEnabled shouldBe false
                    }

                    then("isGotoLastPageEnabled is false"){
                        pagination.isGotoLastPageEnabled shouldBe false
                    }
                }

            }
        }
    }

    given("99 names") {
        val names:List<String> = ((1..99).map { "entry-$it" }).toList()
        val total = names.size

        and("a page size of 10") {
            val pageSize = 10

            and("having the first page") {
                val pageable = PageRequest.of(0, 10)
                val page = PageImpl(names.subList(0, 10), pageable, total.toLong())

                `when`("creating Pagination"){
                    val pagination = Pagination(page)

                    then("isPaginationEnabled is true"){
                        pagination.isPaginationEnabled shouldBe true
                    }

                    then("isGotoFirstPageEnabled should be false"){
                        pagination.isGotoFirstPageEnabled shouldBe false
                    }

                    then("isGotoLastPageEnabled is true"){
                        pagination.isGotoLastPageEnabled shouldBe true
                    }
                }
            }
            and("having the second page") {
                val pageable = PageRequest.of(2, 10)
                val page = PageImpl(names.subList(10, 20), pageable, total.toLong())

                `when`("creating Pagination"){
                    val pagination = Pagination(page)

                    then("isPaginationEnabled is true"){
                        pagination.isPaginationEnabled shouldBe true
                    }

                    then("isGotoFirstPageEnabled should be true"){
                        pagination.isGotoFirstPageEnabled shouldBe true
                    }

                    then("isGotoLastPageEnabled is true"){
                        pagination.isGotoLastPageEnabled shouldBe true
                    }
                }
            }
        }
    }
})