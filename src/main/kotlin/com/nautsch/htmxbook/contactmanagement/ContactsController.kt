package com.nautsch.htmxbook.contactmanagement

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

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
    @GetMapping("/new")
    fun contacts_new(model: ModelMap): ModelAndView {
        model.addAttribute("newContact", NewContactForm())
        return ModelAndView("new", model)
    }

    @PostMapping("/new")
    fun handleNewContact(@ModelAttribute("newContact") newContact: NewContactForm, model: ModelMap): String {

        contactRepository.save(ContactUnsaved(
            name = newContact.name,
            email = newContact.email,
            phone = newContact.phone
        ))

        return "redirect:/contacts"
    }
}

class NewContactForm {
    var name: String = ""
    var email: String = ""
    var phone: String = ""
}