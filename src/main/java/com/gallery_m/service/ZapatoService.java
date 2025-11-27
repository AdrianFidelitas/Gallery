package com.gallery_m.service;

import com.gallery_m.domain.Zapato;
import com.gallery_m.repository.ZapatoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ZapatoService {
    
    @Autowired
    private ZapatoRepository zapatoRepository;
    
    /* Métodos de Consulta */
    
    @Transactional(readOnly = true)
    public List<Zapato> getZapatos(boolean activo) {
        if (activo) {
            return zapatoRepository.findByActivoTrue();
        }
        return zapatoRepository.findAll();
    }
    
    // ✅ NUEVO: Obtener zapatos activos
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosActivos() {
        return zapatoRepository.findByActivoTrue();
    }
    
    // ✅ NUEVO: Obtener zapatos por categoría
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosPorCategoria(Integer idCategoria, boolean incluirInactivos) {
        if (incluirInactivos) {
            return zapatoRepository.findByCategoriaIdCategoria(idCategoria);
        } else {
            return zapatoRepository.findByCategoriaIdCategoriaAndActivo(idCategoria, true);
        }
    }
    
    // ✅ NUEVO: Obtener un zapato por ID
    @Transactional(readOnly = true)
    public Optional<Zapato> getZapato(Integer idZapato) {
        return zapatoRepository.findById(idZapato);
    }
    
    /* Métodos de Modificación */
    
    @Transactional
    public void save(Zapato zapato, MultipartFile imagenFile) {
        try {
            // ✅ Aquí va la lógica para procesar la imagen si es necesario
            if (imagenFile != null && !imagenFile.isEmpty()) {
                // Lógica para guardar la imagen y actualizar rutaImagen
                // Por ahora, solo guardamos el zapato sin procesar imagen
            }
            
            zapatoRepository.save(zapato);
            
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el zapato: " + e.getMessage());
        }
    }
    
    // ✅ NUEVO: Método save sobrecargado sin imagen
    @Transactional
    public void save(Zapato zapato) {
        save(zapato, null);
    }
    
    @Transactional
    public void delete(Integer idZapato) {
        try {
            Optional<Zapato> zapatoOpt = zapatoRepository.findById(idZapato);
            
            if (zapatoOpt.isPresent()) {
                Zapato zapato = zapatoOpt.get();
                
                // ✅ Opción 1: Eliminación física (DELETE)
                // zapatoRepository.delete(zapato);
                
                // ✅ Opción 2: Eliminación lógica (UPDATE activo = false) - RECOMENDADO
                zapato.setActivo(false);
                zapatoRepository.save(zapato);
                
            } else {
                throw new IllegalArgumentException("No se encontró el zapato con ID: " + idZapato);
            }
            
        } catch (IllegalArgumentException e) {
            throw e; // Relanzamos excepciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el zapato: " + e.getMessage());
        }
    }
    
    // ✅ NUEVO: Método para contar zapatos activos
    @Transactional(readOnly = true)
    public long countZapatosActivos() {
        return zapatoRepository.countByActivoTrue();
    }
    
    // ✅ NUEVO: Método para verificar si existe un zapato por nombre
    @Transactional(readOnly = true)
    public boolean existsByNombreZapato(String nombreZapato) {
        return zapatoRepository.existsByNombreZapato(nombreZapato);
    }
}