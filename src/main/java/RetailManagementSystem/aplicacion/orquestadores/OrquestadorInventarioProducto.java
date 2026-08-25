package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOInventario;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.servicios.ServicioInventario;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.time.LocalDate;
import java.util.List;

public class OrquestadorInventarioProducto {

    //ATRIBUTOS:

    private final ServicioProductos servicioProductos;
    private final ServicioInventario servicioInventario;

    private final EnsambladorDTOProducto ensambladorDTOProducto;
    private final EnsambladorDTOInventario ensambladorDTOInventario;

    //CONSTRUCTOR:

    public OrquestadorInventarioProducto(
            ServicioProductos servicioProductos, ServicioInventario servicioInventario,
            EnsambladorDTOProducto ensambladorDTOProducto, EnsambladorDTOInventario ensambladorDTOInventario
    ) {
        this.servicioProductos = servicioProductos;
        this.servicioInventario = servicioInventario;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
    }

    //MÉTODOS:

    public ProductoResumenDTO validarEspacioInventarioYGuardarProducto(
            int idInventario, Producto producto, LocalDate fecha
    )
    {
        this.servicioInventario.verificarEspacioDisponible(idInventario, producto.getStock());
        this.servicioProductos.registrarProducto(idInventario, producto);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(producto, fecha);
    }

    public ProductoResumenDTO validarEspacioInventarioYAumentarStockProducto(
            int idInventario, int cantidadAAumentarProducto, String codigoProducto, LocalDate fecha
    ) {
        this.servicioInventario.verificarEspacioDisponible(idInventario, cantidadAAumentarProducto);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioProductos.aumentarStockDeProductoDeInventario(
                        idInventario, codigoProducto, cantidadAAumentarProducto
                ) , fecha
        );
    }

    public List<InventarioDTO> obtenerTodosLosInventarios(){
        return this.ensambladorDTOInventario.ensamblarDetalleInventarioGeneral(
                this.servicioInventario.obtenerTodosLosInventarios()
        );
    }

    public void validarEspacioInventarioYMoverProducto(
            int idInventarioSalida, int idInventarioDestino, String codigoProducto, int stockProducto
    ){
        this.servicioInventario.verificarEspacioDisponible(idInventarioDestino, stockProducto);
        this.servicioProductos.moverProductoAInventario(idInventarioSalida, idInventarioDestino, codigoProducto);
    }

    public InventarioDTO actualizarInventario(
            UsuarioDTOCompleto usuario, int idInventario, String nombreNuevo
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_INVENTARIOS);
        return this.ensambladorDTOInventario.ensamblarDatosInventario(
                this.servicioInventario.actualizarInventario(idInventario, nombreNuevo)
        );
    }

    public InventarioDTO registrarInventario(
            UsuarioDTOCompleto usuario, String nombre, int capacidadMaxima
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_INVENTARIOS);
        return this.ensambladorDTOInventario.ensamblarDatosInventario(
                this.servicioInventario.agregarInventario(nombre, capacidadMaxima)
        );
    }

}//===================================================================================================================//

