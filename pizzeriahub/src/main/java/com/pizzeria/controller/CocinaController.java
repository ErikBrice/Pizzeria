package com.pizzeria.controller;

import com.pizzeria.model.Orden;
import com.pizzeria.repository.OrdenItemRepository;
import com.pizzeria.service.OrdenService;
import com.pizzeria.dto.PizzaMasPedidaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Comparator;

@Controller
@RequestMapping("/cocina")
public class CocinaController {

    private final OrdenService ordenService;
    private final OrdenItemRepository ordenItemRepository;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public CocinaController(OrdenService ordenService, OrdenItemRepository ordenItemRepository) {
        this.ordenService = ordenService;
        this.ordenItemRepository = ordenItemRepository;
    }

    @GetMapping
    public String listarOrdenesPendientes(Model model) {
        // Estados válidos
        List<String> estados = List.of("Orden recibida", "En preparación");

        // Obtener órdenes por esos estados
        List<Orden> ordenesPendientes = ordenService.findByEstados(estados);
        ordenesPendientes.sort(
                Comparator.comparing((Orden o) -> estados.indexOf(o.getEstado()))
                          .thenComparing(Orden::getNroPedido, Comparator.reverseOrder())
        );
        model.addAttribute("ordenes", ordenesPendientes);

        // Obtener Top 5 de pizzas más pedidas
        List<PizzaMasPedidaDTO> topPizzas = ordenItemRepository.findTop5Pizzas();
        model.addAttribute("topPizzas", topPizzas);

        return "cocina/index";
    }

    @GetMapping("/{id}")
    public String verDetalleOrden(@PathVariable Long id, Model model) {
        Orden orden = ordenService.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        model.addAttribute("orden", orden);
        return "cocina/detalle";
    }

    @PostMapping("/actualizar-estado")
    public String actualizarEstado(@RequestParam Long idOrden, @RequestParam String estado, RedirectAttributes attributes) {
        try {
            ordenService.actualizarEstado(idOrden, estado);
            attributes.addFlashAttribute("success", "La orden se ha procesado exitosamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al procesar la orden: " + e.getMessage());
        }

        return "redirect:/cocina";
    }
}