package com.tiendagenerica.ms_productos.controller;

import com.tiendagenerica.ms_productos.dto.ProductoDTO;
import com.tiendagenerica.ms_productos.model.Producto;
import com.tiendagenerica.ms_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(
            productoService.listarProductos());       // ← antes: listarTodos()
    }

    @GetMapping("/buscar/{codigo}")
    public ResponseEntity<Producto> buscar(
            @PathVariable Long codigo) {
        return ResponseEntity.ok(
            productoService.buscarPorCodigo(codigo)   // ← igual, pero ahora
                .orElseThrow(() ->                    //   retorna Optional
                    new RuntimeException(
                        "Producto no encontrado: " 
                        + codigo)));
    }

    @PostMapping("/guardar")
    public ResponseEntity<Producto> guardar(
            @RequestBody ProductoDTO dto) {           // ← antes: Producto
        return ResponseEntity.ok(
            productoService.guardarProducto(dto));    // ← antes: guardar()
    }

    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long codigo,
            @RequestBody ProductoDTO dto) {           // ← antes: Producto
        return ResponseEntity.ok(
            productoService.actualizarProducto(       // ← antes: actualizar()
                codigo, dto));
    }

    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<String> eliminar(
            @PathVariable Long codigo) {
        productoService.eliminarProducto(codigo);     // ← antes: eliminar()
        return ResponseEntity.ok(
            "Producto eliminado correctamente");
    }

    @PostMapping("/cargar-csv")
    public ResponseEntity<String> cargarCSV(
            @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(
            productoService.cargarDesdeCSV(archivo)); // ← igual
    }

    // Verificar si hay stock suficiente
// → usado por MS-Ventas
    @GetMapping("/stock/{codigo}/{cantidad}")
    public ResponseEntity<Boolean> verificarStock(
            @PathVariable Long codigo,
            @PathVariable Integer cantidad) {
        return ResponseEntity.ok(
                productoService.hayStock(codigo, cantidad));
    }

    // Descontar stock → llamado por MS-Ventas
    @PutMapping("/descontar-stock/{codigo}/{cantidad}")
    public ResponseEntity<String> descontarStock(
            @PathVariable Long codigo,
            @PathVariable Integer cantidad) {
        productoService.descontarStock(codigo, cantidad);
        return ResponseEntity.ok(
                "Stock actualizado correctamente");
    }

}