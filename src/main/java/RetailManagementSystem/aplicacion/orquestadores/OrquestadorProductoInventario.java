package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.dominio.entidades.comercial.Producto;

public class OrquestadorProductoInventario {

    //ATRIBUTOS:

    private final ServicioProductos servicioProductos;
    private final ServicioInventario servicioInventario;

    private final EnsambladorDTOInventario ensambladorDTOInventario;

    //CONSTRUCTOR:

    public OrquestadorProductoInventario(
            ServicioProductos servicioProductos, ServicioInventario servicioInventario,
            EnsambladorDTOInventario ensambladorDTOInventario
    ) {
        this.servicioProductos = servicioProductos;
        this.servicioInventario = servicioInventario;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
    }

    //MÉTODOS:

    public void validarEspacioInventarioYGuardarProducto(int idInventario, Producto producto){
        this.servicioInventario.verificarEspacioDisponible(idInventario, producto.getStock());
        this.servicioProductos.registrarProducto(idInventario, producto);
    }

    public void validarEspacioInventarioYAumentarStockProducto(
            int idInventario, int cantidadAAumentarProducto, String codigoProducto
    ) {
        this.servicioInventario.verificarEspacioDisponible(idInventario, cantidadAAumentarProducto);
        this.servicioProductos.aumentarStockDeProductoDeInventario(
                idInventario, codigoProducto, cantidadAAumentarProducto
        );
    }

    public void validarEspacioInventarioYMoverProducto(
            int idInventarioSalida, int idInventarioDestino, String codigoProducto, int stockProducto
    ){
        this.servicioInventario.verificarEspacioDisponible(idInventarioDestino, stockProducto);
        this.servicioProductos.moverProductoAInventario(idInventarioSalida, idInventarioDestino, codigoProducto);
    }

    public InventarioDTO actualizarInventario(int idInventario, String nombreNuevo){
        return this.ensambladorDTOInventario.ensamblarDatosInventario(
                this.servicioInventario.actualizarInventario(idInventario, nombreNuevo)
        );
    }

    public InventarioDTO registrarInventario(String nombre, int capacidadMaxima){
        return this.ensambladorDTOInventario.ensamblarDatosInventario(
                this.servicioInventario.agregarInventario(nombre, capacidadMaxima)
        );
    }

}//===================================================================================================================//

