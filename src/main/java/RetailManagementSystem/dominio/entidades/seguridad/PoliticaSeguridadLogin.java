package RetailManagementSystem.dominio.entidades.seguridad;

public class PoliticaSeguridadLogin {

    //ATRIBUTOS:

    private final int maxIntentos;

    private final int minutosBloqueo;

    //GETTERS Y SETTERS:

    public int getMinutosBloqueo() {
        return minutosBloqueo;
    }

    public int getMaxIntentos() {
        return maxIntentos;
    }

    //CONSTRUCTOR:

    private PoliticaSeguridadLogin(int maxIntentos, int minutosBloqueo) {
        this.maxIntentos = maxIntentos;
        this.minutosBloqueo = minutosBloqueo;
    }

    public PoliticaSeguridadLogin reconstruirDesdeBD(int maxIntentos, int minutosBloqueo){
        return new PoliticaSeguridadLogin(maxIntentos, minutosBloqueo);
    }

}//===================================================================================================================//

