package Utils;

import Domain.Comanda;
import Domain.Produs;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.text.NumberFormat;

public class TableUtils {
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public static void setupProductTable(TableView<Produs> table) {
        table.getColumns().clear();

        TableColumn<Produs, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Produs, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        TableColumn<Produs, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));

        TableColumn<Produs, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));
        priceColumn.setCellFactory(column -> new TableCell<Produs, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", price));
                }
            }
        });

        table.getColumns().addAll(idColumn, categoryColumn, nameColumn, priceColumn);
    }

    public static void setupOrderTable(TableView<Comanda> table) {
        table.getColumns().clear();

        TableColumn<Comanda, Integer> idColumn = new TableColumn<>("Order ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Comanda, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<Comanda, String> productsColumn = new TableColumn<>("Products");
        productsColumn.setCellValueFactory(cell -> {
            Comanda comanda = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    comanda.getProduseCantitate().entrySet().stream()
                            .map(e -> e.getKey().getNume() + " (" + e.getValue() + ")")
                            .collect(java.util.stream.Collectors.joining(", "))
            );
        });

        TableColumn<Comanda, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getTotal()).asObject()
        );
        totalColumn.setCellFactory(column -> new TableCell<Comanda, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(total));
                }
            }
        });

        table.getColumns().addAll(idColumn, dateColumn, productsColumn, totalColumn);
    }
}