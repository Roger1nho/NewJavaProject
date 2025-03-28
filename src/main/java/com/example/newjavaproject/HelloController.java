package com.example.newjavaproject;

import Domain.Comanda;
import Domain.Produs;
import Repository.RepositoryException;
import Repository.SQLComandaRepository;
import Repository.SQLProdusRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class HelloController {
    // Product UI Components
    @FXML private TableView<Produs> productTable;
    @FXML private TextField productIdField;
    @FXML private TextField productNameField;
    @FXML private TextField productCategoryField;
    @FXML private TextField productPriceField;

    // Order UI Components
    @FXML private TableView<Comanda> orderTable;
    @FXML private TextField orderIdField;
    @FXML private TextField orderDateField;
    @FXML private ListView<Produs> availableProductsList;
    @FXML private ListView<Produs> selectedProductsList;
    @FXML private Spinner<Integer> quantitySpinner;

    // Data
    private ObservableList<Produs> products = FXCollections.observableArrayList();
    private ObservableList<Comanda> orders = FXCollections.observableArrayList();
    private ObservableList<Produs> availableProducts = FXCollections.observableArrayList();
    private ObservableList<Produs> selectedProducts = FXCollections.observableArrayList();

    // Repositories
    private SQLProdusRepository productRepository = new SQLProdusRepository();
    private SQLComandaRepository orderRepository = new SQLComandaRepository();

    public HelloController() throws RepositoryException {
    }

    @FXML
    public void initialize() throws RepositoryException {
        productTable.setItems(products);
        products.addAll(productRepository.findAll());

        orderTable.setItems(orders);
        orders.addAll(orderRepository.findAll());

        availableProducts.setAll(productRepository.findAll());
        availableProductsList.setItems(availableProducts);
        selectedProductsList.setItems(selectedProducts);

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> showProductDetails(newSelection));

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> showOrderDetails(newSelection));
    }

    private void showProductDetails(Produs product) {
        if (product != null) {
            productIdField.setText(String.valueOf(product.getId()));
            productNameField.setText(product.getNume());
            productCategoryField.setText(product.getCategorie());
            productPriceField.setText(String.valueOf(product.getPret()));
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            Produs product = new Produs(
                    Integer.parseInt(productIdField.getText()),
                    productCategoryField.getText(),
                    productNameField.getText(),
                    Double.parseDouble(productPriceField.getText())
            );
            productRepository.add(product);
            products.add(product);
            availableProducts.add(product);
            clearProductFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to add product", e.getMessage());
        }
    }


    private void showOrderDetails(Comanda order) {
        if (order != null) {
            orderIdField.setText(String.valueOf(order.getId()));
            orderDateField.setText(order.getData());
            selectedProducts.setAll(order.getProduseCantitate().keySet());
        }
    }

    @FXML
    private void handleCreateOrder() {
        try {
            Map<Produs, Integer> productsWithQuantities = new HashMap<>();
            for (Produs product : selectedProducts) {
                productsWithQuantities.put(product, quantitySpinner.getValue());
            }

            Comanda order = new Comanda(
                    Integer.parseInt(orderIdField.getText()),
                    productsWithQuantities,
                    orderDateField.getText()
            );
            orderRepository.add(order);
            orders.add(order);
            clearOrderFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to create order", e.getMessage());
        }
    }


    private void clearProductFields() {
        productIdField.clear();
        productNameField.clear();
        productCategoryField.clear();
        productPriceField.clear();
    }

    private void clearOrderFields() {
        orderIdField.clear();
        orderDateField.clear();
        selectedProducts.clear();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAddProductToOrder() {
        Produs selected = availableProductsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedProducts.add(selected);
            availableProducts.remove(selected);
        }
    }

    @FXML
    private void handleRemoveProductFromOrder() {
        Produs selected = selectedProductsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            availableProducts.add(selected);
            selectedProducts.remove(selected);
        }
    }
}