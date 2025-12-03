package com.segurosargos.hotelbook.config;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configuracion MVC adicional para soporte de i18n con cambio de idioma
 * mediante el parametro de request "lang" y para exponer la configuracion
 * global de CORS aplicada a /api/**.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * LocaleResolver basado en sesion, con espanol como idioma por defecto.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("es"));
        return localeResolver;
    }

    /**
     * Interceptor que permite cambiar el idioma usando el parametro "lang".
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

    /**
     * Configuracion global de CORS para los endpoints REST de la API.
     * En el perfil de desarrollo se permite el origen http://localhost:3000
     * para las rutas /api/**, limitando metodos y cabeceras permitidas.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
    }
}
