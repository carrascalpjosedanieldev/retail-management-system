package RetailManagementSystem.dominio.entidades.seguridad;

import java.util.Objects;

public class Permiso {

    //ATRIBUTOS:

    private final Integer idPermiso;

    private final String nombre;

    private String descripcion;

    private final String modulo;

    private boolean activo;

    //GETTERS Y SETTERS:

    public Integer getIdPermiso() {
        return idPermiso;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getModulo() {
        return modulo;
    }

    public boolean isActivo() {
        return activo;
    }

    //VALIDACIÓN:

    private void validarDescripcion(String descripcion){
        if (descripcion == null || descripcion.isBlank()){
            throw new IllegalArgumentException("Descripción del Permiso Nula");
        }
    }

    //CONSTRUCTORES:

    private Permiso(
            Integer idPermiso, String nombre, String descripcion, String modulo, boolean activo
    ) {
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Permiso Vacío");
        }
        validarDescripcion(descripcion);
        this.idPermiso = idPermiso;
        this.nombre = nombre.trim().toUpperCase();
        this.descripcion = descripcion;
        this.modulo = modulo;
        this.activo = activo;
    }

    public static Permiso reconstruirDesdeBD(
            Integer idPermiso, String nombre, String descripcion, String modulo, boolean activo
    ){
        return new Permiso(idPermiso, nombre, descripcion, modulo, activo);
    }

    public static Permiso crearNuevo(String nombre, String descripcion, String modulo, boolean activo) {
        return new Permiso(null, nombre, descripcion, modulo, activo);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        return nombre.equalsIgnoreCase(permiso.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    public void cambiarDescripcion(String descripcionNueva){
        validarDescripcion(descripcionNueva);
        this.descripcion = descripcionNueva;
    }

    public void cambiarEstado(){
        this.activo = !this.isActivo();
    }

}//==================================================================================================================//

