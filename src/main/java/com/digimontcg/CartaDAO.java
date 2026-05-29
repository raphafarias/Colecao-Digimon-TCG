package com.digimontcg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartaDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/digimon_tcg?useTimezone=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "SUA_SENHA_AQUI"; // Substitua pela sua senha do MySQL

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // Retorna um array com [Total no Catálogo, Total Únicos na Coleção, Volume Físico Total]
    public int[] getEstatisticas() {
        int[] stats = {0, 0, 0};
        String sql = "SELECT COUNT(*), SUM(CASE WHEN quantidade > 0 THEN 1 ELSE 0 END), SUM(quantidade) FROM cartas";
        try (Connection conn = conectar(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                stats[0] = rs.getInt(1); // Total de registros no banco
                stats[1] = rs.getInt(2); // Quantidade de cartas únicas que o usuário tem (Qtd > 0)
                stats[2] = rs.getInt(3); // Soma de todas as cópias físicas
            }
        } catch (SQLException e) { 
            System.err.println("Erro ao buscar estatísticas: " + e.getMessage()); 
        }
        return stats;
    }

    public void salvar(Carta carta) {
        String sql = "INSERT INTO cartas (codigo, nome, cor, nivel, imagem_url, quantidade) VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nome = ?, cor = ?, nivel = ?, imagem_url = ?, quantidade = cartas.quantidade";
        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, carta.getCodigo());
            stmt.setString(2, carta.getName());
            stmt.setString(3, carta.getColor());
            stmt.setInt(4, carta.getLevel());
            stmt.setString(5, carta.getImagemUrl());
            stmt.setInt(6, 0); 

            stmt.setString(7, carta.getName());
            stmt.setString(8, carta.getColor());
            stmt.setInt(9, carta.getLevel());
            stmt.setString(10, carta.getImagemUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar/atualizar carta: " + e.getMessage());
        }
    }

    public void atualizarQuantidade(String codigo, int novaQuantidade) {
        String sql = "UPDATE cartas SET quantidade = ? WHERE codigo = ?";
        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.max(0, novaQuantidade));
            stmt.setString(2, codigo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar quantidade: " + e.getMessage());
        }
    }

    public List<Carta> listarTodos() {
        List<Carta> lista = new ArrayList<>();
        String sql = "SELECT * FROM cartas ORDER BY nome ASC";
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Carta c = new Carta();
                c.setCodigo(rs.getString("codigo"));
                c.setName(rs.getString("nome"));
                c.setColor(rs.getString("cor"));
                c.setLevel(rs.getInt("nivel"));
                c.setImagemUrl(rs.getString("imagem_url"));
                c.setQuantidade(rs.getInt("quantidade"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar cartas: " + e.getMessage());
        }
        return lista;
    }
}