package RetailManagementSystem.dominio.entidades.seguridad;

import RetailManagementSystem.dominio.excepciones.RolNoDisponibleException;

import java.time.LocalDateTime;
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

    private final transient Set<String> permisosCacheados;

    private int intentosFallidos;

    private LocalDateTime bloqueadoHasta;

    private final String hash;

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

    public Set<String> getPermisosCacheados() {
        return Collections.unmodifiableSet(this.permisosCacheados);
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }
    private void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public LocalDateTime getBloqueadoHasta() {
        return this.bloqueadoHasta;
    }
    private void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public String getHash() {
        return hash;
    }

    public boolean isActivo() {
        return activo;
    }
    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    //CONSTRUCTORES:

    private Usuario(
            Integer idUsuario, String nombre, String apellido, String email, int intentosFallidos,
            LocalDateTime bloqueadoHasta, String hash, boolean activo
    ) {
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
        this.intentosFallidos = intentosFallidos;
        this.bloqueadoHasta = bloqueadoHasta;
        this.hash = hash;
        this.activo = activo;
    }

    public static Usuario reconstruirDesdeBD(
            Integer id_usuario, String nombre, String apellido, String email, int intentosFallidos,
            LocalDateTime bloqueadoHasta, String hash, boolean activo
    ){
        return new Usuario(id_usuario, nombre, apellido, email, intentosFallidos, bloqueadoHasta, hash, activo);
    }

    public static Usuario crearNuevo(String nombre, String apellido, String email, String hash, boolean activo){
        return new Usuario(null, nombre, apellido, email, 0, null, hash, activo);
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

    public void registrarIntentoFallido(int maxIntentosFallidos, int minutosDeBloqueo, LocalDateTime fechaReferencia){
        if (maxIntentosFallidos <= 0) {
            throw new IllegalArgumentException("El máximo de intentos debe ser mayor a 0");
        }
        if (minutosDeBloqueo <= 0) {
            throw new IllegalArgumentException("Los minutos de bloqueo deben ser mayores a 0");
        }
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }
        setIntentosFallidos(this.intentosFallidos + 1);
        if (this.intentosFallidos >= maxIntentosFallidos){
            LocalDateTime bloqueadoHasta = fechaReferencia.plusMinutes(minutosDeBloqueo);
            setBloqueadoHasta(bloqueadoHasta);
        }
    }

    public void limpiarIntentosFallidos(){
        setIntentosFallidos(0);
        setBloqueadoHasta(null);
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

