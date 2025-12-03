package com.pizzeria.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
public class Orden implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Long idOrden;

    @Column(name = "nro_pedido", unique = true)
    private String nroPedido;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "total_pago", nullable = false)
    private BigDecimal totalPago;

    @Column(name = "costo_envio", nullable = false)
    private BigDecimal costoEnvio;

    @Column(name = "estado", nullable = false)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_motorizado", nullable = true)
    private Usuario motorizado;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdenItem> items = new ArrayList<>();

    // Constructores
    public Orden() {
        this.fechaHora = LocalDateTime.now();
        this.estado = "Orden recibida";
        this.costoEnvio = new BigDecimal("5.00");
    }

    public Orden(Usuario usuario, String metodoPago, BigDecimal totalPago) {
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
        this.metodoPago = metodoPago;
        this.totalPago = totalPago;
        this.estado = "Orden recibida";
        this.costoEnvio = new BigDecimal("5.00");
    }

    // Métodos para gestionar la relación con orden_items
    public void addItem(OrdenItem item) {
        items.add(item);
        item.setOrden(this);
    }

    public void removeItem(OrdenItem item) {
        items.remove(item);
        item.setOrden(null);
    }

    // Getters y setters
    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public Long getId() {
        return idOrden;
    }

    public void setId(Long id) {
        this.idOrden = id;
    }

    public String getNroPedido() {
        return nroPedido;
    }

    public void setNroPedido(String nroPedido) {
        this.nroPedido = nroPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        this.fechaActualizacion = LocalDateTime.now();
        
        if ("Entregado".equals(estado)) {
            this.fechaEntrega = LocalDateTime.now();
        }
    }

    public Usuario getMotorizado() {
        return motorizado;
    }

    public void setMotorizado(Usuario motorizado) {
        this.motorizado = motorizado;
    }

    public List<OrdenItem> getItems() {
        return items;
    }

    public void setItems(List<OrdenItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Orden{" +
                "idOrden=" + idOrden +
                ", nroPedido='" + nroPedido + '\'' +
                ", usuario=" + usuario +
                ", fechaHora=" + fechaHora +
                ", fechaActualizacion=" + fechaActualizacion +
                ", fechaEntrega=" + fechaEntrega +
                ", metodoPago='" + metodoPago + '\'' +
                ", totalPago=" + totalPago +
                ", estado='" + estado + '\'' +
                '}';
    }
}