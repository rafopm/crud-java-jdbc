package com.alura.controlstock.dao;

import com.alura.controlstock.conexion.ConnectionFactory;
import com.alura.controlstock.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {
    private ConnectionFactory fabricaConexion;
    public CategoriaDao() {
        this.fabricaConexion = new ConnectionFactory();
    }

    public List<Categoria> listar() {
        String SQL = "SELECT * FROM categoria;";
        final Connection con = this.fabricaConexion.recuperaConexion();
        try (con) {
            final PreparedStatement sentencia = con.prepareStatement(SQL);
            final ResultSet data = sentencia.executeQuery();
            try (sentencia; data) {
                List<Categoria> listaCategoria = new ArrayList<>();
                while (data.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setId(data.getInt(1));
                    categoria.setNombre(data.getString(2));
                    listaCategoria.add(categoria);
                }
                return listaCategoria;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el categoria", e);
        }
    }


    public List<Categoria> obtenerCategoriaPorId(int id) {
        String SQL = "SELECT * FROM categoria WHERE id=?;";
        final Connection con = this.fabricaConexion.recuperaConexion();
        try (con) {
            final PreparedStatement sentencia = con.prepareStatement(SQL);
            sentencia.setInt(1, id);
            final ResultSet data = sentencia.executeQuery();

            try (sentencia; data) {
                List<Categoria> listaCategoria = new ArrayList<>();
                while (data.next()) {
                    Categoria categoria = new Categoria();
                    categoria.setId(data.getInt(1));
                    categoria.setNombre(data.getString(2));
                    listaCategoria.add(categoria);
                }
                System.out.println(listaCategoria);
                return listaCategoria;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el categoria", e);
        }
    }

    public String obtenerNombreCategoria(int id) {
        String nombreCategoria = null;
        final Connection con = this.fabricaConexion.recuperaConexion();
        try(con)  {
            String query = "SELECT nombre FROM categoria WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombreCategoria = rs.getString("nombre");
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener el nombre de la categoría: " + ex.getMessage());
        }
        return nombreCategoria;
    }

}