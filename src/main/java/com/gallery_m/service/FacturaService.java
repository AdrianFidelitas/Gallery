package com.gallery_m.service;

import com.gallery_m.domain.*;
import com.gallery_m.repository.FacturaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {
   
    private final FacturaRepository facturaRepository;
    
    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    /**
     * Obtiene una factura con todas sus ventas y relaciones cargadas
     * Uso: Para mostrar el detalle completo de una factura
     */
    @Transactional(readOnly = true)
    public Factura getFacturaConVentas(Integer idFactura) {
        return facturaRepository.findByIdFacturaConDetalle(idFactura)
                .orElseThrow(() -> new NoSuchElementException(
                    "Factura con ID " + idFactura + " no encontrada."));
    }
    
    /**
     * Obtiene una factura simple sin cargar las relaciones
     * Uso: Para operaciones que no requieren las ventas
     */
    @Transactional(readOnly = true)
    public Factura getFactura(Integer idFactura) {
        return facturaRepository.findById(idFactura)
                .orElseThrow(() -> new NoSuchElementException(
                    "Factura con ID " + idFactura + " no encontrada."));
    }
    
    /**
     * Obtiene todas las facturas de un usuario
     */
    @Transactional(readOnly = true)
    public List<Factura> getFacturasPorUsuario(Integer idUsuario) {
        return facturaRepository.findByUsuarioIdUsuarioOrderByFechaDesc(idUsuario);
    }
    
    /**
     * Obtiene todas las facturas por estado
     */
    @Transactional(readOnly = true)
    public List<Factura> getFacturasPorEstado(EstadoFactura estado) {
        return facturaRepository.findByEstado(estado);
    }
    
    /**
     * Obtiene todas las facturas
     */
    @Transactional(readOnly = true)
    public List<Factura> getAllFacturas() {
        return facturaRepository.findAll();
    }
    
    /**
     * Guarda una factura
     */
    @Transactional
    public Factura save(Factura factura) {
        return facturaRepository.save(factura);
    }
    
    /**
     * Anula una factura
     */
    @Transactional
    public void anularFactura(Integer idFactura) {
        Factura factura = getFactura(idFactura);
        
        if (factura.getEstado() == EstadoFactura.Anulada) {
            throw new IllegalStateException("La factura ya está anulada.");
        }
        
        factura.setEstado(EstadoFactura.Anulada);
        facturaRepository.save(factura);
    }
    
    /**
     * Cuenta facturas por estado
     */
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoFactura estado) {
        return facturaRepository.countByEstado(estado);
    }
}