package RetailManagementSystem.dominio.enums;

public enum ClaveConfiguracion {

    DATOS_TIENDA("NOMBRE_PROYECTO_PROPIO_ORIGINAL"),

    MAX_INTENTOS_LOGIN("SEGURIDAD_MAX_INTENTOS"),

    MINUTOS_BLOQUEO("SEGURIDAD_MINUTOS_BLOQUEO");

    private final String claveBD;

    ClaveConfiguracion(String claveBD) {
        this.claveBD = claveBD;
    }

    public String getClaveBD() {
        return claveBD;
    }

}//===================================================================================================================//
