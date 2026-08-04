package RetailManagementSystem.dominio.entidades.seguridad;

import RetailManagementSystem.dominio.excepciones.PermisoNoDisponibleExeption;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Rol(Integer idRol, String nombre, Set<Permiso> permisos, boolean activo) {
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Rol Vacío");
        }
        this.idRol = idRol;
        this.nombre = nombre;
        this.permisos = permisos;
        this.activo = activo;
    }

    public static Rol reconstruirDesdeBD(Integer id_rol, String nombre, Set<Permiso> permisos, boolean activo){
        return new Rol(id_rol, nombre, permisos, activo);
    }

    public static Rol crearNuevo(String nombre, boolean activo){
        return new Rol(null, nombre, null, activo);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return nombre.equalsIgnoreCase(rol.nombre);
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

    public void activarRol() {
        if (this.activo) {
            throw new IllegalStateException("El Rol ya está activo.");
        }
        this.activo = true;
    }

    public void desactivarRol() {
        if (!this.activo) {
            throw new IllegalStateException("El Rol ya está inactivo.");
        }
        this.activo = false;
    }

    public void anadirPermiso(Permiso permiso) {
        if (!permiso.isActivo()) {
            throw new PermisoNoDisponibleExeption("El Permiso '" + permiso.getNombre() + "' no está activo.");
        }
        this.permisos.add(permiso);
    }

    public void quitarPermiso(Permiso permiso) {
        if (!this.permisos.contains(permiso)) {
            return;
        }
        this.permisos.remove(permiso);
    }

    public Set<String> obtenerNombresPermisos() {
        return this.permisos.stream().map(Permiso::getNombre).collect(Collectors.toUnmodifiableSet());
    }

}//===================================================================================================================//

