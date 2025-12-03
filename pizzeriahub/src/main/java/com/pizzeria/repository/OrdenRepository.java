package com.pizzeria.repository;

import com.pizzeria.model.Usuario;
import com.pizzeria.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    List<Orden> findByUsuario(Usuario usuario);
    
    List<Orden> findByEstado(String estado);

    List<Orden> findByEstadoIn(List<String> estados);

    List<Orden> findByUsuarioAndEstado(Usuario usuario, String estado);

    List<Orden> findByMotorizadoAndEstadoIn(Usuario motorizado, List<String> estados);

    Optional<Orden> findByNroPedido(String nroPedido);
    
    // --- CORRECCIÓN AQUÍ ---
    // Hemos eliminado la anotación @Query completa.
    // Spring Data JPA leerá el nombre del método y creará la consulta
    // correcta para SQL Server (usando TOP 1) automáticamente.
    List<Orden> findTop1ByNroPedidoIsNotNullOrderByNroPedidoDesc();
}