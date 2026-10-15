package modelo;

public class Servicio {

    private final String numero;
    private final String titular;
    private final String direccion;
    private final DatosConsumo consumo;

    public Servicio(String numero, String titular, String direccion, DatosConsumo consumo) {
        this.numero = numero;
        this.titular = titular;
        this.direccion = direccion;
        this.consumo = consumo;
    }

    public boolean coincide(String texto) {
        return texto != null && !texto.isEmpty() && numero.contains(texto);
    }

    public DatosConsumo getConsumo() {
        return consumo;
    }

    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public String getDireccion() {
        return direccion;
    }
}
