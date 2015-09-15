package com.apteka.faktura;

import com.apteka.faktura.model.Invoice;
import com.apteka.faktura.view.InvoiceDetailsController;
import com.apteka.faktura.view.InvoiceListController;
import com.cathive.fx.guice.FXMLController;
import com.cathive.fx.guice.GuiceApplication;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
public class MainApp extends GuiceApplication {

    @Inject
    private GuiceFXMLLoader loader;
    @Inject
    @Named("i18n-resources")
    private ResourceBundle resources;

    private Stage primaryStage;
    private BorderPane rootLayout;


    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<Invoice> invoiceData = FXCollections.observableArrayList();
    private InvoiceListController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("");

        initRootLayout();

        showPersonOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() throws IOException {
        // Load root layout from fxml file.
        rootLayout = loader.load(getClass().getResource("view/RootLayout.fxml"), resources).getRoot();

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() throws IOException {
        // Load person overview.

        GuiceFXMLLoader.Result result = loader.load(getClass().getResource("view/InvoiceList.fxml"), resources);
        controller = result.getController();
        AnchorPane personOverview = result.getRoot();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(personOverview);

    }

    @Override
    public void init(List<com.google.inject.Module> modules) throws Exception {

        modules.addAll(InjectorInitializer.getModules());
    }

    /**
     * Returns the data as an observable list of Persons.
     *
     * @return
     */
    public ObservableList<Invoice> getInvoiceData() {
        return invoiceData;
    }

    public static void main(String[] args) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Application.launch(args);
    }

    public void showInvoiceDetails(Invoice invoice) {
        try {

            // Load the fxml file and create a new stage for the popup dialog.
            GuiceFXMLLoader.Result result = loader.load(getClass().getResource("view/InvoiceDetails.fxml"), resources);

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(result.getRoot());
            dialogStage.setScene(scene);
            InvoiceDetailsController invoiceDetailsController = result.getController();
            invoiceDetailsController.setInvoice(invoice);
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            Long amountOfProcessedGoods = invoiceDetailsController.getAmountOfProcessedGoods();
            invoice.setAmountOfProcessedGoods(amountOfProcessedGoods);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
