package com.pizzeria.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "apellido")
    private String apellido;
    
    @Column(name = "correo")
    private String correo;
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "pwd_usuario")
    private String pwdUsuario;
    
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "activo")
    private Boolean activo = true; // Por default, los usuarios nuevos estarán activos

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Orden> ordenes;

    @OneToMany(mappedBy = "motorizado", fetch = FetchType.LAZY)
    private List<Orden> ordenesMotorizados;

    // Constructor vacío
    public Usuario() {}
    
    // Constructor con campos
    public Usuario(String nombre, String apellido, String correo, String telefono, String pwdUsuario, Long idRol, String direccion, Boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.telefono = telefono;
        this.pwdUsuario = pwdUsuario;
        this.idRol = idRol;
        this.direccion = direccion;
        this.activo = activo;
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPwdUsuario() {
        return pwdUsuario;
    }

    public void setPwdUsuario(String pwdUsuario) {
        this.pwdUsuario = pwdUsuario;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes;
    }

    public List<Orden> getOrdenesMotorizados() {
        return ordenesMotorizados;
    }

    public void setOrdenesMotorizados(List<Orden> ordenesMotorizados) {
        this.ordenesMotorizados = ordenesMotorizados;
    }

    // Método para obtener el nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // Método para obtener el rol como objeto Rol
    public Rol getRol() {
        if (this.idRol != null) {
            Rol rol = new Rol();
            rol.setIdRol(this.idRol);
            switch (this.idRol.intValue()) {
                case 1: rol.setNombreRol("ADMIN"); break;
                case 2: rol.setNombreRol("CLIENTE"); break;
                case 3: rol.setNombreRol("COCINA"); break;
                case 4: rol.setNombreRol("DELIVERY"); break;
                case 5: rol.setNombreRol("MOTORIZADO"); break;
            }
            return rol;
        }
        return null;
    }

    // Método para establecer el rol
    public void setRol(Rol rol) {
        if (rol != null) {
            this.idRol = rol.getIdRol();
        }
    }

    // Método toString
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", idRol=" + idRol +
                '}';
    }
} 