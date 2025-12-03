package com.pizzeria.service;

import com.pizzeria.model.Usuario;
import com.pizzeria.model.Rol;
import com.pizzeria.repository.UsuarioRepository;
import com.pizzeria.repository.RolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          SessionRegistry sessionRegistry) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Usuario findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public List<Rol> getRolesDisponibles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarMotorizados() {
        return usuarioRepository.findByIdRol(5L);
    }

    @Transactional
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void registrarUsuario(Usuario usuario) {
        String nombreRol = (usuario.getRol() != null &&
                usuario.getRol().getNombreRol() != null &&
                !usuario.getRol().getNombreRol().isBlank())
                ? usuario.getRol().getNombreRol()
                : "ROLE_CLIENTE";

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol con nombre: " + nombreRol));

        usuario.setRol(rol);

        usuario.setPwdUsuario(passwordEncoder.encode(usuario.getPwdUsuario()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(String correo, String passwordActual, String passwordNuevo) {
        Usuario usuario = findByCorreo(correo);
        
        if (!passwordEncoder.matches(passwordActual, usuario.getPwdUsuario())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPwdUsuario(passwordEncoder.encode(passwordNuevo));
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public boolean validarPassword(String correo, String password) {
        Usuario usuario = findByCorreo(correo);
        return passwordEncoder.matches(password, usuario.getPwdUsuario());
    }

    @Transactional
    public void eliminarCuentaAnonimamente(String correo) {
        Usuario usuario = findByCorreo(correo);

        if (usuario != null) {
            // Guardamos el correo para buscarlo en la lista de sesiones activas
            String correoOriginal = usuario.getCorreo();

            // Cambiamos el correo a uno interno para que no pueda volver a iniciar sesión
            usuario.setCorreo("eliminado_" + usuario.getIdUsuario() + "_dummy@lapizzeria.com");

            // Anonimizamos los datos
            usuario.setNombre("Usuario_eliminado_" + usuario.getIdUsuario());
            usuario.setApellido(null);

            usuario.setTelefono(null);
            usuario.setPwdUsuario(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
            usuario.setIdRol(2L);
            usuario.setActivo(false);

            // Guardamos el usuario con los datos anonimizados
            usuarioRepository.save(usuario);

            // Expiramos todas las sesiones activas del usuario
            for (Object principal : sessionRegistry.getAllPrincipals()) {
                if (principal instanceof UserDetails userDetails &&
                    userDetails.getUsername().equals(correoOriginal)) {
                    for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                        session.expireNow();
                    }
                    break;
                }
            }
        } else {
            throw new RuntimeException("No se encontró al usuario para eliminar.");
        }
    }

    @Transactional
    public String resetearPassword(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoPassword = generarPasswordAleatorio();
        usuario.setPwdUsuario(passwordEncoder.encode(nuevoPassword));

        usuarioRepository.save(usuario);
        return nuevoPassword;
    }

    private String generarPasswordAleatorio() {
        int longitud = 8;
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789#&%@!$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return sb.toString();
    }

    @Transactional
    public String save(Usuario usuarioForm) {
        if (usuarioForm.getIdUsuario() == null) {
            return guardarNuevoUsuario(usuarioForm);
        } else {
            return actualizarUsuarioExistente(usuarioForm);
        }
    }

    private String guardarNuevoUsuario(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getIdRol() == null) {
            return "Debe seleccionar un rol para el usuario.";
        }

        if (usuario.getPwdUsuario() == null || usuario.getPwdUsuario().trim().isEmpty()) {
            return "La contraseña es obligatoria para nuevos usuarios.";
        }

        if (existsByCorreo(usuario.getCorreo())) {
            return "Ya existe una cuenta con ese correo electrónico.";
        }

        usuario.setPwdUsuario(passwordEncoder.encode(usuario.getPwdUsuario()));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return null; // Retorna null si no hay errores
    }

    private String actualizarUsuarioExistente(Usuario usuarioForm) {
        Usuario usuarioExistente = findById(usuarioForm.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuarioExistente.getCorreo().equals(usuarioForm.getCorreo().trim()) &&
              existsByCorreo(usuarioForm.getCorreo().trim())) {
            return "Ya existe una cuenta con ese correo electrónico.";
        }

        copiarCamposEditados(usuarioForm, usuarioExistente);
        usuarioRepository.save(usuarioExistente);
        return null; // Retorna null si no hay errores
    }

    private void copiarCamposEditados(Usuario usuarioForm, Usuario usuarioExistente) {
        usuarioExistente.setNombre(usuarioForm.getNombre());
        usuarioExistente.setApellido(usuarioForm.getApellido());
        usuarioExistente.setTelefono(usuarioForm.getTelefono());
        usuarioExistente.setDireccion(usuarioForm.getDireccion());

        // Solo en contexto admin
        if (!usuarioExistente.getCorreo().equals(usuarioForm.getCorreo().trim())) {
            usuarioExistente.setCorreo(usuarioForm.getCorreo().trim());
        }

        if (usuarioForm.getRol() != null && usuarioForm.getRol().getIdRol() != null) {
            usuarioExistente.setRol(usuarioForm.getRol());
        }
    }
}