package ProyectoPropio1.aplicacion.ensambladores;

import ProyectoPropio1.dominio.entidades.Carrito;
import ProyectoPropio1.dominio.entidades.ItemCarrito;
import ProyectoPropio1.aplicacion.dto.ItemCarritoDTO;
import ProyectoPropio1.aplicacion.dto.VistaPreviaCarritoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOCarrito {

    public EnsambladorDTOCarrito() {
    }

    private ItemCarritoDTO ensamblarItemCarritoDTO(ItemCarrito itemCarrito, LocalDate fecha){
        String nombreArticulo = itemCarrito.getItemFacturable().getNombre();
        BigDecimal precioUnitario = itemCarrito.getItemFacturable().getValorVenta(fecha);
        return new ItemCarritoDTO(nombreArticulo, itemCarrito.getCantidad(), precioUnitario, itemCarrito.calcularSubtotal(fecha));
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

