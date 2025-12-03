package com.pizzeria.controller;

import com.pizzeria.model.Usuario;
import com.pizzeria.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    private static final Logger logger = LoggerFactory.getLogger(RegistroController.class);
    
    private final UsuarioService usuarioService;
    
    // Se eliminó la anotación @Autowired porque es innecesaria
    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        logger.info("Accediendo a la página de registro");
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("title", "Registro | La Pizzería");
        return "registro";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("usuario") Usuario usuario,
                                     BindingResult result, 
                                     Model model, 
                                     RedirectAttributes attributes) {
        logger.info("Procesando solicitud de registro para: {}", usuario.getCorreo());
        
        // Verificar si ya existe un usuario con ese correo
        if (usuarioService.existsByCorreo(usuario.getCorreo())) {
            result.rejectValue("correo", "error.usuario", "Ya existe una cuenta con ese correo electrónico");
        }
        
        // Verificar errores de validación
        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de registro: {}", result.getAllErrors());
            model.addAttribute("title", "Registro | La Pizzería");
            return "registro";
        }
        
        try {
            // Registrar el usuario (este método encripta la contraseña y asigna el rol del usuario por default)
            usuarioService.registrarUsuario(usuario);
            logger.info("Usuario registrado exitosamente: {}", usuario.getCorreo());
            
            attributes.addFlashAttribute("success", "¡Registro exitoso! Ahora puede iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error al registrar el usuario", e);
            model.addAttribute("error", "Ocurrió un error al procesar su registro. Por favor, inténtelo de nuevo.");
            model.addAttribute("title", "Registro | La Pizzería");
            return "registro";
        }
    }
}