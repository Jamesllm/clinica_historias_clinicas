package vista;

// Clases java
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

// Conexion a la base de datos
import conexion.Conexion;

// Clases
import clases.Genero;
import clases.Usuario;
import clases.Paciente;
import clases.ConsultaMedica;
import clases.ComprobantePago;
import estructuras.ArbolAVLPaciente;

// Estructuras
import estructuras.ArregloGenero;
import estructuras.ListaPaciente;
import estructuras.ColaDinamicaPaciente;
import estructuras.ListaConsultaMedica;
import estructuras.PilaDinamicaComprobante;
import estructuras.ArbolABBPaciente;
import javax.swing.ImageIcon;
import javax.swing.JTable;

public class Aplicacion extends javax.swing.JFrame {

    private Conexion conexionDB;
    private ArregloGenero arregloGenero;
    private ListaPaciente listaPaciente;
    private Usuario usuarioActual;
    private ListaConsultaMedica listaConsultaMedica;
    private ColaDinamicaPaciente colaPacientes;
    private PilaDinamicaComprobante pilaComprobantes;
    private ArbolABBPaciente arbolBBPacientes;
    private ArbolAVLPaciente arbolAVLPacientes;

    public Aplicacion(Conexion conexionDB, Usuario usuarioActual) {
        this(conexionDB);
        this.usuarioActual = usuarioActual;

        // Establecer el usuario logueado
        txtUsuarioLogueado.setText(usuarioActual.getNombreCompleto());
        txtUsuarioLogueado_especialidad.setText(usuarioActual.getEspecialidad().getNombre());
    }

    public Aplicacion(Conexion conexionDB) {
        initComponents();
        this.setLocationRelativeTo(null);

        // Establecer el icono a la aplicacion
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/icono.png")).getImage());

        this.conexionDB = conexionDB;

        // Inicializar estructuras de datos
        arregloGenero = new ArregloGenero();
        listaPaciente = new ListaPaciente();
        listaConsultaMedica = new ListaConsultaMedica();
        colaPacientes = new ColaDinamicaPaciente();
        pilaComprobantes = new PilaDinamicaComprobante();
        arbolBBPacientes = new ArbolABBPaciente();
        arbolAVLPacientes = new ArbolAVLPaciente();

        // Cargar datos desde la BD
        colaPacientes.cargarDesdeBD();
        pilaComprobantes.cargarDesdeBD();

        cargarGenerosEnCombo();
        cargarPacientesEnTabla();
        cargarConsultasEnTabla();
        cargarArbolBBPacientes();
        cargarArbolVLPacientes();
        llenarTablaComprobantes(pilaComprobantes, tablaComprobantes);

        // Agregar listener para cerrar la conexión cuando se cierre la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarConexion();
                System.exit(0);
            }
        });

        // Listener para seleccionar fila de la tabla y cargar datos en los inputs
        tablaPacientes.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting() && tablaPacientes.getSelectedRow() != -1) {
                    int row = tablaPacientes.getSelectedRow();
                    txtDNI.setText(tablaPacientes.getValueAt(row, 0).toString());
                    txtNombre.setText(tablaPacientes.getValueAt(row, 1).toString());
                    txtApellidoPaterno.setText(tablaPacientes.getValueAt(row, 2).toString());
                    txtApellidoMaterno.setText(tablaPacientes.getValueAt(row, 3).toString());
                    // Cambiar para que el JDateChooser reciba un Date
                    try {
                        Object fechaObj = tablaPacientes.getValueAt(row, 4);
                        if (fechaObj != null) {
                            java.util.Date fecha = null;
                            if (fechaObj instanceof java.util.Date) {
                                fecha = (java.util.Date) fechaObj;
                            } else {
                                // Intentar parsear si es String
                                fecha = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fechaObj.toString());
                            }
                            txtFechaNacimiento.setDate(fecha);
                        } else {
                            txtFechaNacimiento.setDate(null);
                        }
                    } catch (Exception e) {
                        txtFechaNacimiento.setDate(null);
                    }

                    // Seleccionar género en el combo
                    String generoTabla = tablaPacientes.getValueAt(row, 5).toString();
                    for (int i = 0; i < jcbxGenero.getItemCount(); i++) {
                        Genero generoCombo = (Genero) jcbxGenero.getItemAt(i);
                        if (generoCombo.getNombre().equals(generoTabla)) {
                            jcbxGenero.setSelectedIndex(i);
                            break;
                        }
                    }
                    txtDireccion.setText(tablaPacientes.getValueAt(row, 6).toString());
                    txtTelefono.setText(tablaPacientes.getValueAt(row, 7).toString());
                }
            }
        });

        // Listener para seleccionar fila de la tabla de consultas y cargar datos en los inputs de consulta
        tablaConsultas.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting() && tablaConsultas.getSelectedRow() != -1) {
                    int row = tablaConsultas.getSelectedRow();

                    // Diagnóstico y tratamiento
                    jta_diagnostico.setText(tablaConsultas.getValueAt(row, 3).toString());
                    jta_tratamiento.setText(tablaConsultas.getValueAt(row, 4).toString());

                    // Paciente (combo) - buscar por DNI
                    String pacienteCombo = tablaConsultas.getValueAt(row, 1).toString();
                    String dniTabla = pacienteCombo.split(" - ")[0].trim();
                    for (int i = 0; i < jcbxPaciente.getItemCount(); i++) {
                        String item = jcbxPaciente.getItemAt(i);
                        String dniCombo = item.split(" - ")[0].trim();
                        if (dniCombo.equals(dniTabla)) {
                            jcbxPaciente.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        // Modelo de la tabla
        tablaColaPacientes.setModel(new DefaultTableModel(
                new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno"}, 0
        ));

        // Refresca la vista
        jPanel_Atender.revalidate();
        jPanel_Atender.repaint();
        actualizarTablaColaPacientes();
        actualizarLabelsAtencion();
    }

    private void cargarGenerosEnCombo() {
        arregloGenero.cargarDesdeBD();
        jcbxGenero.removeAllItems();

        for (int i = 0; i < arregloGenero.getCount(); i++) {
            Genero genero = arregloGenero.getGeneros()[i];
            if (genero.isEstado()) {
                jcbxGenero.addItem(genero);
            }
        }
    }

    private void cargarPacientesEnCombo() {
        jcbxPaciente.removeAllItems();
        listaPaciente.cargarDesdeBD();
        ListaPaciente.NodoPaciente actual = listaPaciente.getCabeza();

        while (actual != null) {
            Paciente p = actual.paciente;
            String nombreCompleto = p.getDni() + " - " + p.getNombre() + " " + p.getApellidoPaterno() + " " + p.getApellidoMaterno();
            jcbxPaciente.addItem(nombreCompleto);
            actual = actual.siguiente;
        }
    }

    private void cargarPacientesEnTabla() {
        listaPaciente.cargarDesdeBD();
        DefaultTableModel modeloTablaPaciente = new DefaultTableModel(
                new Object[]{"DNI", "Nombre", "Apellido P.", "Apellido M.", "Fecha N.", "Género", "Dirección", "Teléfono", "Fecha E.", "Fecha S."}, 0
        );
        ListaPaciente.NodoPaciente actual = listaPaciente.getCabeza();
        while (actual != null) {
            Paciente p = actual.paciente;
            modeloTablaPaciente.addRow(new Object[]{
                p.getDni(),
                p.getNombre(),
                p.getApellidoPaterno(),
                p.getApellidoMaterno(),
                p.getFechaNacimiento(),
                p.getGenero(),
                p.getDireccion(),
                p.getTelefono(),
                p.getFechaEntrada(),
                p.getFechaSalida()
            });
            actual = actual.siguiente;
        }
        tablaPacientes.setModel(modeloTablaPaciente);
        cargarPacientesEnCombo();
    }

    private void cargarConsultasEnTabla() {
        listaConsultaMedica.cargarDesdeBD();

        DefaultTableModel modeloTablaConsulta = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "Usuario", "Diagnóstico", "Tratamiento", "Fecha Registro"}, 0
        );
        ListaConsultaMedica.NodoConsulta actual = listaConsultaMedica.cabeza;

        while (actual != null) {
            ConsultaMedica consulta = actual.consulta;

            Paciente paciente = consulta.getPaciente();
            Usuario usuario = consulta.getUsuario();

            String nombrePaciente = String.format("%s - %s %s", paciente.getDni(), paciente.getNombre(), paciente.getApellidoPaterno());
            String nombreUsuario = String.format("%s %s", usuario.getNombre(), usuario.getApellidoPaterno());

            modeloTablaConsulta.addRow(new Object[]{
                consulta.getIdConsultaMedica(),
                nombrePaciente,
                nombreUsuario,
                consulta.getDiagnostico(),
                consulta.getTratamiento(),
                consulta.getFechaRegistro()
            });
            actual = actual.siguiente;
        }
        tablaConsultas.setModel(modeloTablaConsulta);
    }

    private void cargarArbolBBPacientes() {
        // Cargar pacientes desde la BD a la lista
        listaPaciente.cargarDesdeBD();
        // Cargar la lista en el árbol ABB
        arbolBBPacientes.cargarDesdeLista(listaPaciente);
    }

    private void cargarArbolVLPacientes() {
        // Cargar pacientes desde la BD a la lista
        listaPaciente.cargarDesdeBD();
        // Cargar la lista en el árbol ABB
        arbolAVLPacientes.cargarDesdeLista(listaPaciente);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jPanel4 = new javax.swing.JPanel();
        lblCambio = new javax.swing.JLabel();
        PanelTab = new javax.swing.JTabbedPane();
        jPanel_Atender = new javax.swing.JPanel();
        btnAtenderPaciente = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tablaColaPacientes = new javax.swing.JTable();
        lbl_atender_ahora = new javax.swing.JLabel();
        lbl_atender_ahora1 = new javax.swing.JLabel();
        lbl_atender_ahora2 = new javax.swing.JLabel();
        lbl_atender_siguiente = new javax.swing.JLabel();
        jPanel_Paciente = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        txtDNI = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        txtApellidoPaterno = new javax.swing.JTextField();
        Genero = new javax.swing.JLabel();
        jcbxGenero = new javax.swing.JComboBox<>();
        lbl4 = new javax.swing.JLabel();
        lbl5 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        Genero1 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        btnGuardarPaciente = new javax.swing.JButton();
        btnActualizarPaciente = new javax.swing.JButton();
        txtApellidoMaterno = new javax.swing.JTextField();
        lbl6 = new javax.swing.JLabel();
        txtFechaNacimiento = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaPacientes = new javax.swing.JTable();
        jPanel_Consultas = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        lbl7 = new javax.swing.JLabel();
        lbl8 = new javax.swing.JLabel();
        Genero2 = new javax.swing.JLabel();
        jcbxPaciente = new javax.swing.JComboBox<>();
        btnGuardarConsulta = new javax.swing.JButton();
        btnActualizarConsulta = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jta_tratamiento = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jta_diagnostico = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaConsultas = new javax.swing.JTable();
        jPanel_Comprobantes = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        Genero3 = new javax.swing.JLabel();
        btnVerUltimoComprobante = new javax.swing.JButton();
        btnMostrarComprobantes = new javax.swing.JButton();
        btnDeshacerComprobante = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaComprobantes = new javax.swing.JTable();
        jPanel_BuscarPaciente_ABB = new javax.swing.JPanel();
        Genero4 = new javax.swing.JLabel();
        ABB_InputBuscar = new javax.swing.JTextField();
        ABB_BuscarPaciente = new javax.swing.JToggleButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        ABB_Tabla = new javax.swing.JTable();
        ABB_MostrarTodos = new javax.swing.JButton();
        jPanel_BuscarPaciente_AVL = new javax.swing.JPanel();
        Genero5 = new javax.swing.JLabel();
        AVL_InputBuscar = new javax.swing.JTextField();
        AVL_BuscarPaciente = new javax.swing.JToggleButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        AVL_Tabla = new javax.swing.JTable();
        AVL_MostrarTodos = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnAtender = new javax.swing.JButton();
        btnPacientes = new javax.swing.JButton();
        btnConsultas = new javax.swing.JButton();
        btnConsultas1 = new javax.swing.JButton();
        btnConsultas2 = new javax.swing.JButton();
        btnConsultas3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtUsuarioLogueado_especialidad = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtUsuarioLogueado = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MI_CerrarSesion = new javax.swing.JMenuItem();

        jMenu2.setText("File");
        jMenuBar2.add(jMenu2);

        jMenu3.setText("Edit");
        jMenuBar2.add(jMenu3);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Historias Clinicas");
        setBackground(new java.awt.Color(255, 255, 255));
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(27, 55, 79));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCambio.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCambio.setForeground(new java.awt.Color(255, 255, 255));
        lblCambio.setText("Pacientes");
        jPanel4.add(lblCambio, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 1210, 50));

        jPanel_Atender.setBackground(new java.awt.Color(255, 255, 255));

        btnAtenderPaciente.setBackground(new java.awt.Color(27, 55, 79));
        btnAtenderPaciente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAtenderPaciente.setForeground(new java.awt.Color(255, 255, 255));
        btnAtenderPaciente.setText("Atender paciente");
        btnAtenderPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtenderPacienteActionPerformed(evt);
            }
        });

        tablaColaPacientes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaColaPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(tablaColaPacientes);

        lbl_atender_ahora.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbl_atender_ahora.setText("jLabel1");

        lbl_atender_ahora1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_atender_ahora1.setText("Siguiente");

        lbl_atender_ahora2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_atender_ahora2.setText("Atender ahora");

        lbl_atender_siguiente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbl_atender_siguiente.setText("jLabel1");

        javax.swing.GroupLayout jPanel_AtenderLayout = new javax.swing.GroupLayout(jPanel_Atender);
        jPanel_Atender.setLayout(jPanel_AtenderLayout);
        jPanel_AtenderLayout.setHorizontalGroup(
            jPanel_AtenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_AtenderLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel_AtenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_atender_ahora)
                    .addComponent(lbl_atender_ahora1)
                    .addComponent(lbl_atender_siguiente)
                    .addComponent(lbl_atender_ahora2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 329, Short.MAX_VALUE)
                .addGroup(jPanel_AtenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAtenderPaciente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        jPanel_AtenderLayout.setVerticalGroup(
            jPanel_AtenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_AtenderLayout.createSequentialGroup()
                .addGroup(jPanel_AtenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_AtenderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_AtenderLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(lbl_atender_ahora2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lbl_atender_ahora)
                        .addGap(40, 40, 40)
                        .addComponent(lbl_atender_ahora1)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_atender_siguiente)))
                .addGap(18, 18, 18)
                .addComponent(btnAtenderPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        PanelTab.addTab("tab2", jPanel_Atender);

        jPanel_Paciente.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        lbl1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl1.setText("DNI");

        txtDNI.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDNI.setMargin(new java.awt.Insets(2, 10, 2, 10));

        txtNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNombre.setMargin(new java.awt.Insets(2, 10, 2, 10));

        lbl2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl2.setText("Nombre");

        lbl3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl3.setText("Apellido Paterno");

        txtApellidoPaterno.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtApellidoPaterno.setMargin(new java.awt.Insets(2, 10, 2, 10));

        Genero.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Genero.setText("Género");

        lbl4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl4.setText("Direccion");

        lbl5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl5.setText("Teléfono");

        txtTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTelefono.setMargin(new java.awt.Insets(2, 10, 2, 10));

        Genero1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Genero1.setText("Fecha Nacimiento");

        txtDireccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDireccion.setMargin(new java.awt.Insets(2, 10, 2, 10));

        btnGuardarPaciente.setBackground(new java.awt.Color(153, 255, 204));
        btnGuardarPaciente.setText("Guardar");
        btnGuardarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarPacienteActionPerformed(evt);
            }
        });

        btnActualizarPaciente.setText("Actualizar");
        btnActualizarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarPacienteActionPerformed(evt);
            }
        });

        txtApellidoMaterno.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtApellidoMaterno.setMargin(new java.awt.Insets(2, 10, 2, 10));

        lbl6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl6.setText("Apellido Materno");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtDireccion)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lbl1)
                                    .addComponent(lbl2)
                                    .addComponent(lbl3)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                                    .addComponent(txtDNI)
                                    .addComponent(txtApellidoPaterno)
                                    .addComponent(lbl5)
                                    .addComponent(txtTelefono))
                                .addComponent(lbl4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGuardarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActualizarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Genero1)
                            .addComponent(Genero))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jcbxGenero, javax.swing.GroupLayout.Alignment.LEADING, 0, 186, Short.MAX_VALUE)
                            .addComponent(lbl6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFechaNacimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDNI, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApellidoPaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApellidoMaterno, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(Genero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbxGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Genero1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnGuardarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizarPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        tablaPacientes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Fecha Nacimiento", "Genero", "Direccion", "Telefono"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaPacientes);

        javax.swing.GroupLayout jPanel_PacienteLayout = new javax.swing.GroupLayout(jPanel_Paciente);
        jPanel_Paciente.setLayout(jPanel_PacienteLayout);
        jPanel_PacienteLayout.setHorizontalGroup(
            jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_PacienteLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 305, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 883, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_PacienteLayout.setVerticalGroup(
            jPanel_PacienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel_PacienteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 544, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelTab.addTab("tab1", jPanel_Paciente);

        jPanel_Consultas.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        lbl7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl7.setText("Diagnostico");

        lbl8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl8.setText("Tratamiento:");

        Genero2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Genero2.setText("Paciente:");

        jcbxPaciente.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnGuardarConsulta.setBackground(new java.awt.Color(27, 55, 79));
        btnGuardarConsulta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGuardarConsulta.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarConsulta.setText("Guardar");
        btnGuardarConsulta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarConsultaActionPerformed(evt);
            }
        });

        btnActualizarConsulta.setBackground(new java.awt.Color(102, 255, 255));
        btnActualizarConsulta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnActualizarConsulta.setText("Actualizar");

        jta_tratamiento.setColumns(20);
        jta_tratamiento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jta_tratamiento.setRows(5);
        jScrollPane3.setViewportView(jta_tratamiento);

        jta_diagnostico.setColumns(20);
        jta_diagnostico.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jta_diagnostico.setRows(5);
        jScrollPane4.setViewportView(jta_diagnostico);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnActualizarConsulta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardarConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jcbxPaciente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Genero2)
                            .addComponent(lbl7, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl8, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Genero2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbxPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lbl8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActualizarConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardarConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        tablaConsultas.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tablaConsultas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Paciente", "Fecha"
            }
        ));
        jScrollPane2.setViewportView(tablaConsultas);

        javax.swing.GroupLayout jPanel_ConsultasLayout = new javax.swing.GroupLayout(jPanel_Consultas);
        jPanel_Consultas.setLayout(jPanel_ConsultasLayout);
        jPanel_ConsultasLayout.setHorizontalGroup(
            jPanel_ConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ConsultasLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        jPanel_ConsultasLayout.setVerticalGroup(
            jPanel_ConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ConsultasLayout.createSequentialGroup()
                .addGroup(jPanel_ConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_ConsultasLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        PanelTab.addTab("tab2", jPanel_Consultas);

        jPanel_Comprobantes.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        Genero3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Genero3.setText("Comprobante");

        btnVerUltimoComprobante.setText("Ver ultimo comprobante");
        btnVerUltimoComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerUltimoComprobanteActionPerformed(evt);
            }
        });

        btnMostrarComprobantes.setText("Mostrar comprobantes");
        btnMostrarComprobantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarComprobantesActionPerformed(evt);
            }
        });

        btnDeshacerComprobante.setText("Eliminar ultimo comprobante");
        btnDeshacerComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeshacerComprobanteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnDeshacerComprobante, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(Genero3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMostrarComprobantes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .addComponent(btnVerUltimoComprobante, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(197, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Genero3)
                .addGap(18, 18, 18)
                .addComponent(btnVerUltimoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeshacerComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMostrarComprobantes, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );

        tablaComprobantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(tablaComprobantes);

        javax.swing.GroupLayout jPanel_ComprobantesLayout = new javax.swing.GroupLayout(jPanel_Comprobantes);
        jPanel_Comprobantes.setLayout(jPanel_ComprobantesLayout);
        jPanel_ComprobantesLayout.setHorizontalGroup(
            jPanel_ComprobantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ComprobantesLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel_ComprobantesLayout.setVerticalGroup(
            jPanel_ComprobantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ComprobantesLayout.createSequentialGroup()
                .addGroup(jPanel_ComprobantesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_ComprobantesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelTab.addTab("tab2", jPanel_Comprobantes);

        jPanel_BuscarPaciente_ABB.setBackground(new java.awt.Color(255, 255, 255));

        Genero4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Genero4.setText("Ingrese DNI");

        ABB_BuscarPaciente.setText("Buscar");
        ABB_BuscarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ABB_BuscarPacienteActionPerformed(evt);
            }
        });

        ABB_Tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"
            }
        ));
        jScrollPane6.setViewportView(ABB_Tabla);

        ABB_MostrarTodos.setText("Mostrar todos");
        ABB_MostrarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ABB_MostrarTodosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_BuscarPaciente_ABBLayout = new javax.swing.GroupLayout(jPanel_BuscarPaciente_ABB);
        jPanel_BuscarPaciente_ABB.setLayout(jPanel_BuscarPaciente_ABBLayout);
        jPanel_BuscarPaciente_ABBLayout.setHorizontalGroup(
            jPanel_BuscarPaciente_ABBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_BuscarPaciente_ABBLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel_BuscarPaciente_ABBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Genero4)
                    .addGroup(jPanel_BuscarPaciente_ABBLayout.createSequentialGroup()
                        .addComponent(ABB_InputBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ABB_BuscarPaciente)))
                .addGap(18, 18, 18)
                .addGroup(jPanel_BuscarPaciente_ABBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ABB_MostrarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel_BuscarPaciente_ABBLayout.setVerticalGroup(
            jPanel_BuscarPaciente_ABBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_BuscarPaciente_ABBLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel_BuscarPaciente_ABBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_BuscarPaciente_ABBLayout.createSequentialGroup()
                        .addComponent(Genero4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_BuscarPaciente_ABBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ABB_BuscarPaciente, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                            .addComponent(ABB_InputBuscar))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ABB_MostrarTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        PanelTab.addTab("tab5", jPanel_BuscarPaciente_ABB);

        jPanel_BuscarPaciente_AVL.setBackground(new java.awt.Color(255, 255, 255));

        Genero5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Genero5.setText("Ingrese DNI");

        AVL_BuscarPaciente.setText("Buscar");
        AVL_BuscarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AVL_BuscarPacienteActionPerformed(evt);
            }
        });

        AVL_Tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"
            }
        ));
        jScrollPane8.setViewportView(AVL_Tabla);

        AVL_MostrarTodos.setText("Mostrar todos");
        AVL_MostrarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AVL_MostrarTodosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_BuscarPaciente_AVLLayout = new javax.swing.GroupLayout(jPanel_BuscarPaciente_AVL);
        jPanel_BuscarPaciente_AVL.setLayout(jPanel_BuscarPaciente_AVLLayout);
        jPanel_BuscarPaciente_AVLLayout.setHorizontalGroup(
            jPanel_BuscarPaciente_AVLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_BuscarPaciente_AVLLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel_BuscarPaciente_AVLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Genero5)
                    .addGroup(jPanel_BuscarPaciente_AVLLayout.createSequentialGroup()
                        .addComponent(AVL_InputBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AVL_BuscarPaciente)))
                .addGap(18, 18, 18)
                .addGroup(jPanel_BuscarPaciente_AVLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AVL_MostrarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel_BuscarPaciente_AVLLayout.setVerticalGroup(
            jPanel_BuscarPaciente_AVLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_BuscarPaciente_AVLLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel_BuscarPaciente_AVLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_BuscarPaciente_AVLLayout.createSequentialGroup()
                        .addComponent(Genero5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_BuscarPaciente_AVLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AVL_BuscarPaciente, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                            .addComponent(AVL_InputBuscar))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AVL_MostrarTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        PanelTab.addTab("tab5", jPanel_BuscarPaciente_AVL);

        getContentPane().add(PanelTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 124, 1200, 600));

        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        btnAtender.setText("Atender");
        btnAtender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtenderActionPerformed(evt);
            }
        });
        jPanel5.add(btnAtender);

        btnPacientes.setText("Pacientes");
        btnPacientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPacientesActionPerformed(evt);
            }
        });
        jPanel5.add(btnPacientes);

        btnConsultas.setText("Consultas");
        btnConsultas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultasActionPerformed(evt);
            }
        });
        jPanel5.add(btnConsultas);

        btnConsultas1.setText("Comprobantes");
        btnConsultas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultas1ActionPerformed(evt);
            }
        });
        jPanel5.add(btnConsultas1);

        btnConsultas2.setText("Buscar Paciente");
        btnConsultas2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultas2ActionPerformed(evt);
            }
        });
        jPanel5.add(btnConsultas2);

        btnConsultas3.setText("Buscar Paciente AVL");
        btnConsultas3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultas3ActionPerformed(evt);
            }
        });
        jPanel5.add(btnConsultas3);

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 890, 30));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Especialidad:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 100, -1));

        txtUsuarioLogueado_especialidad.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtUsuarioLogueado_especialidad.setText("jLabel2");
        getContentPane().add(txtUsuarioLogueado_especialidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Usuario:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 60, -1));

        txtUsuarioLogueado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtUsuarioLogueado.setText("jLabel2");
        getContentPane().add(txtUsuarioLogueado, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        jMenu1.setText("Cuenta");

        MI_CerrarSesion.setText("Cerrar Sesion");
        MI_CerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_CerrarSesionActionPerformed(evt);
            }
        });
        jMenu1.add(MI_CerrarSesion);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MI_CerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_CerrarSesionActionPerformed
        cerrarConexion();
        this.dispose(); // Cierra la ventana actual
        System.exit(0); // Termina la aplicación
    }//GEN-LAST:event_MI_CerrarSesionActionPerformed

    private void btnPacientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPacientesActionPerformed
        PanelTab.setSelectedIndex(1);
        lblCambio.setText("Pacientes");

        // Cargar la nueva data y actualizar la tabla
        cargarPacientesEnTabla();
    }//GEN-LAST:event_btnPacientesActionPerformed

    private void btnConsultasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultasActionPerformed
        PanelTab.setSelectedIndex(2);
        lblCambio.setText("Consultas");

        // Cargar la nueva data y actualizar la tabla
        cargarConsultasEnTabla();
    }//GEN-LAST:event_btnConsultasActionPerformed

    private void btnGuardarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPacienteActionPerformed
        // Obtener los datos de los inputs
        String dni = txtDNI.getText();
        String nombre = txtNombre.getText();
        String apellidoPaterno = txtApellidoPaterno.getText();
        String apellidoMaterno = txtApellidoMaterno.getText();
        Date fechaNacimiento = txtFechaNacimiento.getDate();
        String genero = "";
        if (jcbxGenero.getSelectedItem() != null) {
            Genero generoSeleccionado = (Genero) jcbxGenero.getSelectedItem();
            genero = generoSeleccionado.getNombre();
        }
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();

        if (fechaNacimiento == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha de nacimiento.");
            return;
        }

        try {
            Date fechaEntrada = new Date(); // Fecha actual como entrada
            Date fechaSalida = new Date(); // Por defecto igual a entrada
            Paciente paciente = new Paciente(fechaEntrada, fechaSalida, dni, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, genero, direccion, telefono);

            listaPaciente.guardarEnBD(paciente);

            cargarPacientesEnTabla();
            limpiarInputsPaciente();

            tablaPacientes.clearSelection();
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarPacienteActionPerformed

    private void btnActualizarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarPacienteActionPerformed
        // Obtener los datos de los inputs
        String dni = txtDNI.getText();
        String nombre = txtNombre.getText();
        String apellidoPaterno = txtApellidoPaterno.getText();
        String apellidoMaterno = txtApellidoMaterno.getText();
        Date fechaNacimiento = txtFechaNacimiento.getDate();
        String genero = "";
        if (jcbxGenero.getSelectedItem() != null) {
            Genero generoSeleccionado = (Genero) jcbxGenero.getSelectedItem();
            genero = generoSeleccionado.getNombre();
        }
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();

        if (fechaNacimiento == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha de nacimiento.");
            return;
        }

        try {
            // Crear un objeto paciente con los datos actualizados
            Paciente pacienteActualizado = new Paciente(
                    new Date(),
                    new Date(),
                    dni, nombre, apellidoPaterno, apellidoMaterno,
                    fechaNacimiento, genero, direccion, telefono
            );

            // Actualizar en la base de datos
            listaPaciente.actualizarEnBD(pacienteActualizado);

            // Mostrar mensaje de éxito
            javax.swing.JOptionPane.showMessageDialog(this, "Paciente actualizado correctamente.");

            // Refrescar la tabla desde la base de datos
            cargarPacientesEnTabla();

            // Limpiar inputs y quitar selección
            limpiarInputsPaciente();
            tablaPacientes.clearSelection();

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnActualizarPacienteActionPerformed

    // Método para limpiar los inputs de paciente
    private void limpiarInputsPaciente() {
        txtDNI.setText("");
        txtNombre.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtFechaNacimiento.setDate(null);
        jcbxGenero.setSelectedIndex(-1);
        txtDireccion.setText("");
        txtTelefono.setText("");
    }

    private void btnGuardarConsultaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarConsultaActionPerformed
        String diagnostico = jta_diagnostico.getText().trim();
        String tratamiento = jta_tratamiento.getText().trim();
        String pacienteSeleccionado = (String) jcbxPaciente.getSelectedItem();
        Date fechaRegistro = new java.util.Date();

        if (diagnostico.isEmpty() || tratamiento.isEmpty() || pacienteSeleccionado == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe completar todos los campos.");
            return;
        }

        String dni = pacienteSeleccionado.split(" - ")[0];
        Paciente paciente = null;
        ListaPaciente.NodoPaciente actual = listaPaciente.getCabeza();

        while (actual != null) {
            if (actual.paciente.getDni().equals(dni)) {
                paciente = actual.paciente;
                break;
            }
            actual = actual.siguiente;
        }
        if (paciente == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Paciente no encontrado.");
            return;
        }

        Usuario usuario = usuarioActual;
        if (usuario == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay usuario logueado.");
            return;
        }

        ConsultaMedica consulta = new ConsultaMedica(
                0, diagnostico, tratamiento, fechaRegistro, paciente, usuario, null
        );

        try {
            int idConsulta = listaConsultaMedica.guardarEnBD(consulta);
            if (idConsulta != -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Consulta médica guardada correctamente.");
                cargarConsultasEnTabla();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al guardar la consulta médica.");
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al guardar la consulta: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarConsultaActionPerformed

    private void btnAtenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtenderActionPerformed
        PanelTab.setSelectedIndex(0);
        lblCambio.setText("Atender");

        // Recargar la cola desde la BD para obtener los pacientes más recientes
        colaPacientes.cargarDesdeBD();

        // Actualizar la interfaz
        actualizarTablaColaPacientes();
        actualizarLabelsAtencion();
    }//GEN-LAST:event_btnAtenderActionPerformed

    private void btnAtenderPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtenderPacienteActionPerformed
        if (!colaPacientes.isEmpty()) {
            // Obtener el paciente del frente sin sacarlo de la cola aún
            Paciente paciente = colaPacientes.peek();

            // Abrir el JDialog pasándole el paciente
            JD_Atender jd_atender = new JD_Atender(this, true, paciente, usuarioActual, listaConsultaMedica);
            jd_atender.setLocationRelativeTo(null);
            jd_atender.setVisible(true);

            // Verificar si se guardó la consulta exitosamente
            if (jd_atender.isConsultaGuardada()) {
                // Solo ahora sacar el paciente de la cola
                colaPacientes.dequeue();

                // Actualizar la interfaz
                actualizarTablaColaPacientes();
                actualizarLabelsAtencion();

                javax.swing.JOptionPane.showMessageDialog(null, "Paciente atendido: " + paciente.getNombre() + " " + paciente.getApellidoPaterno());
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "No hay pacientes en la cola.");
        }
    }//GEN-LAST:event_btnAtenderPacienteActionPerformed

    private void btnConsultas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultas1ActionPerformed
        PanelTab.setSelectedIndex(3);
        lblCambio.setText("Comprobantes");


    }//GEN-LAST:event_btnConsultas1ActionPerformed

    private void btnConsultas2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultas2ActionPerformed
        PanelTab.setSelectedIndex(4);
        lblCambio.setText("Buscar Paciente ABB");
    }//GEN-LAST:event_btnConsultas2ActionPerformed

    private void ABB_BuscarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ABB_BuscarPacienteActionPerformed
        String dni = ABB_InputBuscar.getText().trim();
        if (dni.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar un DNI para buscar.");
            return;
        }

        Paciente paciente = arbolBBPacientes.buscar(dni);
        if (paciente != null) {
            // Mostrar el paciente encontrado en la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"}, 0
            );
            modelo.addRow(new Object[]{
                paciente.getDni(),
                paciente.getNombre(),
                paciente.getApellidoPaterno(),
                paciente.getApellidoMaterno(),
                paciente.getGenero()
            });
            ABB_Tabla.setModel(modelo);

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Paciente encontrado: " + paciente.getNombre() + " " + paciente.getApellidoPaterno());
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "No se encontró ningún paciente con DNI: " + dni);
            // Limpiar tabla
            ABB_Tabla.setModel(new DefaultTableModel(
                    new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"}, 0
            ));
        }
    }//GEN-LAST:event_ABB_BuscarPacienteActionPerformed

    private void ABB_MostrarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ABB_MostrarTodosActionPerformed
        if (arbolBBPacientes.estaVacio()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay pacientes en el árbol ABB.");
            return;
        }

        // Obtener todos los pacientes ordenados por DNI (in-order)
        java.util.List<Paciente> pacientes = arbolBBPacientes.obtenerPacientesOrdenados();

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"}, 0
        );

        for (Paciente p : pacientes) {
            modelo.addRow(new Object[]{
                p.getDni(),
                p.getNombre(),
                p.getApellidoPaterno(),
                p.getApellidoMaterno(),
                p.getGenero()
            });
        }

        ABB_Tabla.setModel(modelo);

        javax.swing.JOptionPane.showMessageDialog(this,
                "Se muestran " + pacientes.size() + " pacientes ordenados por DNI (recorrido in-order)");
    }//GEN-LAST:event_ABB_MostrarTodosActionPerformed

    // Método para llenar la tabla con los comprobantes de la pila
    public void llenarTablaComprobantes(PilaDinamicaComprobante pila, JTable tablaComprobantes) {
        // Definir las columnas
        String[] columnas = {"ID", "Fecha Emisión", "Forma de Pago"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        // Recorrer la pila
        PilaDinamicaComprobante.Nodo actual = pila.tope;
        while (actual != null) {
            ComprobantePago c = actual.comprobante;
            Object[] fila = {
                c.getIdComprobantePago(),
                c.getFechaEmision(), // Asegúrate de que el formato sea legible
                c.getPago()
            };
            modelo.addRow(fila);
            actual = actual.siguiente;
        }

        // Asignar el modelo a la tabla
        tablaComprobantes.setModel(modelo);
    }

    private void btnVerUltimoComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerUltimoComprobanteActionPerformed
        ComprobantePago ultimo = pilaComprobantes.peek();
        if (ultimo != null) {
            // Definir las columnas de la tabla
            String[] columnas = {"ID", "Fecha Emisión", "Forma de Pago"};
            javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(columnas, 0);

            // Crear la fila con los datos del último comprobante
            Object[] fila = {
                ultimo.getIdComprobantePago(),
                ultimo.getFechaEmision(),
                ultimo.getPago()
            };
            modelo.addRow(fila);

            // Asignar el modelo a la tabla
            tablaComprobantes.setModel(modelo);
        } else {
            // Si no hay comprobante, puedes limpiar la tabla o mostrar un mensaje
            javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel();
            tablaComprobantes.setModel(modelo);
            javax.swing.JOptionPane.showMessageDialog(this, "No hay comprobantes en la pila.");
        }
    }//GEN-LAST:event_btnVerUltimoComprobanteActionPerformed

    private void btnMostrarComprobantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarComprobantesActionPerformed
        pilaComprobantes.cargarDesdeBD();
        llenarTablaComprobantes(pilaComprobantes, tablaComprobantes);
    }//GEN-LAST:event_btnMostrarComprobantesActionPerformed

    private void btnDeshacerComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeshacerComprobanteActionPerformed
        try {
            // Elimina el último comprobante de la pila
            pilaComprobantes.pop();

            // Actualiza la tabla mostrando todos los comprobantes restantes
            llenarTablaComprobantes(pilaComprobantes, tablaComprobantes);

            javax.swing.JOptionPane.showMessageDialog(this, "Se eliminó el último comprobante correctamente.");
        } catch (RuntimeException e) {
            // Si la pila está vacía, muestra un mensaje de error
            javax.swing.JOptionPane.showMessageDialog(this, "No hay comprobantes para eliminar.");
        }
    }//GEN-LAST:event_btnDeshacerComprobanteActionPerformed

    private void AVL_BuscarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AVL_BuscarPacienteActionPerformed
        String dni = AVL_InputBuscar.getText().trim();
        if (dni.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar un DNI para buscar.");
            return;
        }

        Paciente paciente = arbolAVLPacientes.buscar(dni);
        if (paciente != null) {
            // Mostrar el paciente encontrado en la tabla
            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"}, 0
            );
            modelo.addRow(new Object[]{
                paciente.getDni(),
                paciente.getNombre(),
                paciente.getApellidoPaterno(),
                paciente.getApellidoMaterno(),
                paciente.getGenero()
            });
            AVL_Tabla.setModel(modelo);

            javax.swing.JOptionPane.showMessageDialog(this,
                    "Paciente encontrado: " + paciente.getNombre() + " " + paciente.getApellidoPaterno());
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "No se encontró ningún paciente con DNI: " + dni);
            // Limpiar tabla
            AVL_Tabla.setModel(new DefaultTableModel(
                    new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"}, 0
            ));
        }
    }//GEN-LAST:event_AVL_BuscarPacienteActionPerformed

    private void AVL_MostrarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AVL_MostrarTodosActionPerformed
        if (arbolAVLPacientes.estaVacio()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay pacientes en el árbol AVL.");
            return;
        }

        // Obtener todos los pacientes ordenados por DNI (in-order)
        java.util.List<Paciente> pacientes = arbolAVLPacientes.obtenerPacientesOrdenados();

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Género"}, 0
        );

        for (Paciente p : pacientes) {
            modelo.addRow(new Object[]{
                p.getDni(),
                p.getNombre(),
                p.getApellidoPaterno(),
                p.getApellidoMaterno(),
                p.getGenero()
            });
        }

        AVL_Tabla.setModel(modelo);

        javax.swing.JOptionPane.showMessageDialog(this,
                "Se muestran " + pacientes.size() + " pacientes ordenados");
    }//GEN-LAST:event_AVL_MostrarTodosActionPerformed

    private void btnConsultas3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultas3ActionPerformed
        PanelTab.setSelectedIndex(5);
        lblCambio.setText("Buscar Paciente AVL");
    }//GEN-LAST:event_btnConsultas3ActionPerformed

    private void actualizarTablaColaPacientes() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Fecha y hora entrada"}, 0
        );
        ColaDinamicaPaciente.Nodo actual = colaPacientes.frente;
        while (actual != null) {
            Paciente p = actual.paciente;
            modelo.addRow(new Object[]{p.getDni(), p.getNombre(), p.getApellidoPaterno(), p.getApellidoMaterno(), p.getFechaEntrada()});
            actual = actual.siguiente;
        }
        tablaColaPacientes.setModel(modelo);
    }

    private String formatearPaciente(Paciente paciente) {
        return paciente.getNombre() + " " + paciente.getApellidoPaterno() + " - " + paciente.getDni();
    }

    private void actualizarLabelsAtencion() {
        // Paciente actual (frente de la cola)
        if (colaPacientes.frente != null) {
            Paciente actual = colaPacientes.frente.paciente;
            lbl_atender_ahora.setText(formatearPaciente(actual));

            // Siguiente paciente
            if (colaPacientes.frente.siguiente != null) {
                Paciente siguiente = colaPacientes.frente.siguiente.paciente;
                lbl_atender_siguiente.setText(formatearPaciente(siguiente));
            } else {
                lbl_atender_siguiente.setText("No hay siguiente paciente");
            }
        } else {
            lbl_atender_ahora.setText("No hay pacientes en la cola");
            lbl_atender_siguiente.setText("No hay siguiente paciente");
        }
    }

    /**
     * Método para cerrar la conexión de manera segura
     */
    private void cerrarConexion() {
        if (conexionDB != null) {
            conexionDB.cerrarConexion();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton ABB_BuscarPaciente;
    private javax.swing.JTextField ABB_InputBuscar;
    private javax.swing.JButton ABB_MostrarTodos;
    private javax.swing.JTable ABB_Tabla;
    private javax.swing.JToggleButton AVL_BuscarPaciente;
    private javax.swing.JTextField AVL_InputBuscar;
    private javax.swing.JButton AVL_MostrarTodos;
    private javax.swing.JTable AVL_Tabla;
    private javax.swing.JLabel Genero;
    private javax.swing.JLabel Genero1;
    private javax.swing.JLabel Genero2;
    private javax.swing.JLabel Genero3;
    private javax.swing.JLabel Genero4;
    private javax.swing.JLabel Genero5;
    private javax.swing.JMenuItem MI_CerrarSesion;
    private javax.swing.JTabbedPane PanelTab;
    private javax.swing.JButton btnActualizarConsulta;
    private javax.swing.JButton btnActualizarPaciente;
    private javax.swing.JButton btnAtender;
    private javax.swing.JButton btnAtenderPaciente;
    private javax.swing.JButton btnConsultas;
    private javax.swing.JButton btnConsultas1;
    private javax.swing.JButton btnConsultas2;
    private javax.swing.JButton btnConsultas3;
    private javax.swing.JButton btnDeshacerComprobante;
    private javax.swing.JButton btnGuardarConsulta;
    private javax.swing.JButton btnGuardarPaciente;
    private javax.swing.JButton btnMostrarComprobantes;
    private javax.swing.JButton btnPacientes;
    private javax.swing.JButton btnVerUltimoComprobante;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel_Atender;
    private javax.swing.JPanel jPanel_BuscarPaciente_ABB;
    private javax.swing.JPanel jPanel_BuscarPaciente_AVL;
    private javax.swing.JPanel jPanel_Comprobantes;
    private javax.swing.JPanel jPanel_Consultas;
    private javax.swing.JPanel jPanel_Paciente;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JComboBox<Genero> jcbxGenero;
    private javax.swing.JComboBox<String> jcbxPaciente;
    private javax.swing.JTextArea jta_diagnostico;
    private javax.swing.JTextArea jta_tratamiento;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lblCambio;
    private javax.swing.JLabel lbl_atender_ahora;
    private javax.swing.JLabel lbl_atender_ahora1;
    private javax.swing.JLabel lbl_atender_ahora2;
    private javax.swing.JLabel lbl_atender_siguiente;
    private javax.swing.JTable tablaColaPacientes;
    private javax.swing.JTable tablaComprobantes;
    private javax.swing.JTable tablaConsultas;
    private javax.swing.JTable tablaPacientes;
    private javax.swing.JTextField txtApellidoMaterno;
    private javax.swing.JTextField txtApellidoPaterno;
    private javax.swing.JTextField txtDNI;
    private javax.swing.JTextField txtDireccion;
    private com.toedter.calendar.JDateChooser txtFechaNacimiento;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JLabel txtUsuarioLogueado;
    private javax.swing.JLabel txtUsuarioLogueado_especialidad;
    // End of variables declaration//GEN-END:variables
}
