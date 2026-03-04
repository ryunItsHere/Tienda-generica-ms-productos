package com.tiendagenerica.ms_productos.controller;

import com.tiendagenerica.ms_productos.model.Producto;
import com.tiendagenerica.ms_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Listar todos los productos
    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // Buscar producto por código
    @GetMapping("/buscar/{codigo}")
    public ResponseEntity<Producto> buscar(@PathVariable Long codigo) {
        return ResponseEntity.ok(productoService.buscarPorCodigo(codigo));
    }

    // Guardar un producto individual
    @PostMapping("/guardar")
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    // Actualizar producto
    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long codigo,
            @RequestBody Producto producto) {
        return ResponseEntity.ok(
                productoService.actualizar(codigo, producto));
    }

    // Eliminar producto
    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<String> eliminar(@PathVariable Long codigo) {
        productoService.eliminar(codigo);
        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    // Carga masiva desde CSV
    @PostMapping("/cargar-csv")
    public ResponseEntity<String> cargarCSV(
            @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(
                productoService.cargarDesdeCSV(archivo));
    }
}
