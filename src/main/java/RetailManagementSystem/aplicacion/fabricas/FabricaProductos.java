package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.dominio.entidades.comercial.*;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioDescuentos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioImpuestos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioPoliticaVencimiento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FabricaProductos {

    //ATRIBUTOS:

    private final ServicioImpuestos servicioImpuestos;

    private final ServicioDescuentos servicioDescuentos;

    private final ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    private record ComponentesComunes(Impuesto impuesto, Descuento descuento) {}

    //CONSTRUCTORES:

    public FabricaProductos(
            ServicioImpuestos servicioImpuestos, ServicioDescuentos servicioDescuentos,
            ServicioPoliticaVencimiento servicioPoliticaVencimiento
    ) {
        this.servicioImpuestos = servicioImpuestos;
        this.servicioDescuentos = servicioDescuentos;
        this.servicioPoliticaVencimiento = servicioPoliticaVencimiento;
    }

    //MÉTODOS:

    private ComponentesComunes obtenerYValidarComponentes(int idImpuesto, int idDescuento) {
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        if (!impuesto.isActivo()) {
            throw new IllegalArgumentException(
                    "NO se puede Asignar el Impuesto -" + impuesto.getNombre() + "- Porque se Encuentra Inactivo."
            );
        }
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        if (!descuento.isActivo()) {
            throw new IllegalArgumentException(
                    "NO se puede Asignar el Descuento -" + descuento.getNombre() + "- Porque se Encuentra Inactivo."
            );
        }
        return new ComponentesComunes(impuesto, descuento);
    }

    public ProductoRopa fabricarProductoRopa(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, int idImpuesto,
            int idDescuento, String tallaString
    ) {
        ComponentesComunes componentes = obtenerYValidarComponentes(idImpuesto, idDescuento);
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString.toUpperCase().trim());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La Talla Ingresada NO está entre las Opciones (Usa S, M, L, XL etc).");
        }
        return ProductoRopa.crearNuevo(
                nombre, valorCompra, porcentajeGanancia, stock, componentes.impuesto(), componentes.descuento(), talla
        );
    }

    public ProductoPerecedero fabricarProductoPerecedero(
            String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, int idImpuesto,
            int idDescuento, LocalDate fechaVencimiento, int idPolitica, LocalDate fechaActual
    ) {
        if (fechaVencimiento.isBefore(fechaActual)){
            throw new IllegalArgumentException("NO se puede Registrar el Producto porque ya está Vencido");
        }
        ComponentesComunes componentes = obtenerYValidarComponentes(idImpuesto, idDescuento);
        PoliticaVencimiento politicaVencimiento =
                this.servicioPoliticaVencimiento.obtenerPoliticaVencimiento(idPolitica);
        if (!politicaVencimiento.isActiva()){
            throw new IllegalArgumentException(
                    "NO se puede Asignar la Política de Vencimiento -" + politicaVencimiento.getNombre() +
                            "- Porque se Encuentra Inactiva."
            );
        }
        return ProductoPerecedero.crearNuevo(
                nombre, valorCompra, porcentajeGanancia, stock, componentes.impuesto(), componentes.descuento(),
                fechaVencimiento, politicaVencimiento
        );
    }

}//===================================================================================================================//

