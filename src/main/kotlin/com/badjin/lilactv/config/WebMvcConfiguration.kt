package com.badjin.lilactv.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    @Value("\${resource.external.root}")
    private val externalRoot: String? = null
    override fun addResourceHandlers (registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/BadJin/**").addResourceLocations(externalRoot!!).setCachePeriod(31536000)
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCachePeriod(20)
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/").setCachePeriod(20)
        registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:/static/vendor/").setCachePeriod(20)
    }
}

