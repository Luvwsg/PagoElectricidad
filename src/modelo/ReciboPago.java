package modelo;

import java.util.Date;
import modelo.Servicio;
import modelo.Servicio;
import modelo.Tarjeta;
import modelo.Tarjeta;

public class ReciboPago {

    private static int contadorFolios = 0;

    private final int folio;
    private final Date fecha;
    private final double importe;
    private final String ultimos4;
    private final String nombre;
    private final String numeroServicio;
    private final String periodo;

    public ReciboPago(Servicio servicio, DatosConsumo consumo, Tarjeta tarjeta) {
        this.folio = ++contadorFolios;
        this.fecha = new Date();
        this.importe = consumo.getImporte();
        this.ultimos4 = tarjeta.ultimos4();
        this.nombre = servicio.getTitular();
        this.numeroServicio = servicio.getNumero();
        this.periodo = consumo.getPeriodo();
    }

    public int getFolio() {
        return folio;
    }

    public Date getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public String getUltimos4() {
        return ultimos4;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNumeroServicio() {
        return numeroServicio;
    }

    public String getPeriodo() {
        return periodo;
    }
}
