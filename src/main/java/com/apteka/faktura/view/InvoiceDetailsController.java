package com.apteka.faktura.view;

import com.apteka.faktura.MainApp;
import com.apteka.faktura.core.InvoiceService;
import com.apteka.faktura.model.Invoice;
import com.apteka.faktura.model.InvoicePosition;
import com.cathive.fx.guice.FXMLController;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.prism.impl.Disposer.Record;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
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
    TextField amount;
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
    @FXML
    private ImageView imgWebCamCapturedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();


    @Inject
    @Named("i18n-resources")
    private ResourceBundle resources;

    private ObservableList<InvoicePosition> invoicePositionData = FXCollections.observableArrayList();
    private Invoice invoice;
    private Map<InvoicePosition, Integer> dataToRevert = new HashMap<>();

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

        ChangeListener<Boolean> badean = (arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                List<InvoicePosition> invoicePositions = getCurrentInvoicePosition();
                if (!invoicePositions.isEmpty() && getAmount() > 0) {
                    addAmountToInvoicePositions(invoicePositions);
                    revertButton.setDisable(dataToRevert.isEmpty());
                    imgWebCamCapturedImage.setImage(new Image(takeImage()));
                } else {
                    if (invoicePositions.isEmpty()) {
                        statusLabel.setText("badean");
                    } else {
                        statusLabel.setText("negativeAmount");
                    }
                    revertButton.setDisable(true);
                }
            }
        };
        ean.focusedProperty().addListener(badean);
        revertButton.setOnMouseClicked(event -> {
            if (!dataToRevert.isEmpty()) {
                for (Map.Entry<InvoicePosition, Integer> toRevert : dataToRevert.entrySet()) {
                    toRevert.getKey().addAmount(-toRevert.getValue());
                    invoiceService.saveInvoice(toRevert.getKey(), -toRevert.getValue());
                    refreshTable();
                }
                dataToRevert.clear();
                revertButton.setDisable(true);
            }
        });
    }

    int addAmountToInvoicePositions(List<InvoicePosition> invoicePositions) {
        dataToRevert.clear();
        int amount = getAmount();
        for (InvoicePosition ip : invoicePositions) {
            while (ip.isOpen()) {
                int prevAmount = amount;
                amount = addAmountToInvoice(ip, amount);
                dataToRevert.put(ip,prevAmount-amount);
                if(amount<=0){
                    break;
                }
            }
        }
        if(amount>0){
            InvoicePosition current = invoicePositions.get(0);
            current.forceAddAmount(amount);
            if (dataToRevert.containsKey(current)) {
                dataToRevert.put(current,dataToRevert.get(current)+amount);
            } else {
                dataToRevert.put(current,amount);
            }
            invoiceService.saveInvoice(current, amount);
            refreshTable();
            seriaLabel.setText(current.getSeria());
            nameLabel.setText(current.getNazwa());
            priceLabel.setText(Double.valueOf(current.getCena()).toString());
            dataLabel.setText(new SimpleDateFormat("dd-MM-yyyy").format(current.getData()));
            statusLabel.setText(current.getStatus());
            amount = 0;
        }
        return amount;
    }

    private InputStream takeImage() {
        BufferedImage image = MainApp.webcam.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private int addAmountToInvoice(InvoicePosition current, int amount) {
        int prevAmount = amount;
        amount = current.addAmount(amount);
        invoiceService.saveInvoice(current, prevAmount - amount);
        refreshTable();
        seriaLabel.setText(current.getSeria());
        nameLabel.setText(current.getNazwa());
        priceLabel.setText(Double.valueOf(current.getCena()).toString());
        dataLabel.setText(new SimpleDateFormat("dd-MM-yyyy").format(current.getData()));
        statusLabel.setText(current.getStatus());
        return amount;
    }

    private List<InvoicePosition> getCurrentInvoicePosition() {
        List<InvoicePosition> ret = invoicePositionData.stream()
                .filter(ip -> ip.getKodKr().equals(ean.getText()))
                .filter(ip -> ip.isOpen())
                .collect(Collectors.toList());
        //jezeli nie ma znalezionej otwartej pozycji ale chce dalej dodac to wez wszystkie tez pelne wpisy
        if (ret.isEmpty()) {
            ret = invoicePositionData.stream()
                    .filter(ip -> ip.getKodKr().equals(ean.getText()))
                    .collect(Collectors.toList());
        }
        return ret;
    }

    private int getAmount() {
        return this.amount.getText() == null || this.amount.getText().isEmpty() ? 1 : Integer.valueOf(this.amount.getText());
    }

    private void refreshTable() {
        Collections.sort(invoicePositionData);
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
            } else if (invoicePosition.isZero()) {
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
        List<InvoicePosition> invoicePositions = invoiceService.getInvoicePositions(invoice.getDokfId());
        Collections.sort(invoicePositions);
        invoicePositionData.addAll(invoicePositions);
    }

    public Long getAmountOfProcessedGoods() {
        return invoicePositionData.stream()
                .filter(ip->!ip.isOpen())
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
                Collections.sort(invoicePositionData);
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                InvoicePosition current = (InvoicePosition) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                if(current.isZero()){
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
