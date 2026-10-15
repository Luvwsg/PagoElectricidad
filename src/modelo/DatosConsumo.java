package modelo;

import java.util.Date;

public class DatosConsumo {

    private final String periodo;
    private final int kWh;
    private final String tarifa;
    private final double importe;
    private final Date fechaLimite;
    private boolean pagado;

    public DatosConsumo(String periodo, int kWh, String tarifa, double importe, Date fechaLimite) {
        this.periodo = periodo;
        this.kWh = kWh;
        this.tarifa = tarifa;
        this.importe = importe;
        this.fechaLimite = fechaLimite;
        this.pagado = false;
    }

    public boolean tieneAdeudo() {
        return !pagado && importe > 0;
    }

    public void marcarPagado() {
        this.pagado = true;
    }

    public String getPeriodo() {
        return periodo;
    }

    public int getKWh() {
        return kWh;
    }

    public String getTarifa() {
        return tarifa;
    }

    public double getImporte() {
        return importe;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public boolean isPagado() {
        return pagado;
    }
}
