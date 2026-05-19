package com.sistema.cxc.presentacion;

import com.sistema.cxc.modelo.Cobrador;
import com.sistema.cxc.modelo.FormaPago;
import com.sistema.cxc.modelo.PagoDetalle;
import com.sistema.cxc.negocio.NegocioCobrador;
import com.sistema.cxc.negocio.NegocioFormaPago;
import com.sistema.cxc.negocio.NegocioPagoDetalle;
import com.sistema.facturacion.modelo.FacturaCabecera;
import com.sistema.facturacion.negocio.NegocioFactura;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PantallaPagos {

    private final NegocioPagoDetalle negocioPago = new NegocioPagoDetalle();
    private final NegocioFactura negocioFactura = new NegocioFactura();
    private final NegocioCobrador negocioCobrador = new NegocioCobrador();
    private final NegocioFormaPago negocioFormaPago = new NegocioFormaPago();

    private TextField txtBuscarFacturaId;
    private Label lblInfoFactura;
    private FacturaCabecera facturaActiva;


    private TextField txtIdPagoDetalle;
    private Button btnBuscarPago;
    private Button btnModificarPago;
    private Button btnLimpiarPago;

    private DatePicker dpFechaPago;
    private TextField txtValor;
    private ComboBox<Cobrador> cbCobrador;
    private ComboBox<FormaPago> cbFormaPago;
    private TableView<PagoDetalle> tablaPagos;

    private VBox panelFormulario;
    private VBox panelTabla;

    public void mostrar(Stage stage) {
        stage.setTitle("Gestión de Cuentas por Cobrar (Pagos) - CxC");


        VBox panelCabecera = new VBox(10);
        panelCabecera.setPadding(new Insets(10));
        panelCabecera.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-border-radius: 5;");

        Label lblTituloCabecera = new Label("1. Seleccionar Factura (Cabecera)");
        lblTituloCabecera.setStyle("-fx-font-weight: bold;");

        HBox buscadorBox = new HBox(10);
        buscadorBox.getChildren().add(new Label("ID Factura:"));
        txtBuscarFacturaId = new TextField();
        Button btnBuscarFactura = new Button("Buscar Factura");
        buscadorBox.getChildren().addAll(txtBuscarFacturaId, btnBuscarFactura);

        lblInfoFactura = new Label("Ninguna factura seleccionada.");
        lblInfoFactura.setStyle("-fx-text-fill: blue;");

        panelCabecera.getChildren().addAll(lblTituloCabecera, buscadorBox, lblInfoFactura);


        panelFormulario = new VBox(10);
        panelFormulario.setPadding(new Insets(10));
        panelFormulario.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-border-radius: 5;");

        Label lblTituloDetalle = new Label("2. Registrar/Modificar Pago (Detalle)");
        lblTituloDetalle.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);


        grid.add(new Label("ID Pago:"), 0, 0);
        HBox boxBusquedaPago = new HBox(10);
        txtIdPagoDetalle = new TextField();
        txtIdPagoDetalle.setPromptText("Auto-generado");
        btnBuscarPago = new Button("Buscar Pago");
        boxBusquedaPago.getChildren().addAll(txtIdPagoDetalle, btnBuscarPago);
        grid.add(boxBusquedaPago, 1, 0);


        grid.add(new Label("Fecha de Pago:"), 0, 1);
        dpFechaPago = new DatePicker();
        grid.add(dpFechaPago, 1, 1);


        grid.add(new Label("Valor a Pagar:"), 0, 2);
        txtValor = new TextField();
        grid.add(txtValor, 1, 2);


        grid.add(new Label("Cobrador:"), 0, 3);
        cbCobrador = new ComboBox<>();
        configurarComboBoxCobrador();
        grid.add(cbCobrador, 1, 3);


        grid.add(new Label("Forma de Pago:"), 0, 4);
        cbFormaPago = new ComboBox<>();
        configurarComboBoxFormaPago();
        grid.add(cbFormaPago, 1, 4);


        HBox panelBotones = new HBox(10);
        Button btnAgregarPago = new Button("Agregar Pago");
        btnModificarPago = new Button("Modificar");
        btnLimpiarPago = new Button("Limpiar");
        panelBotones.getChildren().addAll(btnAgregarPago, btnModificarPago, btnLimpiarPago);

        panelFormulario.getChildren().addAll(lblTituloDetalle, grid, panelBotones);


        panelTabla = new VBox(10);
        panelTabla.setPadding(new Insets(10));

        tablaPagos = new TableView<>();


        TableColumn<PagoDetalle, Integer> colId = new TableColumn<>("ID Pago");
        colId.setCellValueFactory(new PropertyValueFactory<>("idPagoDetalle"));

        TableColumn<PagoDetalle, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));

        TableColumn<PagoDetalle, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        TableColumn<PagoDetalle, String> colCobrador = new TableColumn<>("Cobrador");
        colCobrador.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCobrador().getNombre()));

        TableColumn<PagoDetalle, String> colFormaPago = new TableColumn<>("Forma Pago");
        colFormaPago.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormaPago().getNombre()));


        tablaPagos.getColumns().addAll(colId, colFecha, colValor, colCobrador, colFormaPago);
        tablaPagos.setPrefHeight(200);

        Button btnEliminarPago = new Button("Eliminar Pago Seleccionado");
        panelTabla.getChildren().addAll(new Label("Historial de Pagos de la Factura:"), tablaPagos, btnEliminarPago);


        btnBuscarFactura.setOnAction(e -> buscarFactura());
        btnAgregarPago.setOnAction(e -> agregarPago());
        btnEliminarPago.setOnAction(e -> eliminarPago());
        btnBuscarPago.setOnAction(e -> buscarPago());
        btnModificarPago.setOnAction(e -> modificarPago());
        btnLimpiarPago.setOnAction(e -> limpiarCamposPago());


        tablaPagos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtIdPagoDetalle.setText(String.valueOf(newSelection.getIdPagoDetalle()));
                txtValor.setText(String.valueOf(newSelection.getValor()));


                if(newSelection.getFechaPago() != null){
                    dpFechaPago.setValue(new java.sql.Date(newSelection.getFechaPago().getTime()).toLocalDate());
                }


                cbCobrador.getItems().stream()
                        .filter(c -> c.getNombre().equals(newSelection.getCobrador().getNombre()))
                        .findFirst().ifPresent(cbCobrador::setValue);


                cbFormaPago.getItems().stream()
                        .filter(f -> f.getNombre().equals(newSelection.getFormaPago().getNombre()))
                        .findFirst().ifPresent(cbFormaPago::setValue);
            }
        });


        panelFormulario.setDisable(true);
        panelTabla.setDisable(true);

        VBox layoutPrincipal = new VBox(10);
        layoutPrincipal.setPadding(new Insets(15));
        layoutPrincipal.getChildren().addAll(panelCabecera, panelFormulario, panelTabla);

        Scene scene = new Scene(layoutPrincipal, 600, 700);
        stage.setScene(scene);
        stage.show();
    }

    private void buscarFactura() {
        try {
            Integer idFactura = Integer.parseInt(txtBuscarFacturaId.getText());
            facturaActiva = negocioFactura.buscar(idFactura);

            if (facturaActiva != null) {
                lblInfoFactura.setText("Factura N°: " + facturaActiva.getNumeroFactura() +
                        " | Cliente: " + facturaActiva.getCliente().getNombre() +
                        " | Total: $" + facturaActiva.getValorTotal());

                panelFormulario.setDisable(false);
                panelTabla.setDisable(false);

                limpiarCamposPago();
                refrescarTablaPagos();
            } else {
                lblInfoFactura.setText("Factura no encontrada.");
                facturaActiva = null;
                panelFormulario.setDisable(true);
                panelTabla.setDisable(true);
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Ingrese un ID de factura válido (número).", Alert.AlertType.ERROR);
        }
    }

    private void agregarPago() {
        if (facturaActiva == null || dpFechaPago.getValue() == null || txtValor.getText().isEmpty() ||
                cbCobrador.getValue() == null || cbFormaPago.getValue() == null) {
            mostrarAlerta("Error", "Complete todos los campos del pago.", Alert.AlertType.WARNING);
            return;
        }

        try {
            PagoDetalle nuevoPago = new PagoDetalle();
            nuevoPago.setFactura(facturaActiva);
            nuevoPago.setFechaPago(Date.from(dpFechaPago.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            nuevoPago.setValor(Double.parseDouble(txtValor.getText()));
            nuevoPago.setCobrador(cbCobrador.getValue());
            nuevoPago.setFormaPago(cbFormaPago.getValue());

            int res = negocioPago.insertar(nuevoPago);
            if (res == 1) {
                mostrarAlerta("Éxito", "Pago registrado correctamente.", Alert.AlertType.INFORMATION);
                limpiarCamposPago();
                refrescarTablaPagos();
            } else {
                mostrarAlerta("Error", "No se pudo registrar el pago.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El valor del pago debe ser un número válido.", Alert.AlertType.ERROR);
        }
    }


    private void buscarPago() {
        if (txtIdPagoDetalle.getText().isEmpty()) {
            mostrarAlerta("Atención", "Ingrese el ID del pago a buscar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            int idPago = Integer.parseInt(txtIdPagoDetalle.getText());
            PagoDetalle pagoEncontrado = negocioPago.buscar(idPago);

            if (pagoEncontrado != null) {

                if (!pagoEncontrado.getFactura().getIdFactura().equals(facturaActiva.getIdFactura())) {
                    mostrarAlerta("Aviso", "El pago existe, pero pertenece a otra factura.", Alert.AlertType.WARNING);
                    return;
                }

                tablaPagos.getItems().stream()
                        .filter(p -> p.getIdPagoDetalle().equals(idPago))
                        .findFirst().ifPresent(p -> tablaPagos.getSelectionModel().select(p));
            } else {
                mostrarAlerta("No encontrado", "No existe un pago con ese ID.", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El ID debe ser numérico.", Alert.AlertType.ERROR);
        }
    }


    private void modificarPago() {
        if (txtIdPagoDetalle.getText().isEmpty()) {
            mostrarAlerta("Atención", "Seleccione o busque un pago primero.", Alert.AlertType.WARNING);
            return;
        }
        try {
            PagoDetalle pagoExistente = negocioPago.buscar(Integer.parseInt(txtIdPagoDetalle.getText()));
            if (pagoExistente != null) {
                pagoExistente.setFechaPago(Date.from(dpFechaPago.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                pagoExistente.setValor(Double.parseDouble(txtValor.getText()));
                pagoExistente.setCobrador(cbCobrador.getValue());
                pagoExistente.setFormaPago(cbFormaPago.getValue());

                int res = negocioPago.modificar(pagoExistente);
                if (res == 1) {
                    mostrarAlerta("Éxito", "Pago modificado correctamente.", Alert.AlertType.INFORMATION);
                    limpiarCamposPago();
                    refrescarTablaPagos();
                } else {
                    mostrarAlerta("Error", "No se pudo modificar el pago.", Alert.AlertType.ERROR);
                }
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El valor del pago debe ser numérico.", Alert.AlertType.ERROR);
        }
    }

    private void eliminarPago() {
        PagoDetalle pagoSeleccionado = tablaPagos.getSelectionModel().getSelectedItem();
        if (pagoSeleccionado != null) {
            negocioPago.eliminar(pagoSeleccionado.getIdPagoDetalle());
            limpiarCamposPago();
            refrescarTablaPagos();
        } else {
            mostrarAlerta("Atención", "Seleccione un pago de la tabla para eliminar.", Alert.AlertType.WARNING);
        }
    }


    private void limpiarCamposPago() {
        txtIdPagoDetalle.clear();
        dpFechaPago.setValue(null);
        txtValor.clear();
        cbCobrador.getSelectionModel().clearSelection();
        cbFormaPago.getSelectionModel().clearSelection();
        tablaPagos.getSelectionModel().clearSelection();
    }

    private void refrescarTablaPagos() {
        if (facturaActiva != null) {
            List<PagoDetalle> pagos = negocioPago.listarPorFactura(facturaActiva.getIdFactura());
            tablaPagos.setItems(FXCollections.observableArrayList(pagos));
        }
    }

    private void configurarComboBoxCobrador() {
        cbCobrador.setItems(FXCollections.observableArrayList(negocioCobrador.listarTodos()));
        cbCobrador.setConverter(new StringConverter<Cobrador>() {
            @Override
            public String toString(Cobrador c) { return c == null ? "" : c.getNombre(); }
            @Override
            public Cobrador fromString(String string) { return null; }
        });
    }

    private void configurarComboBoxFormaPago() {
        cbFormaPago.setItems(FXCollections.observableArrayList(negocioFormaPago.listarTodos()));
        cbFormaPago.setConverter(new StringConverter<FormaPago>() {
            @Override
            public String toString(FormaPago f) { return f == null ? "" : f.getNombre(); }
            @Override
            public FormaPago fromString(String string) { return null; }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }
}