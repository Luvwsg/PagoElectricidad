package modelo;

public class Tarjeta {

    private final String numero;

    public Tarjeta(String numero) {
        this.numero = numero == null ? "" : numero;
    }
    
    public boolean esValida() {
        return soloDigitos().matches("\\d{16}");
    }

    public String ultimos4() {
        String digitos = soloDigitos();
        return esValida() ? digitos.substring(digitos.length() - 4) : "";
    }

    private String soloDigitos() {
        return numero.replaceAll("[\\s-]", "");
    }

    public String getNumero() {
        return numero;
    }
}
