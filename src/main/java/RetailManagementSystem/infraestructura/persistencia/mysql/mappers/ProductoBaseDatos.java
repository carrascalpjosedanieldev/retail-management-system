package RetailManagementSystem.infraestructura.persistencia.mysql.mappers;

import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import java.math.BigDecimal;

public record ProductoBaseDatos(
        String codigo, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, boolean activo,
        Impuesto impuesto, Descuento descuento
) { }

