package com.pizzeria.repository;

import com.pizzeria.model.Orden;
import com.pizzeria.model.OrdenItem;
import com.pizzeria.dto.PizzaMasPedidaDTO;
import com.pizzeria.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenItemRepository extends JpaRepository<OrdenItem, Long> {
    
    List<OrdenItem> findByOrden(Orden orden);
    
    List<OrdenItem> findByPizza(Pizza pizza);

    // CORRECCIÓN PARA SQL SERVER:
    // 1. Usamos "SELECT TOP 5" al inicio.
    // 2. Eliminamos "LIMIT 5" del final.
    // 3. Cambiamos "ORDER BY totalPedidos" por "ORDER BY SUM(oi.cantidad)" para mayor seguridad en SQL Server.
    @Query(value = "SELECT TOP 5 p.nombre_pizza AS nombrePizza, SUM(oi.cantidad) AS totalPedidos " +
            "FROM orden_items oi " +
            "JOIN pizzas p ON oi.id_pizza = p.id_pizza " +
            "GROUP BY p.nombre_pizza " +
            "ORDER BY SUM(oi.cantidad) DESC", nativeQuery = true)
    List<PizzaMasPedidaDTO> findTop5Pizzas();
}