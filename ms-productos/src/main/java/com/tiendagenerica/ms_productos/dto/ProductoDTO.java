package com.tiendagenerica.ms_productos.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long codigoProducto;
    private String nombreProducto;
    private Long nitproveedor;
    private Double precioCompra;
    private Double ivacompra;
    private Double precioVenta;
}