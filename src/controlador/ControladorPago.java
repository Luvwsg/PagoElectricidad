package controlador;

import modelo.ModeloPago;

public class ControladorPago {

    private final ModeloPago modelo;

    public ControladorPago(ModeloPago modelo) {
        this.modelo = modelo;
    }

    public void buscar(String texto) {
        modelo.buscarServicios(texto);
    }

    public void seleccionar(int indice) {
        modelo.seleccionarServicio(indice);
    }

    public void pagar(String numero) {
        modelo.pagar(numero);
    }
}
