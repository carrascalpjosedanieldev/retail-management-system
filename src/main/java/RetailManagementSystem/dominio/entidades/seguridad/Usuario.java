package RetailManagementSystem.dominio.entidades.seguridad;

import RetailManagementSystem.dominio.excepciones.RolNoDisponibleException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Usuario {

    //ATRIBUTOS:

    private final Integer idUsuario;

    private String nombre;

    private String apellido;

    private String email;

    private final Set<Rol> roles;

    private transient Set<String> permisosCacheados;

    private boolean activo;

    //GETTERS Y SETTERS:

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }
    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }
    private void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }
    private void setEmail(String email) {
        this.email = email;
    }

    public Set<Rol> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    public boolean isActivo() {
        return activo;
    }
    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTORES:

    private Usuario(Integer idUsuario, String nombre, String apellido, String email, boolean activo) {
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Usuario Vacío");
        }
        if (apellido == null || apellido.isBlank()){
            throw new IllegalArgumentException("Apellido del Usuario Vacío");
        }
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("Email del Usuario Vacío");
        }
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.roles = new HashSet<>();
        this.permisosCacheados = new HashSet<>();
        this.activo = activo;
    }

    public static Usuario reconstruirDesdeBD(Integer id_usuario, String nombre, String apellido, String email, boolean activo){
        return new Usuario(id_usuario, nombre, apellido, email, activo);
    }

    public static Usuario crearNuevo(String nombre, String apellido, String email, boolean activo){
        return new Usuario(null, nombre, apellido, email, activo);
    }

    //MÉTODOS:

    public void cambiarNombre(String nombreNuevo){
        if (nombreNuevo == null || nombreNuevo.isBlank()){
            throw new IllegalArgumentException("Nombre del Usuario Vacío");
        }
        setNombre(nombreNuevo);
    }

    public void cambiarApellido(String apellidoNuevo){
        if (apellidoNuevo == null || apellidoNuevo.isBlank()){
            throw new IllegalArgumentException("Apellido del Usuario Vacío");
        }
        setApellido(apellidoNuevo);
    }

    public void cambiarEmail(String emailNuevo){
        if (emailNuevo == null || emailNuevo.isBlank()){
            throw new IllegalArgumentException("Email del Usuario Vacío");
        }
        setEmail(emailNuevo);
    }

    public void anadirRol(Rol rolNuevo){
        if (!rolNuevo.isActivo()){
            throw new RolNoDisponibleException("El Rol que quieres Agregar NO esta Activo");
        }
        if (this.roles.add(rolNuevo)) {
            actualizarCachePermisos();
        }
    }

    public boolean tienePermiso(String nombrePermiso) {
        return this.permisosCacheados.contains(nombrePermiso.toUpperCase());
    }

    private void actualizarCachePermisos() {
        this.permisosCacheados.clear();
        for (Rol rol : this.roles) {
            for (Permiso permiso : rol.getPermisos()) {
                this.permisosCacheados.add(permiso.getNombre().toUpperCase());
            }
        }
    }

    public void quitarRol(Rol rolAQuitar){
        if (!this.roles.contains(rolAQuitar)){
            throw new IllegalArgumentException("NO tienes ese Rol en tu lista de Roles");
        }
        this.roles.remove(rolAQuitar);
    }

    public void activarUsuario(){
        if (isActivo()){
            throw new IllegalStateException("El Usuario ya esta Activo");
        }
        setActivo(true);
    }

    public void desactivarUsuario(){
        if (!isActivo()){
            throw new IllegalStateException("El Usuario ya esta Inactivo");
        }
        setActivo(false);
    }

}//===================================================================================================================//

