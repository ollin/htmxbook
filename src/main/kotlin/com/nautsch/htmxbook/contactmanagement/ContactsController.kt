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
        model.addAttribute("newContact", ContactForm())
        return ModelAndView("new", model)
    }

    @PostMapping("/new")
    fun handleNewContact(
        @ModelAttribute("newContact") newContact: ContactForm,
        bindingResult: BindingResult,
        model: ModelMap,
        redirectAttributes: RedirectAttributes,
    ): String {

        contactRepository.save(
            ContactUnsaved(
                name = newContact.name,
                email = newContact.email,
                phone = newContact.phone
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
        @Valid @ModelAttribute("contact") editContact: EditContactForm,
        bindingResult: BindingResult,
        model: ModelMap,
        redirectAttributes: RedirectAttributes,
    ): String {

        contactRepository.save(
            Contact(
                id = UUID.fromString(editContact.id),
                name = editContact.name,
                email = editContact.email,
                phone = editContact.phone
            )
        )

        redirectAttributes.addFlashAttribute("message", "Contact saved")

        return "redirect:/contacts/${editContact.id}"
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
    @Size(min = 3, max = 250)
    var name: String = ""
    @NotEmpty(message = "Contact's email cannot be empty.")
    var email: String = ""
    @NotEmpty(message = "Contact's phone cannot be empty.")
    var phone: String = ""
}

class EditContactForm : ContactForm(){
    var id: String = ""
}