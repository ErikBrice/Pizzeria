package com.pizzeria.repository;

import com.pizzeria.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    
    Optional<Pizza> findByNombrePizza(String nombrePizza);
    
    List<Pizza> findByStockGreaterThan(Integer stock);

    List<Pizza> findByActivoTrue(); // Método para traer solo las pizzas activas

    @Query("SELECT p FROM Pizza p WHERE p.activo = true AND p.stock > 0")
    List<Pizza> findAllActivasConStock();

    boolean existsByNombrePizza(String nombrePizza);
}