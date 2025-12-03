/**
 * La Pizzería script personalizado
 */

// Inicialización de componentes
document.addEventListener('DOMContentLoaded', function() {
    console.log("Inicializando scripts de La Pizzería...");
    
    // Para mensajes de alerta
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(function(alert) {
            try {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            } catch (e) {
                console.log("Error al cerrar alerta:", e);
            }
        });
    }, 5000);
    
    // Inicializar funcionalidad del carrito
    setupCartFunctionality();
    
    // Inicializar formulario de checkout
    setupCheckoutForm();
    
    // Inicializar funcionalidades adicionales
    setupTooltips();
    setupPopovers();
    setupAlertAutoDismiss();
    setupCancelacionOrdenes();
});

/**
 * Configurar el carrito de compras
 */
function setupCartFunctionality() {
    // Botones para añadir al carrito
    var addToCartButtons = document.querySelectorAll('.add-to-cart');
    addToCartButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            var productId = this.getAttribute('data-id');
            var isAuthenticated = this.getAttribute('data-auth') === 'true';
            
            if (!isAuthenticated) {
                window.location.href = '/login';
                return;
            }
            
            addToCart(productId, 1);
        });
    });
    
    // Actualizar contador del carrito
    updateCartCounter();
    
    // Botones de incremento y decremento de cantidad en el carrito
    var incrementButtons = document.querySelectorAll('.btn-increment');
    var decrementButtons = document.querySelectorAll('.btn-decrement');
    
    incrementButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var input = this.closest('.quantity-controls').querySelector('.quantity-input');
            var currentValue = parseInt(input.value);
            input.value = currentValue + 1;
            
            // Actualizar subtotal
            var itemId = this.closest('tr').getAttribute('data-item-id');
            updateCartItemQuantity(itemId, currentValue + 1);
        });
    });
    
    decrementButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            var input = this.closest('.quantity-controls').querySelector('.quantity-input');
            var currentValue = parseInt(input.value);
            if (currentValue > 1) {
                input.value = currentValue - 1;
                
                // Actualizar subtotal
                var itemId = this.closest('tr').getAttribute('data-item-id');
                updateCartItemQuantity(itemId, currentValue - 1);
            }
        });
    });
}

/**
 * Añadir un producto al carrito
 */
function addToCart(productId, quantity) {
    console.log("Añadiendo producto al carrito:", productId, quantity);
    
    fetch('/carrito/agregar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || ''
        },
        body: 'pizzaId=' + productId + '&cantidad=' + quantity
    })
    .then(response => {
        if (response.ok) {
            // Actualizar el contador del carrito sin recargar la página
            updateCartCounter();
            
            // Mostrar notificación de éxito
            showSuccessToast("¡Producto añadido al carrito!", "Puedes seguir comprando");
        } else {
            console.error('Error al añadir al carrito');
            showErrorToast("Error al añadir al carrito", "Por favor intenta de nuevo");
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showErrorToast("Error al comunicarse con el servidor", "Por favor intenta de nuevo");
    });
}

/**
 * Muestra un mensaje de éxito
 */
function showSuccessToast(title, message) {
    if (typeof bootstrap !== 'undefined' && bootstrap.Toast) {
        var toastEl = document.createElement('div');
        toastEl.className = 'toast bg-success text-white position-fixed top-0 end-0 m-3';
        toastEl.style.zIndex = '9999';
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');
        
        toastEl.innerHTML = `
            <div class="toast-header bg-success text-white">
                <strong class="me-auto">${title}</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        `;
        
        document.body.appendChild(toastEl);
        var toast = new bootstrap.Toast(toastEl, { autohide: true, delay: 3000 });
        toast.show();

        toastEl.addEventListener('hidden.bs.toast', function() {
            document.body.removeChild(toastEl);
        });
    } else {
        alert(title + ": " + message);
    }
}

/**
 * Muestra un mensaje de error
 */
function showErrorToast(title, message) {
    if (typeof bootstrap !== 'undefined' && bootstrap.Toast) {
        var toastEl = document.createElement('div');
        toastEl.className = 'toast bg-danger text-white position-fixed top-0 end-0 m-3';
        toastEl.style.zIndex = '9999';
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');
        
        toastEl.innerHTML = `
            <div class="toast-header bg-danger text-white">
                <strong class="me-auto">${title}</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        `;
        
        document.body.appendChild(toastEl);
        var toast = new bootstrap.Toast(toastEl, { autohide: true, delay: 5000 });
        toast.show();

        toastEl.addEventListener('hidden.bs.toast', function() {
            document.body.removeChild(toastEl);
        });
    } else {
        alert(title + ": " + message);
    }
}

/**
 * Actualizar cantidad de un ítem en el carrito
 */
function updateCartItemQuantity(itemId, quantity) {
    console.log("Actualizando cantidad en carrito:", itemId, quantity);
    
    fetch('/carrito/actualizar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || ''
        },
        body: 'itemId=' + itemId + '&cantidad=' + quantity
    })
    .then(response => {
        if (response.ok) {
            // Recargar la página para actualizar los totales
            window.location.reload();
        } else {
            console.error('Error al actualizar el carrito');
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

/**
 * Actualizar contador del carrito en la barra de navegación
 */
function updateCartCounter() {
    fetch('/carrito/contador')
    .then(response => response.text())
    .then(count => {
        var cartCounters = document.querySelectorAll('.badge');
        cartCounters.forEach(function(counter) {
            if (counter.closest('a[href*="carrito"]')) {
                counter.textContent = count;
                counter.style.display = parseInt(count) > 0 ? 'inline-block' : 'none';
            }
        });
    })
    .catch(error => {
        console.error('Error al actualizar contador del carrito:', error);
    });
}

/**
 * Configurar formulario de checkout
 */
function setupCheckoutForm() {
    let checkoutForm = document.getElementById('checkoutForm');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', function(event) {
            if (!checkoutForm.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            checkoutForm.classList.add('was-validated');
        });
    }
}

/**
 * Control del navbar en scroll
 */
let lastScrollTop = 0;
const navbar = document.querySelector('.main-navbar');
const mediaQuery = window.matchMedia('(max-width: 991.98px)');

window.addEventListener('scroll', () => {
    if (mediaQuery.matches) {
        let currentScroll = window.pageYOffset || document.documentElement.scrollTop;
        
        if (currentScroll > lastScrollTop) {
            // Scroll hacia abajo
            navbar.classList.add('navbar-scrolled');
        } else {
            // Scroll hacia arriba
            navbar.classList.remove('navbar-scrolled');
        }
        lastScrollTop = currentScroll <= 0 ? 0 : currentScroll;
    }
});

// Función para manejar la cancelación de órdenes
function setupCancelacionOrdenes() {
    const formsCancelacion = document.querySelectorAll('.cancelar-orden-form');
    
    formsCancelacion.forEach(form => {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            try {
                const formData = new FormData(form);
                const response = await fetch(form.action, {
                    method: 'POST',
                    headers: {
                        'X-CSRF-TOKEN': formData.get('_csrf')
                    },
                    body: formData
                });
                
                if (response.ok) {
                    // Cerrar el modal
                    const modal = form.closest('.modal');
                    const modalInstance = bootstrap.Modal.getInstance(modal);
                    modalInstance.hide();
                    
                    // Actualizar el estado en la tabla
                    const ordenId = form.action.split('/').slice(-2)[0];
                    const row = document.querySelector(`tr[data-orden-id="${ordenId}"]`);
                    if (row) {
                        const estadoCell = row.querySelector('td:nth-child(4)');
                        const accionesCell = row.querySelector('td:nth-child(5)');
                        
                        // Actualizar badge de estado
                        estadoCell.innerHTML = `
                            <span class="badge bg-danger">
                                <i class="fas fa-times me-1"></i> Cancelado
                            </span>
                        `;
                        
                        // Remover botón de cancelar
                        const btnCancelar = accionesCell.querySelector('.btn-outline-danger');
                        if (btnCancelar) {
                            btnCancelar.remove();
                        }
                    }
                    
                    // Mostrar mensaje de éxito
                    showSuccessToast('Orden cancelada', 'La orden ha sido cancelada exitosamente');
                    
                    // Recargar la página después de 2 segundos
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                } else {
                    throw new Error('Error al cancelar la orden');
                }
            } catch (error) {
                console.error('Error:', error);
                showErrorToast('Error', 'Error al cancelar la orden. Por favor, intenta nuevamente.');
            }
        });
    });
}

// Función para mostrar alertas
function showAlert(type, message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check' : 'exclamation'}-circle me-1"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    const container = document.querySelector('.container');
    container.insertBefore(alertDiv, container.firstChild);

    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// Función para configurar tooltips
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
})

// Inicializar todas las funcionalidades cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    setupTooltips();
    setupPopovers();
    setupAlertAutoDismiss();
    setupCartFunctionality();
    setupCheckoutForm();
    setupCancelacionOrdenes();
}); 