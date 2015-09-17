package com.apteka.faktura.view;

import com.apteka.faktura.MainApp;
import com.apteka.faktura.core.InvoiceService;
import com.apteka.faktura.model.Invoice;
import com.apteka.faktura.model.InvoicePosition;
import com.cathive.fx.guice.FXMLController;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.prism.impl.Disposer.Record;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
public class InvoiceListController {
    @FXML
    private TableView<Invoice> invoiceTable;
    @FXML
    private TableColumn<Invoice, String> invoiceNoColumn;
    @FXML
    private TableColumn<Invoice, Number> percent;
    @FXML
    private TableColumn<Invoice, Number> noOfPositionsColumn;
    @FXML
    private TableColumn<Invoice, Date> issueDateColumn;
    @Inject
    private InvoiceService invoiceService;
    @Inject
    private MainApp mainApp;

    private ObservableList<Invoice> invoiceData = FXCollections.observableArrayList();


    @Inject
    @Named("i18n-resources")
    private ResourceBundle resources;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public InvoiceListController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        // Initialize the InvoiceParameter table with the two columns.
        invoiceNoColumn.setCellValueFactory(cellData -> cellData.getValue().invoiceNoProperty());
        percent.setCellValueFactory(cellData -> cellData.getValue().percentProperty());
        noOfPositionsColumn.setCellValueFactory(cellData -> cellData.getValue().noOfPositionsProperty());
        issueDateColumn.setCellValueFactory(cellData -> cellData.getValue().issueDateProperty());

        initTable();

    }

    private void initTable() {
        invoiceData.clear();
        invoiceData.addAll(invoiceService.getActiveInvoices());
        Collections.sort(invoiceData);
        invoiceTable.setItems(invoiceData);
        invoiceTable.setRowFactory(tv -> {
            TableRow<Invoice> row = new TableRow<Invoice>() {
                @Override
                protected void updateItem(Invoice invoice, boolean empty) {
                    super.updateItem(invoice, empty);
                    if (!empty) {
                        if (invoice.isProcessed()) {
                            if (!getStyleClass().contains("processed")) {
                                getStyleClass().add("processed");
                            }
                        } else {
                            getStyleClass().removeAll(Collections.singleton("processed"));
                        }
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    mainApp.showInvoiceDetails(row.getItem());
                    Collections.sort(invoiceData);
                    percent.setVisible(false);
                    percent.setVisible(true);
                }
            });
            return row;
        });
    }


}
