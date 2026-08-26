package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.dominio.puertos.RepositorioPermiso;
import RetailManagementSystem.infraestructura.inyeccion.ContenedorDependencias;

import java.util.List;

public class PermisosApp {

    //INICIALIZADOR:

    private static boolean inicializado = false;

    public static void inicializarYValidarSincronizacionPermisos(){
        if (inicializado) return;

        RepositorioPermiso repositorioPermiso = ContenedorDependencias.getRepositorioPermiso();

        List<String> permisosBD = repositorioPermiso.obtenerNombresTodosLosPermisos();

        List<String> permisosCodigo = List.of(
                PermisosApp.PROCESAR_VENTA,
                PermisosApp.VER_HISTORIAL_VENTAS,

                PermisosApp.VER_INVENTARIOS,
                PermisosApp.ADMINISTRAR_INVENTARIOS,
                PermisosApp.VER_PRODUCTOS,
                PermisosApp.ADMINISTRAR_PRODUCTOS,
                PermisosApp.TRASLADAR_PRODUCTOS,

                PermisosApp.VER_SERVICIOS,
                PermisosApp.REGISTRAR_SERVICIOS,
                PermisosApp.MODIFICAR_SERVICIOS,
                PermisosApp.CAMBIAR_ESTADO_SERVICIOS,

                PermisosApp.VER_IMPUESTOS,
                PermisosApp.REGISTRAR_IMPUESTOS,
                PermisosApp.MODIFICAR_IMPUESTOS,
                PermisosApp.CAMBIAR_ESTADO_IMPUESTOS,

                PermisosApp.VER_DESCUENTOS,
                PermisosApp.REGISTRAR_DESCUENTOS,
                PermisosApp.MODIFICAR_DESCUENTOS,
                PermisosApp.CAMBIAR_ESTADO_DESCUENTOS,

                PermisosApp.VER_POLITICAS_V,
                PermisosApp.REGISTRAR_POLITICAS_V,
                PermisosApp.MODIFICAR_POLITICAS_V,
                PermisosApp.CAMBIAR_ESTADO_POLITICAS_V,

                PermisosApp.EDITAR_PERFIL_DE_TIENDA,

                PermisosApp.VER_ROLES,
                PermisosApp.REGISTRAR_ROLES,
                PermisosApp.EDITAR_ROLES,
                PermisosApp.ADMINISTRAR_PERMISOS_DE_ROLES,

                PermisosApp.VER_PERMISOS,
                PermisosApp.GESTIONAR_PERMISOS,

                PermisosApp.VER_USUARIOS,
                PermisosApp.REGISTRAR_USUARIOS,
                PermisosApp.EDITAR_USUARIOS,
                PermisosApp.CAMBIAR_ESTADO_USUARIOS,
                PermisosApp.GESTIONAR_ROLES_USUARIO,
                PermisosApp.RESTABLECER_CONTRASENA_USUARIO
        );

        for (String permiso : permisosCodigo) {
            if (!permisosBD.contains(permiso)) {
                throw new IllegalStateException(
                        "Error Crítico: El Permiso -" + permiso + "- Existe en el Código " +
                                "pero NO en la Base de Datos. Por favor, Sincroniza los Nombres."
                );
            }
        }

        inicializado = true;
    }

    //PERMISOS:

    public static final String PROCESAR_VENTA = "PROCESAR VENTA";
    public static final String VER_HISTORIAL_VENTAS = "VER HISTORIAL DE VENTAS";

    public static final String VER_INVENTARIOS = "VER INVENTARIOS";
    public static final String ADMINISTRAR_INVENTARIOS = "ADMINISTRAR INVENTARIOS";
    public static final String VER_PRODUCTOS = "VER PRODUCTOS";
    public static final String ADMINISTRAR_PRODUCTOS = "ADMINISTRAR PRODUCTOS";
    public static final String TRASLADAR_PRODUCTOS = "TRASLADAR PRODUCTOS";

    public static final String VER_SERVICIOS = "VER SERVICIOS";
    public static final String REGISTRAR_SERVICIOS = "REGISTRAR SERVICIOS";
    public static final String MODIFICAR_SERVICIOS = "MODIFICAR SERVICIOS";
    public static final String CAMBIAR_ESTADO_SERVICIOS = "CAMBIAR ESTADO SERVICIOS";

    public static final String VER_IMPUESTOS = "VER IMPUESTOS";
    public static final String REGISTRAR_IMPUESTOS = "REGISTRAR IMPUESTOS";
    public static final String MODIFICAR_IMPUESTOS = "MODIFICAR IMPUESTOS";
    public static final String CAMBIAR_ESTADO_IMPUESTOS = "CAMBIAR ESTADO IMPUESTOS";

    public static final String VER_DESCUENTOS = "VER DESCUENTOS";
    public static final String REGISTRAR_DESCUENTOS = "REGISTRAR DESCUENTOS";
    public static final String MODIFICAR_DESCUENTOS = "MODIFICAR DESCUENTOS";
    public static final String CAMBIAR_ESTADO_DESCUENTOS = "CAMBIAR ESTADO DESCUENTOS";

    public static final String VER_POLITICAS_V = "VER POLÍTICAS V";
    public static final String REGISTRAR_POLITICAS_V = "REGISTRAR POLÍTICAS V";
    public static final String MODIFICAR_POLITICAS_V = "MODIFICAR POLÍTICAS V";
    public static final String CAMBIAR_ESTADO_POLITICAS_V = "CAMBIAR ESTADO POLÍTICAS V";

    public static final String EDITAR_PERFIL_DE_TIENDA = "EDITAR PERFIL DE TIENDA";

    public static final String VER_ROLES = "VER ROLES";
    public static final String EDITAR_ROLES = "EDITAR ROLES";
    public static final String REGISTRAR_ROLES = "REGISTRAR ROLES";
    public static final String ADMINISTRAR_PERMISOS_DE_ROLES = "ADMINISTRAR PERMISOS DE ROLES";

    public static final String VER_USUARIOS = "VER USUARIOS";
    public static final String REGISTRAR_USUARIOS = "REGISTRAR USUARIOS";
    public static final String EDITAR_USUARIOS = "EDITAR USUARIOS";
    public static final String CAMBIAR_ESTADO_USUARIOS = "CAMBIAR ESTADO USUARIOS";
    public static final String GESTIONAR_ROLES_USUARIO = "GESTIONAR ROLES DEL USUARIO";
    public static final String RESTABLECER_CONTRASENA_USUARIO = "RESTABLECER CONTRASEÑA USUARIO";

    public static final String VER_PERMISOS = "VER PERMISOS";
    public static final String GESTIONAR_PERMISOS = "GESTIONAR PERMISOS";



}//===================================================================================================================//

