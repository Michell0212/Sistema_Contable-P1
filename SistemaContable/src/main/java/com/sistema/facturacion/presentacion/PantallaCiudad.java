package com.sistema.facturacion.presentacion;

import com.sistema.facturacion.modelo.CiudadEntrega;
import com.sistema.facturacion.negocio.NegocioCiudad;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaCiudad {

    private NegocioCiudad negocio = new NegocioCiudad();

    private TextField txtId      = new TextField();
    private TextField txtNombre  = new TextField();
    private TextField txtBuscar  = new TextField();
    private Label     lblMensaje = new Label();

    private TableView<CiudadEntrega> tabla = new TableView<>();
    private ObservableList<CiudadEntrega> datos = FXCollections.observableArrayList();

    public void mostrar(Stage stage) {
        stage.setTitle("Facturación | Ciudad de Entrega");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(15));

        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f0f0f0;");

        form.add(new Label("ID:"),     0, 0); form.add(txtId,      1, 0);
        form.add(new Label("Nombre:"), 0, 1); form.add(txtNombre,  1, 1);

        // ── Buscador ──
        txtBuscar.setPromptText("Buscar por ID o nombre...");
        txtBuscar.setPrefWidth(280);
        Button btnBuscarCampo  = new Button("Buscar");
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
        TableColumn<CiudadEntrega, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idCiudad"));
        colId.setPrefWidth(80);

        TableColumn<CiudadEntrega, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(300);

        tabla.getColumns().addAll(colId, colNombre);
        tabla.setItems(datos);
        tabla.setPrefHeight(250);

        lblMensaje.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        VBox layout = new VBox(10, form, botones, filaBuscar, lblMensaje, tabla);
        layout.setPadding(new Insets(10));

        // ── Eventos CRUD ──
        btnNuevo.setOnAction(e -> {
            txtId.setText(String.valueOf(negocio.obtenerSiguienteId()));
            txtNombre.clear();
            lblMensaje.setText("");
        });

        btnInsertar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Presione 'Nuevo' primero.");
                return;
            }
            if (txtNombre.getText().isEmpty()) {
                lblMensaje.setText("El nombre es obligatorio.");
                return;
            }
            CiudadEntrega c = new CiudadEntrega();
            c.setIdCiudad(Integer.parseInt(txtId.getText()));
            c.setNombre(txtNombre.getText());
            if (negocio.insertar(c) == 1) {
                lblMensaje.setText("Ciudad insertada correctamente.");
                cargarTabla();
                limpiar();
            } else {
                lblMensaje.setText("Error al insertar ciudad.");
            }
        });

        btnModificar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Seleccione una ciudad de la tabla primero.");
                return;
            }
            CiudadEntrega c = new CiudadEntrega();
            c.setIdCiudad(Integer.parseInt(txtId.getText()));
            c.setNombre(txtNombre.getText());
            if (negocio.modificar(c) == 1) {
                lblMensaje.setText("Ciudad modificada correctamente.");
                cargarTabla();
                limpiar();
            } else {
                lblMensaje.setText("Error al modificar ciudad.");
            }
        });

        btnEliminar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Seleccione una ciudad de la tabla primero.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar esta ciudad?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    if (negocio.eliminar(Integer.parseInt(txtId.getText())) == 1) {
                        lblMensaje.setText("Ciudad eliminada correctamente.");
                        cargarTabla();
                        limpiar();
                    } else {
                        lblMensaje.setText("Error al eliminar ciudad.");
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
            // Buscar por todos los campos
            var lista = negocio.buscarPorNombre(val);
            datos.clear();
            if (lista != null && !lista.isEmpty()) {
                datos.addAll(lista);
                lblMensaje.setText(lista.size() + " registro(s) encontrado(s).");
            } else {
                lblMensaje.setText("No se encontraron registros.");
            }
        });

        txtBuscar.setOnAction(e -> btnBuscarCampo.fire());

        btnMostrarTodos.setOnAction(e -> {
            txtBuscar.clear();
            cargarTabla();
            lblMensaje.setText("");
        });

        // Seleccionar fila en tabla
        tabla.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        txtId.setText(String.valueOf(newVal.getIdCiudad()));
                        txtNombre.setText(newVal.getNombre());
                        lblMensaje.setText("");
                    }
                });

        cargarTabla();

        Scene scene = new Scene(layout, 550, 500);
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
        txtNombre.clear();
        txtBuscar.clear();
        lblMensaje.setText("");
        tabla.getSelectionModel().clearSelection();
    }
}