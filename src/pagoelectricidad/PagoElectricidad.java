package pagoelectricidad;

import controlador.ControladorPago;
import javax.swing.SwingUtilities;
import modelo.ModeloPago;
import vista.VistaPago;

public class PagoElectricidad {

    public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
            ModeloPago modelo = new ModeloPago();
            ControladorPago controlador = new ControladorPago(modelo);
            VistaPago vista = new VistaPago(controlador, modelo);
            vista.setVisible(true);
        });
        
    }
    
}
