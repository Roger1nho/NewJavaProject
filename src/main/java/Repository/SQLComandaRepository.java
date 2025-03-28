package Repository;

import Domain.Comanda;
import Domain.Produs;

import java.sql.*;
import java.util.*;
import java.io.IOException;

public class SQLComandaRepository implements IRepo<Comanda>, AutoCloseable {
    private static final String DB_URL = "jdbc:sqlite:database.db";
    private Connection connection;

    public SQLComandaRepository() throws RepositoryException {
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
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create orders table
            String sql = "CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY, " +
                    "data TEXT NOT NULL)";
            stmt.execute(sql);

            // Create order_products junction table
            sql = "CREATE TABLE IF NOT EXISTS order_products (" +
                    "order_id INTEGER, " +
                    "product_id INTEGER, " +
                    "quantity INTEGER, " +
                    "PRIMARY KEY (order_id, product_id), " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(id))";
            stmt.execute(sql);

        } catch (SQLException e) {
            throw new RepositoryException("Failed to initialize orders tables", e);
        }
    }

    @Override
    public void add(Comanda order) throws RepositoryException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Insert order
            String sql = "INSERT INTO orders(id, data) VALUES(?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, order.getId());
                pstmt.setString(2, order.getData());
                pstmt.executeUpdate();
            }

            // Insert order products
            sql = "INSERT INTO order_products(order_id, product_id, quantity) VALUES(?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map.Entry<Produs, Integer> entry : order.getProduseCantitate().entrySet()) {
                    pstmt.setInt(1, order.getId());
                    pstmt.setInt(2, entry.getKey().getId());
                    pstmt.setInt(3, entry.getValue());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            throw new RepositoryException("Failed to add order", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) {}
            }
        }
    }

    @Override
    public void update(Comanda order) throws RepositoryException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE orders SET data = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, order.getData());
                pstmt.setInt(2, order.getId());
                pstmt.executeUpdate();
            }

            sql = "DELETE FROM order_products WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, order.getId());
                pstmt.executeUpdate();
            }

            // Insert new products
            sql = "INSERT INTO order_products(order_id, product_id, quantity) VALUES(?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map.Entry<Produs, Integer> entry : order.getProduseCantitate().entrySet()) {
                    pstmt.setInt(1, order.getId());
                    pstmt.setInt(2, entry.getKey().getId());
                    pstmt.setInt(3, entry.getValue());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            throw new RepositoryException("Failed to update order", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) {}
            }
        }
    }

    @Override
    public void delete(Comanda order) throws RepositoryException, IOException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sql = "DELETE FROM orders WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, order.getId());
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            throw new RepositoryException("Failed to delete order", e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) {}
            }
        }
    }

    @Override
    public Comanda findById(int id) throws RepositoryException {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String data = rs.getString("data");
                Map<Produs, Integer> products = getOrderProducts(id);
                return new Comanda(id, products, data);
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Failed to find order", e);
        }
    }

    @Override
    public List<Comanda> findAll() throws RepositoryException {
        List<Comanda> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String data = rs.getString("data");
                Map<Produs, Integer> products = getOrderProducts(id);
                orders.add(new Comanda(id, products, data));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Failed to retrieve orders", e);
        }
        return orders;
    }

    private Map<Produs, Integer> getOrderProducts(int orderId) throws SQLException {
        Map<Produs, Integer> products = new HashMap<>();
        String sql = "SELECT p.id, p.categorie, p.nume, p.pret, op.quantity " +
                "FROM products p JOIN order_products op ON p.id = op.product_id " +
                "WHERE op.order_id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Produs product = new Produs(
                        rs.getInt("id"),
                        rs.getString("categorie"),
                        rs.getString("nume"),
                        rs.getDouble("pret")
                );
                int quantity = rs.getInt("quantity");
                products.put(product, quantity);
            }
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