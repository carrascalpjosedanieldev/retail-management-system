package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioProducto;

public class ServicioGestionStock {

    //ATRIBUTOS

    private final RepositorioProducto repositorioProducto;

    private final RepositorioInventario repositorioInventario;

    private final GestorTransaccional gestorTransaccional;

    //CONSTRUCTOR:

    public ServicioGestionStock(
            RepositorioProducto repositorioProducto, RepositorioInventario repositorioInventario,
            GestorTransaccional gestorTransaccional
    ) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioInventario = repositorioInventario;
        this.gestorTransaccional = gestorTransaccional;
    }

    //MÉTODOS:

    public void registrarProductoEnInventario(int idInventario, Producto producto){
        gestorTransaccional.ejecutarEnTransaccion(()->{
            this.repositorioInventario.validarCapacidadInventario(idInventario, producto.getStock());
            this.repositorioProducto.insertarProducto(producto, idInventario);
        });
    }

}

