package com.gallery_m.controller;

import com.gallery_m.domain.Categoria;
import com.gallery_m.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

/*Esto dice, todos las rutas con /categoria la va a manejar esta clase*/
@RequestMapping("/categoria")
public class CategoriaController {

    /*Para enlazar categoria service con categoria controller*/
    @Autowired
    private CategoriaService categoriaService;

    /*Aca defini el metodo que va a atender la ruta categoria/listado*/
    @GetMapping("/listado")
    public String listado(
            @RequestParam(value = "busqueda", required = false) String busqueda,
            Model model) {

        List<Categoria> categorias;

        // Si hay texto de búsqueda, buscar; si no, mostrar todas
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            categorias = categoriaService.buscarCategorias(busqueda, false);
            model.addAttribute("busqueda", busqueda);
        } else {
            categorias = categoriaService.getCategorias(false);
        }

        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());

        return "/categoria/listado";
    }
}
