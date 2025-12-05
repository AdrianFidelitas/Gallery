package com.gallery_m.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "facturas")
@NoArgsConstructor 
@AllArgsConstructor
public class Factura implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoFactura estado;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    // Relación One-to-Many con Venta
    @OneToMany(mappedBy = "factura", fetch = FetchType.LAZY, 
               cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Venta> ventas = new ArrayList<>();
    
    /**
     * Método auxiliar para agregar una venta a la factura
     * Mantiene la relación bidireccional sincronizada
     */
    public void agregarVenta(Venta venta) {
        ventas.add(venta);
        venta.setFactura(this);
    }
    
    /**
     * Método auxiliar para remover una venta
     */
    public void removerVenta(Venta venta) {
        ventas.remove(venta);
        venta.setFactura(null);
    }
    
    /**
     * Calcula el total sumando todos los subtotales de las ventas
     */
    public BigDecimal calcularTotal() {
        return ventas.stream()
            .map(Venta::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Hooks de JPA para gestionar fechas automáticamente
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaModificacion = LocalDateTime.now();
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }
}