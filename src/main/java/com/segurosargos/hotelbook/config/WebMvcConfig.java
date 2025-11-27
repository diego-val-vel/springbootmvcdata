package com.segurosargos.hotelbook.config;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configuración MVC adicional para soporte de i18n con cambio de idioma
 * mediante el parámetro de request "lang".
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * LocaleResolver basado en sesión, con español como idioma por defecto.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("es"));
        return localeResolver;
    }

    /**
     * Interceptor que permite cambiar el idioma usando el parámetro "lang".
     * Ejemplos:
     *  - /rooms?lang=es
     *  - /rooms?lang=en
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * Registro del interceptor de cambio de idioma en la cadena de filtros MVC.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
