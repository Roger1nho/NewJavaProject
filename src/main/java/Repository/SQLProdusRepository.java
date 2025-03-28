package Repository;

import Domain.Produs;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class SQLProdusRepository implements IRepo<Produs>, AutoCloseable {
    private static final String DB_URL = "jdbc:sqlite:database.db";
    private Connection connection;

    public SQLProdusRepository() throws RepositoryException {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    private void initializeDatabase() throws RepositoryException {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY, " +
                "categorie TEXT NOT NULL, " +
                "nume TEXT NOT NULL, " +
                "pret REAL NOT NULL)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Failed to initialize products table", e);
        }
    }

    @Override
    public void add(Produs product) throws RepositoryException {
        String sql = "INSERT INTO products(id, categorie, nume, pret) VALUES(?,?,?,?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, product.getId());
            pstmt.setString(2, product.getCategorie());
            pstmt.setString(3, product.getNume());
            pstmt.setString(4, product.getPret());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to add product", e);
        }
    }

    @Override
    public void update(Produs product) throws RepositoryException {
        String sql = "UPDATE products SET categorie = ?, nume = ?, pret = ? WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, product.getCategorie());
            pstmt.setString(2, product.getNume());
            pstmt.setString(3, product.getPret());
            pstmt.setInt(4, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to update product", e);
        }
    }

    @Override
    public void delete(Produs product) throws RepositoryException, IOException {
        String sql = "DELETE FROM products WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, product.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Failed to delete product", e);
        }
    }

    @Override
    public Produs findById(int id) throws RepositoryException {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Produs(
                        rs.getInt("id"),
                        rs.getString("categorie"),
                        rs.getString("nume"),
                        rs.getDouble("pret")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find product", e);
        }
    }

    @Override
    public List<Produs> findAll() throws RepositoryException {
        List<Produs> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Produs(
                        rs.getInt("id"),
                        rs.getString("categorie"),
                        rs.getString("nume"),
                        rs.getDouble("pret")
                ));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to retrieve products", e);
        }
        return products;
    }

    @Override
    public void refresh() throws RepositoryException {

    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}