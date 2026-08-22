package RetailManagementSystem.dominio.entidades.seguridad;

import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.RolNoDisponibleException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Usuario {

    //ATRIBUTOS:

    private final Long idUsuario;

    private String nombre;

    private String apellido;

    private String email;

    private final Set<Rol> roles;

    private final transient Set<String> permisosCacheados;

    private int intentosFallidos;

    private LocalDateTime bloqueadoHasta;

    private String hash;

    private boolean activo;

    private boolean debeCambiarContrasena;

    //GETTERS Y SETTERS:

    public Long getIdUsuario() {
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

    public List<Rol> getRoles() {
        return List.copyOf(this.roles);
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
    private void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isActivo() {
        return activo;
    }
    private void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isDebeCambiarContrasena() {
        return debeCambiarContrasena;
    }
    private void setDebeCambiarContrasena(boolean debeCambiarContrasena) {
        this.debeCambiarContrasena = debeCambiarContrasena;
    }

    //CONSTRUCTORES:

    private Usuario(
            Long idUsuario, String nombre, String apellido, String email,
            int intentosFallidos, LocalDateTime bloqueadoHasta, String hash, boolean activo,
            boolean debeCambiarContrasena
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
        if (intentosFallidos < 0){
            throw new IllegalArgumentException("Intentos Fallidos Inválidos");
        }
        if (hash == null || hash.isBlank()){
            throw new IllegalArgumentException("Hash invalido");
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
        this.debeCambiarContrasena = debeCambiarContrasena;
    }

    public static Usuario reconstruirDesdeBD(
            Long id_usuario, String nombre, String apellido, String email,
            int intentosFallidos, LocalDateTime bloqueadoHasta, String hash, boolean activo,
            boolean debeCambiarContrasena
    ){
        return new Usuario(id_usuario, nombre, apellido, email, intentosFallidos, bloqueadoHasta, hash, activo,
                debeCambiarContrasena);
    }

    public static Usuario crearNuevo(
            String nombre, String apellido, String email, String hash, boolean activo
    ){
        return new Usuario(null, nombre, apellido, email, 0, null, hash,
                activo, true);
    }

    //MÉTODOS PARA ACTUALIZAR DATOS:

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

    //MÉTODOS DE VALIDACIÓN LOGIN:

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

    public void limpiarIntentosFallidosYBloqueo(){
        setIntentosFallidos(0);
        setBloqueadoHasta(null);
    }

    //MÉTODOS PARA ROLES Y PERMISOS:

    public void anadirRol(Rol rolNuevo){
        if (!rolNuevo.isActivo()){
            throw new RolNoDisponibleException("El Rol que quieres Agregar NO esta Activo");
        }
        if (this.roles.add(rolNuevo)) {
            actualizarCachePermisos();
        }
    }

    public void recuperarRolDeBD(Rol rol){
        if (this.roles.add(rol)) {
            actualizarCachePermisos();
        }
    }

    public boolean tienePermiso(String nombrePermiso) {
        return this.permisosCacheados.contains(nombrePermiso.toUpperCase());
    }

    public boolean tieneRol(Rol rol){
        return this.roles.contains(rol);
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

    public Set<String> obtenerPermisosTotales() {
        Set<String> permisosTotales = new HashSet<>();
        for (Rol rol : this.roles) {
            permisosTotales.addAll(rol.obtenerNombresPermisos());
        }
        return Collections.unmodifiableSet(permisosTotales);
    }

    public Set<String> obtenerNombresRoles() {
        return roles.stream().map(Rol::getNombre).collect(Collectors.toUnmodifiableSet());
    }

    //MÉTODOS PARA PREESTABLECER CONTRASEÑA:

    public void asignarContrasenaTemporal(String nuevoHash) {
        setHash(nuevoHash);
        setDebeCambiarContrasena(true);
        setIntentosFallidos(0);
        setBloqueadoHasta(null);
    }

    public void establecerContrasenaDefinitiva(String nuevoHash) {
        setHash(nuevoHash);
        setDebeCambiarContrasena(false);
    }

}//===================================================================================================================//

