package com.alura.controlstock.controller;

import com.alura.controlstock.dao.CategoriaDao;
import com.alura.controlstock.dao.ProductoDao;
import com.alura.controlstock.model.Categoria;
import com.alura.controlstock.model.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;
import org.w3c.dom.events.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewProductoController implements Initializable {
    private final int itemsPerPage = 10;
    @FXML
    private Button btnReporte;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;
    @FXML
    private TableColumn<Producto, Integer> idCol;
    @FXML
    private TableColumn<Producto, String> nombreCol;
    @FXML
    private TableColumn<Producto, String> descripcionCol;
    @FXML
    private TableColumn<Producto, Integer> cantidadCol;
    @FXML
    private TableColumn<Producto, Integer> categoriaCol;
    @FXML
    private Button btnGuardar;
    @FXML
    private ListView<String> lvCategoria;
    @FXML
    private TextField txtCantidad;
    @FXML
    private Button btnCancelar;
    @FXML
    private TableView<Producto> tvProducto;
    @FXML
    private TextField txtDescripcion;
    private Producto productoSelect;
    private ProductoDao productoDao;
    private CategoriaDao categoriaDao;
    private ContextMenu cmOpciones;

    private int idCategoria = 0;
    @FXML
    private Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idCategoria = 0;
        this.categoriaDao = new CategoriaDao();
        List<Categoria> categoria = categoriaDao.listar();
        ObservableList<String> listaCategoria = FXCollections.observableArrayList();
        //ObservableList<Categoria> listaCategoria = FXCollections.observableList(categoria);
        for (Categoria c : categoria) {
            listaCategoria.add(c.getNombre());
        }
        lvCategoria.setItems(listaCategoria);

        this.productoDao = new ProductoDao();
        cargarProductos();

        cmOpciones = new ContextMenu();
        MenuItem miEditar = new MenuItem("Editar");
        MenuItem miEliminar = new MenuItem("Eliminar");

        cmOpciones.getItems().addAll(miEditar, miEliminar);
        miEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = tvProducto.getSelectionModel().getSelectedIndex();
                productoSelect = (Producto) tvProducto.getItems().get(index);
                System.out.println("Producto select" + productoSelect);
                txtNombre.setText(productoSelect.getNombre());
                txtDescripcion.setText(productoSelect.getDescripcion());
                txtCantidad.setText(String.valueOf(productoSelect.getCantidad()));
                btnCancelar.setDisable(false);
                // Obtén la categoría seleccionada
                Integer categoriaSeleccionada = tvProducto.getSelectionModel().getSelectedItem().getCategoria_id();
                String categoriaResultado = categoriaDao.obtenerNombreCategoria(categoriaSeleccionada).toString();
                int indice = listaCategoria.indexOf(categoriaResultado);
                if (indice >= 0) {
                    lvCategoria.getSelectionModel().select(indice);
                    lvCategoria.scrollTo(indice);
                }
            }
        });

        miEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int index = tvProducto.getSelectionModel().getSelectedIndex();
                Producto productoEliminar = (Producto) tvProducto.getItems().get(index);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmación");
                alert.setHeaderText(null);
                alert.setContentText("¿Realmente desea eliminar el producto: " + productoEliminar.getNombre() + " ?");
                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    boolean rsp = false;
                    rsp = productoDao.eliminar(productoEliminar.getId());

                    if (rsp) {
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Éxito");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Se eliminó correctamente el producto");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();

                        cargarProductos();
                    } else {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Hubo un error, consulte con el administrador");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();
                    }

                }
            }
        });
        tvProducto.setContextMenu(cmOpciones);
    }

    @FXML
    void btnGuardarOnAction(ActionEvent event) {

        if (productoSelect == null) {
            Producto producto = new Producto();
            producto.setNombre(txtNombre.getText());
            producto.setDescripcion(txtDescripcion.getText());
            producto.setCantidad(Integer.parseInt(txtCantidad.getText()));
            producto.setCategoria_id(idCategoria);

            boolean rsp = false;
            try {
                rsp = this.productoDao.registrar(producto);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (rsp) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Se registró correctamente el producto");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                limpiarCampos();
                cargarProductos();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error, consulte con el administrador");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        } else {
            productoSelect.setNombre(txtNombre.getText());
            productoSelect.setDescripcion(txtDescripcion.getText());
            productoSelect.setCantidad(Integer.parseInt(txtCantidad.getText()));
            if(idCategoria>0 ){
                productoSelect.setCategoria_id(idCategoria);
            } else {
                productoSelect.setCategoria_id(productoSelect.getCategoria_id());
            }
            //usuarioSelect.setIdusuario(txtIdusuario.());
            boolean rsp = this.productoDao.editar(productoSelect);
            if (rsp) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Se guardó correctamente el producto");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();

                limpiarCampos();
                cargarProductos();
                productoSelect = null;
                btnCancelar.setDisable(true);
                idCategoria = 0;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error, consulte con el administrador");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCantidad.setText("");
        idCategoria=0;
    }

    public void cargarProductos() {

        tvProducto.getItems().clear();
        tvProducto.getColumns().clear();
        List<Producto> productosDB = this.productoDao.listar();
        ObservableList<Producto> data = FXCollections.observableArrayList(productosDB);

        idCol.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("id"));
        nombreCol.setCellValueFactory(new PropertyValueFactory<Producto, String>("nombre"));
        descripcionCol.setCellValueFactory(new PropertyValueFactory<Producto, String>("descripcion"));
        cantidadCol.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("cantidad"));
        categoriaCol.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("categoria_id"));

        System.out.println(data);
        tvProducto.setItems(data);
        tvProducto.getColumns().addAll(idCol, nombreCol, descripcionCol, cantidadCol, categoriaCol);
    }

    @FXML
    void btnCancelarOnAction(ActionEvent event) {
        productoSelect = null;
        limpiarCampos();
        btnCancelar.setDisable(true);
    }

    @FXML
    void btnReporteOnAction(ActionEvent event) {
    }

    @FXML
    public void onMouseClicked(javafx.scene.input.MouseEvent mouseEvent) {
        List<Categoria> categoria = categoriaDao.listar();
        String categoriaSeleccionada = lvCategoria.getSelectionModel().getSelectedItem();
        Optional<Categoria> categoriaOptional = categoria.stream()
                .filter(cat -> cat.getNombre().equals(categoriaSeleccionada))
                .findFirst();

        Categoria categoriaEncontrada = categoriaOptional.get();
        idCategoria = categoriaEncontrada.getId();
        System.out.println(idCategoria);
    }
}
