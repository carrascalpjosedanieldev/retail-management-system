package RetailManagementSystem.aplicacion.ensambladores;

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

    //CONSTRUCTOR:

    public EnsambladorDTOCarrito() {
    }

    //MÉTODOS:

    private ItemCarritoDTO ensamblarItemCarritoDTO(ItemCarrito itemCarrito, LocalDate fecha){
        ItemFacturable item = itemCarrito.getItemFacturable();
        BigDecimal impuesto = item.calcularImpuesto(item.getValorFinalSinImpuesto(fecha));
        return new ItemCarritoDTO(
                item.getCodigo(),
                item.getTipoItem(),
                item.getNombre(),
                itemCarrito.getCantidad(),
                item.getValorVenta(fecha),
                itemCarrito.calcularSubtotal(fecha),
                impuesto
        );
    }

    public VistaPreviaCarritoDTO ensamblarVistaPreviaCarritoDTO(Carrito carrito, LocalDate fecha){
        List<ItemCarritoDTO> itemsCarrito = new ArrayList<>();
        for (ItemCarrito itemCarrito:carrito.getItems().values()){
            ItemCarritoDTO itemCarritoDTO = ensamblarItemCarritoDTO(itemCarrito, fecha);
            itemsCarrito.add(itemCarritoDTO);
        }
        return new VistaPreviaCarritoDTO(itemsCarrito, carrito.calcularTotal(fecha));
    }

}//===================================================================================================================//

