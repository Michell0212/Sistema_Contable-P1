package com.sistema.cxc.presentacion;

import com.sistema.facturacion.presentacion.PantallaCiudad;
import com.sistema.facturacion.presentacion.PantallaCliente;
import com.sistema.facturacion.presentacion.PantallaFactura;
import javafx.application.Application;
import javafx.stage.Stage;

public class PruebaCxC extends Application {

    @Override
    public void start(Stage primaryStage) {

        PantallaFactura pantalla = new PantallaFactura();
        pantalla.mostrar(primaryStage);
    }

    public static void main(String[] args) {

        launch(args);
    }
}