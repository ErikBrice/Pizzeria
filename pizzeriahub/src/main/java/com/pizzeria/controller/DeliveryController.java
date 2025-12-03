package com.pizzeria.controller;

import com.pizzeria.model.Orden;
import com.pizzeria.model.Usuario;
import com.pizzeria.service.OrdenService;
import com.pizzeria.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    private final OrdenService ordenService;
    private final UsuarioService usuarioService;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public DeliveryController(OrdenService ordenService, UsuarioService usuarioService) {
        this.ordenService = ordenService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarOrdenesParaEntrega(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioActual = usuarioService.findByCorreo(auth.getName());

        List<String> estados = List.of("Preparado", "En entrega");
        List<Orden> ordenesParaEntrega;

        if (usuarioActual.getIdRol() == 5L) {
            // Motorizado ve solo sus pedidos en esos estados
            ordenesParaEntrega = ordenService.findByMotorizadoAndEstados(usuarioActual, estados);
        } else {
            // Admin Delivery ve todos los pedidos en esos estados
            ordenesParaEntrega = ordenService.findByEstados(estados);
            model.addAttribute("motorizados", usuarioService.listarMotorizados());
        }

        ordenesParaEntrega.sort(
                Comparator.comparing((Orden o) -> estados.indexOf(o.getEstado()))
                          .thenComparing(Orden::getNroPedido, Comparator.reverseOrder())
        );

        model.addAttribute("ordenes", ordenesParaEntrega);
        return "delivery/index";
    }

    @GetMapping("/{id}")
    public String verDetalleOrden(@PathVariable Long id, Model model) {
        Orden orden = ordenService.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        model.addAttribute("orden", orden);
        return "delivery/detalle";
    }

    @PostMapping("/actualizar-estado")
    public String actualizarEstado(@RequestParam Long idOrden, @RequestParam String estado, RedirectAttributes attributes) {
        try {
            ordenService.actualizarEstado(idOrden, estado);
            attributes.addFlashAttribute("success", "La orden se ha procesado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al procesar la orden: " + e.getMessage());
        }

        return "redirect:/delivery";
    }

    @PostMapping("/asignar-motorizado")
    public String asignarMotorizado(@RequestParam Long idOrden, @RequestParam Long idMotorizado, RedirectAttributes attributes) {
        try {
            ordenService.asignarMotorizado(idOrden, idMotorizado.equals(-1L) ? null : idMotorizado);
            attributes.addFlashAttribute("success", "Motorizado asignado correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al asignar motorizado: " + e.getMessage());
        }
        return "redirect:/delivery";
    }
}