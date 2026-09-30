package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.entidades.ventas.ItemCarrito;
import RetailManagementSystem.aplicacion.dto.ventas.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.dto.ventas.VistaPreviaCarritoDTO;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    private ItemCarritoDTO ensamblarItemCarritoDTO(ItemCarrito itemCarrito, LocalDate fecha){
        ItemFacturable item = itemCarrito.getItemFacturable();
        BigDecimal valorFinalSinImpuesto = this.calculadoraPrecios.calcularValorFinalSinImpuesto(item, fecha);
        BigDecimal impuesto = this.calculadoraPrecios.calcularImpuesto(valorFinalSinImpuesto, item.getImpuesto());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(item, fecha);
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

    public VistaPreviaCarritoDTO ensamblarVistaPreviaCarritoDTO(Carrito carrito, LocalDate fecha){
        List<ItemCarritoDTO> itemsCarrito = new ArrayList<>();
        carrito.getItems().values().forEach(itemCarrito -> {
            ItemCarritoDTO itemCarritoDTO = ensamblarItemCarritoDTO(itemCarrito, fecha);
            itemsCarrito.add(itemCarritoDTO);
        });
        return new VistaPreviaCarritoDTO(itemsCarrito, carrito.calcularTotal(fecha));
    }

}//===================================================================================================================//

