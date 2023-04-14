package com.alura.controlstock.dao;

import com.alura.controlstock.conexion.ConexionMySQL;
import com.alura.controlstock.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {
    private ConexionMySQL fabricaConexion;

    public ProductoDao() {
        this.fabricaConexion = new ConexionMySQL();
    }

    public boolean registrar(Producto producto) throws SQLException {
        String nombre = producto.getNombre();
        String descripcion = producto.getDescripcion();
        Integer cantidad = producto.getCantidad();
        Integer maximoCantidad = 50;

        try {

            Connection con = this.fabricaConexion.getConnection();
            String SQL = "INSERT INTO producto(nombre, descripcion, cantidad) values(?,?,?)";
            PreparedStatement sentencia = con.prepareStatement(SQL);
            sentencia.setString(1, producto.getNombre());
            sentencia.setString(2, producto.getDescripcion());
            sentencia.setInt(3, producto.getCantidad());


            sentencia.executeUpdate();
            sentencia.close();
            return true;

        } catch (Exception e) {
            System.err.println("Ocurrió un error al registrar el producto");
            System.err.println("Mensaje del error" + e.getMessage());
            System.err.println("Detalle del error: ");
            e.printStackTrace();
            return false;
        }

    }


    public List<Producto> listar() throws SQLException {
        List<Producto> listaProductos = new ArrayList<>();
        Connection con = this.fabricaConexion.getConnection();
        try {

            String SQL = "SELECT * FROM producto;";
            PreparedStatement sentencia = con.prepareStatement(SQL);
            ResultSet data = sentencia.executeQuery();
            while (data.next()) {
                Producto producto = new Producto();
                producto.setId(data.getInt(1));
                producto.setNombre(data.getString(2));
                producto.setDescripcion(data.getString(3));
                producto.setCantidad(data.getInt(4));
                listaProductos.add(producto);
            }
            data.close();
            sentencia.close();


        } catch (Exception e) {
            System.err.println("Ocurrió un error al mostrar los productos");
            System.err.println("Mensaje del error" + e.getMessage());
            System.err.println("Detalle del error: ");
            e.printStackTrace();
        }

        return listaProductos;
    }

    public boolean editar(Producto producto) {

        try {
            String SQL = "UPDATE producto set nombre=?, descripcion=?, cantidad=? "
                    + "WHERE id=?";
            Connection con = this.fabricaConexion.getConnection();
            PreparedStatement sentencia = con.prepareStatement(SQL);
            sentencia.setString(1, producto.getNombre());
            sentencia.setString(2, producto.getDescripcion());
            sentencia.setInt(3, producto.getCantidad());

            sentencia.setInt(4, producto.getId());

            sentencia.executeUpdate();
            sentencia.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrió un error al editar el producto");
            System.err.println("Mensaje del error" + e.getMessage());
            System.err.println("Detalle del error: ");
            e.printStackTrace();

            return false;
        }

    }

    public boolean eliminar( int id) {

        try {
            String SQL = "DELETE from producto where id = ?";
            Connection con = this.fabricaConexion.getConnection();
            PreparedStatement sentencia = con.prepareStatement(SQL);
            sentencia.setInt(1,id);
            sentencia.executeUpdate();
            sentencia.close();

            return true;
        } catch (Exception e) {
            System.err.println("Ocurrió un error al eliminar el producto");
            System.err.println("Mensaje del error" + e.getMessage());
            System.err.println("Detalle del error: ");
            e.printStackTrace();

            return false;
        }
    }

}
