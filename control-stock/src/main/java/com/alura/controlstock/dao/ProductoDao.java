package com.alura.controlstock.dao;

import com.alura.controlstock.conexion.ConnectionFactory;
import com.alura.controlstock.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {
    private ConnectionFactory fabricaConexion;

    public ProductoDao() {
        this.fabricaConexion = new ConnectionFactory();
    }

    public boolean registrar(Producto producto) throws SQLException {
        Integer cantidad = producto.getCantidad();
        Integer maximoCantidad = 50;

        String SQL = "INSERT INTO producto(nombre, descripcion, cantidad) values(?,?,?)";

        final Connection con = this.fabricaConexion.recuperaConexion();
        //PreparedStatement evita inyección de código
        final PreparedStatement sentencia = con.prepareStatement(SQL);
        try (con; sentencia) {
            //Tomamos el control de la transacción con setAutoCommit
            con.setAutoCommit(false);
            sentencia.setString(1, producto.getNombre());
            sentencia.setString(2, producto.getDescripcion());

            if (cantidad <= maximoCantidad) {
                sentencia.setInt(3, cantidad);
                sentencia.executeUpdate();
            } else {
                int cantidadTotal = cantidad;
                do {
                    int cantidadParaGuardar = Math.min(cantidadTotal, maximoCantidad);
                    sentencia.setInt(3, cantidadParaGuardar);
                    sentencia.executeUpdate();
                    cantidadTotal -= maximoCantidad;
                } while (cantidadTotal > 0);
            }
            //Ejecutamos los cambios
            con.commit();
            return true;
        } catch (SQLException e) {
            //Se cancela toda la operación
            con.rollback();
            System.err.println("Ocurrió un error al registrar el producto");
            System.err.println("Mensaje del error" + e.getMessage());
            System.err.println("Detalle del error: ");
            e.printStackTrace();
            throw e;
        }
    }

    public List<Producto> listar() throws SQLException {
        String SQL = "SELECT * FROM producto;";
        final Connection con = this.fabricaConexion.recuperaConexion();
        final PreparedStatement sentencia = con.prepareStatement(SQL);
        final ResultSet data = sentencia.executeQuery();

        try (con; sentencia; data) {
            List<Producto> listaProductos = new ArrayList<>();
            while (data.next()) {
                Producto producto = new Producto();
                producto.setId(data.getInt(1));
                producto.setNombre(data.getString(2));
                producto.setDescripcion(data.getString(3));
                producto.setCantidad(data.getInt(4));
                listaProductos.add(producto);
            }
            return listaProductos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto", e);
        }

    }

    public boolean editar(Producto producto) throws SQLException {

        String SQL = "UPDATE producto set nombre=?, descripcion=?, cantidad=? WHERE id=?";
        final Connection con = this.fabricaConexion.recuperaConexion();
        final PreparedStatement sentencia = con.prepareStatement(SQL);
        try (con; sentencia) {
            sentencia.setString(1, producto.getNombre());
            sentencia.setString(2, producto.getDescripcion());
            sentencia.setInt(3, producto.getCantidad());
            sentencia.setInt(4, producto.getId());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException  e) {
            throw new RuntimeException("Error al eliminar el producto", e);
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String SQL = "DELETE from producto where id = ?";
        final Connection con = this.fabricaConexion.recuperaConexion();
        final PreparedStatement sentencia = con.prepareStatement(SQL);
        try (con; sentencia) {
            sentencia.setInt(1, id);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException  e) {
            throw new RuntimeException("Error al eliminar el producto", e);
        }
    }
}