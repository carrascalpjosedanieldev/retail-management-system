package RetailManagementSystem.dominio.entidades.seguridad;

import RetailManagementSystem.dominio.excepciones.PermisoNoDisponibleExeption;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Rol {

    //ATRIBUTOS:

    private final Integer idRol;

    private String nombre;

    private final Set<Permiso> permisos;

    private boolean activo;

    //GETTERS Y SETTERS:

    public Integer getIdRol() {
        return idRol;
    }

    public String getNombre() {
        return nombre;
    }
    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Permiso> getPermisos() {
        return Collections.unmodifiableSet(this.permisos);
    }

    public boolean isActivo() {
        return activo;
    }
    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTORES:

    private Rol(Integer idRol, String nombre, boolean activo) {
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Rol Vacío");
        }
        this.idRol = idRol;
        this.nombre = nombre;
        this.permisos = new HashSet<>();
        this.activo = activo;
    }

    public static Rol reconstruirDesdeBD(Integer id_rol, String nombre, boolean activo){
        return new Rol(id_rol, nombre, activo);
    }

    public static Rol crearNuevo(String nombre, boolean activo){
        return new Rol(null, nombre, activo);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        return nombre.equalsIgnoreCase(permiso.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }

    public void cambiarNombre(String nombreNuevo){
        if (nombreNuevo == null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("Nombre del Rol Vacío");
        }
        setNombre(nombreNuevo);
    }

    public void anadirPermiso(Permiso permiso) {
        if (!permiso.isActivo()){
            throw new PermisoNoDisponibleExeption("El Permiso que quieres Agregar NO esta Activo");
        }
        this.permisos.add(permiso);
    }

    public void quitarPermiso(Permiso permiso){
        if (this.permisos.contains(permiso)){
            throw new IllegalArgumentException("NO");
        }
        this.permisos.remove(permiso);
    }

    public void activarRol(){
        if (isActivo()){
            throw new IllegalStateException("El Rol ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivarRol(){
        if (!isActivo()){
            throw new IllegalStateException("El Rol ya esta Inactivo");
        }
        setActivo(false);
    }

}//===================================================================================================================//

