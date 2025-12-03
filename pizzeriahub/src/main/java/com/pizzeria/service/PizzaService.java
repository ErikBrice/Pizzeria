package com.pizzeria.service;

import com.pizzeria.model.Pizza;
import com.pizzeria.repository.PizzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAll() {
        return pizzaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAvailable() {
        return pizzaRepository.findByStockGreaterThan(0);
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAllActivas() { return pizzaRepository.findByActivoTrue(); }

    @Transactional(readOnly = true)
    public List<Pizza> findAllActivasConStock() { return pizzaRepository.findAllActivasConStock(); }

    @Transactional(readOnly = true)
    public Optional<Pizza> findById(Long id) {
        return pizzaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Pizza> findByNombre(String nombrePizza) {
        return pizzaRepository.findByNombrePizza(nombrePizza);
    }

    @Transactional
    public Pizza save(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    @Transactional
    public void actualizarStock(Long pizzaId, Integer cantidad) {
        Optional<Pizza> optionalPizza = pizzaRepository.findById(pizzaId);
        if (optionalPizza.isPresent()) {
            Pizza pizza = optionalPizza.get();
            Integer nuevoStock = pizza.getStock() - cantidad;
            if (nuevoStock < 0) {
                nuevoStock = 0;
            }
            pizza.setStock(nuevoStock);
            pizzaRepository.save(pizza);
        }
    }

    @Transactional
    public void delete(Long id) {
        pizzaRepository.deleteById(id);
    }



    @Transactional
    public void toggleActivo(Long id) {
        Pizza pizza = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pizza no encontrada: " + id));
        pizza.setActivo(!pizza.getActivo()); // cambiar true a false o viceversa
        save(pizza);
    }
}