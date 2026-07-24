package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.*;

import java.util.*;

public class Tienda {

    //ATRIBUTOS:

    private String nombreTienda;

    //GETTERS Y SETTERS:

    public String getNombreTienda() {
        return nombreTienda;
    }

    private void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    //CONSTRUCTORES:

    private Tienda(String nombreTienda){
        if (nombreTienda==null || nombreTienda.isBlank()){
            throw new IllegalArgumentException("Nombre de la Tienda Vacío");
        }
        this.nombreTienda = nombreTienda;
    }

    public static Tienda crearNueva(String nombreTienda){
        return new Tienda(nombreTienda);
    }

    //METODOS PARA MODIFICAR TIENDA:

    public void cambiarNombreTienda(String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre de la Tienda Vacío");
        }
        setNombreTienda(nuevoNombre);
    }

}

