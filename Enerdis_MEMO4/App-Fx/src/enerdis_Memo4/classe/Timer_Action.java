package enerdis_Memo4.classe;

import enerdis_Memo4.Controller.Controller_Main;
import javafx.application.Platform;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Timer_Action {
    private final Bdd bdd;
    private final Gestion_Module gestion_module;
    private final DecimalFormat decimalFormat_date = new DecimalFormat("00");

    public Timer_Action() {
        gestion_module = new Gestion_Module();
        bdd = new Bdd();
    }

    public void timer_task(Controller_Main controller_main) {
        TimerTask task_bdd = new TimerTask() {
            @Override
            public void run() {
                //check composant
                System.out.println("\u001B[34m" + "\n-------------------------------------------------------\nEnvoie a la bdd\n");
                System.out.println(LocalDateTime.now());
                if (gestion_module.verfication_gps(Gestion_Module.getPort_gps())) {
                    if (Gestion_Module.getLatitude() != null && Gestion_Module.getLongitude() != null) {
                        Controller_Main.setEtat_gps(true);
                        int identifiant_esclave = Controller_Main.getEsclave().getIdentifiant();
                        String port_esclave = Controller_Main.getEsclave().getPort();
                        if (gestion_module.check_esclave(identifiant_esclave, port_esclave)) {
                            Controller_Main.setEtat_module(true);
                            if (gestion_module.recuperation_donnees(identifiant_esclave, port_esclave, controller_main)) {
                                if (bdd.connexion()) {
                                    Controller_Main.setEtat_bdd(true);
                                    System.out.println("\u001B[34m" + "connexion a la bdd reussi");
                                    if (bdd.ecriture_mesures(Gestion_Mesures.getValeur())) {
                                        System.out.println("\u001B[34m" + "recuperation du max et min de ce jours");
                                        Mesure mesure_max = bdd.valeur_max(String.valueOf(LocalDateTime.now().getYear()), decimalFormat_date.format(LocalDateTime.now().getMonthValue()), decimalFormat_date.format(LocalDateTime.now().getDayOfMonth())),
                                                mesure_min = bdd.valeur_min(String.valueOf(LocalDateTime.now().getYear()), String.valueOf(LocalDateTime.now().getMonthValue()), String.valueOf(LocalDateTime.now().getDayOfMonth()));
                                        Platform.runLater(() -> controller_main.afficher_valeur_label_direct(mesure_max, mesure_min));
                                    } else {
                                        System.out.println("\u001B[34m" + "impossible d'ecrire dans la bdd");
                                    }
                                } else {
                                    Controller_Main.setEtat_bdd(false);
                                    System.out.println("\u001B[34m" + "connexion a la bdd echoué");
                                }
                            } else {
                                System.out.println("\u001B[34m" + "Aucune données");
                            }
                        } else {
                            Controller_Main.setEtat_module(false);
                        }
                    } else {
                        Controller_Main.setEtat_gps(false);
                    }
                } else {
                    Controller_Main.setEtat_gps(false);
                    System.out.println("GPS non detecte");
                }
            }
        };
        Timer timer_bdd = new Timer(true);
        timer_bdd.scheduleAtFixedRate(task_bdd, 0, 20 * 1000);

        //partie lora
        TimerTask task_lora = new TimerTask() {
            @Override
            public void run() {
                System.out.println("\u001B[35m" + "\n-------------------------------------------------------\nEnvoie au lora\n");
                System.out.println(LocalDateTime.now());
                if (verification_connexion_lora(Controller_Main.getRn2483())) {
                    Controller_Main.setEtat_lora(true);
                    if (bdd.connexion()) {
                        float[] tableau_trame = bdd.recuperation_mesure();
                        System.out.println("Trame envoyé LoRa: " + Arrays.toString(tableau_trame));
                        Controller_Main.getRn2483().envoyerPayload(Controller_Main.getRn2483().floatToByte(tableau_trame));
                        System.out.println("\u001B[35m" + "connexion au lora réussi ");
                    }
                } else {
                    Controller_Main.setEtat_lora(false);
                    System.out.println("\u001B[35m" + "connexion au lora echoué ");
                }
            }
        };
        Timer timer_lora = new Timer(true);
        timer_lora.scheduleAtFixedRate(task_lora, 20 * 1000, 300 * 1000);

    }

    public boolean verification_connexion_lora(Rn2483 rn2483) {
        return rn2483.verifierConnecter();
    }

    public boolean verification_gps() {
        if (gestion_module.check_gps()) {
            return true;
        } else {
            System.out.println("impossible de trouver le gps");
            return false;
        }
    }
}
