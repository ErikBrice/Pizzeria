package com.pizzeria.service;

// 1. Importaciones de JUnit 5 (El motor de las pruebas)
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

// 2. Importaciones de Mockito (Para simular la base de datos)
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

// 3. Tus Clases del Modelo
import com.pizzeria.model.Orden;
import com.pizzeria.model.OrdenItem;
import com.pizzeria.model.Pizza;
import com.pizzeria.model.Usuario;
import com.pizzeria.repository.OrdenRepository;
import com.pizzeria.repository.UsuarioRepository;

// Utilidades de Java
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Habilitamos Mockito para esta clase de prueba
@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    // --- DEFINICIÓN DE MOCKS (Actores Falsos) ---
    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PizzaService pizzaService;

    // --- INYECCIÓN DEL SERVICIO REAL ---
    @InjectMocks
    private OrdenService ordenService;


    // --- PRUEBA 1: El Camino Feliz (Crear Orden Exitosamente) ---
    @Test
    void testCrearOrden_CalculaTotal_Y_GeneraNumero() {
        // A. ARRANGE (Preparar el escenario)
        
        // 1. Crear un usuario ficticio
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L); // Usamos setIdUsuario (el correcto)

        // 2. Crear una pizza ficticia
        Pizza pizza = new Pizza();
        pizza.setIdPizza(100L); // Asumimos que tienes este ID

        // 3. Crear un item de orden (2 pizzas a 15.50 cada una)
        OrdenItem item = new OrdenItem();
        item.setPizza(pizza);
        item.setCantidad(2);
        item.setPrecioUnd(new BigDecimal("15.50")); 
        // Nota: Tu clase OrdenItem calculará getSubtotal() = 31.00 automáticamente

        // 4. Entrenar al Mock del Repositorio
        // "Cuando busques el último número de pedido, di que no hay ninguno (lista vacía)"
        when(ordenRepository.findTop1ByNroPedidoIsNotNullOrderByNroPedidoDesc())
            .thenReturn(Collections.emptyList());

        // "Cuando guardes, devuelve la misma orden que te entregué"
        when(ordenRepository.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        // B. ACT (Ejecutar la acción real)
        Orden ordenCreada = ordenService.crearOrden(usuario, List.of(item), "EFECTIVO");

        // C. ASSERT (Verificar resultados)
        
        // Verificamos que la orden se creó
        assertNotNull(ordenCreada, "La orden no debería ser nula");
        
        // Verificamos que generó el primer número de pedido correctamente
        assertEquals("PZC-00000001", ordenCreada.getNroPedido());
        
        // Verificamos el cálculo matemático del total (15.50 * 2 = 31.00)
        assertEquals(0, new BigDecimal("31.00").compareTo(ordenCreada.getTotalPago()), 
            "El total debería ser 31.00");
        
        // Verificamos que el estado inicial sea correcto
        assertEquals("Orden recibida", ordenCreada.getEstado());

        // Verificamos que se llamó al servicio de Pizza para descontar stock
        verify(pizzaService).actualizarStock(100L, 2);
    }


    // --- PRUEBA 2: Validar Lógica de Negocio (Consecutivo de Pedido) ---
    @Test
    void testGenerarNumeroPedido_DebeSeguirLaSecuencia() {
        // A. ARRANGE
        // Simulamos que la última orden en la BD fue la PZC-00000041
        Orden ordenAntigua = new Orden();
        ordenAntigua.setNroPedido("PZC-00000041");

        when(ordenRepository.findTop1ByNroPedidoIsNotNullOrderByNroPedidoDesc())
            .thenReturn(List.of(ordenAntigua));
        
        when(ordenRepository.save(any(Orden.class))).thenAnswer(i -> i.getArgument(0));

        // Creamos una nueva orden vacía para guardar
        Orden nuevaOrden = new Orden();
        nuevaOrden.setItems(Collections.emptyList()); // Sin items para simplificar

        // B. ACT
        Orden resultado = ordenService.save(nuevaOrden);

        // C. ASSERT
        // Debería ser 42 (41 + 1)
        assertEquals("PZC-00000042", resultado.getNroPedido());
    }


    // --- PRUEBA 3: Validar Manejo de Errores (Motorizado no existe) ---
    @Test
    void testAsignarMotorizado_LanzaExcepcion_SiNoExiste() {
        // A. ARRANGE
        Long idOrden = 1L;
        Long idMotorizadoInexistente = 999L;

        Orden orden = new Orden();
        
        // La orden sí existe...
        when(ordenRepository.findById(idOrden)).thenReturn(Optional.of(orden));
        // ...pero el motorizado NO existe (Empty)
        when(usuarioRepository.findById(idMotorizadoInexistente)).thenReturn(Optional.empty());

        // B. ACT & ASSERT
        // Esperamos que el código lance RuntimeException
        Exception excepcion = assertThrows(RuntimeException.class, () -> {
            ordenService.asignarMotorizado(idOrden, idMotorizadoInexistente);
        });

        assertEquals("Motorizado no encontrado", excepcion.getMessage());
    }
}