package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DetalleCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.FormularioProductoDTO;
import RetailManagementSystem.dominio.entidades.comercial.*;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioDescuentos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioImpuestos;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.util.Map;

public class FabricaProductos {

    //ATRIBUTOS:

    private final Map<TipoProducto, EstrategiaFabricarProducto<?, ?>> estrategiasFabricacion;

    private final ServicioImpuestos servicioImpuestos;

    private final ServicioDescuentos servicioDescuentos;

    private record ComponentesComunes(Impuesto impuesto, Descuento descuento) { }

    //CONSTRUCTORES:

    public FabricaProductos(
            Map<TipoProducto, EstrategiaFabricarProducto<?, ?>> estrategiasFabricacion,
            ServicioImpuestos servicioImpuestos, ServicioDescuentos servicioDescuentos
    ) {
        this.estrategiasFabricacion = estrategiasFabricacion;
        this.servicioImpuestos = servicioImpuestos;
        this.servicioDescuentos = servicioDescuentos;
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

    public Producto fabricarProducto(FormularioProductoDTO datosProducto, ContextoEvaluacion contextoEvaluacion) {
        EstrategiaFabricarProducto<Producto, DetalleCreacionProductoDTO> estrategia =
                obtenerEstrategia(datosProducto.datosGenerales().tipoProducto());
        ComponentesComunes componentes = obtenerYValidarComponentes(
                datosProducto.datosGenerales().idImpuesto(), datosProducto.datosGenerales().idDescuento()
        );
        return estrategia.fabricarProducto(
                datosProducto.datosGenerales(), datosProducto.detalle(), contextoEvaluacion,
                componentes.impuesto, componentes.descuento
        );
    }

    @SuppressWarnings("unchecked")
    private <T extends Producto, D extends DetalleCreacionProductoDTO> EstrategiaFabricarProducto<T, D>
    obtenerEstrategia(TipoProducto tipoProducto) {
        EstrategiaFabricarProducto<T, D> estrategia =
                (EstrategiaFabricarProducto<T, D>) estrategiasFabricacion.get(tipoProducto);
        if (estrategia == null){
            throw new IllegalStateException(
                    "NO Existe una Estrategia de Fabricación de Producto para el Tipo: " + tipoProducto
            );
        }
        return estrategia;
    }

}//===================================================================================================================//

