package com.nautsch.htmxbook

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class CustomHeaderFilter : Filter {
    override fun doFilter(
        request: ServletRequest?,
        response: ServletResponse?,
        chain: FilterChain?
    ) {
        val httpResponse = response as HttpServletResponse
        httpResponse.setHeader("Vary", "HX-Request")
        chain?.doFilter(request, response)
    }
}