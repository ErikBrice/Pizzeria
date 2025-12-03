package com.pizzeria.controller;

import com.pizzeria.model.Pizza;
import com.pizzeria.model.Usuario;
import com.pizzeria.service.PizzaService;
import com.pizzeria.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PizzaService pizzaService;
    private final UsuarioService usuarioService;

    // @Autowired es innecesario aquí y puede ser eliminado
    public AdminController(PizzaService pizzaService, UsuarioService usuarioService) {
        this.pizzaService = pizzaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String adminHome() {
        return "admin/index";
    }

    @GetMapping("/pizzas")
    public String listarPizzas(Model model) {
        model.addAttribute("pizzas", pizzaService.findAll());
        return "admin/pizzas/lista";
    }

    @GetMapping("/pizzas/nuevo")
    public String nuevaPizzaForm(Model model) {
        model.addAttribute("pizza", new Pizza());
        return "admin/pizzas/form";
    }

    @PostMapping("/pizzas/guardar")
    public String guardarPizza(@ModelAttribute("pizza") Pizza pizza,
                               BindingResult result,
                               RedirectAttributes attributes) {
        
        if (result.hasErrors()) {
            return "admin/pizzas/form";
        }
        
        pizzaService.save(pizza);
        attributes.addFlashAttribute("success", "Pizza guardada con éxito");
        
        return "redirect:/admin/pizzas";
    }

    @GetMapping("/pizzas/editar/{id}")
    public String editarPizzaForm(@PathVariable("id") Long id, Model model) {
        Pizza pizza = pizzaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pizza inválida: " + id));
        
        model.addAttribute("pizza", pizza);
        return "admin/pizzas/form";
    }

    @PostMapping("/pizzas/eliminar/{id}")
    public String eliminarPizza(@PathVariable("id") Long id, RedirectAttributes attributes) {
        pizzaService.delete(id);
        attributes.addFlashAttribute("success", "Pizza eliminada con éxito");
        return "redirect:/admin/pizzas";
    }

    @PostMapping("/pizzas/cambiar-estado/{id}")
    public String cambiarEstadoPizza(@PathVariable("id") Long id) {
        pizzaService.toggleActivo(id);
        return "redirect:/admin/pizzas";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "admin/usuarios/lista";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuarioForm(Model model) {
        Usuario usuario = new Usuario();
        usuario.setRol(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", usuarioService.getRolesDisponibles());
        return "admin/usuarios/form";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuarioForm,
                                 BindingResult result,
                                 RedirectAttributes attributes) {
        
        if (result.hasErrors()) {
            return "admin/usuarios/form";
        }
        
        String error = usuarioService.save(usuarioForm);
        if (error != null) {
            attributes.addFlashAttribute("error", error);
            return "admin/usuarios/form";
        }
        
        attributes.addFlashAttribute("success", "Usuario guardado con éxito");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuarioForm(@PathVariable("id") Long id, Model model, RedirectAttributes attributes) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inválido: " + id));

        if (usuario.getNombre().startsWith("Usuario_eliminado_")) {
            attributes.addFlashAttribute("error", "No se puede editar un usuario eliminado.");
            return "redirect:/admin/usuarios";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", usuarioService.getRolesDisponibles());
        return "admin/usuarios/form";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            Usuario usuario = usuarioService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario inválido: " + id));

            if (usuario.getNombre().startsWith("Usuario_eliminado_")) {
                attributes.addFlashAttribute("error", "Este usuario ya ha sido eliminado.");
                return "redirect:/admin/usuarios";
            }

            usuarioService.eliminarCuentaAnonimamente(usuario.getCorreo());
            attributes.addFlashAttribute("success", "Usuario eliminado con éxito");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/resetear-password/{id}")
    public String resetearPassword(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            Usuario usuario = usuarioService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario inválido: " + id));

            if (usuario.getNombre().startsWith("Usuario_eliminado_")) {
                attributes.addFlashAttribute("error", "No se puede resetear la contraseña de un usuario eliminado.");
                return "redirect:/admin/usuarios";
            }

            String nuevoPassword = usuarioService.resetearPassword(id);
            attributes.addFlashAttribute("success", "¡Contraseña reseteada! La nueva contraseña es: " + nuevoPassword);
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al resetear la contraseña: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}