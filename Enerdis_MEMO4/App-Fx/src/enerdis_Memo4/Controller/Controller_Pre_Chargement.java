package enerdis_Memo4.Controller;

import enerdis_Memo4.classe.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller_Pre_Chargement implements Initializable {

    public ImageView verification_GPS_ImageView, verification_Module_ImageView, verification_Lora_ImageView, verification_Bdd_ImageView;
    public Button quitter_button;
    public Optional<ButtonType> optional;
    public VBox chargement_Vbox;
    public GridPane gridpane_detection;

    public Image cross_Image, check_Image, loading_Image;
    public Timer_Action timer_action;
    private static boolean check_lora = false;
    public boolean check_gps, check_bdd, check_module;
    public Stage stage;
    public Node source;
    public Alert alert;
    public Liaison_Serie liaisonSerie;
    public ObservableList<Esclave> list_module;
    public Rn2483 rn2483;
    public Bdd bdd;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargement_Vbox.getChildren().remove(gridpane_detection);
        cross_Image = new Image("enerdis_Memo4/img/cross.png");
        check_Image = new Image("enerdis_Memo4/img/check.png");
        loading_Image = new Image("enerdis_Memo4/img/loading.png");
        timer_action = new Timer_Action();

        Button detection = new Button("Détection");
        detection.getStyleClass().add("button_background");
        detection.setOnAction(event -> {
            detection.setDisable(true);
            chargement_Vbox.getChildren().add(gridpane_detection);
            chargement_Vbox.getChildren().remove(detection);
            detection();
            detection.setDisable(false);
        });
        chargement_Vbox.getChildren().add(detection);
    }

    public void detection() {
        int i = 1;
        while (i != 5) {
            switch (i) {
                case 1:
                    if (timer_action.verification_gps()) {
                        if (Gestion_Module.getLatitude() != null && Gestion_Module.getLongitude() != null) {
                            check_gps = true;
                            System.out.println("\nlatitude: " + Gestion_Module.getLatitude() + "\nlongitude: " + Gestion_Module.getLongitude());
                        } else {
                            check_gps = false;
                        }
                    } else {
                        check_gps = false;
                    }
                    Platform.runLater(() -> {
                        if (check_gps) {
                            verification_GPS_ImageView.setImage(check_Image);
                        } else {
                            verification_GPS_ImageView.setImage(cross_Image);
                        }
                    });
                    System.out.println("Detection du gps : " + check_gps);
                    break;
                case 4:
                    //check module
                    Gestion_Module gestion_module = new Gestion_Module();
                    //pour chaque port verifier si c'est un memo4
                    list_module = FXCollections.observableArrayList();
                    liaisonSerie = new Liaison_Serie();
                    for (String port : liaisonSerie.listePorts()) {
                        if (!port.equals(Gestion_Module.getPort_gps()) && !port.equals("/dev/ttyAMA0")) {
                            int reponse = gestion_module.recuperation_esclave(port);
                            if (reponse != 0) {
                                list_module.add(new Esclave(reponse, port));
                            }
                        }
                    }
                    check_module = !list_module.isEmpty();
                    Platform.runLater(() -> {
                        if (check_module) {
                            verification_Module_ImageView.setImage(check_Image);
                        } else {
                            verification_Module_ImageView.setImage(cross_Image);
                        }
                    });

                    System.out.println("Detection de module : " + check_module);
                    break;
                case 2:
                    //check Lora
                    rn2483 = new Rn2483();
                    rn2483.seConnecter();
                    Platform.runLater(() -> {
                        if (check_lora) {
                            verification_Lora_ImageView.setImage(check_Image);
                        } else {
                            verification_Lora_ImageView.setImage(cross_Image);
                        }
                    });

                    System.out.println("Connexion Lora : " + check_lora);
                    break;
                case 3:
                    //check base de donnée
                    bdd = new Bdd();
                    //recuperation annee et mois
                    if (bdd.connexion()) {
                        check_bdd = true;
                        Controller_Main.setPrix(bdd.recuperation_prix());
                        bdd.deconnexion();
                    }else {
                        check_bdd = false;
                    }
                    Platform.runLater(() -> {
                        if (check_bdd) {
                            verification_Bdd_ImageView.setImage(check_Image);
                        } else {
                            verification_Bdd_ImageView.setImage(cross_Image);
                        }
                    });

                    System.out.println("\u001B[34m"+"Connexion a la Base de donnée : " + check_bdd);
                    break;
            }
            i++;
        }
        //si gps bdd et module sont detecter lancer l'application
        if (check_gps && check_bdd && check_module) {
            Platform.runLater(() -> {
                Button continuer_boutton = new Button("Continuer");
                continuer_boutton.setOnAction(event -> {
                    continuer_boutton.setDisable(true);
                    source = (Node) event.getSource();
                    stage = (Stage) source.getScene().getWindow();
                    stage.close();
                    Stage primaryStage = new Stage();
                    try {
                        //passage des variables

                        Controller_Main.setRn2483(rn2483);
                        Controller_Main.setPort_gps(Gestion_Module.getPort_gps());
                        Controller_Main.setEtat_bdd(check_bdd);
                        Controller_Main.setEtat_gps(check_gps);
                        Controller_Main.setEtat_module(check_module);
                        Controller_Main.setEsclave(list_module.get(0));
                        Controller_Main.setEtat_lora(check_lora);

                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/enerdis_Memo4/FXML/main.fxml")));
                        Scene scene = new Scene(root);
                        primaryStage.setScene(scene);
                        primaryStage.initStyle(StageStyle.UNDECORATED);
                        primaryStage.setTitle("Enerdis");
                        primaryStage.setResizable(false);
                        primaryStage.setFullScreen(false);
                        primaryStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                chargement_Vbox.getChildren().add(continuer_boutton);
            });
        } else {

            Button reesssayer = new Button("Réessayer");
            reesssayer.setOnAction(event -> {
                reesssayer.setDisable(true);
                chargement_Vbox.getChildren().remove(reesssayer);
                detection();
                reesssayer.setDisable(false);
            });
            chargement_Vbox.getChildren().add(reesssayer);
        }
    }

    public void quitter_button_Action(ActionEvent actionEvent) {
        source = (Node) actionEvent.getSource();
        stage = (Stage) source.getScene().getWindow();
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(((Node) actionEvent.getSource()).getScene().getWindow());
        alert.setTitle("Quitter");
        alert.setHeaderText("Êtes-vous sur de vouloir quitter?");
        optional = alert.showAndWait();

        if (optional.get() == ButtonType.OK) {
            stage.close();
        } else if (optional.get() == ButtonType.CANCEL) {
            alert.close();
        }
    }

    public static boolean isCheck_lora() {
        return check_lora;
    }

    public static void setCheck_lora(boolean check_lora) {
        Controller_Pre_Chargement.check_lora = check_lora;
    }
}
