package com.tiendagenerica.ms_productos.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class ProveedorClient {

    @Autowired
    private RestTemplate restTemplate;

    // Lee la URL desde application.properties
    @Value("${ms.proveedores.url}")
    private String proveedoresUrl;

    public boolean existeProveedor(Long nit) {
        try {
            String url = proveedoresUrl 
                + "/proveedor/existsbynit/" + nit;

            Boolean resultado = restTemplate.getForObject(
                url, Boolean.class);

            return Boolean.TRUE.equals(resultado);

        } catch (HttpClientErrorException.NotFound e) {
            // MS-Proveedores retornó 404 → NIT no existe
            return false;
        } catch (Exception e) {
            // MS-Proveedores no está disponible
            System.err.println(
                ">>> Error al contactar MS-Proveedores: " 
                + e.getMessage());
            // Si el servicio no responde, permitimos 
            // guardar para no bloquear el sistema
            return true;
        }
    }
}