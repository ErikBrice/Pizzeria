package com.pizzeria.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Carrito implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<ItemCarrito> items;
    private BigDecimal total;
    
    public Carrito() {
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }
    
    public void agregarItem(Pizza pizza, int cantidad) {
        // Buscar si el producto ya existe en el carrito
        Optional<ItemCarrito> itemExistente = items.stream()
                .filter(item -> item.getPizza().getIdPizza().equals(pizza.getIdPizza()))
                .findFirst();
        
        if (itemExistente.isPresent()) {
            // Si existe, incrementa la cantidad
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            // Si no existe, crea nuevo item
            ItemCarrito nuevoItem = new ItemCarrito(pizza, cantidad);
            items.add(nuevoItem);
        }
        
        // Recalcular el total
        calcularTotal();
    }
    
    public void removerItem(Long idPizza) {
        items.removeIf(item -> item.getPizza().getIdPizza().equals(idPizza));
        calcularTotal();
    }
    
    public void actualizarCantidad(Long idPizza, int cantidad) {
        if (cantidad <= 0) {
            removerItem(idPizza);
            return;
        }
        
        items.stream()
                .filter(item -> item.getPizza().getIdPizza().equals(idPizza))
                .findFirst()
                .ifPresent(item -> {
                    item.setCantidad(cantidad);
                    calcularTotal();
                });
    }
    
    public void vaciar() {
        items.clear();
        total = BigDecimal.ZERO;
    }
    
    private void calcularTotal() {
        total = items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getItemCount() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }
    
    // Getters y setters
    public List<ItemCarrito> getItems() {
        return items;
    }
    
    public void setItems(List<ItemCarrito> items) {
        this.items = items;
        calcularTotal();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    @Override
    public String toString() {
        return "Carrito{" +
                "items=" + items +
                ", total=" + total +
                '}';
    }
    
    // Clase para un ítem del carrito
    public static class ItemCarrito implements Serializable {
        private static final long serialVersionUID = 1L;
        private Pizza pizza;
        private int cantidad;
        private BigDecimal subtotal;
        
        public ItemCarrito(Pizza pizza, int cantidad) {
            this.pizza = pizza;
            this.cantidad = cantidad;
            calcularSubtotal();
        }
        
        private void calcularSubtotal() {
            if (pizza != null && pizza.getPrecioUnd() != null) {
                subtotal = pizza.getPrecioUnd().multiply(BigDecimal.valueOf(cantidad));
            } else {
                subtotal = BigDecimal.ZERO;
            }
        }
        
        // Getters y setters
        public Pizza getPizza() {
            return pizza;
        }
        
        public void setPizza(Pizza pizza) {
            this.pizza = pizza;
            calcularSubtotal();
        }
        
        public int getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
            calcularSubtotal();
        }
        
        public BigDecimal getSubtotal() {
            return subtotal;
        }
        
        @Override
        public String toString() {
            return "ItemCarrito{" +
                    "pizza=" + (pizza != null ? pizza.getIdPizza() : "null") +
                    ", cantidad=" + cantidad +
                    ", subtotal=" + subtotal +
                    '}';
        }
    }
} 