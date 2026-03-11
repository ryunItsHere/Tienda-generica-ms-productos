package com.tiendagenerica.ms_productos.service;

import com.tiendagenerica.ms_productos.client.ProveedorClient;
import com.tiendagenerica.ms_productos.dto.ProductoDTO;
import com.tiendagenerica.ms_productos.model.Producto;
import com.tiendagenerica.ms_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorClient proveedorClient;

    @Value("${validar.proveedor.activo}")
    private boolean validarProveedor;

    // ─────────────────────────────────────────
    // Guardar producto
    // ─────────────────────────────────────────
    public Producto guardarProducto(ProductoDTO dto) {

        // Validación del NIT con MS-Proveedores
        if (validarProveedor) {
            boolean existe = proveedorClient
                .existeProveedor(dto.getNitproveedor());

            if (!existe) {
                throw new RuntimeException(
                    "El proveedor con NIT " 
                    + dto.getNitproveedor() 
                    + " no existe en el sistema");
            }
        }

        Producto producto = new Producto();
        producto.setNombreProducto(dto.getNombreProducto());
        producto.setNitproveedor(dto.getNitproveedor());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setIvacompra(dto.getIvacompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStock(dto.getStock() != null
                ? dto.getStock() : 0);
        return productoRepository.save(producto);
    }

    // ─────────────────────────────────────────
    // Listar todos
    // ─────────────────────────────────────────
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // ─────────────────────────────────────────
    // Buscar por código
    // ─────────────────────────────────────────
    public Optional<Producto> buscarPorCodigo(Long codigo) {
        return productoRepository.findById(codigo);
    }

    // ─────────────────────────────────────────
    // Actualizar producto
    // ─────────────────────────────────────────
    public Producto actualizarProducto(Long codigo, 
                                        ProductoDTO dto) {
        Producto producto = productoRepository.findById(codigo)
            .orElseThrow(() -> new RuntimeException(
                "Producto no encontrado con código: " + codigo));

        // Valida NIT si cambió
        if (validarProveedor && 
            !producto.getNitproveedor()
                     .equals(dto.getNitproveedor())) {

            boolean existe = proveedorClient
                .existeProveedor(dto.getNitproveedor());

            if (!existe) {
                throw new RuntimeException(
                    "El proveedor con NIT " 
                    + dto.getNitproveedor() 
                    + " no existe en el sistema");
            }
        }

        producto.setNombreProducto(dto.getNombreProducto());
        producto.setNitproveedor(dto.getNitproveedor());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setIvacompra(dto.getIvacompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setStock(dto.getStock() != null
                ? dto.getStock() : producto.getStock());

        return productoRepository.save(producto);
    }

    // ─────────────────────────────────────────
    // Eliminar producto
    // ─────────────────────────────────────────
    public void eliminarProducto(Long codigo) {
        Producto producto = productoRepository.findById(codigo)
            .orElseThrow(() -> new RuntimeException(
                "Producto no encontrado con código: " + codigo));
        productoRepository.delete(producto);
    }

    // ─────────────────────────────────────────
    // Cargar desde CSV
    // ─────────────────────────────────────────
    public String cargarDesdeCSV(MultipartFile archivo) {
        int exitosos = 0;
        int fallidos = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream()))) {

            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                try {
                    String[] datos = linea.split(",");
                    ProductoDTO dto = new ProductoDTO();
                    dto.setNombreProducto(datos[0].trim());
                    dto.setNitproveedor(Long.parseLong(
                        datos[1].trim()));
                    dto.setPrecioCompra(Double.parseDouble(
                        datos[2].trim()));
                    dto.setIvacompra(Double.parseDouble(
                        datos[3].trim()));
                    dto.setPrecioVenta(Double.parseDouble(
                        datos[4].trim()));

                    guardarProducto(dto);
                    exitosos++;

                } catch (Exception e) {
                    fallidos++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                "Error al procesar el archivo CSV: " 
                + e.getMessage());
        }

        return "CSV procesado: " + exitosos 
            + " exitosos, " + fallidos + " fallidos";
    }

    // ─────────────────────────────────────────
// Verificar stock disponible
// → usado por MS-Ventas
// ─────────────────────────────────────────
    public boolean hayStock(Long codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado: " + codigo));
        return producto.getStock() >= cantidad;
    }

    // ─────────────────────────────────────────
// Descontar stock al confirmar venta
// → usado por MS-Ventas
// ─────────────────────────────────────────
    public void descontarStock(Long codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado: " + codigo));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException(
                    "Stock insuficiente para: "
                            + producto.getNombreProducto()
                            + ". Disponible: " + producto.getStock());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

}