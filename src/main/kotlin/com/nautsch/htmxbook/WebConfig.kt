package com.nautsch.htmxbook

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.pattern.PathPatternParser
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.view.ThymeleafViewResolver
import org.thymeleaf.templatemode.TemplateMode.HTML
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        configurer.setPatternParser(PathPatternParser())
    }

    @Bean
    fun templateResolver(): ClassLoaderTemplateResolver {
        val resolver = ClassLoaderTemplateResolver()
        resolver.prefix = "static/public/"
        resolver.suffix = ".html"
        resolver.templateMode = HTML
        resolver.isCacheable = false
        resolver.characterEncoding = "UTF-8"
        resolver.order = 0
        return resolver
    }

    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val engine = SpringTemplateEngine()
        engine.setTemplateResolver(templateResolver())
        engine.addDialect(LayoutDialect())
        return engine
    }

    @Bean
    fun viewResolver(): ViewResolver {
        val resolver = ThymeleafViewResolver()
        resolver.templateEngine = templateEngine()
        return resolver
    }
}
