package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.Carrito;
import RetailManagementSystem.dominio.entidades.ItemCarrito;
import RetailManagementSystem.aplicacion.dto.ItemCarritoDTO;
import RetailManagementSystem.aplicacion.dto.VistaPreviaCarritoDTO;
import RetailManagementSystem.dominio.entidades.ItemFacturable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOCarrito {

    public EnsambladorDTOCarrito() {
    }

    private ItemCarritoDTO ensamblarItemCarritoDTO(ItemCarrito itemCarrito, LocalDate fecha){
        ItemFacturable item = itemCarrito.getItemFacturable();
        String codigoArticulo = item.getCodigo();
        String tipoArticulo = item.getTipoItem().name();
        String nombreArticulo = item.getNombre();
        BigDecimal precioUnitario = item.getValorVenta(fecha);
        BigDecimal impuesto = item.calcularImpuesto(item.getValorFinalSinImpuesto(fecha), fecha);
        return new ItemCarritoDTO(
                codigoArticulo, tipoArticulo, nombreArticulo, itemCarrito.getCantidad(), precioUnitario,
                itemCarrito.calcularSubtotal(fecha), impuesto
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

}

