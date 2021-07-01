package enerdis_Memo4.classe;

import enerdis_Memo4.Controller.Controller_Main;
import javafx.collections.ObservableList;
import jssc.SerialPort;

public class Gestion_Module {
    private static String latitude, longitude;
    private static String port_gps = null;
    private static boolean reponse, detection_gps;

    private final Liaison_Serie liaisonSerie;
    private final Mesure mesure;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Gestion_Module() {
        mesure = new Mesure();
        liaisonSerie = new Liaison_Serie();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int recuperation_esclave(String port) {
        int numero_eslave = 0;
        for (int i = 80; i <= 84; i++) {
            if (mesure.Recuperation_Mesure(i, port, "Numero Esclave")) {
                if (Liaison_Serie.isReponse()) {
                    if (i == Liaison_Serie.getReponse_byte()) {
                        numero_eslave = i;
                        break;
                    } else {
                        System.out.println("l'id ne correspond pas a l'id recu");
                        break;
                    }
                } else {
                    System.out.println("Pas de reponse sur l'id: " + i);
                }
            }
        }
        return numero_eslave;
    }

    public boolean recuperation_donnees(int identifiant, String port, Controller_Main controller_main) {
        Gestion_Mesures gestion_mesures = new Gestion_Mesures();
        return gestion_mesures.afficher_mesures(identifiant, port, controller_main,controller_main.valeur_Filtre_ChoiceBox.getValue());
    }

    public boolean check_esclave(int identifiant, String port) {
        return mesure.Recuperation_Mesure(identifiant, port, "Numero Esclave");
    }

    public boolean verfication_gps(String port) {
        liaisonSerie.configurationPort(port, SerialPort.BAUDRATE_9600);
        Liaison_Serie.setType("string");
        System.out.println("\u001B[31m" + "\nScan du port : " + port);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (reponse) {
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latitude = Liaison_Serie.getLatitude();
            longitude = Liaison_Serie.getLongitude();
            port_gps = port;
            liaisonSerie.fermer_Port();
            return true;
        } else {
            System.out.println("\u001B[31m" + "aucune reponse\n");
        }
        liaisonSerie.fermer_Port();

        return false;
    }

    public boolean check_gps() {
        Liaison_Serie.setType("string");
        ObservableList<String> list_port = liaisonSerie.listePorts();
        for (String port : list_port) {
            liaisonSerie.configurationPort(port, SerialPort.BAUDRATE_9600);
            Liaison_Serie.setType("string");
            System.out.println("\u001B[31m" + "\nScan du port : " + port);
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (reponse) {
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                latitude = Liaison_Serie.getLatitude();
                longitude = Liaison_Serie.getLongitude();
                port_gps = port;
                liaisonSerie.fermer_Port();
                return true;
            } else {
                System.out.println("\u001B[31m" + "aucune reponse\n");
                liaisonSerie.fermer_Port();
            }
        }
        return false;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public static String getLatitude() {
        return latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static String getPort_gps() {
        return port_gps;
    }

    public static boolean isReponse() {
        return reponse;
    }

    public static void setReponse(boolean reponse) {
        Gestion_Module.reponse = reponse;
    }

    public static boolean isDetection_gps() {
        return detection_gps;
    }

    public static void setDetection_gps(boolean detection_gps) {
        Gestion_Module.detection_gps = detection_gps;
    }
}
