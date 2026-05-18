package com.sistema.inventario.presentacion;

import com.sistema.inventario.modelo.Articulo;
import com.sistema.inventario.negocio.NegocioArticulo;
import java.math.BigDecimal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaArticulo {

    private NegocioArticulo negocio = new NegocioArticulo();

    private TextField txtId = new TextField();
    private TextField txtNombre = new TextField();
    private TextField txtPrecio = new TextField();
    private TextField txtBuscar = new TextField();
    private Label lblMensaje = new Label();

    private TableView<Articulo> tabla = new TableView<>();
    private ObservableList<Articulo> datos = FXCollections.observableArrayList();

    public void mostrar(Stage stage) {
        stage.setTitle("Inventario | Gestión de Artículos");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(15));

        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f0f0f0;");

        form.add(new Label("ID:"), 0, 0); form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1); form.add(txtNombre, 1, 1);
        form.add(new Label("Precio:"), 0, 2); form.add(txtPrecio, 1, 2);

        // ── Buscador ──
        txtBuscar.setPromptText("Buscar por nombre...");
        txtBuscar.setPrefWidth(280);
        Button btnBuscarCampo = new Button("Buscar");
        Button btnMostrarTodos = new Button("Mostrar Todos");
        HBox filaBuscar = new HBox(8, txtBuscar, btnBuscarCampo, btnMostrarTodos);
        filaBuscar.setPadding(new Insets(5, 15, 5, 15));

        // ── Botones CRUD ──
        Button btnNuevo = new Button("Nuevo");
        Button btnInsertar = new Button("Insertar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");

        HBox botones = new HBox(10, btnNuevo, btnInsertar, btnModificar, btnEliminar, btnLimpiar);
        botones.setPadding(new Insets(10));
        botones.setAlignment(Pos.CENTER);

        // ── Tabla ──
        TableColumn<Articulo, BigDecimal> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colId.setPrefWidth(80);

        TableColumn<Articulo, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(250);

        TableColumn<Articulo, BigDecimal> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setPrefWidth(120);

        tabla.getColumns().addAll(colId, colNombre, colPrecio);
        tabla.setItems(datos);
        tabla.setPrefHeight(250);

        lblMensaje.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        VBox layout = new VBox(10, form, botones, filaBuscar, lblMensaje, tabla);
        layout.setPadding(new Insets(10));

        // ── Eventos CRUD ──
        btnNuevo.setOnAction(e -> {
            // Aquí pedimos el Max+1 a la base de datos
            txtId.setText(String.valueOf(negocio.obtenerSiguienteId()));
            txtNombre.clear();
            txtPrecio.clear();
            lblMensaje.setText("");
            lblMensaje.setStyle("-fx-text-fill: blue;");
        });

        btnInsertar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Presione 'Nuevo' primero.");
                lblMensaje.setStyle("-fx-text-fill: red;");
                return;
            }
            if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
                lblMensaje.setText("El nombre y precio son obligatorios.");
                lblMensaje.setStyle("-fx-text-fill: red;");
                return;
            }
            
            try {
                Articulo a = new Articulo();
                // Asignamos el ID manualmente leyendo el Textfield
                a.setIdArticulo(new BigDecimal(txtId.getText()));
                a.setNombre(txtNombre.getText());
                a.setPrecio(new BigDecimal(txtPrecio.getText()));
                
                if (negocio.insertar(a) == 1) {
                    lblMensaje.setStyle("-fx-text-fill: blue;");
                    lblMensaje.setText("Artículo insertado correctamente.");
                    cargarTabla();
                    limpiar();
                } else {
                    lblMensaje.setStyle("-fx-text-fill: red;");
                    lblMensaje.setText("Error al insertar el artículo.");
                }
            } catch (NumberFormatException ex) {
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText("Error: El precio debe ser un número válido.");
            }
        });

        btnModificar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Seleccione un artículo de la tabla primero.");
                lblMensaje.setStyle("-fx-text-fill: red;");
                return;
            }
            
            try {
                Articulo a = new Articulo();
                a.setIdArticulo(new BigDecimal(txtId.getText()));
                a.setNombre(txtNombre.getText());
                a.setPrecio(new BigDecimal(txtPrecio.getText()));
                
                if (negocio.modificar(a) == 1) {
                    lblMensaje.setStyle("-fx-text-fill: blue;");
                    lblMensaje.setText("Artículo modificado correctamente.");
                    cargarTabla();
                    limpiar();
                } else {
                    lblMensaje.setStyle("-fx-text-fill: red;");
                    lblMensaje.setText("Error al modificar el artículo.");
                }
            } catch (NumberFormatException ex) {
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText("Error: El precio debe ser un número válido.");
            }
        });

        btnEliminar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Seleccione un artículo de la tabla primero.");
                lblMensaje.setStyle("-fx-text-fill: red;");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar este artículo?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    BigDecimal idAEliminar = new BigDecimal(txtId.getText());
                    if (negocio.eliminar(idAEliminar) == 1) {
                        lblMensaje.setStyle("-fx-text-fill: blue;");
                        lblMensaje.setText("Artículo eliminado correctamente.");
                        cargarTabla();
                        limpiar();
                    } else {
                        lblMensaje.setStyle("-fx-text-fill: red;");
                        lblMensaje.setText("Error al eliminar el artículo.");
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
            var lista = negocio.buscarPorNombre(val);
            datos.clear();
            if (lista != null && !lista.isEmpty()) {
                datos.addAll(lista);
                lblMensaje.setStyle("-fx-text-fill: blue;");
                lblMensaje.setText(lista.size() + " registro(s) encontrado(s).");
            } else {
                lblMensaje.setStyle("-fx-text-fill: red;");
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
                        txtId.setText(String.valueOf(newVal.getIdArticulo()));
                        txtNombre.setText(newVal.getNombre());
                        txtPrecio.setText(String.valueOf(newVal.getPrecio()));
                        lblMensaje.setText("");
                    }
                });

        cargarTabla();

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarTabla() {
        datos.clear();
        var lista = negocio.listarTodos();
        if (lista != null) {
            datos.addAll(lista);
        }
    }

    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtBuscar.clear();
        lblMensaje.setText("");
        tabla.getSelectionModel().clearSelection();
    }
}