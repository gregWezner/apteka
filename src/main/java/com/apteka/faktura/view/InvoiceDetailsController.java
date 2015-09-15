package com.apteka.faktura.view;

import com.apteka.faktura.core.InvoiceService;
import com.apteka.faktura.model.Invoice;
import com.apteka.faktura.model.InvoicePosition;
import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.prism.impl.Disposer.Record;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@FXMLController
public class InvoiceDetailsController {

    @FXML
    private TableColumn<InvoicePosition, String> nazwa;
    @FXML
    private TableColumn<InvoicePosition, Number> idtowr;
    @FXML
    private TableColumn<InvoicePosition, String> kodkr;
    @FXML
    private TableColumn<InvoicePosition, Number> ilzkl;
    @FXML
    private TableColumn<InvoicePosition, Number> ilakt;
    @FXML
    private TableColumn<InvoicePosition, Number> MNOZN;
    @FXML
    private TableColumn<InvoicePosition, Date> datwz;
    @FXML
    private TableColumn<InvoicePosition, String> seria;
    @FXML
    private TableColumn<InvoicePosition, Number> iddokf;
    @FXML
    private TableColumn<InvoicePosition, Number> id;
    @FXML
    private TableColumn<InvoicePosition, Number> ilosc;
    @FXML
    private TextField amount;
    @FXML
    private TextField ean;
    @FXML
    private Label seriaLabel;
    @FXML
    private Label dataLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Button revertButton;

    @FXML
    private TableView<InvoicePosition> invoicePositionTable;

    @Inject
    private InvoiceService invoiceService;


    @Inject
    @Named("i18n-resources")
    private ResourceBundle resources;

    private ObservableList<InvoicePosition> invoicePositionData = FXCollections.observableArrayList();
    private Invoice invoice;
    private InvoicePosition lastInvoicePosition;
    private int lastAmount;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public InvoiceDetailsController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        nazwa.setCellValueFactory(cellData -> cellData.getValue().nazwaProperty());
        kodkr.setCellValueFactory(cellData -> cellData.getValue().kodKrProperty());
        ilzkl.setCellValueFactory(cellData -> cellData.getValue().ilzklProperty());
        ilosc.setCellValueFactory(cellData -> cellData.getValue().iloscProperty());

        invoicePositionTable.setItems(invoicePositionData);

        invoicePositionTable.setRowFactory(new Callback<TableView<InvoicePosition>, TableRow<InvoicePosition>>() {
            @Override
            public TableRow<InvoicePosition> call(TableView<InvoicePosition> tableView) {
                final TableRow<InvoicePosition> row = new TableRow<InvoicePosition>() {
                    @Override
                    protected void updateItem(InvoicePosition invoicePosition, boolean empty) {
                        super.updateItem(invoicePosition, empty);
                        updateItemStyle(invoicePosition, empty, getStyleClass());
                    }
                };
                return row;
            }
        });

        //Insert Button
        TableColumn col_action = new TableColumn<>("");
        invoicePositionTable.getColumns().add(col_action);

        col_action.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Record, Boolean>,
                        ObservableValue<Boolean>>() {

                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Record, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });

        //Adding the Button to the cell
        col_action.setCellFactory(
                p -> new ButtonCell());

        ean.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                Optional<InvoicePosition> any = getCurrentInvoicePosition();
                if (any.isPresent()) {
                    InvoicePosition current = any.get();
                    lastInvoicePosition = current;
                    int amount = getAmount();
                    lastAmount = amount;
                    addAmountToInvoice(current, amount);
                    revertButton.setDisable(false);
                } else {
                    statusLabel.setText("badean");
                    revertButton.setDisable(true);
                }
            }
        });
        revertButton.setOnMouseClicked(event -> {
            if (lastInvoicePosition !=null) {
                addAmountToInvoice(lastInvoicePosition, -lastAmount);
                revertButton.setDisable(true);
            }
        });
    }

    private void addAmountToInvoice(InvoicePosition current, int amount) {
        current.addAmount(amount);
        invoiceService.saveInvoice(current, amount);
        refreshTable();
        seriaLabel.setText(current.getSeria());
        nameLabel.setText(current.getNazwa());
        priceLabel.setText(Double.valueOf(current.getCena()).toString());
        dataLabel.setText(new SimpleDateFormat("dd-MM-yyyy").format(current.getData()));
        statusLabel.setText(current.getStatus());
    }

    private Optional<InvoicePosition> getCurrentInvoicePosition() {
        Optional<InvoicePosition> any = invoicePositionData.stream()
                .filter(ip -> ip.getKodKr().equals(ean.getText()))
                .filter(ip -> ip.isOpen())
                .findFirst();
        //jezeli nie ma znalezionej pozycji ale chce dalej dodac to wez pierwsza ktora ma taki sam kod ean
        if (!any.isPresent()) {
            any = invoicePositionData.stream()
                    .filter(ip -> ip.getKodKr().equals(ean.getText()))
                    .findFirst();
        }
        return any;
    }

    private int getAmount() {
        return this.amount.getText() == null || this.amount.getText().isEmpty() ? 1 : Integer.valueOf(this.amount.getText());
    }

    private void refreshTable() {
        invoicePositionTable.getColumns().stream()
                .forEach(col->{
                    col.setVisible(false);
                    col.setVisible(true);
                });
    }

    public void updateItemStyle(InvoicePosition invoicePosition, boolean empty, ObservableList<String> styleClass) {
        if (!empty) {
            if (invoicePosition.isCorrect()) {
                if (!styleClass.contains("correct")) {
                    styleClass.add("correct");
                }
                styleClass.removeAll(Collections.singleton("incorrect"));
            } else if (invoicePosition.isOverflow()) {
                if (!styleClass.contains("incorrect")) {
                    styleClass.add("incorrect");
                }
                styleClass.removeAll(Collections.singleton("correct"));
            } else if (invoicePosition.isToValidate()) {
                styleClass.removeAll(Collections.singleton("correct"));
                styleClass.removeAll(Collections.singleton("incorrect"));
            }
        } else {
            styleClass.removeAll(Collections.singleton("correct"));
            styleClass.removeAll(Collections.singleton("incorrect"));
        }
    }

    public void setInvoice(Invoice invoice) {
        invoicePositionData.removeAll(invoicePositionData);
        invoicePositionData.addAll(invoiceService.getInvoicePositions(invoice.getDokfId()));
    }

    public Long getAmountOfProcessedGoods() {
        return invoicePositionData.stream()
                .filter(ip->!ip.isToValidate())
                .collect(Collectors.counting());
    }

    //Define the button cell
    private class ButtonCell extends TableCell<Record, Boolean> {
        final Button cellButton = new Button("OK");

        ButtonCell(){

            //Action when the button is pressed
            cellButton.setOnAction(t -> {
                // get Selected Item
                InvoicePosition current = (InvoicePosition) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                //remove selected item from the table list
                current.setCorrect();
                invoiceService.saveInvoice(current, current.getIlzkl());
                updateItem(false, false);
                updateItemStyle(current, false, ButtonCell.this.getTableRow().getStyleClass());
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                InvoicePosition current = (InvoicePosition) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                if(current.isToValidate()){
                    setGraphic(cellButton);
                } else {
                    setGraphic(null);
                }
            } else {
                setGraphic(null);
            }
        }
    }

}
