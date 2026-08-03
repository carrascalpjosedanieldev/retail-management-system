package RetailManagementSystem.dominio.entidades.seguridad;

import java.util.Objects;

public class Permiso {

    //ATRIBUTOS:

    private final Integer idPermiso;

    private final String nombre;

    private String descripcion;

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
    private void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }
    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTORES:

    private Permiso(Integer idPermiso, String nombre, String descripcion, boolean activo) {
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Permiso Vacío");
        }
        if (descripcion == null) {
            throw new IllegalArgumentException("La Descripción NO puede ser Nula");
        }
        this.idPermiso = idPermiso;
        this.nombre = nombre.trim().toUpperCase();
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public static Permiso reconstruirDesdeBD(Integer idPermiso, String nombre, String descripcion, boolean activo){
        return new Permiso(idPermiso, nombre, descripcion, activo);
    }

    public static Permiso crearNuevo(String nombre, String descripcion, boolean activo){
        return new Permiso(null, nombre, descripcion, activo);
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
        if (descripcionNueva == null){
            throw new IllegalArgumentException("Descripción del Permiso Nula");
        }
        setDescripcion(descripcionNueva);
    }

    public void activarPermiso(){
        if (isActivo()){
            throw new IllegalStateException("El Permiso ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivarPermiso(){
        if (!isActivo()){
            throw new IllegalStateException("El Permiso ya esta Inactivo");
        }
        setActivo(false);
    }

}//==================================================================================================================//

