package com.tiendagenerica.ms_productos.repository;

import com.tiendagenerica.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Busca productos por NIT de proveedor
    List<Producto> findByNitproveedor(Long nitproveedor);

    // Verifica si existe un producto con ese código
    boolean existsByCodigoProducto(Long codigoProducto);
}