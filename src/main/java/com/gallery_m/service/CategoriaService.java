package com.gallery_m.service;

import com.gallery_m.domain.Categoria;
import com.gallery_m.repository.CategoriaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//Anotacion que es un servicio
@Service
public class CategoriaService {

    //Metodo que ocupa el repositorio, crea el objeto repository
    @Autowired
    private CategoriaRepository categoriaRepository;

    //Este es un metodo para ver la tabla, registros, en una lista
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        //If si el estado es activo en esa columna
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }

        //Si no devuelve todos
        return categoriaRepository.findAll();
    }

    //Busca un registro por el idCategoria y lo retorna
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }
    @Autowired
    private FirebaseStorageService firebaseStorageService;

    //Actualiza o inserta un registro y sube una imagen a la nube
    public void save(Categoria categoria, MultipartFile imagenFile) {
        categoria = categoriaRepository.save(categoria);
        if (!imagenFile.isEmpty()) { //Hay Imagen
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "categoria",
                        categoria.getIdCategoria());
                categoria.setRutaImagen(rutaImagen);
                categoriaRepository.save(categoria);
            } catch (Exception e) {

            }
        }

    }

    //Elimina un registro....
    @Transactional
    public void delete(Integer idCategoria) {
        //Se verifica si existe la categoria
        if (!categoriaRepository.existsById(idCategoria)) {
            //Se lanza una excepcion
            throw new IllegalArgumentException("La categoria" + idCategoria + "No Existe");
        }
        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la categoria, por que tiene datos asociados");
        }
    }

    @Transactional(readOnly = true)
    public List<Categoria> buscarCategorias(String textoBusqueda, boolean activo) {
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            return getCategorias(activo);
        }
        return categoriaRepository.buscarPorNombreYActivo(textoBusqueda.trim(), activo);
    }
}
