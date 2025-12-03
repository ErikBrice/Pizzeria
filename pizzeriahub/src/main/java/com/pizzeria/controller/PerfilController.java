package com.pizzeria.controller;

import com.pizzeria.model.Usuario;
import com.pizzeria.model.Orden;
import com.pizzeria.service.UsuarioService;
import com.pizzeria.service.OrdenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final OrdenService ordenService;

    public PerfilController(UsuarioService usuarioService, OrdenService ordenService) {
        this.usuarioService = usuarioService;
        this.ordenService = ordenService;
    }

    @GetMapping
    public String verPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.findByCorreo(auth.getName());
        List<Orden> ordenes = ordenService.findByUsuario(usuario);
        usuario.setOrdenes(ordenes);
        model.addAttribute("usuario", usuario);
        return "perfil/index";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm, RedirectAttributes attributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioActual = usuarioService.findByCorreo(auth.getName());

            usuarioForm.setIdUsuario(usuarioActual.getIdUsuario());
            usuarioForm.setCorreo(usuarioActual.getCorreo());

            String error = usuarioService.save(usuarioForm);
            if (error != null) {
                attributes.addFlashAttribute("error", error);
                return "redirect:/perfil";
            }

            attributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                @RequestParam String passwordNuevo,
                                @RequestParam String passwordConfirmar,
                                RedirectAttributes attributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correo = auth.getName();

            if (!passwordNuevo.equals(passwordConfirmar)) {
                throw new RuntimeException("Las contraseñas nuevas no coinciden");
            }

            usuarioService.cambiarPassword(correo, passwordActual, passwordNuevo);

            attributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/eliminar-cuenta")
    public String eliminarCuenta(@RequestParam String passwordUsuario,
                                 RedirectAttributes attributes,
                                 HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correo = auth.getName();

            if (!usuarioService.validarPassword(correo, passwordUsuario)) {
                throw new RuntimeException("La contraseña no coincide.");
            }

            usuarioService.eliminarCuentaAnonimamente(correo);

            // Cierra la sesión completamente
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();

            attributes.addFlashAttribute("success", "Tu cuenta ha sido eliminada correctamente.");
            return "redirect:/login?logout";
        } catch (Exception e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/perfil";
        }
    }
} 