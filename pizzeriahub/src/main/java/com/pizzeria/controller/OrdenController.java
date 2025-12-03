package com.pizzeria.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pizzeria.model.Usuario;
import com.pizzeria.model.Orden;
import com.pizzeria.service.UsuarioService;
import com.pizzeria.service.OrdenService;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/ordenes")
public class OrdenController {

    private final UsuarioService usuarioService;
    private final OrdenService ordenService;

    public OrdenController(UsuarioService usuarioService, OrdenService ordenService) {
        this.usuarioService = usuarioService;
        this.ordenService = ordenService;
    }

    @GetMapping
    public String listarOrdenes(Model model, Authentication authentication) {
        try {
            Usuario usuario = usuarioService.findByCorreo(authentication.getName());
            List<Orden> ordenes = ordenService.findByUsuario(usuario);
            ordenes.sort(Comparator.comparing(Orden::getNroPedido).reversed());
            model.addAttribute("ordenes", ordenes);
            return "ordenes/index";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al cargar las órdenes: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/{id}")
    public String verDetalleOrden(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            Usuario usuario = usuarioService.findByCorreo(authentication.getName());
            Orden orden = ordenService.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            if (!orden.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new RuntimeException("No tienes permiso para ver esta orden");
            }

            model.addAttribute("orden", orden);
            return "ordenes/detalle";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/ordenes";
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarOrden(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.findByCorreo(authentication.getName());
            Orden orden = ordenService.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            if (!orden.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new RuntimeException("No tienes permiso para cancelar esta orden");
            }

            if (!"Orden recibida".equals(orden.getEstado())) {
                throw new RuntimeException("Solo se pueden cancelar órdenes nuevas");
            }

            ordenService.actualizarEstado(id, "Cancelado");
            redirectAttributes.addFlashAttribute("success", "La orden ha sido cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la orden: " + e.getMessage());
        }

        return "redirect:/ordenes";
    }
} 