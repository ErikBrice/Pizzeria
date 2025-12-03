package com.pizzeria.service;

import com.pizzeria.model.Usuario;
import com.pizzeria.model.Rol;
import com.pizzeria.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        List<GrantedAuthority> authorities = new ArrayList<>();
        Rol rol = usuario.getRol();
        if (rol != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol()));
        }

        return new User(usuario.getCorreo(),
                usuario.getPwdUsuario(),
                true,
                true,
                true,
                true,
                authorities);
    }
} 