package com.tiendagenerica.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigoProducto;  // ← la BD lo genera automáticamente

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    @Column(name = "nombre_producto", nullable = false, length = 50)
    private String nombreProducto;

    @NotNull(message = "El NIT del proveedor es obligatorio")
    @Column(name = "nitproveedor", nullable = false)
    private Long nitproveedor;

    @NotNull(message = "El precio de compra es obligatorio")
    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;
    @NotNull(message = "El IVA de compra es obligatorio")
    @Column(name = "ivacompra", nullable = false)
    private Double ivacompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;
}