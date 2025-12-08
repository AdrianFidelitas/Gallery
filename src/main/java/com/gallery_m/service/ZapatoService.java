package com.gallery_m.service;

import com.gallery_m.domain.Zapato;
import com.gallery_m.repository.ZapatoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    //Metodo para guardar, diferente a clase como clase no esta funcioandon
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

            // Procesar la iamange
            if (imagenFile != null && !imagenFile.isEmpty()) {
                // Validar que sea una imagen
                String contentType = imagenFile.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    // Procesar y obtener ruta de la imagen
                    String rutaImagen = procesarYGuardarImagen(imagenFile);
                    zapato.setRutaImagen(rutaImagen);
                } else {
                    throw new IllegalArgumentException("El archivo debe ser una imagen (JPG, PNG, GIF)");
                }
            } // Si hay una imagne se pondra una imagen x por defecto 
            else if (zapato.getIdZapato() == null
                    && (zapato.getRutaImagen() == null || zapato.getRutaImagen().trim().isEmpty())) {
                zapato.setRutaImagen("https://via.placeholder.com/300x300?text=Sin+Imagen");
            }

            zapatoRepository.save(zapato);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el zapato: " + e.getMessage());
        }
    }

   //Este metodo es extra para poder procesar la imagen y guardarla  
    private String procesarYGuardarImagen(MultipartFile imagenFile) throws IOException {
        try {
            // 1. Crear nombre único para el archivo
            String nombreOriginal = imagenFile.getOriginalFilename();
            String extension = "";

            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            } else {
                extension = ".jpg"; // extensión por defecto
            }

            String nombreArchivo = "zapato_" + System.currentTimeMillis() + extension;

            // 2. Definir ruta donde guardar (en el proyecto)
            String rutaRelativa = "/uploads/zapatos/" + nombreArchivo;
            String rutaAbsoluta = "src/main/resources/static" + rutaRelativa;

            // 3. Crear directorio si no existe
            Path directorio = Paths.get("src/main/resources/static/uploads/zapatos/");
            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }

            // 4. Guardar archivo
            Path rutaCompleta = Paths.get(rutaAbsoluta);
            Files.write(rutaCompleta, imagenFile.getBytes());

            // 5. Retornar ruta relativa para guardar en BD
            return rutaRelativa;

        } catch (IOException e) {
            throw new IOException("Error al guardar la imagen: " + e.getMessage(), e);
        }
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
                            "No se puede desactivar el zapato '" + zapato.getNombreZapato()
                            + "' (Talla: " + zapato.getTalla() + ") porque tiene "
                            + zapato.getExistencias() + " existencias."
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
