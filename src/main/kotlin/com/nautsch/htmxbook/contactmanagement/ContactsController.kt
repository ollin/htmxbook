package com.nautsch.htmxbook.contactmanagement

import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_SEE_OTHER
import jakarta.validation.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import kotlin.reflect.KClass

@Controller
@RequestMapping("/contacts")
class ContactsController(
    private val contactRepository: ContactRepository
) {

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
        val log = LoggerFactory.getLogger(ContactsController::class.java)
        val DEFAULT_PAGE_REQUEST = PageRequest.of(0, DEFAULT_PAGE_SIZE).withSort(Sort.by("email").descending())
    }
    @GetMapping("")
    fun contacts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "1000") size: Int,
        @RequestParam(defaultValue = "email,asc") sort: Array<String>,
        @RequestParam(defaultValue = "") query: String,
        @RequestHeader("HX-Trigger") hx_trigger: String?,
        model: ModelMap,
    ): ModelAndView {
        try {
            val contactsPage = if ("".equals(query.trim()))
                contactRepository.fetchAll(pageable(sort, page, size))
            else
                contactRepository.fetch(pageable(sort, page, size), query)

            model.addAttribute("contactsPage", contactsPage)
            model.addAttribute("contacts", contactsPage.content)
            model.addAttribute("currentPage", contactsPage.number + 1)
            model.addAttribute("totalItems", contactsPage.totalElements)
            model.addAttribute("totalPages", contactsPage.totalPages)
            model.addAttribute("pageSize", contactsPage.size)
        } catch (e: Exception) {
            log.error("Error fetching contacts", e)
            model.addAttribute("message", "Error fetching contacts")
        }

        if ("search".equals(hx_trigger)) {
            return ModelAndView("fragments/rows :: contact_rows", model)
        }
        return ModelAndView("index", model)

    }

    private fun pageable(
        sort: Array<String>,
        page: Int,
        size: Int
    ): Pageable {
        val sortField = sort[0]
        val sortDirection = sort[1]

        val direction = if (sortDirection == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val order = Sort.Order(direction, sortField)

        val pageable: Pageable = PageRequest.of(page, size, Sort.by(order))
        return pageable
    }

    @GetMapping("/count")
    fun count(): ResponseEntity<String> {
        try {
            Thread.sleep(1000)
            return ResponseEntity.ok("Total: ${contactRepository.count()}")
        } catch (InterruptedException: Exception) {
            Thread.currentThread().interrupt()
            return ResponseEntity.status(500).body("Error fetching count")
        }
    }

    @GetMapping("/{id}")
    fun getContact(@PathVariable id: String, model: ModelMap): ModelAndView {
        val contact = contactRepository.findById(UUID.fromString(id))
        model.addAttribute("contact", contact)
        return ModelAndView("show", model)
    }

    @GetMapping("/email_unique_validation")
    fun validateEmailUniquness(
        @RequestParam(required = false) email: String?,
        model: ModelMap,
    ): Any {
        val bindingResult = BeanPropertyBindingResult(NewContactForm(), "contact")

        if (email != null) {
            val isExisting = contactRepository.isExisting(email)

            if (isExisting) {
                bindingResult.addError(FieldError("contact", "email", "Email already exists."))
            }
        }

        return ModelAndView("fragments/errors :: contact_email_error", bindingResult.model)
    }

    @GetMapping("/new")
    fun contacts_new(model: ModelMap): ModelAndView {
        model.addAttribute("contact", NewContactForm())
        return ModelAndView("new", model)
    }

    @PostMapping("/new")
    fun handleNewContact(
        @Valid @ModelAttribute("contact") contact: NewContactForm,
        bindingResult: BindingResult,
        model: ModelMap,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (bindingResult.hasErrors()) {
            return "new"
        }

        contactRepository.save(
            ContactUnsaved(
                name = contact.name,
                email = contact.email,
                phone = contact.phone
            )
        )

        redirectAttributes.addFlashAttribute("message", "Contact created")

        return "redirect:/contacts"
    }

    @GetMapping("/{id}/edit")
    fun editContact(
        @PathVariable id: String,
        model: ModelMap,
    ): ModelAndView {
        val contact = contactRepository.findById(UUID.fromString(id))
        model.addAttribute("contact", contact)
        return ModelAndView("edit", model)
    }

    @PostMapping("/{id}/edit")
    fun handleEditContact(
        @PathVariable id: String,
        @Valid @ModelAttribute("contact") editContact: EditContactForm,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {

        if (bindingResult.hasErrors()) {
            return "edit"
        }

        contactRepository.save(
            Contact(
                id = UUID.fromString(id),
                name = editContact.name,
                email = editContact.email,
                phone = editContact.phone
            )
        )

        redirectAttributes.addFlashAttribute("message", "Contact saved")

        return "redirect:/contacts/${id}"
    }

    @DeleteMapping("/{id}")
    fun deleteContact(
        @PathVariable id: String,
        model: ModelMap,
        redirectAttributes: RedirectAttributes,
        response: HttpServletResponse
    ) {
        contactRepository.delete(UUID.fromString(id))
        redirectAttributes.addFlashAttribute("message", "Contact deleted")
        response.status = SC_SEE_OTHER
        response.setHeader("Location", "/contacts")
    }
}

open class ContactForm {

    @NotEmpty(message = "Contact's name cannot be empty.")
    @Size(min = 3, max = 250, message = "Contact's name must be between 3 and 250 characters.")
    var name: String = ""

    @NotEmpty(message = "Contact's phone cannot be empty.")
    var phone: String = ""
}

open class NewContactForm: ContactForm() {

    @NotEmpty(message = "Contact's email cannot be empty.")
    @UniqueEmail
    var email: String = ""
}

open class EditContactForm : ContactForm() {
    @NotEmpty(message = "Contact's id cannot be empty.")
    var id: String = ""


    @NotEmpty(message = "Contact's email cannot be empty.")
    var email: String = ""
}

@Constraint(validatedBy = [UniqueEmailValidator::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class UniqueEmail(
    val message: String = "Email already exists.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueEmailValidator(private val repository: ContactRepository) : ConstraintValidator<UniqueEmail, String> {

    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        if (email == null) {
            return false
        }
        return repository.isExisting(email).not()
    }
}