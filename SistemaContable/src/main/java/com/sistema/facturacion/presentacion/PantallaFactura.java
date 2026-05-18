package com.sistema.facturacion.presentacion;

import com.sistema.facturacion.modelo.CiudadEntrega;
import com.sistema.facturacion.modelo.Cliente;
import com.sistema.facturacion.modelo.FacturaCabecera;
import com.sistema.facturacion.modelo.FacturaDetalle;
import com.sistema.facturacion.negocio.NegocioCiudad;
import com.sistema.facturacion.negocio.NegocioCliente;
import com.sistema.facturacion.negocio.NegocioFactura;
import com.sistema.inventario.modelo.Articulo;
import com.sistema.inventario.negocio.NegocioArticulo;
import com.sistema.inventario.negocio.NegocioReporteInv;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class PantallaFactura {

    private NegocioReporteInv negocioReportes = new NegocioReporteInv();
    private NegocioFactura negocioFactura = new NegocioFactura();
    private NegocioCliente negocioCliente = new NegocioCliente();
    private NegocioCiudad negocioCiudad = new NegocioCiudad();
    private NegocioArticulo negocioArticulo = new NegocioArticulo();

    // Componentes de Cabecera
    private TextField txtIdFactura = new TextField();
    private TextField txtNumeroFactura = new TextField();
    private DatePicker dpFecha = new DatePicker();
    private ComboBox<Cliente> cmbCliente = new ComboBox<>();
    private ComboBox<CiudadEntrega> cmbCiudad = new ComboBox<>();
    private TextField txtTotal = new TextField();
    private Label lblMensaje = new Label();
    private Button btnNuevo = new Button("Nueva Factura");

    // Componentes de Detalle
    private TextField txtIdDetalle = new TextField();
    private ComboBox<Articulo> cmbArticulo = new ComboBox<>();
    private TextField txtStockActual = new TextField(); 
    private TextField txtCantidad = new TextField();
    private TextField txtPrecio = new TextField();

    // Tablas y Listas Observables
    private TableView<FacturaCabecera> tablaFacturas = new TableView<>();
    private ObservableList<FacturaCabecera> datosFacturas = FXCollections.observableArrayList();

    private TableView<FacturaDetalle> tablaDetalle = new TableView<>();
    private ObservableList<FacturaDetalle> datosDetalle = FXCollections.observableArrayList();

    private List<FacturaDetalle> detallesTemporal = new ArrayList<>();
    private int contadorDetalle = 1;

    /**
     * Constructor explícito sin parámetros requerido por el MenuPrincipal.
     */
    public PantallaFactura() {
    }

    public void mostrar(Stage stage) {
        stage.setTitle("Facturación | Gestión de Facturas");

        // Formulario de Cabecera
        GridPane formCab = new GridPane();
        formCab.setHgap(10); formCab.setVgap(8); formCab.setPadding(new Insets(10));

        txtIdFactura.setEditable(false);
        txtIdFactura.setStyle("-fx-background-color: #f0f0f0;");
        txtTotal.setEditable(false);
        txtTotal.setStyle("-fx-background-color: #f0f0f0; -fx-font-weight: bold;");

        formCab.add(new Label("ID Factura:"), 0, 0);       formCab.add(txtIdFactura, 1, 0);
        formCab.add(new Label("Nro. Factura:"), 0, 1);     formCab.add(txtNumeroFactura, 1, 1);
        formCab.add(new Label("Fecha:"), 0, 2);            formCab.add(dpFecha, 1, 2);
        formCab.add(new Label("Cliente:"), 0, 3);          formCab.add(cmbCliente, 1, 3);
        formCab.add(new Label("Ciudad Entrega:"), 0, 4);   formCab.add(cmbCiudad, 1, 4);
        formCab.add(new Label("Total Estimado:"), 0, 5);   formCab.add(txtTotal, 1, 5);
        formCab.add(btnNuevo, 1, 6);

        cmbCliente.setItems(FXCollections.observableArrayList(negocioCliente.listarTodos()));
        cmbCliente.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getNombre() + " - " + c.getCedula());
            }
        });
        cmbCliente.setButtonCell(cmbCliente.getCellFactory().call(null));
        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c == null ? "" : c.getNombre() + " - " + c.getCedula();
            }
            @Override
            public Cliente fromString(String string) { return null; }
        });

        cmbCiudad.setItems(FXCollections.observableArrayList(negocioCiudad.listarTodos()));
        cmbCiudad.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(CiudadEntrega c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getNombre());
            }
        });
        cmbCiudad.setButtonCell(cmbCiudad.getCellFactory().call(null));
        cmbCiudad.setConverter(new StringConverter<CiudadEntrega>() {
            @Override
            public String toString(CiudadEntrega c) {
                return c == null ? "" : c.getNombre();
            }
            @Override
            public CiudadEntrega fromString(String string) { return null; }
        });

        // Formulario de Detalle de Artículos
        GridPane formDet = new GridPane();
        formDet.setHgap(10); formDet.setVgap(8); formDet.setPadding(new Insets(10));

        txtIdDetalle.setEditable(false);
        txtIdDetalle.setStyle("-fx-background-color: #f0f0f0;");
        
        txtStockActual.setEditable(false);
        txtStockActual.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #2E75B6; -fx-font-weight: bold;");
        txtStockActual.setPrefWidth(60);
        txtStockActual.setPromptText("Stock");

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
                try {
                    int stock = negocioReportes.obtenerStockActual(newVal.getIdArticulo());
                    txtStockActual.setText(String.valueOf(stock));
                } catch (Exception ex) {
                    txtStockActual.setText("Error");
                }
            } else {
                txtPrecio.clear();
                txtStockActual.clear();
            }
        });

        HBox boxArticuloStock = new HBox(10, cmbArticulo, new Label("Stock Actual:"), txtStockActual);
        boxArticuloStock.setAlignment(Pos.CENTER_LEFT);

        formDet.add(new Label("ID Fila:"), 0, 0);     formDet.add(txtIdDetalle, 1, 0);
        formDet.add(new Label("Artículo:"), 0, 1);    formDet.add(boxArticuloStock, 1, 1); 
        formDet.add(new Label("Cantidad:"), 0, 2);    formDet.add(txtCantidad, 1, 2);
        formDet.add(new Label("Precio Unit:"), 0, 3); formDet.add(txtPrecio, 1, 3);

        Button btnAgregarDet = new Button("Agregar Fila");
        Button btnModificarDet = new Button("Modificar Fila");
        Button btnEliminarDet = new Button("Quitar Fila");
        btnAgregarDet.setStyle("-fx-background-color: #2E75B6; -fx-text-fill: white;");
        btnEliminarDet.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");
        HBox btnsDet = new HBox(8, btnAgregarDet, btnModificarDet, btnEliminarDet);
        btnsDet.setPadding(new Insets(5));

        TableColumn<FacturaDetalle, Integer> colIdDet = new TableColumn<>("ID Fila");
        colIdDet.setCellValueFactory(new PropertyValueFactory<>("idFacturaDet"));

        TableColumn<FacturaDetalle, String> colArt = new TableColumn<>("Artículo");
        colArt.setCellValueFactory(cd -> {
            int idArt = cd.getValue().getIdArticulo();
            return new SimpleStringProperty(obtenerNombreArticuloPorId(idArt));
        });
        colArt.setPrefWidth(180);

        TableColumn<FacturaDetalle, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<FacturaDetalle, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        tablaDetalle.getColumns().addAll(colIdDet, colArt, colCant, colPrecio);
        tablaDetalle.setItems(datosDetalle);
        tablaDetalle.setPrefHeight(150);

        // Panel de Registro Histórico
        Label lblHistorial = new Label("Registro de Facturas Históricas");
        lblHistorial.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E75B6;");

        Button btnGuardar = new Button("Guardar Factura");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnBuscar = new Button("Buscar");

        btnGuardar.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnModificar.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
        btnEliminar.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");

        HBox btnsCrud = new HBox(8, btnGuardar, btnModificar, btnEliminar, btnBuscar);
        btnsCrud.setPadding(new Insets(5));

        TableColumn<FacturaCabecera, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idFactura"));

        TableColumn<FacturaCabecera, String> colNum = new TableColumn<>("Número Factura");
        colNum.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));

        TableColumn<FacturaCabecera, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cd -> {
            if (cd.getValue().getFecha() != null) {
                return new SimpleStringProperty(new SimpleDateFormat("dd/MM/yyyy").format(cd.getValue().getFecha()));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<FacturaCabecera, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCliente() != null ? cd.getValue().getCliente().getNombre() : ""
        ));

        TableColumn<FacturaCabecera, String> colCiudad = new TableColumn<>("Ciudad Entrega");
        colCiudad.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCiudad() != null ? cd.getValue().getCiudad().getNombre() : ""
        ));

        TableColumn<FacturaCabecera, Double> colTotalHist = new TableColumn<>("Total");
        colTotalHist.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));

        tablaFacturas.getColumns().addAll(colId, colNum, colFecha, colCliente, colCiudad, colTotalHist);
        tablaFacturas.setItems(datosFacturas);
        tablaFacturas.setPrefHeight(180);

        lblMensaje.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        // Layout con Contenedor de Scroll
        VBox layout = new VBox(8,
                new Label("CABECERA"), formCab,
                new Separator(),
                new Label("DETALLE DE ARTÍCULOS"), formDet, btnsDet, tablaDetalle,
                new Separator(),
                lblHistorial, btnsCrud, lblMensaje, tablaFacturas
        );
        layout.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);

        // Gestión de Eventos
        btnNuevo.setOnAction(e -> prepararNuevo());

        btnAgregarDet.setOnAction(e -> {
            if (cmbArticulo.getValue() == null || txtCantidad.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
                lblMensaje.setText("Faltan datos del artículo.");
                return;
            }
            try {
                Articulo art = cmbArticulo.getValue();
                int cantSolicitada = Integer.parseInt(txtCantidad.getText().trim());

                // Se delega el control de existencias a la validación centralizada en NegocioFactura
                negocioFactura.validarStockDisponible(art.getIdArticulo(), cantSolicitada);

                FacturaDetalle nuevoDet = new FacturaDetalle();
                nuevoDet.setIdFacturaDet(contadorDetalle++); 
                nuevoDet.setIdArticulo(art.getIdArticulo().intValue()); 
                nuevoDet.setCantidad(cantSolicitada);
                nuevoDet.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));

                detallesTemporal.add(nuevoDet);
                datosDetalle.add(nuevoDet);
                
                calcularTotalVisual();
                limpiarCamposDetalle();
                lblMensaje.setText("Ítem agregado exitosamente.");

            } catch (IllegalArgumentException ex) {
                // Atrapa el saldo insuficiente controlado lanzado desde la regla de negocio
                lblMensaje.setText(ex.getMessage());
            } catch (Exception ex) {
                lblMensaje.setText("Error al procesar la cantidad o saldo.");
                ex.printStackTrace();
            }
        });

        btnModificarDet.setOnAction(e -> {
            FacturaDetalle sel = tablaDetalle.getSelectionModel().getSelectedItem();
            if (sel == null) {
                lblMensaje.setText("Seleccione una fila del detalle superior.");
                return;
            }
            
            try {
                Articulo art = cmbArticulo.getValue();
                int cantSolicitada = Integer.parseInt(txtCantidad.getText().trim());

                // Se delega el control de existencias a la validación centralizada en NegocioFactura
                negocioFactura.validarStockDisponible(art.getIdArticulo(), cantSolicitada);

                sel.setIdArticulo(art.getIdArticulo().intValue());
                sel.setCantidad(cantSolicitada);
                sel.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
                
                tablaDetalle.refresh(); 
                calcularTotalVisual();
                limpiarCamposDetalle();
                lblMensaje.setText("Ítem modificado exitosamente.");

            } catch (IllegalArgumentException ex) {
                lblMensaje.setText(ex.getMessage());
            } catch (Exception ex) {
                lblMensaje.setText("Error al modificar los datos.");
            }
        });

        btnEliminarDet.setOnAction(e -> {
            FacturaDetalle sel = tablaDetalle.getSelectionModel().getSelectedItem();
            if (sel == null) {
                lblMensaje.setText("Seleccione una fila del detalle.");
                return;
            }
            if (sel.getFactura() != null) {
                if (negocioFactura.eliminarDetalle(sel.getIdFacturaDet()) == 1) {
                    datosDetalle.remove(sel);
                }
            } else {
                detallesTemporal.remove(sel);
                datosDetalle.remove(sel);
            }
            calcularTotalVisual();
            limpiarCamposDetalle();
            lblMensaje.setText("Fila quitada.");
        });

        btnGuardar.setOnAction(e -> {
            if (txtIdFactura.getText().isEmpty() || cmbCliente.getValue() == null || cmbCiudad.getValue() == null) {
                lblMensaje.setText("Complete los campos de la cabecera.");
                return;
            }
            if (detallesTemporal.isEmpty()) {
                lblMensaje.setText("La factura debe tener al menos un ítem.");
                return;
            }

            FacturaCabecera f = extraerCabeceraUI();
            
            List<FacturaDetalle> listaEnlazada = new ArrayList<>();
            for (FacturaDetalle det : detallesTemporal) {
                det.setFactura(f); 
                listaEnlazada.add(det);
            }
            f.setDetalles(listaEnlazada);

            if (negocioFactura.insertar(f) == 1) {
                lblMensaje.setText("Factura registrada exitosamente.");
                cargarTablaFacturas();
                prepararNuevo();
            } else {
                lblMensaje.setText("Error al procesar la inserción de la factura.");
            }
        });

        btnModificar.setOnAction(e -> {
            if (txtIdFactura.getText().isEmpty() || cmbCliente.getValue() == null || cmbCiudad.getValue() == null) {
                lblMensaje.setText("Seleccione un registro válido.");
                return;
            }
            lblMensaje.setText("Cabecera lista para actualización.");
        });

        btnEliminar.setOnAction(e -> {
            if (txtIdFactura.getText().isEmpty()) {
                lblMensaje.setText("Seleccione una factura del registro histórico.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar permanentemente la factura?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.YES) {
                    int id = Integer.parseInt(txtIdFactura.getText());
                    if (negocioFactura.eliminar(id) == 1) {
                        lblMensaje.setText("Registro de factura eliminado.");
                        cargarTablaFacturas();
                        prepararNuevo();
                    } else {
                        lblMensaje.setText("No se pudo eliminar el registro.");
                    }
                }
            });
        });

        btnBuscar.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Módulo de Búsqueda");
            dialog.setHeaderText("Ingrese el ID de la Factura:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(idStr -> {
                try {
                    FacturaCabecera f = negocioFactura.buscar(Integer.parseInt(idStr));
                    if (f != null) {
                        cargarDatosEnPantalla(f);
                        lblMensaje.setText("Resultado encontrado.");
                    } else {
                        lblMensaje.setText("No existe la factura con el ID provisto.");
                    }
                } catch (Exception ex) {
                    lblMensaje.setText("Identificador inválido.");
                }
            });
        });

        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                try {
                    FacturaCabecera facturaCompleta = negocioFactura.buscar(newVal.getIdFactura());
                    if (facturaCompleta != null) {
                        cargarDatosEnPantalla(facturaCompleta);
                    } else {
                        cargarDatosEnPantalla(newVal);
                    }
                } catch (Exception ex) {
                    cargarDatosEnPantalla(newVal);
                }
            }
        });

        cargarTablaFacturas();
        prepararNuevo();

        Scene scene = new Scene(scrollPane, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    private void prepararNuevo() {
        txtIdFactura.setText(String.valueOf(negocioFactura.obtenerSiguienteId()));
        txtNumeroFactura.setText("FAC-" + txtIdFactura.getText());
        dpFecha.setValue(java.time.LocalDate.now());
        cmbCliente.setValue(null);
        cmbCiudad.setValue(null);
        txtTotal.setText("0.00");

        detallesTemporal.clear();
        datosDetalle.clear();
        limpiarCamposDetalle();
        contadorDetalle = negocioFactura.obtenerSiguienteIdDetalle();
        tablaFacturas.getSelectionModel().clearSelection();
    }

    private void cargarDatosEnPantalla(FacturaCabecera f) {
        txtIdFactura.setText(String.valueOf(f.getIdFactura()));
        txtNumeroFactura.setText(f.getNumeroFactura());
        txtTotal.setText(String.format("%.2f", f.getValorTotal()));
        
        cmbCliente.setValue(f.getCliente());
        cmbCiudad.setValue(f.getCiudad());
        
        if (f.getFecha() != null) {
            Date fechaSegura = new Date(f.getFecha().getTime());
            dpFecha.setValue(fechaSegura.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        datosDetalle.clear();
        detallesTemporal.clear();
        if (f.getDetalles() != null) {
            datosDetalle.addAll(f.getDetalles());
            detallesTemporal.addAll(f.getDetalles());
        }
    }

    private FacturaCabecera extraerCabeceraUI() {
        FacturaCabecera f = new FacturaCabecera();
        f.setIdFactura(Integer.parseInt(txtIdFactura.getText()));
        f.setNumeroFactura(txtNumeroFactura.getText());
        f.setCliente(cmbCliente.getValue());
        f.setCiudad(cmbCiudad.getValue());
        f.setValorTotal(Double.parseDouble(txtTotal.getText().replace(",", ".")));
        if (dpFecha.getValue() != null) {
            f.setFecha(Date.from(dpFecha.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        return f;
    }

    private void calcularTotalVisual() {
        double total = detallesTemporal.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecio()).sum();
        txtTotal.setText(String.format("%.2f", total));
    }

    private void limpiarCamposDetalle() {
        txtIdDetalle.clear();
        cmbArticulo.setValue(null);
        txtStockActual.clear();
        txtCantidad.clear();
        txtPrecio.clear();
    }

    private void cargarTablaFacturas() {
        datosFacturas.clear();
        var lista = negocioFactura.listarTodos();
        if (lista != null) datosFacturas.addAll(lista);
    }

    /**
     * Resuelve el nombre del artículo utilizando el nuevo método centralizado de NegocioArticulo.
     */
    private String obtenerNombreArticuloPorId(int id) {
        try {
            Articulo art = negocioArticulo.buscarPorId(id);
            if (art != null) {
                return art.getNombre();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ID: " + id;
    }
}