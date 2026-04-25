package com.example.demo.controller;

import com.example.demo.model.Almacen;
import com.example.demo.model.CategoriaProducto;
import com.example.demo.repository.AlmacenesRepository;
import com.example.demo.repository.CategoriaProductoRepository;
import com.example.demo.service.TenancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
public class CatalogosController {

    private final CategoriaProductoRepository categoriaRepo;
    private final AlmacenesRepository almacenRepo;
    private final TenancyService tenancyService;

    // --- CATEGORÍAS ---
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaProducto>> listarCategorias(@RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        return ResponseEntity.ok(categoriaRepo.findByIdNegocio(nId));
    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaProducto> crearCategoria(@RequestBody CategoriaProducto cat, @RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        cat.setIdNegocio(nId); // Asignamos el dueño (Negocio)
        return ResponseEntity.ok(categoriaRepo.save(cat));
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaProducto> actualizarCategoria(@PathVariable Integer id, @RequestBody CategoriaProducto catDetails) {
        return categoriaRepo.findById(id).map(cat -> {
            cat.setNombre(catDetails.getNombre());
            cat.setDescripcion(catDetails.getDescripcion());
            return ResponseEntity.ok(categoriaRepo.save(cat));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        if (categoriaRepo.existsById(id)) {
            categoriaRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- ALMACENES ---

    @GetMapping("/almacenes")
    public ResponseEntity<List<Almacen>> listarAlmacenes(@RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        return ResponseEntity.ok(almacenRepo.findByIdNegocio(nId));
    }

    @PostMapping("/almacenes")
    public ResponseEntity<Almacen> crearAlmacen(@RequestBody Almacen alm, @RequestParam String userId) {
        String nId = tenancyService.resolveNegocioId(userId);
        alm.setIdNegocio(nId); // ASIGNAMOS EL NEGOCIO
        // Por defecto activo
        if (alm.getActivo() == null) alm.setActivo(true);
        return ResponseEntity.ok(almacenRepo.save(alm));
    }

    @PutMapping("/almacenes/{id}")
    public ResponseEntity<Almacen> actualizarAlmacen(@PathVariable Integer id, @RequestBody Almacen almDetails) {
        return almacenRepo.findById(id).map(alm -> {
            alm.setNombre(almDetails.getNombre());
            alm.setDireccion(almDetails.getDireccion());
            alm.setLatitud(almDetails.getLatitud());
            alm.setLongitud(almDetails.getLongitud());
            return ResponseEntity.ok(almacenRepo.save(alm));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/almacenes/{id}")
    public ResponseEntity<Void> eliminarAlmacen(@PathVariable Integer id) {
        if (almacenRepo.existsById(id)) {
            almacenRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}