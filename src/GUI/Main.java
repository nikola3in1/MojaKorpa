package GUI;

import GUI.Data;
import Model.CSVPrinter;
import Model.Product;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.*;

public class Main extends Application {
    private TableView<Product> table = new TableView<Product>();

    //Reference
    private Data data = Data.getInstance();

    //GUI Liste
    private ObservableList<Product> proizvodiTabela = FXCollections.observableArrayList(new ArrayList<Product>());
    private ObservableList<String> comboBoxProizvodi = FXCollections.observableArrayList(data.filterData());

    //GUI promenljive
    private double cenaMaxi = 0.0;
    private double cenaIdea = 0.0;
    private double cenaMoja = 0.0;
    private int brProizvoda = 0;
    private int maxiBrDostupnih = 0;
    private int ideaBrDostupnih = 0;

    //Fontovi
    private Font headingFont = new Font("Arial", 20);
    private Font textFont = new Font("Arial", 17);

    //Labele
    private Label inputLabel = new Label("Izaberite proizvod:");
    private Label tabelaLabel = new Label("Korpa:");
    private Label prodavniceLabel = new Label("Vrednost korpe u:");
    private Label dostuponstLabel = new Label("Dostupnost proizvoda u:");
    private Label brProizvodaLabel = new Label("Broj proizvoda u korpi: 0");
    private Label maxiCenaLabel = new Label("- Maxi: " + cenaMaxi + " din.");
    private Label ideaCenaLabel = new Label("- Idea: " + cenaIdea + " din.");
    private Label mojaKropaCenaLabel = new Label("- MojaKorpa: " + cenaMoja + " din.");
    private Label maxiDostupnostLabel = new Label("- Maxi: " + maxiBrDostupnih + "/" + brProizvoda);
    private Label ideaDostupnostLabel = new Label("- Idea: " + ideaBrDostupnih + "/" + brProizvoda);

    //Racunamo sve korpe
    private void racunaj() {
        cenaMaxi = data.korpaMaxi.racunajKorpu();
        cenaIdea = data.korpaIdea.racunajKorpu();
        cenaMoja = data.korpaMoja.racunajKorpu();
        brProizvoda = proizvodiTabela.size();
        int brUniqeProizvoda = data.korpaMoja.getSize();
        maxiBrDostupnih = data.korpaMaxi.getSize();
        ideaBrDostupnih = data.korpaIdea.getSize();

        brProizvodaLabel.setText("Broj proizvoda u korpi: " + brProizvoda);
        ideaCenaLabel.setText("- Idea: " + data.korpaIdea.getVrednost() + " din.");
        maxiCenaLabel.setText("- Maxi: " + data.korpaMaxi.getVrednost() + " din.");
        mojaKropaCenaLabel.setText("- MojaKorpa: " + cenaMoja + " din.");

        maxiDostupnostLabel.setText("- Maxi: " + maxiBrDostupnih + "/" + brUniqeProizvoda);
        ideaDostupnostLabel.setText("- Idea: " + ideaBrDostupnih + "/" + brUniqeProizvoda);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        GridPane leftPane = new GridPane();

        //Dugmici
        Button azurirajBtn = new Button("Ažuriraj");
        Button stampajBtn = new Button("Štampaj");
        Button dodajBtn = new Button("Dodaj");

        //Tabela
        table.setEditable(true);
        TableColumn proizvodiCol = new TableColumn("Proizvod");
        proizvodiCol.setMinWidth(350);
        proizvodiCol.setMaxWidth(500);
        proizvodiCol.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        TableColumn marketCol = new TableColumn("Market");
        marketCol.setMinWidth(50);
        marketCol.setMaxWidth(80);
        marketCol.setCellValueFactory(new PropertyValueFactory<Product, String>("shop"));
        TableColumn cenaCol = new TableColumn("Cena");
        cenaCol.setMinWidth(50);
        cenaCol.setMaxWidth(100);
        cenaCol.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        TableColumn<Product, Product> ukloniCol = new TableColumn<>("Ukloni");
        ukloniCol.setMaxWidth(80);
        ukloniCol.setMinWidth(80);
        ukloniCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        ukloniCol.setCellFactory(param -> new TableCell<Product, Product>() {
            private Button deleteButton = new Button("X");

            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    return;
                }
                deleteButton.setMaxWidth(ukloniCol.getMaxWidth() - 10);
                deleteButton.setMinWidth(ukloniCol.getMaxWidth() - 10);
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-color: black");
                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> {
                    getTableView().getItems().remove(item);

                    HashMap<String, Product> shops = data.mainMap.get(item.getBarcode());

                    boolean deletedMojaKorpa = false;
                    for (Map.Entry<String, Product> entry : shops.entrySet()) {
                        //Brisemo Maxi proizvod
                        if (entry.getKey().equals("Maxi")) {
                            data.korpaMaxi.ukloniIzKorpe(item);
                            //Proveravamo da li smo u petlji vec uklonili ovaj proizvod iz MojeKorpe
                            if (!deletedMojaKorpa) {
                                data.korpaMoja.ukloniIzKorpe(item);
                                deletedMojaKorpa = true;
                            }
//                            System.out.println("MAXI DELETE");
                        } else if (item.getShop().equals("/")) {
                            //Brisemo proizvod koji ima istu cenu u oba marketa
                            data.korpaIdea.ukloniIzKorpe(entry.getValue());
//                            System.out.println("same DELETE");
                        } else {
                            //Brisemo Idea proizvod
                            //Proveravamo da li smo u petlji vec uklonili ovaj proizvod iz MojeKorpe
                            if (!deletedMojaKorpa) {
                                data.korpaMoja.ukloniIzKorpe(item);
                                deletedMojaKorpa = true;
                            }
                            data.korpaIdea.ukloniIzKorpe(entry.getValue());
                        }

                    }
                    racunaj();
                });
            }
        });

        //Input lista proizvoda
        ComboBox<String> proizvodi = new ComboBox<String>();
        proizvodi.setEditable(true);
        proizvodi.setMaxWidth(250);
        FilteredList<String> filteredItems = new FilteredList<String>(comboBoxProizvodi, p -> true);
        proizvodi.getEditor().textProperty().addListener((observableValue, s, t1) -> {
            final TextField editor = proizvodi.getEditor();
            final String selected = proizvodi.getSelectionModel().getSelectedItem();

            Platform.runLater(() -> {
                if (selected == null || !selected.equals(editor.getText())) {
                    filteredItems.setPredicate(item -> {
                        if (item.toUpperCase().startsWith(t1.toUpperCase())) {
                            return true;
                        } else {
                            return false;
                        }
                    });
                }
            });
        });

        //Eventi
        dodajBtn.setOnAction(e -> {

            if (proizvodi.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greška");
                alert.setHeaderText("Došlo je do greške!");
                alert.setContentText("Pogrešan unos!\nPokušajte da izaberete proizvod iz liste.");
                alert.showAndWait();
                return;
            }

            String nazivProzivoda = proizvodi.getSelectionModel().getSelectedItem().trim();


            Product min = null;
            Product maxi = null;
            Product idea = null;

            boolean found = false;
            for (Product p : data.proizvodiMap.get(nazivProzivoda)) {
                String barcode = p.getBarcode();
                if (data.mainMap.containsKey(barcode)) {
                    HashMap<String, Product> shops = data.mainMap.get(barcode);
                    if (shops.size() > 1) {
                        //Pronasli smo par prozivoda
                        maxi = shops.get("Maxi");
                        idea = shops.get("Idea");
                        found = true;
                        min = data.findMin(maxi, idea);
                        System.out.println("MAXI :" + maxi.getPrice() + ", " + maxi.getBarcode() + "\nIDEA:" + idea.getPrice() + ", " + idea.getBarcode());
                        System.out.println("MIN JE : " + min.getPrice() + "< >" + min.getShop());
                    } else {
                        //Pronasli smo jedan proizvod
                        min = shops.getOrDefault("Maxi", shops.get("Idea"));
                    }
                }
                if (found) {
                    break;
                }
            }

            //Dodajemo u obe korpe
            if (found) {
//                Product noviTemp = new Product(min);
//                Product noviTemp1 = new Product(min);
                Product noviTemp = new Product(idea);
                Product noviTemp1 = new Product(maxi);
                data.korpaIdea.dodajUKorpu(noviTemp);
                System.out.println("dodajemo u maxi");
                data.korpaMaxi.dodajUKorpu(noviTemp1);

            } else {
                if (min.getShop().equals("Maxi")) {
                    data.korpaMaxi.dodajUKorpu(new Product(min));
                } else {
                    data.korpaIdea.dodajUKorpu(new Product(min));
                }
            }
            proizvodiTabela.add(new Product(min));
            data.korpaMoja.dodajUKorpu(new Product(min));
            //Racunamo korpe
            racunaj();

        });
        azurirajBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            String poslednjiUpdate = data.lastUpdate();
            alert.setTitle("Potvrdite akciju");
            alert.setHeaderText("Poslednji put ažurirano: " + poslednjiUpdate);
            alert.setContentText("Jeste li sigurni da želite da ažurirate bazu podataka?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Stage stage = new Stage();
                VBox smallRoot = new VBox();

                Label poruka = new Label("Ažuriranje je u toku, molimo sačekajte.\nObično potraje oko 2 minuta.\nHvala na strpljenju!");
                Button btn = new Button("U redu");
                ProgressIndicator pin = new ProgressIndicator();
                pin.setMaxSize(100, 100);
                pin.setProgress(-1f);
                poruka.setFont(textFont);

                smallRoot.setAlignment(Pos.CENTER);
                smallRoot.setPadding(new Insets(10));
                smallRoot.setSpacing(10);
                smallRoot.getChildren().addAll(poruka, pin);

                btn.setOnAction(e1 -> {
                    stage.close();
                });

                stage.setTitle("Ažuriranje baze podataka");
                stage.setScene(new Scene(smallRoot, 500, 200));
                stage.show();

                Thread t = new Thread(() -> {
                    if (data.azuriraj()) {
                        Platform.runLater(() -> {
                            proizvodiTabela.setAll(FXCollections.observableArrayList(new ArrayList<Product>()));
                            comboBoxProizvodi.setAll(FXCollections.observableArrayList(data.filterData()));
                            System.out.println(comboBoxProizvodi.size() + " COMBOBOX SIZE");
                            poruka.setText("Ažuriranje je završeno!");
                            pin.setProgress(1f);
                            smallRoot.getChildren().add(btn);
                        });
                    } else {
                        Platform.runLater(() -> {
                            poruka.setText("Došlo je do greške :/ Pokušajte ponovo kasnije.");
                            smallRoot.getChildren().remove(pin);

                            smallRoot.getChildren().add(btn);
                        });
                    }
                });
                t.start();


            }


        });
        stampajBtn.setOnAction(e -> {

            if (proizvodiTabela.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Greška");
                alert.setHeaderText("Došlo je do greške!");
                alert.setContentText("Vaša korpa je prazna.\nPokušajte da izaberete proizvod iz liste.");
                alert.showAndWait();
                return;
            }


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potvrdite akciju");
            alert.setHeaderText("Štampanje korpe u .csv fajl.");
            alert.setContentText("Jeste li sigurni da želite da štampate sadržaj\nkorpe u .csv fajl?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                HashSet<Product> proizvodiStampanje = new HashSet<>(data.korpaMoja.getProizvodi());

                for (Product p : proizvodiStampanje) {
                    System.out.println(p);
                }

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(timestamp);
                String timestampStr = timestamp.toString().replace(":",";");
                String fileName = "MojaKropa " + timestampStr;
                CSVPrinter printer = new CSVPrinter(Product.class, proizvodiStampanje, fileName);
                printer.print(true);
            }
        });


        //Populating
        proizvodi.setItems(filteredItems);
        table.setItems(proizvodiTabela);
        table.setPlaceholder(new Label("Trenutno nemate dodatih proizvoda."));
        table.getColumns().addAll(proizvodiCol, marketCol, cenaCol, ukloniCol);

        //Helper Panes
        VBox infoPane = new VBox();
        HBox inputProizvodi = new HBox();
        BorderPane rightPane = new BorderPane();
        BorderPane azurirajPane = new BorderPane();
        StackPane stampajPane = new StackPane();
        azurirajPane.setTop(azurirajBtn);
        stampajPane.getChildren().addAll(stampajBtn);
        inputProizvodi.getChildren().addAll(proizvodi, dodajBtn);

        //Setovanje fontova
        tabelaLabel.setFont(headingFont);
        brProizvodaLabel.setFont(headingFont);
        prodavniceLabel.setFont(headingFont);
        dostuponstLabel.setFont(headingFont);
        ideaCenaLabel.setFont(textFont);
        maxiCenaLabel.setFont(textFont);
        mojaKropaCenaLabel.setFont(textFont);
        maxiDostupnostLabel.setFont(textFont);
        ideaDostupnostLabel.setFont(textFont);

        //Separatori
        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        //Alignment
        inputProizvodi.setSpacing(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setVgap(10);
        rightPane.setPadding(new Insets(10));
        root.setPadding(new Insets(10));
        infoPane.setAlignment(Pos.TOP_LEFT);
        infoPane.setPadding(new Insets(40, 0, 0, 0));
        infoPane.setSpacing(10);
        stampajPane.setPadding(new Insets(25, 0, 0, 100));
        stampajBtn.setMinSize(150, 40);
        proizvodi.setMinWidth(525);
        proizvodi.setMaxWidth(525);

        //Dodavanje cvorova
        infoPane.getChildren().addAll(brProizvodaLabel, sep2, prodavniceLabel, ideaCenaLabel, maxiCenaLabel, mojaKropaCenaLabel, sep1, dostuponstLabel);
        infoPane.getChildren().addAll(ideaDostupnostLabel, maxiDostupnostLabel, stampajPane);
        leftPane.add(inputLabel, 0, 0);
        leftPane.add(inputProizvodi, 0, 1);
        leftPane.add(tabelaLabel, 0, 2);
        leftPane.add(table, 0, 3);
        rightPane.setRight(azurirajPane);
        rightPane.setLeft(infoPane);
        root.setLeft(leftPane);
        root.setCenter(rightPane);

        primaryStage.setTitle("Moja Korpa");
        primaryStage.setScene(new Scene(root, 1000, 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
