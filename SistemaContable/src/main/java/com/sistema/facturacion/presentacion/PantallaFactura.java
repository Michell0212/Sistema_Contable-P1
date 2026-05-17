package com.sistema.facturacion.presentacion;

import com.sistema.facturacion.modelo.CiudadEntrega;
import com.sistema.facturacion.modelo.Cliente;
import com.sistema.facturacion.modelo.FacturaCabecera;
import com.sistema.facturacion.modelo.FacturaDetalle;
import com.sistema.facturacion.negocio.NegocioCiudad;
import com.sistema.facturacion.negocio.NegocioCliente;
import com.sistema.facturacion.negocio.NegocioFactura;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PantallaFactura {

    private NegocioFactura  negocioFactura = new NegocioFactura();
    private NegocioCliente  negocioCliente = new NegocioCliente();
    private NegocioCiudad   negocioCiudad  = new NegocioCiudad();

    // ── Cabecera ──
    private TextField  txtIdFactura     = new TextField();
    private TextField  txtNumeroFactura = new TextField();
    private DatePicker dpFecha          = new DatePicker();
    private ComboBox<Cliente>       cmbCliente = new ComboBox<>();
    private ComboBox<CiudadEntrega> cmbCiudad  = new ComboBox<>();
    private TextField  txtTotal         = new TextField();
    private Label      lblMensaje       = new Label();

    // ── Detalle ──
    private TextField txtIdDetalle   = new TextField();
    private TextField txtIdArticulo  = new TextField();
    private TextField txtCantidad    = new TextField();
    private TextField txtPrecio      = new TextField();

    // ── Tablas ──
    private TableView<FacturaCabecera> tablaFacturas = new TableView<>();
    private ObservableList<FacturaCabecera> datosFacturas = FXCollections.observableArrayList();

    private TableView<FacturaDetalle> tablaDetalle = new TableView<>();
    private ObservableList<FacturaDetalle> datosDetalle = FXCollections.observableArrayList();

    private List<FacturaDetalle> detallesTemporal = new ArrayList<>();
    private int contadorDetalle = 1;

    public void mostrar(Stage stage) {
        stage.setTitle("Facturación | Facturas");

        // ══ CABECERA ══
        GridPane formCab = new GridPane();
        formCab.setHgap(10);
        formCab.setVgap(8);
        formCab.setPadding(new Insets(10));

        txtIdFactura.setEditable(false);
        txtIdFactura.setStyle("-fx-background-color: #f0f0f0;");
        txtTotal.setEditable(false);
        txtTotal.setStyle("-fx-background-color: #f0f0f0;");

        formCab.add(new Label("ID Factura:"),     0, 0); formCab.add(txtIdFactura,    1, 0);
        formCab.add(new Label("Nro. Factura:"),   0, 1); formCab.add(txtNumeroFactura, 1, 1);
        formCab.add(new Label("Fecha:"),          0, 2); formCab.add(dpFecha,          1, 2);
        formCab.add(new Label("Cliente:"),        0, 3); formCab.add(cmbCliente,       1, 3);
        formCab.add(new Label("Ciudad Entrega:"), 0, 4); formCab.add(cmbCiudad,        1, 4);
        formCab.add(new Label("Total:"),          0, 5); formCab.add(txtTotal,         1, 5);

        // ComboBox Cliente
        cmbCliente.setItems(FXCollections.observableArrayList(negocioCliente.listarTodos()));
        cmbCliente.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getNombre() + " - " + c.getCedula());
            }
        });
        cmbCliente.setButtonCell(new ListCell<>() {
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getNombre() + " - " + c.getCedula());
            }
        });

        // ComboBox Ciudad
        cmbCiudad.setItems(FXCollections.observableArrayList(negocioCiudad.listarTodos()));
        cmbCiudad.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(CiudadEntrega c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getNombre());
            }
        });
        cmbCiudad.setButtonCell(new ListCell<>() {
            protected void updateItem(CiudadEntrega c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getNombre());
            }
        });

        // ── Botones Cabecera ──
        Button btnNuevo    = new Button("Nueva Factura");
        Button btnGuardar  = new Button("Guardar");
        Button btnEliminar = new Button("Eliminar");
        Button btnBuscar   = new Button("Buscar");
        Button btnLimpiar  = new Button("Limpiar");

        btnGuardar.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
        btnEliminar.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");

        HBox btnsCab = new HBox(8, btnNuevo, btnGuardar, btnEliminar, btnBuscar, btnLimpiar);
        btnsCab.setPadding(new Insets(5));
        btnsCab.setAlignment(Pos.CENTER_LEFT);

        // ── Tabla Facturas ──
        TableColumn<FacturaCabecera, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colId.setPrefWidth(50);

        TableColumn<FacturaCabecera, String> colNum = new TableColumn<>("Número");
        colNum.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colNum.setPrefWidth(100);

        TableColumn<FacturaCabecera, Date> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setPrefWidth(100);

        TableColumn<FacturaCabecera, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colTotal.setPrefWidth(80);

        tablaFacturas.getColumns().addAll(colId, colNum, colFecha, colTotal);
        tablaFacturas.setItems(datosFacturas);
        tablaFacturas.setPrefHeight(150);

        // ══ DETALLE ══
        Label lblDetalle = new Label("── Detalle de Factura ──");
        lblDetalle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E75B6;");

        Label lblNotaArticulo = new Label(
                "* ID Artículo: ingrese el ID del artículo del módulo Inventarios");
        lblNotaArticulo.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888; -fx-font-style: italic;");

        GridPane formDet = new GridPane();
        formDet.setHgap(10);
        formDet.setVgap(8);
        formDet.setPadding(new Insets(10));

        txtIdDetalle.setEditable(false);
        txtIdDetalle.setStyle("-fx-background-color: #f0f0f0;");
        txtIdArticulo.setPromptText("ID del artículo");
        txtCantidad.setPromptText("Ej: 2");
        txtPrecio.setPromptText("Ej: 15.50");

        formDet.add(new Label("ID Det:"),     0, 0); formDet.add(txtIdDetalle,  1, 0);
        formDet.add(new Label("ID Artículo:"),0, 1); formDet.add(txtIdArticulo, 1, 1);
        formDet.add(new Label("Cantidad:"),   0, 2); formDet.add(txtCantidad,   1, 2);
        formDet.add(new Label("Precio:"),     0, 3); formDet.add(txtPrecio,     1, 3);

        Button btnAgregarDet   = new Button("Agregar Detalle");
        Button btnModificarDet = new Button("Modificar Detalle");
        Button btnEliminarDet  = new Button("Eliminar Detalle");

        btnAgregarDet.setStyle("-fx-background-color: #2E75B6; -fx-text-fill: white;");
        btnEliminarDet.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");

        HBox btnsDet = new HBox(8, btnAgregarDet, btnModificarDet, btnEliminarDet);
        btnsDet.setPadding(new Insets(5));

        // ── Tabla Detalle ──
        TableColumn<FacturaDetalle, Integer> colIdDet = new TableColumn<>("ID Det");
        colIdDet.setCellValueFactory(new PropertyValueFactory<>("idFacturaDet"));
        colIdDet.setPrefWidth(60);

        TableColumn<FacturaDetalle, Integer> colArt = new TableColumn<>("ID Artículo");
        colArt.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colArt.setPrefWidth(100);

        TableColumn<FacturaDetalle, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCant.setPrefWidth(80);

        TableColumn<FacturaDetalle, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setPrefWidth(80);

        tablaDetalle.getColumns().addAll(colIdDet, colArt, colCant, colPrecio);
        tablaDetalle.setItems(datosDetalle);
        tablaDetalle.setPrefHeight(150);

        lblMensaje.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        // ══ LAYOUT ══
        VBox layout = new VBox(8,
                new Label("═══ CABECERA ═══"),
                formCab, btnsCab,
                tablaFacturas,
                lblMensaje,
                lblDetalle, lblNotaArticulo,
                formDet, btnsDet,
                tablaDetalle
        );
        layout.setPadding(new Insets(10));

        // ══ EVENTOS CABECERA ══
        btnNuevo.setOnAction(e -> {
            txtIdFactura.setText(String.valueOf(negocioFactura.obtenerSiguienteId()));
            txtNumeroFactura.setText("FAC-" + txtIdFactura.getText());
            dpFecha.setValue(java.time.LocalDate.now());
            txtTotal.setText("0.00");
            detallesTemporal.clear();
            datosDetalle.clear();
            contadorDetalle = negocioFactura.obtenerSiguienteIdDetalle();
            cmbCliente.setValue(null);
            cmbCiudad.setValue(null);
            lblMensaje.setText("");
        });

        btnGuardar.setOnAction(e -> {
            if (txtIdFactura.getText().isEmpty()) {
                lblMensaje.setText("Presione 'Nueva Factura' primero.");
                return;
            }
            if (cmbCliente.getValue() == null || cmbCiudad.getValue() == null) {
                lblMensaje.setText("Seleccione cliente y ciudad.");
                return;
            }
            if (detallesTemporal.isEmpty()) {
                lblMensaje.setText("Agregue al menos un detalle.");
                return;
            }
            FacturaCabecera f = new FacturaCabecera();
            f.setIdFactura(Integer.parseInt(txtIdFactura.getText()));
            f.setNumeroFactura(txtNumeroFactura.getText());
            f.setFecha(java.sql.Date.valueOf(dpFecha.getValue()));
            f.setCliente(cmbCliente.getValue());
            f.setCiudad(cmbCiudad.getValue());
            f.setValorTotal(Double.parseDouble(
                    txtTotal.getText().replace(",", ".")));
            f.setDetalles(new ArrayList<>(detallesTemporal));

            if (negocioFactura.insertar(f) == 1) {
                lblMensaje.setText("Factura guardada correctamente.");
                cargarTablaFacturas();
                limpiar();
            } else {
                lblMensaje.setText("Error al guardar factura.");
            }
        });

        btnEliminar.setOnAction(e -> {
            if (txtIdFactura.getText().isEmpty()) {
                lblMensaje.setText("Seleccione una factura primero.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar esta factura y sus detalles?",
                    ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    if (negocioFactura.eliminar(
                            Integer.parseInt(txtIdFactura.getText())) == 1) {
                        lblMensaje.setText("Factura eliminada.");
                        cargarTablaFacturas();
                        limpiar();
                    } else {
                        lblMensaje.setText("Error al eliminar factura.");
                    }
                }
            });
        });

        btnBuscar.setOnAction(e -> {
            if (txtIdFactura.getText().isEmpty()) {
                lblMensaje.setText("Ingrese un ID para buscar.");
                return;
            }
            FacturaCabecera f = negocioFactura.buscar(
                    Integer.parseInt(txtIdFactura.getText()));
            if (f != null) {
                txtNumeroFactura.setText(f.getNumeroFactura());
                txtTotal.setText(String.valueOf(f.getValorTotal()));
                cmbCliente.setValue(f.getCliente());
                cmbCiudad.setValue(f.getCiudad());
                datosDetalle.clear();
                if (f.getDetalles() != null) datosDetalle.addAll(f.getDetalles());
                lblMensaje.setText("Factura encontrada.");
            } else {
                lblMensaje.setText("Factura no encontrada.");
            }
        });

        btnLimpiar.setOnAction(e -> limpiar());

        tablaFacturas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        txtIdFactura.setText(String.valueOf(newVal.getIdFactura()));
                        txtNumeroFactura.setText(newVal.getNumeroFactura());
                        txtTotal.setText(String.valueOf(newVal.getValorTotal()));
                        cmbCliente.setValue(newVal.getCliente());
                        cmbCiudad.setValue(newVal.getCiudad());
                        datosDetalle.clear();
                        if (newVal.getDetalles() != null)
                            datosDetalle.addAll(newVal.getDetalles());
                        lblMensaje.setText("");
                    }
                });

        // ══ EVENTOS DETALLE ══
        btnAgregarDet.setOnAction(e -> {
            if (txtIdArticulo.getText().isEmpty() || txtCantidad.getText().isEmpty()
                    || txtPrecio.getText().isEmpty()) {
                lblMensaje.setText("Complete ID Artículo, Cantidad y Precio.");
                return;
            }
            try {
                FacturaDetalle det = new FacturaDetalle();
                det.setIdFacturaDet(contadorDetalle++);
                det.setIdArticulo(Integer.parseInt(
                        txtIdArticulo.getText().trim()));
                det.setCantidad(Integer.parseInt(
                        txtCantidad.getText().trim().replace(",", ".")));
                det.setPrecio(Double.parseDouble(
                        txtPrecio.getText().trim().replace(",", ".")));

                detallesTemporal.add(det);
                datosDetalle.add(det);

                double total = detallesTemporal.stream()
                        .mapToDouble(d -> d.getCantidad() * d.getPrecio()).sum();
                txtTotal.setText(String.format("%.2f", total));

                txtIdArticulo.clear();
                txtCantidad.clear();
                txtPrecio.clear();
                lblMensaje.setText("Detalle agregado.");
            } catch (NumberFormatException ex) {
                lblMensaje.setText("ID Artículo y Cantidad deben ser enteros. Precio debe ser numérico.");
            }
        });

        btnEliminarDet.setOnAction(e -> {
            FacturaDetalle sel = tablaDetalle.getSelectionModel().getSelectedItem();
            if (sel == null) {
                lblMensaje.setText("Seleccione un detalle primero.");
                return;
            }
            if (sel.getFactura() != null) {
                if (negocioFactura.eliminarDetalle(sel.getIdFacturaDet()) == 1) {
                    datosDetalle.remove(sel);
                    lblMensaje.setText("Detalle eliminado.");
                }
            } else {
                detallesTemporal.remove(sel);
                datosDetalle.remove(sel);
                double total = detallesTemporal.stream()
                        .mapToDouble(d -> d.getCantidad() * d.getPrecio()).sum();
                txtTotal.setText(String.format("%.2f", total));
                lblMensaje.setText("Detalle eliminado.");
            }
        });

        btnModificarDet.setOnAction(e -> {
            FacturaDetalle sel = tablaDetalle.getSelectionModel().getSelectedItem();
            if (sel == null) {
                lblMensaje.setText("Seleccione un detalle primero.");
                return;
            }
            try {
                sel.setCantidad(Integer.parseInt(
                        txtCantidad.getText().trim().replace(",", ".")));
                sel.setPrecio(Double.parseDouble(
                        txtPrecio.getText().trim().replace(",", ".")));
                if (sel.getFactura() != null) {
                    negocioFactura.modificarDetalle(sel);
                }
                tablaDetalle.refresh();
                lblMensaje.setText("Detalle modificado.");
            } catch (NumberFormatException ex) {
                lblMensaje.setText("Cantidad debe ser entero. Precio debe ser numérico.");
            }
        });

        tablaDetalle.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        txtIdDetalle.setText(String.valueOf(newVal.getIdFacturaDet()));
                        txtIdArticulo.setText(String.valueOf(newVal.getIdArticulo()));
                        txtCantidad.setText(String.valueOf(newVal.getCantidad()));
                        txtPrecio.setText(String.valueOf(newVal.getPrecio()));
                    }
                });

        cargarTablaFacturas();

        Scene scene = new Scene(layout, 700, 780);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarTablaFacturas() {
        datosFacturas.clear();
        var lista = negocioFactura.listarTodos();
        if (lista != null) datosFacturas.addAll(lista);
    }

    private void limpiar() {
        txtIdFactura.clear();
        txtNumeroFactura.clear();
        dpFecha.setValue(null);
        cmbCliente.setValue(null);
        cmbCiudad.setValue(null);
        txtTotal.clear();
        txtIdDetalle.clear();
        txtIdArticulo.clear();
        txtCantidad.clear();
        txtPrecio.clear();
        detallesTemporal.clear();
        datosDetalle.clear();
        lblMensaje.setText("");
        tablaFacturas.getSelectionModel().clearSelection();
        tablaDetalle.getSelectionModel().clearSelection();
    }
}