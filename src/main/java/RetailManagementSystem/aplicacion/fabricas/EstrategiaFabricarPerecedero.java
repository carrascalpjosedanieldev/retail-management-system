package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetallePerecederoDTO;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioPoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

public class EstrategiaFabricarPerecedero implements
        EstrategiaFabricarProducto<ProductoPerecedero, DetallePerecederoDTO> {

    //ATRIBUTOS:

    private final ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    //CONSTRUCTOR:

    public EstrategiaFabricarPerecedero(ServicioPoliticaVencimiento servicioPoliticaVencimiento) {
        this.servicioPoliticaVencimiento = servicioPoliticaVencimiento;
    }

    //MÉTODOS:

    @Override
    public ProductoPerecedero fabricarProducto(
            DatosGeneralesCreacionProductoDTO general, DetallePerecederoDTO detalle,
            ContextoEvaluacion contextoEvaluacion, Impuesto impuesto, Descuento descuento
    ) {
        if (contextoEvaluacion.getFechaEvaluacion().isEmpty()){
            throw new IllegalArgumentException("Se Requiere la Fecha Actual para Fabricar el Producto");
        }
        if (detalle.fechaVencimiento().isBefore(contextoEvaluacion.getFechaEvaluacion().get())){
            throw new IllegalArgumentException("NO se puede Registrar el Producto porque ya está Vencido");
        }
        PoliticaVencimiento politicaVencimiento =
                this.servicioPoliticaVencimiento.obtenerPoliticaVencimiento(detalle.idPoliticaVencimiento());
        if (!politicaVencimiento.isActiva()){
            throw new IllegalArgumentException(
                    "NO se puede Asignar la Política de Vencimiento -" + politicaVencimiento.getNombre() +
                            "- Porque se Encuentra Inactiva."
            );
        }
        return ProductoPerecedero.crearNuevo(
                general.nombre(), general.valorCompra(), general.ganancia(), general.stock(),
                impuesto, descuento, detalle.fechaVencimiento(), politicaVencimiento
        );
    }

}//===================================================================================================================//

