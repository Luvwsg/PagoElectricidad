package modelo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import modelo.Observador;
import modelo.Observador;
import modelo.ReciboPago;
import modelo.ReciboPago;
import modelo.Servicio;
import modelo.Servicio;
import modelo.Tarjeta;
import modelo.Tarjeta;

public class ModeloPago {

    private final List<Servicio> servicios = new ArrayList<>();
    private final List<Servicio> coincidentes = new ArrayList<>();
    private final List<Observador> observadores = new ArrayList<>();
    private Servicio seleccionado;
    private ReciboPago recibo;
    private String mensaje;

    public ModeloPago() {
        cargarServicios();
    }

    public void registrarObservador(Observador obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    private void notificarObservadores() {
        for (Observador obs : new ArrayList<>(observadores)) {
            obs.actualizar();
        }
    }

    public void buscarServicios(String texto) {
        String buscado = texto == null ? "" : texto.trim();
        seleccionado = null;
        recibo = null;
        mensaje = null;
        coincidentes.clear();
        for (Servicio s : servicios) {
            if (s.coincide(buscado)) {
                coincidentes.add(s);
            }
        }
        notificarObservadores();
    }

    public void seleccionarServicio(int indice) {
        recibo = null;
        mensaje = null;
        if (indice >= 0 && indice < coincidentes.size()) {
            seleccionado = coincidentes.get(indice);
            if (!seleccionado.getConsumo().tieneAdeudo()) {
                mensaje = "El servicio no tiene adeudo pendiente.";
            }
        } else {
            seleccionado = null;
        }
        notificarObservadores();
    }

    public void pagar(String numero) {
        mensaje = null;
        if (seleccionado == null) {
            mensaje = "Seleccione un cliente primero.";
        } else if (!seleccionado.getConsumo().tieneAdeudo()) {
            mensaje = "El servicio no tiene adeudo pendiente.";
        } else {
            Tarjeta tarjeta = new Tarjeta(numero);
            if (!tarjeta.esValida()) {
                mensaje = "Número de tarjeta inválido: deben ser 16 dígitos.";
            } else {
                DatosConsumo consumo = seleccionado.getConsumo();
                consumo.marcarPagado();
                recibo = new ReciboPago(seleccionado, consumo, tarjeta);
            }
        }
        notificarObservadores();
    }

    public List<Servicio> getCoincidentes() {
        return Collections.unmodifiableList(coincidentes);
    }

    public DatosConsumo getConsumo() {
        return seleccionado == null ? null : seleccionado.getConsumo();
    }

    public ReciboPago getRecibo() {
        return recibo;
    }

    public String getMensaje() {
        return mensaje;
    }

    private void cargarServicios() {
        servicios.add(new Servicio("123456789012", "Juan Pérez Torres", "Sonora 245, Col. Centro",
                new DatosConsumo("01-30 sep 2026", 312, "1 (doméstica)", 486.50, fecha(2026, Calendar.OCTOBER, 20))));
        servicios.add(new Servicio("123498765432", "María López Ruiz", "Reforma 118, Col. Pitic",
                new DatosConsumo("01-30 sep 2026", 205, "1 (doméstica)", 298.75, fecha(2026, Calendar.OCTOBER, 20))));
        servicios.add(new Servicio("123400112233", "Carlos Ruiz Soto", "Yañez 77, Col. Modelo",
                new DatosConsumo("01-30 sep 2026", 0, "1 (doméstica)", 0.00, fecha(2026, Calendar.OCTOBER, 20))));
        servicios.add(new Servicio("567800112233", "Ana Torres Díaz", "Jalisco 560, Col. Bugambilias",
                new DatosConsumo("01-30 sep 2026", 540, "1C (doméstica)", 1120.20, fecha(2026, Calendar.OCTOBER, 20))));
        servicios.add(new Servicio("998877665544", "Luis Gómez Soto", "Colosio 902, Col. Valle Verde",
                new DatosConsumo("01-30 sep 2026", 128, "1 (doméstica)", 176.40, fecha(2026, Calendar.OCTOBER, 20))));
    }

    private static java.util.Date fecha(int anio, int mes, int dia) {
        return new GregorianCalendar(anio, mes, dia).getTime();
    }
}
