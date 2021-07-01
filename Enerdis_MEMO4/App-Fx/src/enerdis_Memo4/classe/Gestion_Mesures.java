package enerdis_Memo4.classe;

import enerdis_Memo4.Controller.Controller_Main;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gestion_Mesures {
    private final Mesure mesure;
    private static Mesure valeur;
    private static final XYChart.Series<String, Number> dataSeries_Linechart = new XYChart.Series<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private final Date date = new Date();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Gestion_Mesures() {
        this.mesure = new Mesure();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean afficher_mesures(int numero_esclave, String port, Controller_Main controller, String type_commande) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        valeur = mesure.Recuperation_des_Mesure(numero_esclave, port, controller);
        if (valeur != null) {
            System.out.println("\u001B[32m" + "**********Mesures************\nModule : " + numero_esclave +
                    "\u001B[32m" + "\n*tension = " + valeur.getTension() +
                    "\u001B[32m" + "\n*courant = " + valeur.getCourant() +
                    "\u001B[32m" + "\n*puissanceW = " + valeur.getPuissance_W() +
                    "\u001B[32m" + "\n*energieKWH = " + valeur.getEnergie_KWH() +
                    "\u001B[32m" + "\n*qVAR = " + valeur.getQ_VAR() +
                    "\u001B[32m" + "\n*sVA = " + valeur.getS_VA() +
                    "\u001B[32m" + "\n*K = " + valeur.getFacteur_puissance());

            Platform.runLater(() -> {
                controller.actuel_tension_Label.setText(decimalFormat.format(valeur.getTension()) + "V");
                controller.actuel_courant_Label.setText(decimalFormat.format(valeur.getCourant()) + "A");
                controller.actuel_Puissance_w_Label.setText(decimalFormat.format(valeur.getPuissance_W()) + "W");
                controller.actuel_puisssance_KWH_Label.setText(decimalFormat.format(valeur.getEnergie_KWH()) + "kWh");
                controller.actuel_Q_var_Label.setText(decimalFormat.format(valeur.getQ_VAR()) + "Var");
                controller.actuel_S_va_Label.setText(decimalFormat.format(valeur.getS_VA()) + "Va");
                controller.actuel_K_var_Label.setText(decimalFormat.format(valeur.getFacteur_puissance()));

                switch (type_commande) {
                    case "tension (V)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getTension()));
                        break;
                    case "courant (A)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getCourant()));
                        break;
                    case "facteur puissance (K)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getFacteur_puissance()));
                        break;
                    case "puissance (W)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getPuissance_W()));
                        break;
                    case "q (VAR)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getQ_VAR()));
                        break;
                    case "s (VA)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getS_VA()));
                        break;
                    case "energie (kWh)":
                        dataSeries_Linechart.getData().add(new XYChart.Data<>(dateFormat.format(date.getTime()), valeur.getEnergie_KWH()));
                        break;
                }
                if (!controller.graphique_LineChart.getData().contains(dataSeries_Linechart)) {
                    controller.graphique_LineChart.getData().add(dataSeries_Linechart);
                }
            });
            System.out.println("\u001B[32m" + "\n*****************************");
        } else {
            System.out.println("\u001B[32m" + "\nerreur de recuperation des données du module");
            System.out.println("\u001B[32m" + "\n*****************************");
        }

        return valeur != null;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Mesure getValeur() {
        return valeur;
    }

    public static XYChart.Series<String, Number> getDataSeries_Linechart() {
        return dataSeries_Linechart;
    }
}
