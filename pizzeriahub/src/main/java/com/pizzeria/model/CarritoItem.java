package com.pizzeria.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CarritoItem implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long pizzaId;
    private String nombre;
    private BigDecimal precio;
    private Integer cantidad;
    private String imagenUrl;
    
    public CarritoItem() {
    }
    
    public CarritoItem(Pizza pizza, Integer cantidad) {
        this.pizzaId = pizza.getIdPizza();
        this.nombre = pizza.getNombrePizza();
        this.precio = pizza.getPrecioUnd();
        this.cantidad = cantidad;
        this.imagenUrl = pizza.getImagenUrl();
    }
    
    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }
    
    // Getters y Setters
    public Long getPizzaId() {
        return pizzaId;
    }
    
    public void setPizzaId(Long pizzaId) {
        this.pizzaId = pizzaId;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarritoItem that = (CarritoItem) o;
        return pizzaId.equals(that.pizzaId);
    }
    
    @Override
    public int hashCode() {
        return pizzaId.hashCode();
    }
} 