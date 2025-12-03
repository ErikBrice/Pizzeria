package com.pizzeria.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "orden_items")
public class OrdenItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long idItem;

    @ManyToOne
    @JoinColumn(name = "id_orden", nullable = false)
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "id_pizza", nullable = false)
    private Pizza pizza;

    @Column(name = "precio_und", nullable = false)
    private BigDecimal precioUnd;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    // Constructores
    public OrdenItem() {
    }

    public OrdenItem(Pizza pizza, BigDecimal precioUnd, Integer cantidad) {
        this.pizza = pizza;
        this.precioUnd = precioUnd;
        this.cantidad = cantidad;
    }

    // Métodos de negocio
    public BigDecimal getSubtotal() {
        return precioUnd.multiply(new BigDecimal(cantidad));
    }

    // Getters y setters
    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public BigDecimal getPrecioUnd() {
        return precioUnd;
    }

    public void setPrecioUnd(BigDecimal precioUnd) {
        this.precioUnd = precioUnd;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "OrdenItem{" +
                "idItem=" + idItem +
                ", pizza=" + pizza +
                ", precioUnd=" + precioUnd +
                ", cantidad=" + cantidad +
                '}';
    }
} 