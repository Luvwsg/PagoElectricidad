package vista;

import controlador.ControladorPago;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import modelo.DatosConsumo;
import modelo.ModeloPago;
import modelo.Observador;
import modelo.ReciboPago;
import modelo.Servicio;

public class VistaPago extends JFrame implements Observador {

    private static final long serialVersionUID = 1L;

    private static final Color MORADO = new Color(0x725F99);
    private static final Color VERDE = new Color(0x166534);

    private final ControladorPago controlador;
    private final ModeloPago modelo;

    private final JTextField campoServicio = new JTextField(18);
    private final JTextField campoTarjeta = new JTextField(18);
    private final JButton botonPagar = new JButton("Pagar");
    private final JPanel panelTarjeta = new JPanel();
    private final DefaultListModel<Servicio> modeloLista = new DefaultListModel<>();
    private final JList<Servicio> lista = new JList<>(modeloLista);
    private final JPanel panelDetalle = new JPanel(new BorderLayout());
    private final JLabel etiquetaMensaje = new JLabel(" ");

    private boolean actualizando = false;

    public VistaPago(ControladorPago controlador, ModeloPago modelo) {
        super("Pago de servicio de luz");
        this.controlador = controlador;
        this.modelo = modelo;
        construirInterfaz();
        conectarEventos();
        modelo.registrarObservador(this);
        actualizar();
    }

    public void alTeclearNumero() {
        if (actualizando) {
            return;
        }
        controlador.buscar(campoServicio.getText());
    }

    public void alSeleccionar() {
        if (actualizando) {
            return;
        }
        int indice = lista.getSelectedIndex();
        if (indice >= 0) {
            controlador.seleccionar(indice);
        }
    }

    public void alPagar() {
        controlador.pagar(campoTarjeta.getText());
    }

    @Override
    public void actualizar() {
        actualizando = true;
        try {
            List<Servicio> coincidentes = modelo.getCoincidentes();
            DatosConsumo consumo = modelo.getConsumo();
            ReciboPago recibo = modelo.getRecibo();
            String mensaje = modelo.getMensaje();

            refrescarLista(coincidentes, consumo);

            boolean hayAdeudo = consumo != null && consumo.tieneAdeudo();
            boolean mostrarTarjeta = recibo != null || hayAdeudo;
            panelTarjeta.setVisible(mostrarTarjeta);
            if (!mostrarTarjeta && !campoTarjeta.getText().isEmpty()) {
                campoTarjeta.setText("");
            }
            actualizarBotonPagar();

            if (recibo != null) {
                mostrarRecibo(recibo);
            } else if (consumo != null) {
                mostrarConsumo(consumo, lista.getSelectedValue());
            } else {
                mostrarVacio();
            }
            etiquetaMensaje.setText(mensaje == null ? " " : mensaje);
        } finally {
            actualizando = false;
        }
    }

    private void refrescarLista(List<Servicio> coincidentes, DatosConsumo consumo) {
        List<Servicio> actuales = new ArrayList<>();
        for (int i = 0; i < modeloLista.size(); i++) {
            actuales.add(modeloLista.get(i));
        }
        Servicio previo = lista.getSelectedValue();
        if (!actuales.equals(coincidentes)) {
            modeloLista.clear();
            for (Servicio s : coincidentes) {
                modeloLista.addElement(s);
            }
        }
        if (consumo == null) {
            lista.clearSelection();
        } else if (previo != null && coincidentes.contains(previo)) {
            lista.setSelectedValue(previo, true);
        }
    }

    private void actualizarBotonPagar() {
        DatosConsumo consumo = modelo.getConsumo();
        boolean puedePagar = modelo.getRecibo() == null
                && consumo != null
                && consumo.tieneAdeudo()
                && !campoTarjeta.getText().trim().isEmpty();
        botonPagar.setEnabled(puedePagar);
    }

    private void mostrarVacio() {
        JLabel vacio = new JLabel("Los datos de consumo aparecerán aquí.", JLabel.CENTER);
        vacio.setForeground(Color.GRAY);
        colocarDetalle(null, vacio, new Color(0xF9FAFB), Color.DARK_GRAY);
    }

    private void mostrarConsumo(DatosConsumo c, Servicio s) {
        SimpleDateFormat limite = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "MX"));
        String[][] pares = {
            {"Nombre", s == null ? "" : s.getTitular()},
            {"Servicio", s == null ? "" : s.getNumero()},
            {"Dirección", s == null ? "" : s.getDireccion()},
            {"Periodo", c.getPeriodo()},
            {"Consumo", c.getKWh() + " kWh"},
            {"Tarifa", c.getTarifa()},
            {"Importe", dinero(c.getImporte())},
            {"Fecha límite", limite.format(c.getFechaLimite())}
        };
        colocarDetalle("Datos de consumo", cuadricula(pares), new Color(0xF9FAFB), MORADO);
    }

    private void mostrarRecibo(ReciboPago r) {
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String[][] pares = {
            {"Folio", String.format("%06d", r.getFolio())},
            {"Fecha", fecha.format(r.getFecha())},
            {"Nombre", r.getNombre()},
            {"Servicio", r.getNumeroServicio()},
            {"Periodo", r.getPeriodo()},
            {"Tarjeta", "**** **** **** " + r.getUltimos4()},
            {"Importe pagado", dinero(r.getImporte())},
            {"Estado", "PAGADO"}
        };
        colocarDetalle("Recibo de pago", cuadricula(pares), new Color(0xECFDF5), VERDE);
    }

    private JPanel cuadricula(String[][] pares) {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 6));
        panel.setOpaque(false);
        for (String[] par : pares) {
            JLabel etiqueta = new JLabel(par[0]);
            etiqueta.setForeground(Color.GRAY);
            JLabel valor = new JLabel(par[1]);
            valor.setFont(valor.getFont().deriveFont(Font.BOLD));
            panel.add(etiqueta);
            panel.add(valor);
        }
        return panel;
    }

    private void colocarDetalle(String titulo, java.awt.Component contenido, Color fondo, Color colorTitulo) {
        panelDetalle.removeAll();
        panelDetalle.setBackground(fondo);
        panelDetalle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1D5DB)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        if (titulo != null) {
            JLabel encabezado = new JLabel(titulo);
            encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 14f));
            encabezado.setForeground(colorTitulo);
            encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            panelDetalle.add(encabezado, BorderLayout.NORTH);
        }
        panelDetalle.add(contenido, BorderLayout.CENTER);
        panelDetalle.revalidate();
        panelDetalle.repaint();
    }

    private static String dinero(double importe) {
        return String.format(Locale.US, "$%,.2f", importe);
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("  Pago de servicio de luz");
        titulo.setOpaque(true);
        titulo.setBackground(MORADO);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 15f));
        titulo.setPreferredSize(new Dimension(10, 36));

        JPanel izquierda = new JPanel();
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));
        izquierda.add(etiqueta("Número de servicio"));
        izquierda.add(fijarAltura(campoServicio));
        izquierda.add(Box.createVerticalStrut(14));

        panelTarjeta.setLayout(new BoxLayout(panelTarjeta, BoxLayout.Y_AXIS));
        panelTarjeta.setAlignmentX(LEFT_ALIGNMENT);
        panelTarjeta.add(etiqueta("Tarjeta de pago"));
        panelTarjeta.add(fijarAltura(campoTarjeta));
        panelTarjeta.add(Box.createVerticalStrut(8));
        botonPagar.setAlignmentX(LEFT_ALIGNMENT);
        panelTarjeta.add(botonPagar);
        panelTarjeta.setVisible(false);
        izquierda.add(panelTarjeta);
        izquierda.add(Box.createVerticalGlue());

        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> l, Object valor,
                    int indice, boolean seleccionado, boolean foco) {
                Servicio s = (Servicio) valor;
                return super.getListCellRendererComponent(l,
                        "<html><b>" + s.getTitular() + "</b><br><small>" + s.getNumero() + "</small></html>",
                        indice, seleccionado, foco);
            }
        });
        JPanel derecha = new JPanel(new BorderLayout(0, 4));
        derecha.add(etiqueta("Clientes que coinciden"), BorderLayout.NORTH);
        derecha.add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel arriba = new JPanel(new GridLayout(1, 2, 16, 0));
        arriba.add(izquierda);
        arriba.add(derecha);

        etiquetaMensaje.setForeground(new Color(0xB91C1C));
        etiquetaMensaje.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 0));
        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(panelDetalle, BorderLayout.CENTER);
        abajo.add(etiquetaMensaje, BorderLayout.SOUTH);
        abajo.setPreferredSize(new Dimension(10, 190));

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        contenido.add(arriba, BorderLayout.CENTER);
        contenido.add(abajo, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(titulo, BorderLayout.NORTH);
        add(contenido, BorderLayout.CENTER);
        setSize(660, 540);
        setLocationRelativeTo(null);
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private static JTextField fijarAltura(JTextField campo) {
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, campo.getPreferredSize().height + 6));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        return campo;
    }

    private void conectarEventos() {
        campoServicio.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                alTeclearNumero();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                alTeclearNumero();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                alTeclearNumero();
            }
        });
        campoTarjeta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarBotonPagar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarBotonPagar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarBotonPagar();
            }
        });
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                alSeleccionar();
            }
        });
        botonPagar.addActionListener(e -> alPagar());
    }
}
