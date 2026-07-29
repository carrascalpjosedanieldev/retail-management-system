package RetailManagementSystem.dominio.entidades;

public class SesionVenta {

    //ATRIBUTOS:

    private final Carrito carrito;

    //GETTERS Y SETTERS:

    public Carrito getCarrito() {
        return carrito;
    }

    //CONSTRUCTOR:

    private SesionVenta() {
        this.carrito = Carrito.crearNueva();
    }

    public static SesionVenta crearNueva(){
        return new SesionVenta();
    }


}//===================================================================================================================//

