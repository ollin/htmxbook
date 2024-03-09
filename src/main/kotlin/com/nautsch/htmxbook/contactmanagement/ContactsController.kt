package com.nautsch.htmxbook.contactmanagement

import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_SEE_OTHER
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
@RequestMapping("/contacts")
class ContactsController(
    private val contactRepository: ContactRepository
) {
    @GetMapping("")
    fun contacts(model: ModelMap): ModelAndView {
        model.addAttribute("contacts", contactRepository.fetchAll())
        return ModelAndView("index", model)
    }

    @GetMapping("/{id}")
    fun getContact(@PathVariable id: String, model: ModelMap): ModelAndView {
        val contact = contactRepository.findById(UUID.fromString(id))
        model.addAttribute("contact", contact)
        return ModelAndView("show", model)
    }

    @GetMapping("/new")
    fun contacts_new(model: ModelMap): ModelAndView {
        model.addAttribute("contact", ContactForm())
        return ModelAndView("new", model)
    }

    @PostMapping("/new")
    fun handleNewContact(
        @Valid @ModelAttribute("contact") contact: ContactForm,
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
        @Valid @ModelAttribute("contact") editContact: ContactForm,
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
    var id: String? = null
    @NotEmpty(message = "Contact's name cannot be empty.")
    @Size(min = 3, max = 250, message = "Contact's name must be between 3 and 250 characters.")
    var name: String = ""
    @NotEmpty(message = "Contact's email cannot be empty.")
    var email: String = ""
    @NotEmpty(message = "Contact's phone cannot be empty.")
    var phone: String = ""
}