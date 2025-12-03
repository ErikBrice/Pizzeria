package com.pizzeria.controller;

import com.pizzeria.model.Carrito;
import com.pizzeria.model.Pizza;
import com.pizzeria.service.PizzaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final PizzaService pizzaService;

    // Se eliminó la anotación @Autowired de esta sección
    public CarritoController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public String verCarrito(Model model, HttpSession session) {
        Carrito carrito = getCarritoFromSession(session);
        model.addAttribute("carrito", carrito);
        return "carrito";
    }
    
    @GetMapping("/contador")
    @ResponseBody
    public String obtenerContadorCarrito(HttpSession session) {
        Carrito carrito = getCarritoFromSession(session);
        return String.valueOf(carrito.getItemCount());
    }

    @PostMapping("/agregar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(
            @RequestParam("pizzaId") Long pizzaId,
            @RequestParam("cantidad") Integer cantidad,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (cantidad <= 0) {
                response.put("success", false);
                response.put("message", "La cantidad debe ser mayor a 0");
                return ResponseEntity.badRequest().body(response);
            }

            Pizza pizza = pizzaService.findById(pizzaId)
                    .orElseThrow(() -> new IllegalArgumentException("Pizza inválida: " + pizzaId));

            Carrito carrito = getCarritoFromSession(session);
            carrito.agregarItem(pizza, cantidad);
            session.setAttribute("carrito", carrito);

            response.put("success", true);
            response.put("message", "Producto agregado al carrito");
            response.put("itemCount", carrito.getItemCount());
            response.put("total", carrito.getTotal());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al agregar al carrito: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/actualizar")
    public String actualizarCarrito(@RequestParam("pizzaId") Long pizzaId,
                                      @RequestParam("cantidad") Integer cantidad,
                                      HttpSession session,
                                      RedirectAttributes attributes) {
        
        Carrito carrito = getCarritoFromSession(session);
        carrito.actualizarCantidad(pizzaId, cantidad);
        
        attributes.addFlashAttribute("success", "Carrito actualizado");
        return "redirect:/carrito";
    }

    @GetMapping("/eliminar/{pizzaId}")
    public String eliminarDelCarrito(@PathVariable("pizzaId") Long pizzaId,
                                       HttpSession session,
                                       RedirectAttributes attributes) {
        
        Carrito carrito = getCarritoFromSession(session);
        carrito.removerItem(pizzaId);
        
        attributes.addFlashAttribute("success", "Producto eliminado del carrito");
        return "redirect:/carrito";
    }

    @GetMapping("/limpiar")
    public String limpiarCarrito(HttpSession session, RedirectAttributes attributes) {
        Carrito carrito = getCarritoFromSession(session);
        carrito.vaciar();
        
        attributes.addFlashAttribute("success", "Carrito vacío");
        return "redirect:/carrito";
    }

    private Carrito getCarritoFromSession(HttpSession session) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new Carrito();
            session.setAttribute("carrito", carrito);
        }
        return carrito;
    }
}