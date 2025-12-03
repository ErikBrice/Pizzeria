package com.pizzeria.controller;

import com.pizzeria.model.*;
import com.pizzeria.service.UsuarioService;
import com.pizzeria.service.OrdenService;
import com.pizzeria.service.PizzaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final UsuarioService usuarioService;
    private final PizzaService pizzaService;
    private final OrdenService ordenService;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public CheckoutController(UsuarioService usuarioService, PizzaService pizzaService, OrdenService ordenService) {
        this.usuarioService = usuarioService;
        this.pizzaService = pizzaService;
        this.ordenService = ordenService;
    }

    @GetMapping
    public String checkout(Model model, HttpSession session) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");

        if (carrito == null || carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.findByCorreo(auth.getName());

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("costoEnvio", new BigDecimal("5.00"));
        model.addAttribute("usuario", usuario);

        return "checkout";
    }

    @PostMapping("/procesar")
    public String procesarOrden(@RequestParam("metodoPago") String metodoPago,
                                  HttpSession session,
                                  RedirectAttributes attributes) {

        Carrito carrito = (Carrito) session.getAttribute("carrito");

        if (carrito == null || carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.findByCorreo(auth.getName());

        if (usuario == null) {
            return "redirect:/login";
        }

        // Convertir items del carrito a OrdenItem
        List<OrdenItem> ordenItems = new ArrayList<>();
        for (Carrito.ItemCarrito carritoItem : carrito.getItems()) {
            Pizza pizza = pizzaService.findById(carritoItem.getPizza().getIdPizza())
                    .orElseThrow(() -> new IllegalArgumentException("Pizza inválida: " + carritoItem.getPizza().getIdPizza()));

            OrdenItem ordenItem = new OrdenItem(pizza, carritoItem.getSubtotal().divide(BigDecimal.valueOf(carritoItem.getCantidad())), carritoItem.getCantidad());
            ordenItems.add(ordenItem);
        }

        // Crear la orden
        Orden orden = ordenService.crearOrden(usuario, ordenItems, metodoPago);

        // Limpiar el carrito
        carrito.vaciar();

        attributes.addFlashAttribute("success", "¡Compra realizada con éxito! Número de orden: " + orden.getNroPedido());
        return "redirect:/checkout/confirmacion/" + orden.getIdOrden();
    }

    @GetMapping("/confirmacion/{ordenId}")
    public String confirmacion(@PathVariable("ordenId") Long ordenId, Model model, RedirectAttributes attributes) {
        try {
            Orden orden = ordenService.findById(ordenId)
                    .orElseThrow(() -> new IllegalArgumentException("Orden inválida: " + ordenId));
            
            model.addAttribute("orden", orden);
            return "confirmacion";
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al cargar los detalles del pedido: " + e.getMessage());
            return "redirect:/pedidos";
        }
    }
}