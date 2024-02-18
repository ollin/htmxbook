package com.nautsch.htmxbook.contactmanagement

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class ContactsController {

    @GetMapping("/contacts")
    fun contacts(model: ModelMap): ModelAndView {
        model.addAttribute("contacts", emptyList<Contact>())
        return ModelAndView("index", model)
    }
}