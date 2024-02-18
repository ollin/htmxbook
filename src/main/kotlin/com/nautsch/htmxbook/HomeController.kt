package com.nautsch.htmxbook

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class HomeController {

    @GetMapping("/")
    fun home(modelMap: ModelMap): ModelAndView {
        return ModelAndView("redirect:/contacts", modelMap)
    }
}