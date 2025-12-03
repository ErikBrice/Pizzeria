package com.pizzeria.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "pizzas")
public class Pizza implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pizza")
    private Long idPizza;

    @Column(name = "nombre_pizza", nullable = false, unique = true)
    private String nombrePizza;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "precio_und", nullable = false)
    private BigDecimal precioUnd;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "activo")
    private Boolean activo = true; // Por default, las pizzas nuevas estarán activas

    // Constructores
    public Pizza() {
    }

    public Pizza(String nombrePizza, Integer stock, String descripcion, BigDecimal precioUnd, String imagenUrl, Boolean activo) {
        this.nombrePizza = nombrePizza;
        this.stock = stock;
        this.descripcion = descripcion;
        this.precioUnd = precioUnd;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
    }

    // Getters y setters
    public Long getIdPizza() {
        return idPizza;
    }

    public void setIdPizza(Long idPizza) {
        this.idPizza = idPizza;
    }

    public String getNombrePizza() {
        return nombrePizza;
    }

    public void setNombrePizza(String nombrePizza) {
        this.nombrePizza = nombrePizza;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioUnd() {
        return precioUnd;
    }

    public void setPrecioUnd(BigDecimal precioUnd) {
        this.precioUnd = precioUnd;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "idPizza=" + idPizza +
                ", nombrePizza='" + nombrePizza + '\'' +
                ", stock=" + stock +
                ", descripcion='" + descripcion + '\'' +
                ", precioUnd=" + precioUnd +
                ", imagenUrl='" + imagenUrl + '\'' +
                '}';
    }

    // Métodos adicionales
    public boolean isDisponible() {
        return stock > 0;
    }
} 