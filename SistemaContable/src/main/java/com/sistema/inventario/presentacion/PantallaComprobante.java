package com.sistema.inventario.presentacion;

import com.sistema.inventario.modelo.Articulo;
import com.sistema.inventario.modelo.ComprobanteCabecera;
import com.sistema.inventario.modelo.ComprobanteDetalle;
import com.sistema.inventario.modelo.TipoMovimiento;
import com.sistema.inventario.negocio.NegocioComprobante;
import com.sistema.inventario.negocio.NegocioTipoMovimiento;
import com.sistema.inventario.negocio.NegocioArticulo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaComprobante {

    private NegocioComprobante negocioComprobante = new NegocioComprobante();
    private NegocioTipoMovimiento negocioTipoMovimiento = new NegocioTipoMovimiento();
    private NegocioArticulo negocioArticulo = new NegocioArticulo();

    // ── Cabecera ──
    private TextField txtIdComprobante = new TextField();
    private TextField txtNumeroComprobante = new TextField();
    private DatePicker dpFecha = new DatePicker();
    private ComboBox<TipoMovimiento> cmbTipoMovimiento = new ComboBox<>();
    private TextField txtTotalVisual = new TextField();
    private Label lblMensaje = new Label();
    private Button btnNuevo = new Button("Nuevo Comprobante");

    // ── Detalle ──
    private TextField txtIdDetalle = new TextField();
    private ComboBox<Articulo> cmbArticulo = new ComboBox<>();
    private TextField txtCantidad = new TextField();
    private TextField txtPrecio = new TextField();

    // ── Tablas ──
    private TableView<ComprobanteDetalle> tablaDetalle = new TableView<>();
    private ObservableList<ComprobanteDetalle> datosDetalle = FXCollections.observableArrayList();

    private TableView<ComprobanteCabecera> tablaComprobantes = new TableView<>();
    private ObservableList<ComprobanteCabecera> datosComprobantes = FXCollections.observableArrayList();

    private List<ComprobanteDetalle> detallesTemporal = new ArrayList<>();
    private BigDecimal contadorDetalle = BigDecimal.ONE;

    public void mostrar(Stage stage) {
        stage.setTitle("Inventario | Gestión de Comprobantes");

        // ══════ 1. CABECERA ══════
        GridPane formCab = new GridPane();
        formCab.setHgap(10); formCab.setVgap(8); formCab.setPadding(new Insets(10));

        txtIdComprobante.setEditable(false);
        txtIdComprobante.setStyle("-fx-background-color: #f0f0f0;");
        txtTotalVisual.setEditable(false);
        txtTotalVisual.setStyle("-fx-background-color: #f0f0f0; -fx-font-weight: bold;");

        formCab.add(new Label("ID Comprobante:"), 0, 0); formCab.add(txtIdComprobante, 1, 0);
        formCab.add(new Label("Nro. Documento:"), 0, 1); formCab.add(txtNumeroComprobante, 1, 1);
        formCab.add(new Label("Fecha:"), 0, 2); formCab.add(dpFecha, 1, 2);
        formCab.add(new Label("Tipo Movimiento:"), 0, 3); formCab.add(cmbTipoMovimiento, 1, 3);
        formCab.add(new Label("Total Estimado:"), 0, 4); formCab.add(txtTotalVisual, 1, 4);
        formCab.add(btnNuevo, 1, 5);

        cmbTipoMovimiento.setItems(FXCollections.observableArrayList(negocioTipoMovimiento.obtenerTodos()));
        cmbTipoMovimiento.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(TipoMovimiento t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? "" : t.getNombre() + " (" + t.getTipo() + ")");
            }
        });
        cmbTipoMovimiento.setButtonCell(cmbTipoMovimiento.getCellFactory().call(null));

        // ══════ 2. DETALLE ══════
        GridPane formDet = new GridPane();
        formDet.setHgap(10); formDet.setVgap(8); formDet.setPadding(new Insets(10));

        txtIdDetalle.setEditable(false);
        txtIdDetalle.setStyle("-fx-background-color: #f0f0f0;");
        txtCantidad.setPromptText("Ej: 5");
        txtPrecio.setPromptText("Precio Automático");

        cmbArticulo.setItems(FXCollections.observableArrayList(negocioArticulo.listarTodos()));
        cmbArticulo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Articulo a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? "" : a.getIdArticulo() + " - " + a.getNombre());
            }
        });
        cmbArticulo.setButtonCell(cmbArticulo.getCellFactory().call(null));

        cmbArticulo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getPrecio() != null) {
                txtPrecio.setText(newVal.getPrecio().toString());
            } else {
                txtPrecio.clear();
            }
        });

        formDet.add(new Label("ID Fila:"), 0, 0); formDet.add(txtIdDetalle, 1, 0);
        formDet.add(new Label("Artículo:"), 0, 1); formDet.add(cmbArticulo, 1, 1);
        formDet.add(new Label("Cantidad:"), 0, 2); formDet.add(txtCantidad, 1, 2);
        formDet.add(new Label("Precio Unit:"), 0, 3); formDet.add(txtPrecio, 1, 3);

        Button btnAgregarDet = new Button("Agregar Fila");
        Button btnEliminarDet = new Button("Quitar Fila");
        btnAgregarDet.setStyle("-fx-background-color: #2E75B6; -fx-text-fill: white;");
        btnEliminarDet.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");
        HBox btnsDet = new HBox(8, btnAgregarDet, btnEliminarDet);
        btnsDet.setPadding(new Insets(5));

        TableColumn<ComprobanteDetalle, BigDecimal> colIdDet = new TableColumn<>("ID Fila");
        colIdDet.setCellValueFactory(new PropertyValueFactory<>("idComprobanteDet"));
        
        TableColumn<ComprobanteDetalle, String> colArt = new TableColumn<>("Artículo");
        colArt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getIdArticulo() != null ? cd.getValue().getIdArticulo().getNombre() : ""));
        colArt.setPrefWidth(180);

        TableColumn<ComprobanteDetalle, BigInteger> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<ComprobanteDetalle, BigDecimal> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        tablaDetalle.getColumns().addAll(colIdDet, colArt, colCant, colPrecio);
        tablaDetalle.setItems(datosDetalle);
        tablaDetalle.setPrefHeight(250);

        // ══════ 3. COMPROBANTES REGISTRADOS ══════
        Label lblHistorial = new Label("── Comprobantes Registrados ──");
        lblHistorial.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E75B6;");

        Button btnGuardar = new Button("Guardar Nuevo");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnBuscar = new Button("Buscar por ID");

        btnGuardar.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnModificar.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
        btnEliminar.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");

        HBox btnsCrud = new HBox(8, btnGuardar, btnModificar, btnEliminar, btnBuscar);
        btnsCrud.setPadding(new Insets(5));

        TableColumn<ComprobanteCabecera, BigDecimal> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idComprobante"));
        
        TableColumn<ComprobanteCabecera, String> colNum = new TableColumn<>("Número");
        colNum.setCellValueFactory(new PropertyValueFactory<>("numeroComprobante"));
        
        TableColumn<ComprobanteCabecera, Date> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        // 🔥 NUEVA COLUMNA: Tipo de Movimiento
        TableColumn<ComprobanteCabecera, String> colTipo = new TableColumn<>("Movimiento");
        colTipo.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getIdTipoMovimiento() != null ? cd.getValue().getIdTipoMovimiento().getNombre() : "N/A"
        ));

        // Columnas existentes
        TableColumn<ComprobanteCabecera, Integer> colCantArt = new TableColumn<>("Cant. Ítems");
        colCantArt.setCellValueFactory(cd -> {
            int cantidad = cd.getValue().getComprobanteDetalleCollection() != null ? cd.getValue().getComprobanteDetalleCollection().size() : 0;
            return new SimpleObjectProperty<>(cantidad);
        });

        TableColumn<ComprobanteCabecera, String> colTotalHist = new TableColumn<>("Total ($)");
        colTotalHist.setCellValueFactory(cd -> {
            BigDecimal total = BigDecimal.ZERO;
            if (cd.getValue().getComprobanteDetalleCollection() != null) {
                for (ComprobanteDetalle d : cd.getValue().getComprobanteDetalleCollection()) {
                    total = total.add(new BigDecimal(d.getCantidad()).multiply(d.getPrecio()));
                }
            }
            return new SimpleStringProperty(String.format("%.2f", total));
        });

        tablaComprobantes.getColumns().addAll(colId, colNum, colFecha, colTipo, colCantArt, colTotalHist);
        tablaComprobantes.setItems(datosComprobantes);
        tablaComprobantes.setPrefHeight(250);

        lblMensaje.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        // ══════ LAYOUT PRINCIPAL ══════
        VBox layout = new VBox(8,
                new Label("═══ CABECERA ═══"), formCab,
                new Separator(),
                new Label("═══ DETALLE DE ARTÍCULOS ═══"), formDet, btnsDet, tablaDetalle,
                new Separator(),
                lblHistorial, btnsCrud, lblMensaje, tablaComprobantes
        );
        layout.setPadding(new Insets(10));

        // ══════ EVENTOS ══════
        btnNuevo.setOnAction(e -> prepararNuevo());

        btnAgregarDet.setOnAction(e -> {
            if (cmbArticulo.getValue() == null || txtCantidad.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
                lblMensaje.setText("Faltan datos del artículo.");
                return;
            }
            try {
                ComprobanteDetalle det = new ComprobanteDetalle();
                det.setIdComprobanteDet(contadorDetalle);
                det.setIdArticulo(cmbArticulo.getValue());
                det.setCantidad(new BigInteger(txtCantidad.getText().trim()));
                det.setPrecio(new BigDecimal(txtPrecio.getText().trim().replace(",", ".")));

                detallesTemporal.add(det);
                datosDetalle.add(det);
                contadorDetalle = contadorDetalle.add(BigDecimal.ONE);
                calcularTotalVisual(detallesTemporal);

                cmbArticulo.getSelectionModel().clearSelection();
                txtCantidad.clear();
                txtPrecio.clear();
                lblMensaje.setText("Fila agregada.");
            } catch (Exception ex) {
                lblMensaje.setText("Error en números.");
            }
        });

        // 🔥 MEJORA EN EL BOTÓN QUITAR FILA 🔥
        btnEliminarDet.setOnAction(e -> {
            ComprobanteDetalle sel = tablaDetalle.getSelectionModel().getSelectedItem();
            if (sel != null) {
                datosDetalle.remove(sel);
                detallesTemporal.remove(sel);
                calcularTotalVisual(detallesTemporal);
                lblMensaje.setText("Fila quitada correctamente de la vista.");
            } else {
                lblMensaje.setText("Seleccione una fila de la tabla superior para quitarla.");
            }
        });

        // 🔥 GUARDAR
        btnGuardar.setOnAction(e -> {
            if (txtNumeroComprobante.getText().isEmpty() || cmbTipoMovimiento.getValue() == null) return;
            ComprobanteCabecera cab = extraerCabeceraDesdeUI();
            try {
                negocioComprobante.registrarTransaccion(cab, new ArrayList<>(detallesTemporal));
                lblMensaje.setText("Guardado exitosamente.");
                cargarTablaComprobantes();
                prepararNuevo();
            } catch (Exception ex) {
                lblMensaje.setText(ex.getMessage());
            }
        });

        // 🔥 MODIFICAR
        btnModificar.setOnAction(e -> {
            if (txtNumeroComprobante.getText().isEmpty() || cmbTipoMovimiento.getValue() == null) return;
            ComprobanteCabecera cab = extraerCabeceraDesdeUI();
            try {
                negocioComprobante.modificarTransaccion(cab, new ArrayList<>(detallesTemporal));
                cargarTablaComprobantes();
                prepararNuevo();
                // Ponemos el mensaje DESPUÉS de preparar nuevo para que no se borre
                lblMensaje.setText("Comprobante modificado exitosamente."); 
            } catch (Exception ex) {
                lblMensaje.setText(ex.getMessage());
            }
        });

        // 🔥 ELIMINAR
        btnEliminar.setOnAction(e -> {
            try {
                BigDecimal id = new BigDecimal(txtIdComprobante.getText());
                negocioComprobante.eliminarTransaccion(id);
                lblMensaje.setText("Comprobante eliminado.");
                cargarTablaComprobantes();
                prepararNuevo();
            } catch (Exception ex) {
                lblMensaje.setText("Error al eliminar o seleccione un comprobante válido.");
            }
        });

        // 🔥 BUSCAR
        btnBuscar.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Buscar Comprobante");
            dialog.setHeaderText("Ingrese el ID del Comprobante a buscar:");
            Optional<String> result = dialog.showAndWait();
            
            result.ifPresent(idStr -> {
                try {
                    BigDecimal id = new BigDecimal(idStr);
                    ComprobanteCabecera cab = negocioComprobante.buscarPorId(id);
                    if (cab != null) {
                        cargarDatosEnPantalla(cab);
                        lblMensaje.setText("Comprobante encontrado.");
                    } else {
                        lblMensaje.setText("No se encontró ningún comprobante con ID " + idStr);
                    }
                } catch (Exception ex) {
                    lblMensaje.setText("ID inválido.");
                }
            });
        });

        // 🔥 AL TOCAR UNA FILA EN LA TABLA DE HISTORIAL 🔥
        tablaComprobantes.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                cargarDatosEnPantalla(newVal);
                lblMensaje.setText("Comprobante cargado para modificación.");
            }
        });

        // ══════ INICIALIZACIÓN ══════
        cargarTablaComprobantes();
        prepararNuevo(); 

        // 🔥 LA MAGIA DEL SCROLL PANE 🔥
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);           // Metemos todo tu diseño dentro del scroll
        scrollPane.setFitToWidth(true);          // Obligatorio: hace que el diseño ocupe todo el ancho de la ventana
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Muestra la barra vertical solo si hace falta
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Ahora le pasamos el scrollPane a la Scene en lugar del layout directamente
        Scene scene = new Scene(scrollPane, 650, 800);
        stage.setScene(scene);
        stage.show();
    }

    // Método auxiliar para evitar repetir código
    private ComprobanteCabecera extraerCabeceraDesdeUI() {
        ComprobanteCabecera cab = new ComprobanteCabecera();
        cab.setIdComprobante(new BigDecimal(txtIdComprobante.getText()));
        cab.setNumeroComprobante(txtNumeroComprobante.getText());
        cab.setIdTipoMovimiento(cmbTipoMovimiento.getValue());
        if (dpFecha.getValue() != null) cab.setFecha(Date.from(dpFecha.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return cab;
    }

    // Método auxiliar para cargar datos al tocar tabla o al buscar
    private void cargarDatosEnPantalla(ComprobanteCabecera cab) {
        txtIdComprobante.setText(String.valueOf(cab.getIdComprobante()));
        txtNumeroComprobante.setText(cab.getNumeroComprobante());
        cmbTipoMovimiento.setValue(cab.getIdTipoMovimiento());
        if (cab.getFecha() != null) dpFecha.setValue(cab.getFecha().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        
        datosDetalle.clear();
        detallesTemporal.clear(); // Vaciamos y rellenamos la temporal para permitir edición
        if (cab.getComprobanteDetalleCollection() != null) {
            List<ComprobanteDetalle> dts = new ArrayList<>(cab.getComprobanteDetalleCollection());
            datosDetalle.addAll(dts);
            detallesTemporal.addAll(dts);
            calcularTotalVisual(dts);
        }
    }

    private void prepararNuevo() {
        txtIdComprobante.setText(String.valueOf(negocioComprobante.obtenerSiguienteId()));
        txtNumeroComprobante.setText("COMP-" + txtIdComprobante.getText());
        dpFecha.setValue(java.time.LocalDate.now());
        cmbTipoMovimiento.getSelectionModel().clearSelection();
        txtTotalVisual.setText("0.00");
        
        detallesTemporal.clear();
        datosDetalle.clear();
        contadorDetalle = negocioComprobante.obtenerSiguienteIdDetalle();
        lblMensaje.setText("Modo Nuevo.");
        tablaComprobantes.getSelectionModel().clearSelection();
    }

    private void cargarTablaComprobantes() {
        datosComprobantes.clear();
        var lista = negocioComprobante.obtenerHistorialComprobantes();
        if (lista != null) datosComprobantes.addAll(lista);
    }

    private void calcularTotalVisual(List<ComprobanteDetalle> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (ComprobanteDetalle d : lista) {
            BigDecimal cant = new BigDecimal(d.getCantidad());
            total = total.add(cant.multiply(d.getPrecio()));
        }
        txtTotalVisual.setText(String.format("%.2f", total));
    }
}