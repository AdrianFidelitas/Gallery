package com.gallery_m.service;

import com.gallery_m.domain.Zapato;
import com.gallery_m.repository.ZapatoRepository;
import java.time.LocalDateTime;
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
    
    
    //Metodo get zapatos
    @Transactional(readOnly = true)
    public List<Zapato> getZapatos(boolean activo) {
        if (activo) {
            return zapatoRepository.findByActivoTrue();
        }
        return zapatoRepository.findAll();
    }
    
    //Metodo para obtener zapatos activos     
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosActivos() {
        return zapatoRepository.findByActivoTrue();
    }
    
    //Metodo para obtener zapatos por categoria
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosPorCategoria(Integer idCategoria, boolean incluirInactivos) {
        if (incluirInactivos) {
            return zapatoRepository.findByCategoriaIdCategoria(idCategoria);
        } else {
            
            return zapatoRepository.findByCategoriaIdCategoriaAndActivoTrue(idCategoria);
        }
    }
    
    
    //Metodo obtener zapato
    @Transactional(readOnly = true)
    public Optional<Zapato> getZapato(Integer idZapato) {
        return zapatoRepository.findById(idZapato);
    }
    
    
    //Metodo para guardar
    @Transactional
    public void save(Zapato zapato, MultipartFile imagenFile) {
        try {
            // Establecer fechas si no existen
            if (zapato.getIdZapato() == null) {
                if (zapato.getFechaCreacion() == null) {
                    zapato.setFechaCreacion(LocalDateTime.now());
                }
            }
            zapato.setFechaModificacion(LocalDateTime.now());
            
            // Procesar imagen si existe
            if (imagenFile != null && !imagenFile.isEmpty()) {
                
            }
            
            zapatoRepository.save(zapato);
            
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el zapato: " + e.getMessage());
        }
    }
    
    
    //Metodo para guardar
    @Transactional
    public void save(Zapato zapato) {
        save(zapato, null);
    }
    
    
    //Metodo para eliminar
    @Transactional
    public void delete(Integer idZapato) {
        try {
            Optional<Zapato> zapatoOpt = zapatoRepository.findById(idZapato);
            
            if (zapatoOpt.isPresent()) {
                Zapato zapato = zapatoOpt.get();
                
                // Verificar existencias antes de desactivar
                if (zapato.getExistencias() > 0) {
                    throw new IllegalStateException(
                        "No se puede desactivar el zapato '" + zapato.getNombreZapato() + 
                        "' (Talla: " + zapato.getTalla() + ") porque tiene " + 
                        zapato.getExistencias() + " existencias."
                    );
                }
                
                zapato.setActivo(false);
                zapatoRepository.save(zapato);
                
            } else {
                throw new IllegalArgumentException("No se encontró el zapato con ID: " + idZapato);
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el zapato: " + e.getMessage());
        }
    }
    
    
    //Metodo para contar o obtener la cantidad de zapatos activos
    @Transactional(readOnly = true)
    public long countZapatosActivos() {
        return zapatoRepository.countByActivoTrue();
    }
    
    //Metodo para devolver si el zapato existe
    @Transactional(readOnly = true)
    public boolean existsByNombreZapato(String nombreZapato) {
        return zapatoRepository.existsByNombreZapato(nombreZapato);
    }
    
    
    
    //Metodo para actualizar existencais 
    @Transactional
    public Zapato actualizarExistencias(Integer idZapato, Integer nuevasExistencias) {
        //No deja eliminar mas de 0
        if (nuevasExistencias < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas");
        }
        
        Optional<Zapato> zapatoOpt = getZapato(idZapato);
        
        //No elimina zapatos que no tenga o encuente el ID
        if (zapatoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el zapato con ID: " + idZapato);
        }
        
        //Si no que deje actualizar las existencias
        Zapato zapato = zapatoOpt.get();
        zapato.setExistencias(nuevasExistencias);
        zapato.setFechaModificacion(LocalDateTime.now());
        
        return zapatoRepository.save(zapato);
    }
    
    
    //Metodo para obtener la talla de los zapatos
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosPorTalla(String talla, boolean incluirInactivos) {
        if (incluirInactivos) {
            return zapatoRepository.findByTalla(talla);
        } else {
            return zapatoRepository.findByTallaAndActivoTrue(talla);
        }
    }
    
    
    //Metodo para obetner los zapatos agotados 
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosAgotados() {
        return zapatoRepository.findAgotados();
    }
    
    
    //Metodo para obtener los zapatos con bajo inventairo
    @Transactional(readOnly = true)
    public List<Zapato> getZapatosConBajoInventario() {
        return zapatoRepository.findConBajoInventario();
    }
    
    //Metodo para validar/buscar que existe el zapato por talla
    @Transactional(readOnly = true)
    public boolean existsByNombreYTallla(String nombreZapato, String talla) {
        return zapatoRepository.existsByNombreZapatoAndTalla(nombreZapato, talla);
    }
}