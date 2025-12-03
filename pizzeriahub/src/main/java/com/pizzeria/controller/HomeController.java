package com.pizzeria.controller;

import com.pizzeria.model.Carrito;
import com.pizzeria.service.PizzaService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final PizzaService pizzaService;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public HomeController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping({"", "/", "/index"})
    public String home(Model model, HttpSession session) {
        logger.info("Accediendo a la página principal");
        
        try {
            // Asegura que el carrito existe en la sesión
            if (session.getAttribute("carrito") == null) {
                session.setAttribute("carrito", new Carrito());
                logger.info("Creando nuevo carrito en la sesión");
            }

            // Solo se cargan los productos activos con stock
            model.addAttribute("pizzas", pizzaService.findAllActivasConStock());
            
            // Agregar título de la página
            model.addAttribute("title", "La Pizzería | La mejor pizza a domicilio");
            
            return "index";
        } catch (Exception e) {
            logger.error("Error al cargar la página de inicio: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la página de inicio: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/menu")
    public String menu(Model model) {
        logger.info("Accediendo a la página de menú");
        try {
            model.addAttribute("pizzas", pizzaService.findAllActivasConStock());
            model.addAttribute("title", "Menú | La Pizzería");
            return "menu";
        } catch (Exception e) {
            logger.error("Error al cargar el menú: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar el menú: " + e.getMessage());
            return "error";
        }
    }
}