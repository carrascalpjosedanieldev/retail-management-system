package RetailManagementSystem.dominio.entidades.ventas;

public class SesionVenta {

    //ATRIBUTOS:

    private final Carrito carrito;

    //GETTERS Y SETTERS:

    public Carrito getCarrito() {
        return carrito;
    }

    //CONSTRUCTOR:

    private SesionVenta() {
        this.carrito = Carrito.crearNuevo();
    }

    public static SesionVenta crearNueva(){
        return new SesionVenta();
    }

}//===================================================================================================================//

