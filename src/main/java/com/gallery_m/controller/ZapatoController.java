package com.gallery_m.controller;

import com.gallery_m.domain.Zapato;
import com.gallery_m.service.CategoriaService;
import com.gallery_m.service.MarcaService;
import com.gallery_m.service.ZapatoService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/zapato")
public class ZapatoController {

    @Autowired
    private ZapatoService zapatoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private MessageSource messageSource;

    //Listas por categoria
    @GetMapping("/categoria/{idCategoria}")
    public String listarPorCategoria(@PathVariable Integer idCategoria, Model model) {
        try {
            var zapatos = zapatoService.getZapatosPorCategoria(idCategoria, false);

            model.addAttribute("zapatos", zapatos);
            model.addAttribute("totalZapatos", zapatos.size());
            model.addAttribute("categoriaSeleccionada", idCategoria);
            model.addAttribute("categorias", categoriaService.getCategorias(false));
            model.addAttribute("marcas", marcaService.getMarcas());

            //Esto es para listar si esta agotado o bajo en inventarios
            long agotados = zapatos.stream().filter(z -> z.getExistencias() <= 0).count();
            long bajoInventario = zapatos.stream().filter(z -> z.getExistencias() > 0 && z.getExistencias() < 10).count();
            model.addAttribute("agotados", agotados);
            model.addAttribute("bajoInventario", bajoInventario);

            return "zapato/listado";

        } catch (Exception e) {
            System.err.println("ERROR en /zapato/categoria/" + idCategoria);
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar zapatos: " + e.getMessage());
            return "redirect:/zapato/listado";
        }
    }

    //Listado general de los zapatos
    @GetMapping("/listado")
    public String listado(Model model) {
        var zapatos = zapatoService.getZapatosActivos();

        // Esto es para estadisticas
        long totalZapatos = zapatoService.countZapatosActivos();

        List<Zapato> bajoInventario = zapatoService.getZapatosConBajoInventario();
        List<Zapato> agotados = zapatoService.getZapatosAgotados();

        model.addAttribute("zapatos", zapatos);
        model.addAttribute("totalZapatos", totalZapatos);
        model.addAttribute("agotados", agotados.size());
        model.addAttribute("bajoInventario", bajoInventario.size());
        model.addAttribute("categorias", categoriaService.getCategorias(false));
        model.addAttribute("marcas", marcaService.getMarcas());

        return "zapato/listado";
    }

    //Metodo para el detalle del zapato
    @GetMapping("/detalle/{idZapato}")
    public String detalleZapato(@PathVariable Integer idZapato, Model model) {
        try {
            Optional<Zapato> zapatoOpt = zapatoService.getZapato(idZapato);

            if (zapatoOpt.isEmpty()) {
                return "redirect:/zapato/listado";
            }
            Zapato zapato = zapatoOpt.get();

            model.addAttribute("zapato", zapato);

            return "zapato/detalle";

        } catch (Exception e) {
            System.err.println(" Error  en detalle: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/zapato/listado";
        }
    }

    //Metodo de prueba del detalle
    @GetMapping("/test-detalle/{id}")
    @ResponseBody
    public String testDetalle(@PathVariable Integer id) {
        try {

            Optional<Zapato> zapatoOpt = zapatoService.getZapato(id);

            if (zapatoOpt.isPresent()) {
                Zapato z = zapatoOpt.get();
                return "Zapato encontrado " + z.getNombreZapato();
            } else {
                return "Zapato no encontrado" + id;
            }
        } catch (Exception e) {
            return "Error aparte  " + e.getMessage();
        }
    }

    // Metodo guardar 
    @PostMapping("/guardar")
    public String guardar(@Valid Zapato zapato,
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam("categoria.idCategoria") Integer idCategoria,
            @RequestParam("marca.idMarca") Integer idMarca,
            RedirectAttributes redirectAttributes) {

        try {
            // Crear objetos Categoria y Marca con los IDs recibidos
            com.gallery_m.domain.Categoria categoria = new com.gallery_m.domain.Categoria();
            categoria.setIdCategoria(idCategoria);
            zapato.setCategoria(categoria);

            com.gallery_m.domain.Marca marca = new com.gallery_m.domain.Marca();
            marca.setIdMarca(idMarca);
            zapato.setMarca(marca);

            // Si es nuevo zapato (no tiene ID), establecer valores por defecto
            if (zapato.getIdZapato() == null) {
                zapato.setActivo(true);
                if (zapato.getExistencias() == null) {
                    zapato.setExistencias(0);
                }
            }

            zapatoService.save(zapato, imagenFile);
            redirectAttributes.addFlashAttribute("exito",
                    messageSource.getMessage("mensaje.guardado", null, Locale.getDefault()));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar: " + e.getMessage());
        }

        return "redirect:/zapato/listado";
    }

    //Metodo eliminar
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idZapato, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String detalle = "mensaje.eliminado";

        try {
            zapatoService.delete(idZapato);
        } catch (IllegalArgumentException e) {
            tipo = "error";
            detalle = "zapato.error01";
        } catch (IllegalStateException e) {
            tipo = "error";
            detalle = "zapato.error02";
        } catch (Exception e) {
            tipo = "error";
            detalle = "zapato.error03";
        }

        String mensaje = messageSource.getMessage(detalle, null, Locale.getDefault());
        redirectAttributes.addFlashAttribute("tipo", tipo);
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/zapato/listado";
    }

    //Metodo modificar por ID
    @GetMapping("/modificar/{idZapato}")
    public String modificar(@PathVariable Integer idZapato,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Zapato> zapatoOpt = zapatoService.getZapato(idZapato);
        if (zapatoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("zapato.error01", null, Locale.getDefault()));
            return "redirect:/zapato/listado";
        }
        model.addAttribute("zapato", zapatoOpt.get());
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        var marcas = marcaService.getMarcas();
        model.addAttribute("marcas", marcas);

        // List de tallas 
        model.addAttribute("tallasDisponibles",
                List.of("S", "M", "L", "XL"));

        return "zapato/modifica";
    }

    //Metodo para acualizar existencias
    @PostMapping("/actualizar-existencias")
    public String actualizarExistencias(@RequestParam Integer idZapato,
            @RequestParam Integer existencias,
            RedirectAttributes redirectAttributes) {
        try {
            zapatoService.actualizarExistencias(idZapato, existencias);
            redirectAttributes.addFlashAttribute("exito",
                    "Existencias actualizadas correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/zapato/detalle/" + idZapato;
    }

    //Metodo para mostra zapatos por talla
    @GetMapping("/talla/{talla}")
    public String listarPorTalla(@PathVariable String talla, Model model) {
        try {
            var zapatos = zapatoService.getZapatosPorTalla(talla, false);
            model.addAttribute("zapatos", zapatos);
            model.addAttribute("totalZapatos", zapatos.size());
            model.addAttribute("tallaSeleccionada", talla);
            model.addAttribute("categorias", categoriaService.getCategorias(false));
            model.addAttribute("marcas", marcaService.getMarcas());
            return "zapato/listado";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar zapatos: " + e.getMessage());
            return "redirect:/zapato/listado";
        }
    }
}
