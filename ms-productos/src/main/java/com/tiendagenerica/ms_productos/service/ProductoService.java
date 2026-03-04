package com.tiendagenerica.ms_productos.service;

import com.tiendagenerica.ms_productos.model.Producto;
import com.tiendagenerica.ms_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Value("${validar.proveedor.activo}")
    private boolean validarProveedor;

    // ─────────────────────────────────────────
    // CRUD básico
    // ─────────────────────────────────────────

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorCodigo(Long codigo) {
        return productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con código: " + codigo));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long codigo, Producto producto) {
        if (!productoRepository.existsById(codigo)) {
            throw new RuntimeException(
                    "Producto no encontrado con código: " + codigo);
        }
        producto.setCodigoProducto(codigo);
        return productoRepository.save(producto);
    }

    public void eliminar(Long codigo) {
        if (!productoRepository.existsById(codigo)) {
            throw new RuntimeException(
                    "Producto no encontrado con código: " + codigo);
        }
        productoRepository.deleteById(codigo);
    }

    // ─────────────────────────────────────────
    // Carga masiva por CSV
    // ─────────────────────────────────────────

    public String cargarDesdeCSV(MultipartFile archivo) {

        // Valida que el archivo no esté vacío
        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        // Valida que sea un archivo CSV
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || !nombreArchivo.endsWith(".csv")) {
            throw new RuntimeException("El archivo debe ser formato CSV");
        }

        List<Producto> productosGuardados = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int linea = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream()))) {

            String fila;
            while ((fila = reader.readLine()) != null) {
                linea++;

                // Salta la primera línea si es encabezado
                if (linea == 1 && fila.toLowerCase()
                        .contains("codigo")) {
                    continue;
                }

                // Separa los valores por coma
                String[] campos = fila.split(",");

                // Valida que tenga exactamente 6 columnas
                if (campos.length != 6) {
                    errores.add("Línea " + linea +
                            ": debe tener 6 columnas");
                    continue;
                }

                try {
                    Producto producto = new Producto();
                    producto.setCodigoProducto(
                            Long.parseLong(campos[0].trim()));
                    producto.setNombreProducto(campos[1].trim());
                    producto.setNitproveedor(
                            Long.parseLong(campos[2].trim()));
                    producto.setPrecioCompra(
                            Double.parseDouble(campos[3].trim()));
                    producto.setIvacompra(
                            Double.parseDouble(campos[4].trim()));
                    producto.setPrecioVenta(
                            Double.parseDouble(campos[5].trim()));

                    // Validación de proveedor
                    // (se activa cuando MS-Proveedores esté listo)
                    if (validarProveedor) {
                        // TODO: llamar a MS-Proveedores
                        // validarNitProveedor(producto.getNitproveedor());
                    }

                    productosGuardados.add(producto);

                } catch (NumberFormatException e) {
                    errores.add("Línea " + linea +
                            ": formato de número inválido");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al leer el archivo: " + e.getMessage());
        }

        // Guarda todos los productos válidos
        if (!productosGuardados.isEmpty()) {
            productoRepository.saveAll(productosGuardados);
        }

        // Construye el resumen
        return String.format(
                "Carga completada. Guardados: %d. Errores: %d. %s",
                productosGuardados.size(),
                errores.size(),
                errores.isEmpty() ? "" : "Errores: " + errores
        );
    }
}