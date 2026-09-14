package RetailManagementSystem.dominio.entidades.seguridad;

import java.util.Collections;
import java.util.HashSet;
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
        this.nombre = nombre.trim();
    }

    public Set<Permiso> getPermisos() {
        return Collections.unmodifiableSet(this.permisos);
    }

    public boolean isActivo() {
        return activo;
    }

    //VALIDACIONES:

    private void validarNombre(String nombre){
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Rol Vacío");
        }
    }

    private void validarEstado(Boolean activo){
        if (activo == null){
            throw new IllegalArgumentException("El Estado del Rol es Obligatorio");
        }
    }

    private void validarPermiso(Permiso permiso){
        if (permiso == null){
            throw new IllegalArgumentException("El Permiso a Añadir NO Puede ser Nulo");
        }
    }

    //CONSTRUCTORES:

    private Rol(Integer idRol, String nombre, Boolean activo) {
        validarNombre(nombre);
        validarEstado(activo);
        this.idRol = idRol;
        setNombre(nombre);
        this.permisos = new HashSet<>();
        this.activo = activo;
    }

    public static Rol reconstruirDesdeBD(Integer id_rol, String nombre, Boolean activo) {
        return new Rol(id_rol, nombre, activo);
    }

    public static Rol crearNuevo(String nombre, Boolean activo) {
        return new Rol(null, nombre, activo);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rol rol = (Rol) o;
        if (this.idRol == null || rol.idRol == null) {
            return false;
        }
        return this.idRol.equals(rol.idRol);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void cambiarNombre(String nombreNuevo){
        validarNombre(nombreNuevo);
        setNombre(nombreNuevo);
    }

    public void cambiarEstado(){
        this.activo = !this.isActivo();
    }

    public void anadirPermisoNuevo(Permiso permiso) {
        validarPermiso(permiso);
        this.permisos.add(permiso);
    }

    public void recuperarPermisoDeBD(Permiso permiso){
        validarPermiso(permiso);
        this.permisos.add(permiso);
    }

    public void quitarPermiso(Permiso permiso) {
        this.permisos.remove(permiso);
    }

    public Set<String> obtenerNombresPermisos() {
        return this.permisos.stream()
                .map(Permiso::getNombre)
                .collect(Collectors.toUnmodifiableSet());
    }

}//===================================================================================================================//

