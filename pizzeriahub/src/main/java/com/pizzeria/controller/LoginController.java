package com.pizzeria.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "session", required = false) String session,
                       Model model) {
        
        logger.info("Accediendo a la página de login");
        
        // Redireccionar al inicio si ya está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            logger.info("Usuario ya autenticado, redirigiendo a la página principal");
            return "redirect:/";
        }
        
        if (error != null) {
            logger.warn("Error en el inicio de sesión");
            model.addAttribute("error", "Usuario o contraseña incorrectos. Por favor intente nuevamente.");
        }
        
        if (logout != null) {
            logger.info("Usuario ha cerrado sesión");
            model.addAttribute("success", "Ha cerrado sesión exitosamente.");
        }

        if (session != null && session.equals("expired")) {
            logger.warn("Sesión expirada o inválida");
            model.addAttribute("warning", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
        }

        model.addAttribute("title", "Iniciar Sesión | La Pizzería");
        return "login";
    }
} 