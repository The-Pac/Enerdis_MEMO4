package enerdis_Memo4.Controller;

import enerdis_Memo4.classe.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Controller_Main implements Initializable {

    //FXML
    public ChoiceBox<String> choix_Filtre_ChoiceBox, mois_ChoiceBox, annee_ChoiceBox, valeur_Filtre_ChoiceBox;
    public HBox filtre_Graphique_Vbox, information_Hbox;
    public VBox graphique_Vbox;
    public AnchorPane main_AnchroPane;
    public Label date_Label;
    public GridPane statut_Gridpane;
    public Label label_prix, annee_label = new Label("Année: "), mois_label = new Label("Mois: "),
            actuel_Puissance_w_Label, actuel_courant_Label, actuel_tension_Label, actuel_Q_var_Label,
            actuel_S_va_Label, actuel_puisssance_KWH_Label, actuel_K_var_Label,
            actuel_label1, actuel_label2, actuel_label3, actuel_label4, actuel_label5, actuel_label6, actuel_label7, max_K_var_Label, min_K_var_Label, max_Q_var_Label, min_Q_var_Label, max_S_va_Label, min_S_va_Label, max_puisssance_KWH_Label, min_puisssance_KWH_Label,
            max_tension_Label, min_tension_Label, max_courant_Label, min_courant_Label, max_Puissance_w_Label, min_Puissance_w_Label;
    public Button plus_coefficient_prix_Button, moins_coefficient_prix_Button;
    public LineChart<String, Number> graphique_LineChart;
    public BarChart<String, Number> graphique_BarChart;
    public XYChart.Series<String, Number> dataSeries_Barchart;
    public CategoryAxis xAxis_Linechart, xAxis_Barchart;
    public NumberAxis yAxis_Linechart, yAxis_Barchart;
    public ImageView base_de_donnee_ImageView, module_ImageView, gps_ImageView, connexion_lora_ImageView;

    //Variables
    private static Esclave esclave;
    private static Rn2483 rn2483;
    private static double prix;
    private static String port_gps;
    private static final SimpleBooleanProperty etat_bdd = new SimpleBooleanProperty(), etat_module = new SimpleBooleanProperty(), etat_gps = new SimpleBooleanProperty(), etat_lora = new SimpleBooleanProperty();
    public static Controller_Main controller;
    public ObservableList<String> filtre_ObservableList, mois_ObservableList, annee_ObservableList, valeur_filtreObservableList;
    public Commande commande;
    public Gestion_Mesures gestion_mesures;
    public Gestion_Module gestion_esclave;
    public DecimalFormat decimalFormat = new DecimalFormat("#.#"), decimalFormat_date = new DecimalFormat("00");
    public Bdd bdd;
    public String[] strings;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //composant init

        Image bdd_non_ok = new Image("enerdis_Memo4/img/bdd_non_ok.png"),
                bdd_ok = new Image("enerdis_Memo4/img/bdd_ok.png"),
                module_ok = new Image("enerdis_Memo4/img/module_ok.png"),
                module_non_ok = new Image("enerdis_Memo4/img/module_non_ok.png"),
                gps_ok = new Image("enerdis_Memo4/img/gps_ok.png"),
                gps_non_ok = new Image("enerdis_Memo4/img/gps_non_ok.png"),
                lora_ok = new Image("enerdis_Memo4/img/lora_ok.png"),
                lora_non_ok = new Image("enerdis_Memo4/img/lora_non_ok.png");

        ImageView plus_imageView = new ImageView(new Image("enerdis_Memo4/img/plus_img.png")),
                moins_imageView = new ImageView(new Image("enerdis_Memo4/img/moins_img.png"));

        bdd = new Bdd();

        //init image
        if (etat_gps.getValue()) {
            gps_ImageView.setImage(gps_ok);
        } else {
            gps_ImageView.setImage(gps_non_ok);
        }

        if (etat_bdd.getValue()) {
            base_de_donnee_ImageView.setImage(bdd_ok);
        } else {
            base_de_donnee_ImageView.setImage(bdd_non_ok);
        }

        if (etat_lora.getValue()) {
            connexion_lora_ImageView.setImage(lora_ok);
        } else {
            connexion_lora_ImageView.setImage(lora_non_ok);
        }

        if (etat_module.getValue()) {
            module_ImageView.setImage(module_ok);
        } else {
            module_ImageView.setImage(module_non_ok);
        }

        etat_gps.addListener((observable, oldValue, newValue) -> {
            if (etat_gps.getValue()) {
                gps_ImageView.setImage(gps_ok);
            } else {
                gps_ImageView.setImage(gps_non_ok);
            }
        });

        etat_bdd.addListener((observable, oldValue, newValue) -> {
            if (etat_bdd.getValue()) {
                base_de_donnee_ImageView.setImage(bdd_ok);
            } else {
                base_de_donnee_ImageView.setImage(bdd_non_ok);
            }
        });
        //init listiner boolean
        etat_lora.addListener((observable, oldValue, newValue) -> {
            if (etat_lora.getValue()) {
                connexion_lora_ImageView.setImage(lora_ok);
            } else {
                connexion_lora_ImageView.setImage(lora_non_ok);
            }
        });

        etat_module.addListener((observable, oldValue, newValue) -> {
            if (etat_module.getValue()) {
                module_ImageView.setImage(module_ok);
            } else {
                module_ImageView.setImage(module_non_ok);
            }
        });


        afficherheure(date_Label);
        setController(this);
        Platform.runLater(() -> label_prix.setText(String.valueOf(getPrix())));


        moins_imageView.setFitWidth(20);
        moins_imageView.setFitHeight(20);
        plus_imageView.setFitWidth(20);
        plus_imageView.setFitHeight(20);

        moins_coefficient_prix_Button.setGraphic(moins_imageView);
        plus_coefficient_prix_Button.setGraphic(plus_imageView);


        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////

        // classe init
        gestion_mesures = new Gestion_Mesures();
        gestion_esclave = new Gestion_Module();
        commande = new Commande();


        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////
        //init chart
        xAxis_Barchart = new CategoryAxis();
        xAxis_Barchart.setLabel("Mois");
        yAxis_Barchart = new NumberAxis();
        yAxis_Barchart.setLabel("Kwh");
        graphique_BarChart = new BarChart<>(xAxis_Barchart, yAxis_Barchart);
        graphique_BarChart.setTitle("Consommation");
        dataSeries_Barchart = new XYChart.Series<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////

        //init les observableArrayList
        valeur_filtreObservableList = FXCollections.observableArrayList();
        filtre_ObservableList = FXCollections.observableArrayList();
        mois_ObservableList = FXCollections.observableArrayList();
        annee_ObservableList = FXCollections.observableArrayList();

        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////

        //rempli le filtre de tri du tableau
        filtre_ObservableList.add("Direct");
        filtre_ObservableList.add("Choix date");

        for (Commande s : commande.liste_commande()) {
            valeur_filtreObservableList.add(s.getValeur());
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////

        //ajoute et met en valeur la premiere valeur parmis les filtres
        choix_Filtre_ChoiceBox.setItems(filtre_ObservableList);
        choix_Filtre_ChoiceBox.setValue(choix_Filtre_ChoiceBox.getItems().get(0));
        choix_Filtre_ChoiceBox.getStyleClass().add("box");

        valeur_Filtre_ChoiceBox.setItems(valeur_filtreObservableList);
        valeur_Filtre_ChoiceBox.setValue(valeur_Filtre_ChoiceBox.getItems().get(0));
        valeur_Filtre_ChoiceBox.getStyleClass().add("box");


        mois_ChoiceBox = new ChoiceBox<>();
        mois_ChoiceBox.getStyleClass().add("box");
        annee_ChoiceBox = new ChoiceBox<>();
        annee_ChoiceBox.getStyleClass().add("box");
        
        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////
        //afficher dans le linechart toutes les données de la journée


        Platform.runLater(() -> {
            if (bdd.connexion()) {
                Platform.runLater(() -> {
                    bdd.recuperation_donnee_jour(String.valueOf(LocalDateTime.now().getYear()), decimalFormat_date.format(LocalDateTime.now().getMonthValue()), decimalFormat_date.format(LocalDateTime.now().getDayOfMonth()), this, valeur_Filtre_ChoiceBox.getValue());
                    Mesure mesure_max = bdd.valeur_max(String.valueOf(LocalDateTime.now().getYear()), decimalFormat_date.format(LocalDateTime.now().getMonthValue()), decimalFormat_date.format(LocalDateTime.now().getDayOfMonth())), mesure_min = bdd.valeur_min(String.valueOf(LocalDateTime.now().getYear()), String.valueOf(LocalDateTime.now().getMonthValue()), String.valueOf(LocalDateTime.now().getDayOfMonth()));
                    afficher_valeur_label_direct(mesure_max, mesure_min);
                    rangement_tick_chart(choix_Filtre_ChoiceBox.getSelectionModel().getSelectedItem(), mesure_max, mesure_min);
                });
                bdd.deconnexion();
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////

        //lance la task timer
        Timer_Action timer = new Timer_Action();
        timer.timer_task(this);

        ////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////
        //listener sur les filtres
        valeur_Filtre_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (bdd.connexion()) {
                Platform.runLater(() -> {
                    Gestion_Mesures.getDataSeries_Linechart().getData().clear();
                    graphique_LineChart.setTitle(valeur_Filtre_ChoiceBox.getValue());
                    Mesure mesure_max = bdd.valeur_max(String.valueOf(LocalDateTime.now().getYear()), decimalFormat_date.format(LocalDateTime.now().getMonthValue()), decimalFormat_date.format(LocalDateTime.now().getDayOfMonth())), mesure_min = bdd.valeur_min(String.valueOf(LocalDateTime.now().getYear()), String.valueOf(LocalDateTime.now().getMonthValue()), String.valueOf(LocalDateTime.now().getDayOfMonth()));
                    bdd.recuperation_donnee_jour(String.valueOf(LocalDateTime.now().getYear()), decimalFormat_date.format(LocalDateTime.now().getMonthValue()), decimalFormat_date.format(LocalDateTime.now().getDayOfMonth()), this, valeur_Filtre_ChoiceBox.getValue());
                    rangement_tick_chart(choix_Filtre_ChoiceBox.getSelectionModel().getSelectedItem(), mesure_max, mesure_min);
                });
            }
        });

        choix_Filtre_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //si direct est selectionne
            if (choix_Filtre_ChoiceBox.getSelectionModel().getSelectedItem().equals(filtre_ObservableList.get(0))) {
                filtre_Graphique_Vbox.getChildren().removeAll(annee_label, annee_ChoiceBox, mois_label, mois_ChoiceBox);
                filtre_Graphique_Vbox.getChildren().add(valeur_Filtre_ChoiceBox);
                graphique_Vbox.getChildren().remove(graphique_BarChart);
                graphique_Vbox.getChildren().add(graphique_LineChart);

                actuel_label1.setText("Actuel");
                actuel_label2.setText("Actuel");
                actuel_label3.setText("Actuel");
                actuel_label4.setText("Actuel");
                actuel_label5.setText("Actuel");
                actuel_label6.setText("Actuel");
                if (bdd.connexion()) {
                    System.out.println(LocalDateTime.now().getYear() + " " + LocalDateTime.now().getMonthValue() + LocalDateTime.now().getDayOfMonth());
                    Mesure mesure_max = bdd.valeur_max(String.valueOf(LocalDateTime.now().getYear()), String.valueOf(LocalDateTime.now().getMonthValue()), String.valueOf(LocalDateTime.now().getDayOfMonth())), mesure_min = bdd.valeur_min(String.valueOf(LocalDateTime.now().getYear()), String.valueOf(LocalDateTime.now().getMonthValue()), String.valueOf(LocalDateTime.now().getDayOfMonth()));
                    Platform.runLater(() -> {
                        if (Gestion_Mesures.getValeur() != null) {
                            actuel_courant_Label.setText(String.valueOf(Gestion_Mesures.getValeur().getCourant()));
                            actuel_Puissance_w_Label.setText(String.valueOf(Gestion_Mesures.getValeur().getPuissance_W()));
                            actuel_Q_var_Label.setText(String.valueOf(Gestion_Mesures.getValeur().getQ_VAR()));
                            actuel_puisssance_KWH_Label.setText(String.valueOf(Gestion_Mesures.getValeur().getEnergie_KWH()));
                            actuel_S_va_Label.setText(String.valueOf(Gestion_Mesures.getValeur().getS_VA()));
                            actuel_tension_Label.setText(String.valueOf(Gestion_Mesures.getValeur().getTension()));
                        }
                        afficher_valeur_label_direct(mesure_max, mesure_min);
                        bdd.recuperation_donnee_jour(String.valueOf(LocalDateTime.now().getYear()), decimalFormat_date.format(LocalDateTime.now().getMonthValue()), decimalFormat_date.format(LocalDateTime.now().getDayOfMonth()), this, valeur_Filtre_ChoiceBox.getValue());
                    });
                    bdd.deconnexion();
                }
                if (Gestion_Mesures.getValeur() != null) {
                    if (controller.graphique_LineChart.getData().isEmpty()) {
                        controller.graphique_LineChart.getData().add(Gestion_Mesures.getDataSeries_Linechart());
                    }
                }
            }

            //si Choix de date est selectionne
            if (choix_Filtre_ChoiceBox.getSelectionModel().getSelectedItem().equals(filtre_ObservableList.get(1))) {
                filtre_Graphique_Vbox.getChildren().removeAll(valeur_Filtre_ChoiceBox);

                filtre_Graphique_Vbox.getChildren().addAll(annee_label, annee_ChoiceBox, mois_label, mois_ChoiceBox);
                graphique_Vbox.getChildren().remove(graphique_LineChart);
                graphique_Vbox.getChildren().add(graphique_BarChart);

                dataSeries_Barchart = new XYChart.Series<>();

                actuel_label1.setText("Moy");
                actuel_label2.setText("Moy");
                actuel_label3.setText("Moy");
                actuel_label4.setText("Moy");
                actuel_label5.setText("Moy");
                actuel_label6.setText("Moy");

                annee_ObservableList.clear();
                mois_ObservableList.clear();

                mois_ObservableList.add("aucun");
                annee_ObservableList.add("tous");

                if (bdd.connexion()) {
                    for (String annee : bdd.recuperation_annee()) {
                        Platform.runLater(() -> {
                            annee_ObservableList.add(annee);
                            mois_ObservableList.addAll(bdd.recuperation_mois(annee));
                            dataSeries_Barchart.setName(annee);
                            String mois1 = "00", jour1 = "00";
                            Mesure mesure_avg = bdd.valeur_moyenne(annee, mois1, jour1), mesure_max = bdd.valeur_max(annee, mois1, jour1), mesure_min = bdd.valeur_min(annee, mois1, jour1);
                            afficher_valeur_label_date(mesure_avg, mesure_max, mesure_min);
                            dataSeries_Barchart.getData().add(new XYChart.Data<>("Max", mesure_min.getEnergie_KWH()));
                            dataSeries_Barchart.getData().add(new XYChart.Data<>("Avg", mesure_avg.getEnergie_KWH()));
                            dataSeries_Barchart.getData().add(new XYChart.Data<>("Min", mesure_max.getEnergie_KWH()));
                            graphique_BarChart.getData().add(dataSeries_Barchart);
                        });

                    }
                    bdd.deconnexion();
                }
                mois_ChoiceBox.setItems(mois_ObservableList);
                mois_ChoiceBox.setValue(mois_ChoiceBox.getItems().get(0));

                annee_ChoiceBox.setItems(annee_ObservableList);
                annee_ChoiceBox.setValue(annee_ChoiceBox.getItems().get(0));
                ////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////

                mois_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
                    //affiche aucun mois juste l'annee
                    if (!observable1.getValue().equals("aucun")) {
                        graphique_BarChart.getData().clear();
                        if (bdd.connexion()) {
                            for (String annee : bdd.recuperation_annee()) {
                                Platform.runLater(() -> {
                                    dataSeries_Barchart.setName(annee);
                                    String mois1 = "00", jour1 = "00";
                                    Mesure mesure_avg = bdd.valeur_moyenne(annee, mois1, jour1), mesure_max = bdd.valeur_max(annee, mois1, jour1), mesure_min = bdd.valeur_min(annee, mois1, jour1);
                                    afficher_valeur_label_date(mesure_avg, mesure_max, mesure_min);
                                    dataSeries_Barchart.getData().add(new XYChart.Data<>("Max", mesure_min.getEnergie_KWH()));
                                    dataSeries_Barchart.getData().add(new XYChart.Data<>("Avg", mesure_avg.getEnergie_KWH()));
                                    dataSeries_Barchart.getData().add(new XYChart.Data<>("Min", mesure_max.getEnergie_KWH()));
                                    graphique_BarChart.getData().add(dataSeries_Barchart);
                                });
                            }
                            bdd.deconnexion();
                        }
                        //affiche tous les mois
                    } else if (observable1.getValue().equals("tous")) {
                        if (bdd.connexion()) {
                            if (!annee_ChoiceBox.getValue().equals("tous")) {
                                for (String s : bdd.recuperation_mois(annee_ChoiceBox.getValue())) {
                                    Platform.runLater(() -> {
                                        graphique_BarChart.getData().clear();
                                        String annee = annee_ChoiceBox.getValue();
                                        dataSeries_Barchart.setName(s);
                                        String jour1 = "00";
                                        Mesure mesure_avg = bdd.valeur_moyenne(annee, s, jour1), mesure_max = bdd.valeur_max(annee, s, jour1), mesure_min = bdd.valeur_min(annee, s, jour1);
                                        afficher_valeur_label_date(mesure_avg, mesure_max, mesure_min);
                                        dataSeries_Barchart.getData().add(new XYChart.Data<>("Max", mesure_min.getEnergie_KWH()));
                                        dataSeries_Barchart.getData().add(new XYChart.Data<>("Avg", mesure_avg.getEnergie_KWH()));
                                        dataSeries_Barchart.getData().add(new XYChart.Data<>("Min", mesure_max.getEnergie_KWH()));
                                        graphique_BarChart.getData().add(dataSeries_Barchart);
                                    });

                                }
                            }
                            bdd.deconnexion();
                        }
                        //affiche le mois selectionne
                    } else {
                        if (bdd.connexion()) {
                            Platform.runLater(() -> {
                                graphique_BarChart.getData().clear();
                                String annee = annee_ChoiceBox.getValue(), mois = mois_ChoiceBox.getValue();
                                dataSeries_Barchart.setName(annee);
                                String jour1 = "00";
                                Mesure mesure_avg = bdd.valeur_moyenne(annee, mois, jour1), mesure_max = bdd.valeur_max(annee, mois, jour1), mesure_min = bdd.valeur_min(annee, mois, jour1);
                                afficher_valeur_label_date(mesure_avg, mesure_max, mesure_min);
                                dataSeries_Barchart.getData().add(new XYChart.Data<>("Max", mesure_min.getEnergie_KWH()));
                                dataSeries_Barchart.getData().add(new XYChart.Data<>("Avg", mesure_avg.getEnergie_KWH()));
                                dataSeries_Barchart.getData().add(new XYChart.Data<>("Min", mesure_max.getEnergie_KWH()));
                                graphique_BarChart.getData().add(dataSeries_Barchart);
                            });
                            bdd.deconnexion();
                        }
                    }
                });

                ////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////

                annee_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable1, oldValue1, newValue1) -> {
                    //affiche toutes les dates de toutes les annees
                    if (observable1.getValue().equals("tous")) {
                        mois_ObservableList.remove(0);
                        if (bdd.connexion()) {
                            for (String annee : bdd.recuperation_annee()) {
                                Platform.runLater(() -> {
                                    graphique_BarChart.getData().clear();
                                    dataSeries_Barchart.setName(annee);
                                    String mois1 = "00", jour1 = "00";
                                    Mesure mesure_avg = bdd.valeur_moyenne(annee, mois1, jour1), mesure_max = bdd.valeur_max(annee, mois1, jour1), mesure_min = bdd.valeur_min(annee, mois1, jour1);
                                    afficher_valeur_label_date(mesure_avg, mesure_max, mesure_min);
                                    dataSeries_Barchart.getData().add(new XYChart.Data<>("Max", mesure_min.getEnergie_KWH()));
                                    dataSeries_Barchart.getData().add(new XYChart.Data<>("Avg", mesure_avg.getEnergie_KWH()));
                                    dataSeries_Barchart.getData().add(new XYChart.Data<>("Min", mesure_max.getEnergie_KWH()));
                                    graphique_BarChart.getData().add(dataSeries_Barchart);
                                });
                            }
                            bdd.deconnexion();
                        }
                        //affiche l'annee selectionne
                    } else {
                        mois_ObservableList.add(0, "tous");
                        if (bdd.connexion()) {
                            Platform.runLater(() -> {
                                graphique_BarChart.getData().clear();
                                String annee = annee_ChoiceBox.getValue();
                                dataSeries_Barchart.setName(annee);
                                String mois1 = "00", jour1 = "00";
                                Mesure mesure_avg = bdd.valeur_moyenne(annee, mois1, jour1), mesure_max = bdd.valeur_max(annee, mois1, jour1), mesure_min = bdd.valeur_min(annee, mois1, jour1);
                                afficher_valeur_label_date(mesure_avg, mesure_max, mesure_min);
                                dataSeries_Barchart.getData().add(new XYChart.Data<>("Max", mesure_min.getEnergie_KWH()));
                                dataSeries_Barchart.getData().add(new XYChart.Data<>("Avg", mesure_avg.getEnergie_KWH()));
                                dataSeries_Barchart.getData().add(new XYChart.Data<>("Min", mesure_max.getEnergie_KWH()));
                                graphique_BarChart.getData().add(dataSeries_Barchart);
                            });
                            bdd.deconnexion();
                        }
                    }
                });
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void rangement_tick_chart(String type_commande, Mesure mesure_max, Mesure mesure_min) {

        switch (type_commande) {
            case "tension (V)":
                yAxis_Linechart.setLowerBound(0);
                yAxis_Linechart.setUpperBound(mesure_max.getTension() + 1);
                yAxis_Linechart.setTickUnit(10);

                break;
            case "courant (A)":
                yAxis_Linechart.setLowerBound(0);
                yAxis_Linechart.setUpperBound(20);
                yAxis_Linechart.setTickUnit(5);
                break;
            case "facteur puissance (K)":
                ((NumberAxis) graphique_LineChart.getYAxis()).setUpperBound(1);
                ((NumberAxis) graphique_LineChart.getYAxis()).setLowerBound(0);
                ((NumberAxis) graphique_LineChart.getYAxis()).setTickUnit(0.01);
                break;
            case "puissance (W)":
                yAxis_Linechart.setLowerBound(mesure_min.getPuissance_W());
                yAxis_Linechart.setUpperBound(mesure_max.getPuissance_W() + 1);
                yAxis_Linechart.setTickUnit(5);
                break;
            case "q (VAR)":
                yAxis_Linechart.setLowerBound(mesure_min.getQ_VAR());
                yAxis_Linechart.setUpperBound(mesure_max.getQ_VAR() + 1);
                yAxis_Linechart.setTickUnit(5);
                break;
            case "s (VA)":
                yAxis_Linechart.setLowerBound(mesure_min.getS_VA());
                yAxis_Linechart.setUpperBound(mesure_max.getS_VA() + 1);
                yAxis_Linechart.setTickUnit(5);
                break;
            case "energie (kWh)":
                yAxis_Linechart.setLowerBound(mesure_min.getEnergie_KWH());
                yAxis_Linechart.setUpperBound(mesure_max.getEnergie_KWH() + 1);
                yAxis_Linechart.setTickUnit(5);
                break;
        }
    }

    public void afficher_valeur_label_date(Mesure mesure_moyenne, Mesure mesure_max, Mesure mesure_min) {

        //max
        max_courant_Label.setText(String.valueOf(mesure_max.getCourant()));
        max_Puissance_w_Label.setText(String.valueOf(mesure_max.getPuissance_W()));
        max_K_var_Label.setText(String.valueOf(mesure_max.getFacteur_puissance()));
        max_Q_var_Label.setText(String.valueOf(mesure_max.getQ_VAR()));
        max_puisssance_KWH_Label.setText(String.valueOf(mesure_max.getEnergie_KWH()));
        max_S_va_Label.setText(String.valueOf(mesure_max.getS_VA()));
        max_tension_Label.setText(String.valueOf(mesure_max.getTension()));

        //min
        min_courant_Label.setText(String.valueOf(mesure_min.getCourant()));
        min_Puissance_w_Label.setText(String.valueOf(mesure_min.getPuissance_W()));
        min_Q_var_Label.setText(String.valueOf(mesure_min.getQ_VAR()));
        min_K_var_Label.setText(String.valueOf(mesure_max.getFacteur_puissance()));
        min_puisssance_KWH_Label.setText(String.valueOf(mesure_min.getEnergie_KWH()));
        min_S_va_Label.setText(String.valueOf(mesure_min.getS_VA()));
        min_tension_Label.setText(String.valueOf(mesure_min.getTension()));

        //moy
        actuel_courant_Label.setText(String.valueOf(mesure_moyenne.getCourant()));
        actuel_Puissance_w_Label.setText(String.valueOf(mesure_moyenne.getPuissance_W()));
        actuel_Q_var_Label.setText(String.valueOf(mesure_moyenne.getQ_VAR()));
        actuel_K_var_Label.setText(String.valueOf(mesure_moyenne.getFacteur_puissance()));
        actuel_puisssance_KWH_Label.setText(String.valueOf(mesure_moyenne.getEnergie_KWH()));
        actuel_S_va_Label.setText(String.valueOf(mesure_moyenne.getS_VA()));
        actuel_tension_Label.setText(String.valueOf(mesure_moyenne.getTension()));
    }

    public void afficher_valeur_label_direct(Mesure mesure_max, Mesure mesure_min) {
        //max
        max_courant_Label.setText(String.valueOf(mesure_max.getCourant()));
        max_Puissance_w_Label.setText(String.valueOf(mesure_max.getPuissance_W()));
        max_Q_var_Label.setText(String.valueOf(mesure_max.getQ_VAR()));
        max_K_var_Label.setText(String.valueOf(mesure_max.getFacteur_puissance()));
        max_puisssance_KWH_Label.setText(String.valueOf(mesure_max.getEnergie_KWH()));
        max_S_va_Label.setText(String.valueOf(mesure_max.getS_VA()));
        max_tension_Label.setText(String.valueOf(mesure_max.getTension()));

        //min
        min_courant_Label.setText(String.valueOf(mesure_min.getCourant()));
        min_Puissance_w_Label.setText(String.valueOf(mesure_min.getPuissance_W()));
        min_Q_var_Label.setText(String.valueOf(mesure_min.getQ_VAR()));
        min_K_var_Label.setText(String.valueOf(mesure_max.getFacteur_puissance()));
        min_puisssance_KWH_Label.setText(String.valueOf(mesure_min.getEnergie_KWH()));
        min_S_va_Label.setText(String.valueOf(mesure_min.getS_VA()));
        min_tension_Label.setText(String.valueOf(mesure_min.getTension()));

    }

    public void afficherheure(Label label) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            LocalDateTime instantdate = LocalDateTime.now();
            label.setText(instantdate.format(DateTimeFormatter.ofPattern("uuuu/MM/dd kk:mm:ss")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void plus_coefficient_prix_Button_Action() {
        prix += 0.100;
        Platform.runLater(() -> label_prix.setText(String.valueOf(decimalFormat.format(prix))));
    }

    public void moins_coefficient_prix_Button_Action() {
        prix -= 0.100;
        Platform.runLater(() -> label_prix.setText(String.valueOf(decimalFormat.format(prix))));
    }

    public static void setController(Controller_Main controller) {
        Controller_Main.controller = controller;
    }

    public static void setPort_gps(String port_gps) {
        Controller_Main.port_gps = port_gps;
    }

    public static Esclave getEsclave() {
        return esclave;
    }

    public static void setEsclave(Esclave esclave) {
        Controller_Main.esclave = esclave;
    }

    public static double getPrix() {
        return prix;
    }

    public static void setPrix(int prix) {
        Controller_Main.prix = prix;
    }


    public static void setRn2483(Rn2483 rn2483) {
        Controller_Main.rn2483 = rn2483;
    }


    public static void setEtat_bdd(boolean etat_bdd) {
        Controller_Main.etat_bdd.set(etat_bdd);
    }

    public static void setEtat_module(boolean etat_module) {
        Controller_Main.etat_module.set(etat_module);
    }


    public static void setEtat_gps(boolean etat_gps) {
        Controller_Main.etat_gps.set(etat_gps);
    }


    public static void setEtat_lora(boolean etat_lora) {
        Controller_Main.etat_lora.set(etat_lora);
    }

    public static Rn2483 getRn2483() {
        return rn2483;
    }
}
