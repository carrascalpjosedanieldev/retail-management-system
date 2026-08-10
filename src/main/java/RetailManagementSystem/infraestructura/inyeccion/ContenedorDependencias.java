package RetailManagementSystem.infraestructura.inyeccion;

import RetailManagementSystem.aplicacion.ensambladores.*;
import RetailManagementSystem.aplicacion.orquestadores.*;
import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;
import RetailManagementSystem.aplicacion.puertos.ProveedorConfiguracion;
import RetailManagementSystem.aplicacion.servicios.*;
import RetailManagementSystem.dominio.puertos.*;
import RetailManagementSystem.infraestructura.configuracion.ProveedorConfiguracionImpl;
import RetailManagementSystem.infraestructura.persistencia.mysql.*;
import RetailManagementSystem.infraestructura.seguridad.Argon2CodificadorAdapter;

public class ContenedorDependencias {

    //DEPENDENCIAS:

        //ENSAMBLADORES:

    private static EnsambladorDTOCarrito ensambladorDTOCarrito;
    private static EnsambladorDTODescuento ensambladorDTODescuento;
    private static EnsambladorDTOFactura ensambladorDTOFactura;
    private static EnsambladorDTOImpuesto ensambladorDTOImpuesto;
    private static EnsambladorDTOInventario ensambladorDTOInventario;
    private static EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;
    private static EnsambladorDTOProducto ensambladorDTOProducto;
    private static EnsambladorDTOServicio ensambladorDTOServicio;
    private static EnsambladorDTOPermiso ensambladorDTOPermiso;
    private static EnsambladorDTORol ensambladorDTORol;
    private static EnsambladorDTOUsuario ensambladorDTOUsuario;

        //REPOSITORIOS:

    private static RepositorioConfiguracion repositorioConfiguracion;
    private static RepositorioDescuentos repositorioDescuentos;
    private static RepositorioFacturas repositorioFacturas;
    private static RepositorioImpuestos repositorioImpuestos;
    private static RepositorioInventario repositorioInventario;
    private static RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;
    private static RepositorioProducto repositorioProducto;
    private static RepositorioServicio repositorioServicio;
    private static RepositorioPermiso repositorioPermiso;
    private static RepositorioRol repositorioRol;
    private static RepositorioUsuario repositorioUsuario;

        //UTILIDADES:

    private static CodificadorContrasenas codificadorContrasenas;
    private static ProveedorConfiguracion proveedorConfiguracion;

        //SERVICIOS:

    private static ServicioCarrito servicioCarrito;
    private static ServicioConfiguraciones servicioConfiguraciones;
    private static ServicioDescuentos servicioDescuentos;
    private static ServicioFacturas servicioFacturas;
    private static ServicioImpuestos servicioImpuestos;
    private static ServicioInventario servicioInventario;
    private static ServicioPoliticaVencimiento servicioPoliticaVencimiento;
    private static ServicioProductos servicioProductos;
    private static ServicioServicios servicioServicios;
    private static ServicioPermiso servicioPermiso;
    private static ServicioRol servicioRol;
    private static ServicioUsuario servicioUsuario;

        //ORQUESTADORES:

    private static OrquestadorLogin orquestadorLogin;
    private static OrquestadorPermisos orquestadorPermisos;
    private static OrquestadorProductoInventario orquestadorProductoInventario;
    private static OrquestadorRoles orquestadorRoles;
    private static OrquestadorVentas orquestadorVentas;

    //BANDERA SEGURIDAD:

    private static boolean inicializado = false;

    //INICIALIZADOR:

    public static void inicializar() {
        if (inicializado) return;

        //INSTANCIACIÓN DE ENSAMBLADORES:

        ensambladorDTOCarrito = new EnsambladorDTOCarrito();
        ensambladorDTODescuento = new EnsambladorDTODescuento();
        ensambladorDTOFactura = new EnsambladorDTOFactura();
        ensambladorDTOImpuesto = new EnsambladorDTOImpuesto();
        ensambladorDTOPoliticaVencimiento = new EnsambladorDTOPoliticaVencimiento();
        ensambladorDTOProducto = new EnsambladorDTOProducto(
                ensambladorDTOImpuesto, ensambladorDTODescuento, ensambladorDTOPoliticaVencimiento
        );
        ensambladorDTOInventario = new EnsambladorDTOInventario(ensambladorDTOProducto);
        ensambladorDTOServicio = new EnsambladorDTOServicio(
                ensambladorDTOImpuesto, ensambladorDTODescuento
        );
        ensambladorDTOPermiso = new EnsambladorDTOPermiso();
        ensambladorDTORol = new EnsambladorDTORol(ensambladorDTOPermiso);
        ensambladorDTOUsuario = new EnsambladorDTOUsuario();

        //INSTANCIACIÓN DE REPOSITORIOS:

        repositorioConfiguracion = new RepositorioConfiguracionMySQL();
        repositorioDescuentos = new RepositorioDescuentosMySQL();
        repositorioFacturas = new RepositorioFacturasMySQL();
        repositorioImpuestos = new RepositorioImpuestosMySQL();
        repositorioInventario = new RepositorioInventarioMySQL();
        repositorioPoliticaVencimiento = new RepositorioPoliticaVencimientoMySQL();
        repositorioProducto = new RepositorioProductoMySQL();
        repositorioServicio = new RepositorioServicioMySQL();
        repositorioPermiso = new RepositorioPermisoMySQL();
        repositorioRol = new RepositorioRolMySQL();
        repositorioUsuario = new RepositorioUsuarioMySQL();

        //INSTANCIACIÓN DE UTILIDADES:

        codificadorContrasenas = new Argon2CodificadorAdapter();
        proveedorConfiguracion = new ProveedorConfiguracionImpl(repositorioConfiguracion);

        //INSTANCIACIÓN DE SERVICIOS:

        servicioProductos = new ServicioProductos(
                repositorioProducto, repositorioImpuestos, repositorioDescuentos, repositorioPoliticaVencimiento
        );
        servicioServicios = new ServicioServicios(
                repositorioImpuestos, repositorioDescuentos, repositorioServicio
        );
        servicioCarrito = new ServicioCarrito(servicioProductos, servicioServicios);
        servicioConfiguraciones = new ServicioConfiguraciones(repositorioConfiguracion);
        servicioDescuentos = new ServicioDescuentos(repositorioDescuentos);
        servicioFacturas = new ServicioFacturas(repositorioFacturas);
        servicioImpuestos = new ServicioImpuestos(repositorioImpuestos);
        servicioInventario = new ServicioInventario(repositorioInventario);
        servicioPoliticaVencimiento = new ServicioPoliticaVencimiento(repositorioPoliticaVencimiento);
        servicioPermiso = new ServicioPermiso(repositorioPermiso);
        servicioRol = new ServicioRol(repositorioRol);
        servicioUsuario = new ServicioUsuario(repositorioUsuario, codificadorContrasenas, proveedorConfiguracion);

        //INSTANCIACIÓN DE ORQUESTADORES:

        orquestadorLogin = new OrquestadorLogin(servicioUsuario, ensambladorDTOUsuario);
        orquestadorPermisos = new OrquestadorPermisos(ensambladorDTOPermiso, servicioPermiso);
        orquestadorProductoInventario = new OrquestadorProductoInventario(servicioProductos, servicioInventario);
        orquestadorRoles = new OrquestadorRoles(servicioRol, ensambladorDTORol);
        orquestadorVentas = new OrquestadorVentas(
                servicioFacturas, servicioCarrito, servicioProductos, servicioServicios,
                ensambladorDTOFactura, ensambladorDTOCarrito
                );

        inicializado = true;
    }

    //GETTERS DE DEPENDENCIAS:

    private static void validarInicializado(){
        if (!inicializado) {
            throw new IllegalStateException("El Contenedor NO ha sido Inicializado.");
        }
    }

    public static EnsambladorDTOCarrito getEnsambladorDTOCarrito() {
        validarInicializado();
        return ensambladorDTOCarrito;
    }

    public static EnsambladorDTODescuento getEnsambladorDTODescuento() {
        validarInicializado();
        return ensambladorDTODescuento;
    }

    public static EnsambladorDTOFactura getEnsambladorDTOFactura() {
        validarInicializado();
        return ensambladorDTOFactura;
    }

    public static EnsambladorDTOImpuesto getEnsambladorDTOImpuesto() {
        validarInicializado();
        return ensambladorDTOImpuesto;
    }

    public static EnsambladorDTOInventario getEnsambladorDTOInventario() {
        validarInicializado();
        return ensambladorDTOInventario;
    }

    public static EnsambladorDTOPoliticaVencimiento getEnsambladorDTOPoliticaVencimiento() {
        validarInicializado();
        return ensambladorDTOPoliticaVencimiento;
    }

    public static EnsambladorDTOProducto getEnsambladorDTOProducto() {
        validarInicializado();
        return ensambladorDTOProducto;
    }

    public static EnsambladorDTOServicio getEnsambladorDTOServicio() {
        validarInicializado();
        return ensambladorDTOServicio;
    }

    public static EnsambladorDTOPermiso getEnsambladorDTOPermiso() {
        return ensambladorDTOPermiso;
    }

    public static EnsambladorDTORol getEnsambladorDTORol() {
        return ensambladorDTORol;
    }

    public static EnsambladorDTOUsuario getEnsambladorDTOUsuario() {
        validarInicializado();
        return ensambladorDTOUsuario;
    }

    public static RepositorioConfiguracion getRepositorioConfiguracion() {
        validarInicializado();
        return repositorioConfiguracion;
    }

    public static RepositorioDescuentos getRepositorioDescuentos() {
        validarInicializado();
        return repositorioDescuentos;
    }

    public static RepositorioFacturas getRepositorioFacturas() {
        validarInicializado();
        return repositorioFacturas;
    }

    public static RepositorioImpuestos getRepositorioImpuestos() {
        validarInicializado();
        return repositorioImpuestos;
    }

    public static RepositorioInventario getRepositorioInventario() {
        validarInicializado();
        return repositorioInventario;
    }

    public static RepositorioPoliticaVencimiento getRepositorioPoliticaVencimiento() {
        validarInicializado();
        return repositorioPoliticaVencimiento;
    }

    public static RepositorioProducto getRepositorioProducto() {
        validarInicializado();
        return repositorioProducto;
    }

    public static RepositorioServicio getRepositorioServicio() {
        validarInicializado();
        return repositorioServicio;
    }

    public static RepositorioPermiso getRepositorioPermiso() {
        return repositorioPermiso;
    }

    public static RepositorioRol getRepositorioRol() {
        return repositorioRol;
    }

    public static RepositorioUsuario getRepositorioUsuario() {
        validarInicializado();
        return repositorioUsuario;
    }

    public static CodificadorContrasenas getCodificadorContrasenas() {
        validarInicializado();
        return codificadorContrasenas;
    }

    public static ProveedorConfiguracion getProveedorConfiguracion() {
        validarInicializado();
        return proveedorConfiguracion;
    }

    public static ServicioCarrito getServicioCarrito() {
        validarInicializado();
        return servicioCarrito;
    }

    public static ServicioConfiguraciones getServicioConfiguraciones() {
        validarInicializado();
        return servicioConfiguraciones;
    }

    public static ServicioDescuentos getServicioDescuentos() {
        validarInicializado();
        return servicioDescuentos;
    }

    public static ServicioFacturas getServicioFacturas() {
        validarInicializado();
        return servicioFacturas;
    }

    public static ServicioImpuestos getServicioImpuestos() {
        validarInicializado();
        return servicioImpuestos;
    }

    public static ServicioInventario getServicioInventario() {
        validarInicializado();
        return servicioInventario;
    }

    public static ServicioPoliticaVencimiento getServicioPoliticaVencimiento() {
        validarInicializado();
        return servicioPoliticaVencimiento;
    }

    public static ServicioProductos getServicioProductos() {
        validarInicializado();
        return servicioProductos;
    }

    public static ServicioServicios getServicioServicios() {
        validarInicializado();
        return servicioServicios;
    }

    public static ServicioPermiso getServicioPermiso() {
        return servicioPermiso;
    }

    public static ServicioRol getServicioRol() {
        return servicioRol;
    }

    public static ServicioUsuario getServicioUsuario() {
        validarInicializado();
        return servicioUsuario;
    }

    public static OrquestadorLogin getOrquestadorLogin() {
        return orquestadorLogin;
    }

    public static OrquestadorPermisos getOrquestadorPermisos() {
        return orquestadorPermisos;
    }

    public static OrquestadorProductoInventario getOrquestadorProductoInventario() {
        validarInicializado();
        return orquestadorProductoInventario;
    }

    public static OrquestadorRoles getOrquestadorRoles() {
        return orquestadorRoles;
    }

    public static OrquestadorVentas getOrquestadorVentas() {
        validarInicializado();
        return orquestadorVentas;
    }

}//===================================================================================================================//

