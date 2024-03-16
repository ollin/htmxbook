package com.nautsch.htmxbook

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.FormContentFilter
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
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

    /**
     * enables the FormContentFilter to support form data in the request body, also for non-POST requests
     *
     * @see <a href="https://stackoverflow.com/a/74899675">x-www-form-urlencoded Array inconsistently populated in Spring REST call</a>
     */
    @Bean
    @ConditionalOnMissingBean(FormContentFilter::class)
    @ConditionalOnProperty(prefix = "spring.mvc.formcontent.filter", name = ["enabled"], matchIfMissing = true)
    fun formContentFilter(): OrderedFormContentFilter {
        return OrderedFormContentFilter()
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

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(false)
        registry
            .addResourceHandler("/static/public/images/**")
            .addResourceLocations("classpath:/static/public/images/")
            .resourceChain(true)
        registry
            .addResourceHandler("/static/public/css/**")
            .addResourceLocations("classpath:/static/public/css/")
            .resourceChain(true)
    }


}
