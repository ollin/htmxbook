package com.nautsch.htmxbook.contactmanagement

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class ContactsController(
    private val contactRepository: ContactRepository
) {

//    init {
//        contactRepository.save(Contact.createNew(
//            name = "John Doe",
//            email = "john.doe@mail.local",
//            phone = "+1234567890",
//        ))
//        contactRepository.save(Contact.createNew(
//            name = "Melly Blubber",
//            email = "melly.blubber@mail.local",
//            phone = "+1234567891",
//        ))
//        contactRepository.save(Contact.createNew(
//            name = "Dolly Fluff",
//            email = "dolly.fluff@mail.local",
//            phone = "+1234567892",
//        ))
//    }

    @GetMapping("/contacts")
    fun contacts(model: ModelMap): ModelAndView {
        model.addAttribute("contacts", contactRepository.fetchAll())
        return ModelAndView("index", model)
    }
}