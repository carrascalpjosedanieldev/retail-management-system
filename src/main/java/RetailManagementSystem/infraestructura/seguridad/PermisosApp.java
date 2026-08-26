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
                PermisosApp.ADMINISTRAR_SERVICIOS,
                PermisosApp.ADMINISTRAR_IMPUESTOS,
                PermisosApp.ADMINISTRAR_DESCUENTOS,
                PermisosApp.POLITICAS_DE_VENCIMIENTO,
                PermisosApp.EDITAR_PERFIL_DE_TIENDA,
                PermisosApp.EDITAR_ROLES,
                PermisosApp.GESTIONAR_PERMISOS,
                PermisosApp.GESTIONAR_USUARIOS,
                PermisosApp.VER_PERMISOS
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

    public static final String ADMINISTRAR_SERVICIOS = "ADMINISTRAR SERVICIOS";

    public static final String ADMINISTRAR_IMPUESTOS = "ADMINISTRAR IMPUESTOS";

    public static final String ADMINISTRAR_DESCUENTOS = "ADMINISTRAR DESCUENTOS";

    public static final String POLITICAS_DE_VENCIMIENTO = "POLÍTICAS DE VENCIMIENTO";

    public static final String EDITAR_PERFIL_DE_TIENDA = "EDITAR PERFIL DE TIENDA";

    public static final String EDITAR_ROLES = "EDITAR ROLES";

    public static final String GESTIONAR_PERMISOS = "GESTIONAR PERMISOS";

    public static final String GESTIONAR_USUARIOS = "GESTIONAR USUARIOS";

    public static final String VER_PERMISOS = "VER PERMISOS";

    public static final String VER_ROLES = "VER ROLES";

    public static final String REGISTRAR_ROLES = "REGISTRAR ROLES";

    public static final String ADMINISTRAR_PERMISOS_DE_ROLES = "ADMINISTRAR_PERMISOS_DE_ROLES";

}//===================================================================================================================//

