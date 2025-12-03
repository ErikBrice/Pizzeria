package com.pizzeria.service;

import com.pizzeria.model.Usuario;
import com.pizzeria.model.Orden;
import com.pizzeria.model.OrdenItem;
import com.pizzeria.repository.OrdenRepository;
import com.pizzeria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PizzaService pizzaService;

    // Se eliminó la anotación @Autowired porque es innecesaria
    public OrdenService(OrdenRepository ordenRepository,
                        UsuarioRepository usuarioRepository,
                        PizzaService pizzaService) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.pizzaService = pizzaService;
    }

    @Transactional(readOnly = true)
    public List<Orden> findAll() {
        return ordenRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Orden> findById(Long id) {
        return ordenRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Orden> findByUsuario(Usuario usuario) {
        return ordenRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Orden> findByEstado(String estado) {
        return ordenRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Orden> findByEstados(List<String> estados) { return ordenRepository.findByEstadoIn(estados); }

    @Transactional(readOnly = true)
    public List<Orden> findByUsuarioAndEstado(Usuario usuario, String estado) {
        return ordenRepository.findByUsuarioAndEstado(usuario, estado);
    }

    @Transactional(readOnly = true)
    public List<Orden> findByMotorizadoAndEstados(Usuario motorizado, List<String> estados) {
        return ordenRepository.findByMotorizadoAndEstadoIn(motorizado, estados);
    }

    @Transactional
    public Orden save(Orden orden) {
        if (orden.getIdOrden() == null) {
            orden.setFechaHora(LocalDateTime.now());
            orden.setEstado("Orden recibida");
            orden.setCostoEnvio(new BigDecimal("5.00"));
            
            // Generar número de pedido si no tiene uno
            if (orden.getNroPedido() == null || orden.getNroPedido().isEmpty()) {
                orden.setNroPedido(generarNumeroPedido());
            }
        }
        
        // Actualizar el stock de pizzas
        for (OrdenItem item : orden.getItems()) {
            pizzaService.actualizarStock(item.getPizza().getIdPizza(), item.getCantidad());
        }
        
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden crearOrden(Usuario usuario, List<OrdenItem> items, String metodoPago) {
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setMetodoPago(metodoPago);
        
        // Calcular el total
        BigDecimal total = BigDecimal.ZERO;
        for (OrdenItem item : items) {
            total = total.add(item.getSubtotal());
            orden.addItem(item);
        }
        orden.setTotalPago(total);
        
        // Generar número de pedido
        String numeroPedido = generarNumeroPedido();
        orden.setNroPedido(numeroPedido);
        
        return save(orden);
    }

    @Transactional
    public Orden actualizarEstado(Long ordenId, String nuevoEstado) {
        Optional<Orden> optionalOrden = ordenRepository.findById(ordenId);
        if (optionalOrden.isPresent()) {
            Orden orden = optionalOrden.get();
            orden.setEstado(nuevoEstado);
            orden.setFechaActualizacion(LocalDateTime.now());
            
            if ("Entregado".equals(nuevoEstado)) {
                orden.setFechaEntrega(LocalDateTime.now());
            }
            
            return ordenRepository.save(orden);
        }
        return null;
    }

    @Transactional
    public void delete(Long id) {
        ordenRepository.deleteById(id);
    }

    // Genera un número de pedido único con el formato PZC-00000001
    private String generarNumeroPedido() {
        // Obtener la última orden para extraer el último número de pedido
        List<Orden> ordenesExistentes = ordenRepository.findTop1ByNroPedidoIsNotNullOrderByNroPedidoDesc();
        
        int ultimoNumero = 0;
        if (!ordenesExistentes.isEmpty()) {
            Orden ultimaOrden = ordenesExistentes.get(0);
            String ultimoNroPedido = ultimaOrden.getNroPedido();
            
            if (ultimoNroPedido != null && ultimoNroPedido.startsWith("PZC-")) {
                try {
                    ultimoNumero = Integer.parseInt(ultimoNroPedido.substring(4));
                } catch (NumberFormatException e) {
                    ultimoNumero = 0;
                }
            }
        }

        int nuevoNumero = ultimoNumero + 1;
        return String.format("PZC-%08d", nuevoNumero);
    }

    @Transactional
    public void asignarMotorizado(Long idOrden, Long idMotorizado) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        if (idMotorizado != null) {
            Usuario motorizado = usuarioRepository.findById(idMotorizado)
                    .orElseThrow(() -> new RuntimeException("Motorizado no encontrado"));
            orden.setMotorizado(motorizado);
        } else {
            orden.setMotorizado(null);
        }

        ordenRepository.save(orden);
    }
}