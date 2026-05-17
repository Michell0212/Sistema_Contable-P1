package com.sistema.facturacion.presentacion;

import com.sistema.facturacion.modelo.Cliente;
import com.sistema.facturacion.negocio.NegocioCliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaCliente {

    private NegocioCliente negocio = new NegocioCliente();

    private TextField txtId        = new TextField();
    private TextField txtCedula    = new TextField();
    private TextField txtNombre    = new TextField();
    private TextField txtDireccion = new TextField();
    private TextField txtBuscar    = new TextField();
    private Label     lblMensaje   = new Label();

    private TableView<Cliente> tabla = new TableView<>();
    private ObservableList<Cliente> datos = FXCollections.observableArrayList();

    public void mostrar(Stage stage) {
        stage.setTitle("Facturación | Clientes");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f0f0f0;");

        form.add(new Label("ID:"),        0, 0); form.add(txtId,        1, 0);
        form.add(new Label("Cédula:"),    0, 1); form.add(txtCedula,    1, 1);
        form.add(new Label("Nombre:"),    0, 2); form.add(txtNombre,    1, 2);
        form.add(new Label("Dirección:"), 0, 3); form.add(txtDireccion, 1, 3);

        // ── Buscador ──
        txtBuscar.setPromptText("Buscar por ID, cédula, nombre o dirección...");
        txtBuscar.setPrefWidth(300);
        Button btnBuscarCampo = new Button("Buscar");
        Button btnMostrarTodos = new Button("Mostrar Todos");
        HBox filaBuscar = new HBox(8, txtBuscar, btnBuscarCampo, btnMostrarTodos);
        filaBuscar.setPadding(new Insets(5, 15, 5, 15));

        // ── Botones CRUD ──
        Button btnNuevo     = new Button("Nuevo");
        Button btnInsertar  = new Button("Insertar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar  = new Button("Eliminar");
        Button btnLimpiar   = new Button("Limpiar");

        HBox botones = new HBox(10, btnNuevo, btnInsertar,
                btnModificar, btnEliminar, btnLimpiar);
        botones.setPadding(new Insets(10));
        botones.setAlignment(Pos.CENTER);

        // ── Tabla ──
        TableColumn<Cliente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colId.setPrefWidth(60);

        TableColumn<Cliente, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colCedula.setPrefWidth(120);

        TableColumn<Cliente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);

        TableColumn<Cliente, String> colDir = new TableColumn<>("Dirección");
        colDir.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colDir.setPrefWidth(200);

        tabla.getColumns().addAll(colId, colCedula, colNombre, colDir);
        tabla.setItems(datos);
        tabla.setPrefHeight(250);

        lblMensaje.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        VBox layout = new VBox(10, form, botones, filaBuscar, lblMensaje, tabla);
        layout.setPadding(new Insets(10));

        // ── Eventos CRUD ──
        btnNuevo.setOnAction(e -> {
            txtId.setText(String.valueOf(negocio.obtenerSiguienteId()));
            txtCedula.clear();
            txtNombre.clear();
            txtDireccion.clear();
            lblMensaje.setText("");
        });

        btnInsertar.setOnAction(e -> {
            if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty()) {
                lblMensaje.setText("Cédula y Nombre son obligatorios.");
                return;
            }
            Cliente c = new Cliente();
            c.setIdCliente(Integer.parseInt(txtId.getText()));
            c.setCedula(txtCedula.getText());
            c.setNombre(txtNombre.getText());
            c.setDireccion(txtDireccion.getText());
            if (negocio.insertar(c) == 1) {
                lblMensaje.setText("Cliente insertado correctamente.");
                cargarTabla();
                limpiar();
            } else {
                lblMensaje.setText("Error al insertar. Verifique que la cédula no exista.");
            }
        });

        btnModificar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Seleccione un cliente de la tabla primero.");
                return;
            }
            Cliente c = new Cliente();
            c.setIdCliente(Integer.parseInt(txtId.getText()));
            c.setCedula(txtCedula.getText());
            c.setNombre(txtNombre.getText());
            c.setDireccion(txtDireccion.getText());
            if (negocio.modificar(c) == 1) {
                lblMensaje.setText("Cliente modificado correctamente.");
                cargarTabla();
                limpiar();
            } else {
                lblMensaje.setText("Error al modificar cliente.");
            }
        });

        btnEliminar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Seleccione un cliente de la tabla primero.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar este cliente?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    if (negocio.eliminar(Integer.parseInt(txtId.getText())) == 1) {
                        lblMensaje.setText("Cliente eliminado correctamente.");
                        cargarTabla();
                        limpiar();
                    } else {
                        lblMensaje.setText("Error al eliminar cliente.");
                    }
                }
            });
        });

        btnLimpiar.setOnAction(e -> limpiar());

        // ── Eventos Búsqueda ──
        btnBuscarCampo.setOnAction(e -> {
            String val = txtBuscar.getText().trim();
            if (val.isEmpty()) {
                cargarTabla();
                return;
            }
            // Siempre buscar por todos los campos
            var lista = negocio.buscarPorCampo(val);
            datos.clear();
            if (lista != null && !lista.isEmpty()) {
                datos.addAll(lista);
                lblMensaje.setText(lista.size() + " registro(s) encontrado(s).");
            } else {
                lblMensaje.setText("No se encontraron registros.");
            }
        });

        // Buscar al presionar Enter
        txtBuscar.setOnAction(e -> btnBuscarCampo.fire());

        btnMostrarTodos.setOnAction(e -> {
            txtBuscar.clear();
            cargarTabla();
            lblMensaje.setText("");
        });

        // Seleccionar fila en tabla
        tabla.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        txtId.setText(String.valueOf(newVal.getIdCliente()));
                        txtCedula.setText(newVal.getCedula());
                        txtNombre.setText(newVal.getNombre());
                        txtDireccion.setText(newVal.getDireccion() != null
                                ? newVal.getDireccion() : "");
                        lblMensaje.setText("");
                    }
                });

        cargarTabla();

        Scene scene = new Scene(layout, 700, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarTabla() {
        datos.clear();
        var lista = negocio.listarTodos();
        if (lista != null) datos.addAll(lista);
    }

    private void limpiar() {
        txtId.clear();
        txtCedula.clear();
        txtNombre.clear();
        txtDireccion.clear();
        txtBuscar.clear();
        lblMensaje.setText("");
        tabla.getSelectionModel().clearSelection();
    }
}