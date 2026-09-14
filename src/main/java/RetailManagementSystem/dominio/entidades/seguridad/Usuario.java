package RetailManagementSystem.dominio.entidades.seguridad;

import java.time.LocalDateTime;
import java.util.*;

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
        this.nombre = nombre.trim();
    }

    public String getApellido() {
        return apellido;
    }
    private void setApellido(String apellido) {
        this.apellido = apellido.trim();
    }

    public String getEmail() {
        return email;
    }
    private void setEmail(String email) {
        this.email = email.trim();
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

    public LocalDateTime getBloqueadoHasta() {
        return this.bloqueadoHasta;
    }

    public String getHash() {
        return hash;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isDebeCambiarContrasena() {
        return debeCambiarContrasena;
    }

    //VALIDACIONES:

    private void validarNombre(String nombre){
        if (nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Usuario Vacío");
        }
    }

    private void validarApellido(String apellido){
        if (apellido == null || apellido.isBlank()){
            throw new IllegalArgumentException("Apellido del Usuario Vacío");
        }
    }

    private void validarEmail(String email){
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("Email del Usuario Vacío");
        }
    }

    //CONSTRUCTORES:

    private Usuario(
            Long idUsuario, String nombre, String apellido, String email,
            Integer intentosFallidos, LocalDateTime bloqueadoHasta, String hash, Boolean activo,
            Boolean debeCambiarContrasena
    ) {
        validarNombre(nombre);
        validarApellido(apellido);
        validarEmail(email);
        if (intentosFallidos == null || intentosFallidos < 0){
            throw new IllegalArgumentException("Intentos Fallidos Inválidos");
        }
        if (hash == null || hash.isBlank()){
            throw new IllegalArgumentException("Hash invalido");
        }
        this.idUsuario = idUsuario;
        setNombre(nombre);
        setApellido(apellido);
        setEmail(email);
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
            int intentosFallidos, LocalDateTime bloqueadoHasta, String hash, Boolean activo,
            boolean debeCambiarContrasena
    ){
        return new Usuario(id_usuario, nombre, apellido, email, intentosFallidos, bloqueadoHasta, hash, activo,
                debeCambiarContrasena);
    }

    public static Usuario crearNuevo(
            String nombre, String apellido, String email, String hash, Boolean activo
    ){
        return new Usuario(null, nombre, apellido, email, 0, null, hash,
                activo, true);
    }

    //MÉTODOS PARA ACTUALIZAR DATOS:

    public void cambiarNombre(String nombreNuevo){
        validarNombre(nombreNuevo);
        setNombre(nombreNuevo);
    }

    public void cambiarApellido(String apellidoNuevo){
        validarApellido(apellidoNuevo);
        setApellido(apellidoNuevo);
    }

    public void cambiarEmail(String emailNuevo){
        validarEmail(emailNuevo);
        setEmail(emailNuevo);
    }

    public void cambiarEstado(){
        this.activo = !this.isActivo();
    }

    //MÉTODOS DE VALIDACIÓN LOGIN:

    public void registrarIntentoFallido(int maxIntentosFallidos, int minutosDeBloqueo, LocalDateTime fechaReferencia) {
        if (maxIntentosFallidos <= 0) {
            throw new IllegalArgumentException("El máximo de intentos debe ser mayor a 0");
        }
        if (minutosDeBloqueo <= 0) {
            throw new IllegalArgumentException("Los minutos de bloqueo deben ser mayores a 0");
        }
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }
        this.intentosFallidos++;
        if (this.intentosFallidos >= maxIntentosFallidos){
            this.bloqueadoHasta = fechaReferencia.plusMinutes(minutosDeBloqueo);
        }
    }

    public void limpiarIntentosFallidosYBloqueo(){
        this.intentosFallidos = 0;
        this.bloqueadoHasta = null;
    }

    //MÉTODOS PARA ROLES Y PERMISOS:

    public void anadirRol(Rol rolNuevo){
        if (this.roles.add(rolNuevo)) {
            actualizarCachePermisos();
        }
    }

    public void recuperarRolDeBD(Rol rol){
        if (this.roles.add(rol)) {
            actualizarCachePermisos();
        }
    }

    private void actualizarCachePermisos() {
        this.permisosCacheados.clear();
        for (Rol rol : this.roles) {
            for (Permiso permiso : rol.getPermisos()) {
                this.permisosCacheados.add(permiso.getNombre());
            }
        }
    }

    public void quitarRol(Rol rolAQuitar){
        this.roles.remove(rolAQuitar);
    }

    //MÉTODOS PARA PREESTABLECER CONTRASEÑA:

    public void asignarContrasenaTemporal(String nuevoHash) {
        this.hash = nuevoHash;
        this.debeCambiarContrasena = true;
        limpiarIntentosFallidosYBloqueo();
    }

    public void establecerContrasenaDefinitiva(String nuevoHash) {
        this.hash = nuevoHash;
        this.debeCambiarContrasena = false;
    }

}//===================================================================================================================//

