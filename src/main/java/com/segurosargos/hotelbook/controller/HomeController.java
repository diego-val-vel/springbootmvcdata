package com.segurosargos.hotelbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controlador basico para la pagina de inicio y la pagina de inicio de sesion.
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "redirect:/rooms";
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login");
        return modelAndView;
    }
}
