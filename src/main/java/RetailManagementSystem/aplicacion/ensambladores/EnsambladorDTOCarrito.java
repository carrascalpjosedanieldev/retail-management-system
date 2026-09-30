package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.entidades.ventas.ItemCarrito;
import RetailManagementSystem.aplicacion.dto.ventas.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.VistaPreviaCarritoDTO;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOCarrito {

    //ATRIBUTOS:

    private final CalculadoraPrecios calculadoraPrecios;

    //CONSTRUCTOR:

    public EnsambladorDTOCarrito(CalculadoraPrecios calculadoraPrecios) {
        this.calculadoraPrecios = calculadoraPrecios;
    }

    //MÉTODOS:

    private ItemCarritoDTO ensamblarItemCarritoDTO(ItemCarrito itemCarrito, ContextoEvaluacion contextoEvaluacion){
        ItemFacturable item = itemCarrito.getItemFacturable();
        BigDecimal valorFinalSinImpuesto =
                this.calculadoraPrecios.calcularValorFinalSinImpuesto(item, contextoEvaluacion);
        BigDecimal impuesto = this.calculadoraPrecios.calcularImpuesto(valorFinalSinImpuesto, item.getImpuesto());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(item, contextoEvaluacion);
        return new ItemCarritoDTO(
                item.getCodigo(),
                item.getTipoItem(),
                item.getNombre(),
                itemCarrito.getCantidad(),
                valorVenta,
                itemCarrito.calcularSubtotal(),
                impuesto
        );
    }

    public VistaPreviaCarritoDTO ensamblarVistaPreviaCarritoDTO(
            Carrito carrito, ContextoEvaluacion contextoEvaluacion
    ){
        List<ItemCarritoDTO> itemsCarrito = new ArrayList<>();
        carrito.getItems().values().forEach(itemCarrito -> {
            ItemCarritoDTO itemCarritoDTO = ensamblarItemCarritoDTO(itemCarrito, contextoEvaluacion);
            itemsCarrito.add(itemCarritoDTO);
        });
        return new VistaPreviaCarritoDTO(itemsCarrito, carrito.calcularTotal());
    }

}//===================================================================================================================//

