package enerdis_Memo4.classe;

import enerdis_Memo4.Controller.Controller_Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bdd {
    private static String identifiant = "root", mot_de_passe = "toor", url = "jdbc:sqlite:bdd_enerdis_projet.sqlite";
    private static ObservableList<String> jour_ObservableList, mois_ObservableList, annee_ObservableList;
    public ObservableList<Float> donnee_float = FXCollections.observableArrayList();
    private String requete;
    private final String[] colonne = new String[]{"prix", "tension (V)", "facteur puissance (K)", "courant (A)", "puissance (W)", "q (VAR)", "s (VA)", "energie (kWh)"};
    private Connection connection;
    private ResultSet rs;
    private PreparedStatement preparedStatement;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Bdd() {
    }

    //preparation des drivers
    public boolean test_Drivers() {
        try {
            Class.forName("org.sqlite.JDBC");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    //connexion et verification de l'etat des drivers
    public boolean connexion() {
        if (test_Drivers()) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:bdd_enerdis_projet.sqlite");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("\u001B[34m"+"impossible de se connecté a la bdd");
                return false;
            }
        } else {
            System.out.println("\u001B[34m"+"Driver non pret");
            return false;
        }
    }

    //deconnexion
    public void deconnexion() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                System.err.println("\u001B[34m"+"Déconnexion impossible de la bdd");
            }
        }
    }

    public Mesure valeur_max(String annee, String mois, String jour) {
        donnee_float.clear();
        for (String s : colonne) {
            if (annee.equals("00")) {
                if (mois.equals("00")) {
                    if (jour.equals("00")) {
                        requete = "SELECT MAX(`" + s + "`) FROM `mesures` where `date` BETWEEN '2001-12-14 00:00:00' AND '3000-12-31 23:59:59'; ";
                    }
                }
            } else if (mois.equals("00")) {
                if (jour.equals("00")) {
                    requete = "SELECT MAX(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-01-01 00:00:00' AND '" + annee + "-12-31 23:59:59'; ";
                }
            } else if (jour.equals("00")) {
                requete = "SELECT MAX(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-" + mois + "-01 00:00:00' AND '" + annee + "-" + mois + "-31 23:59:59'; ";
            } else {
                requete = "SELECT MAX(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-" + mois + "-" + jour + " 00:00:00' AND '" + annee + "-" + mois + "-" + jour + " 23:59:59'; ";
            }
            try {

                if (connexion()) {
                    preparedStatement = connection.prepareStatement(requete);
                    rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        donnee_float.add((float) rs.getInt(1));
                    }
                    deconnexion();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if (this.rs != null) {
                    try {
                        this.rs.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    this.rs = null;
                }
                if (this.preparedStatement != null) {
                    try {
                        this.preparedStatement.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    this.preparedStatement = null;
                }
            }

        }

        return new Mesure(donnee_float.get(0), donnee_float.get(1)
                , donnee_float.get(2), donnee_float.get(3), donnee_float.get(4)
                , donnee_float.get(5), donnee_float.get(6), donnee_float.get(7));

    }

    public Mesure valeur_min(String annee, String mois, String jour) {
        donnee_float.clear();
        for (String s : colonne) {
            if (annee.equals("00")) {
                if (mois.equals("00")) {
                    if (jour.equals("00")) {
                        requete = "SELECT MIN(`" + s + "`) FROM `mesures` where `date` BETWEEN '2001-12-14 00:00:00' AND '3000-12-31 23:59:59'; ";
                    }
                }
            } else if (mois.equals("00")) {
                if (jour.equals("00")) {
                    requete = "SELECT MIN(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-01-01 00:00:00' AND '" + annee + "-12-31 23:59:59'; ";
                }
            } else if (jour.equals("00")) {
                requete = "SELECT MIN(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-" + mois + "-01 00:00:00' AND '" + annee + "-" + mois + "-31 23:59:59'; ";
            } else {
                requete = "SELECT MIN(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-" + mois + "-" + jour + " 00:00:00' AND '" + annee + "-" + mois + "-" + jour + " 23:59:59'; ";
            }
            try {
                if (connexion()) {
                    preparedStatement = connection.prepareStatement(requete);
                    rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        donnee_float.add((float) rs.getInt(1));
                    }
                    deconnexion();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if (this.rs != null) {
                    try {
                        this.rs.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    this.rs = null;
                }
                if (this.preparedStatement != null) {
                    try {
                        this.preparedStatement.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    this.preparedStatement = null;
                }
            }
        }

        return new Mesure(donnee_float.get(0), donnee_float.get(1)
                , donnee_float.get(2), donnee_float.get(3), donnee_float.get(4)
                , donnee_float.get(5), donnee_float.get(6), donnee_float.get(7));

    }

    public Mesure valeur_moyenne(String annee, String mois, String jour) {
        donnee_float.clear();
        for (String s : colonne) {
            if (annee.equals("00")) {
                if (mois.equals("00")) {
                    if (jour.equals("00")) {
                        requete = "SELECT AVG(`" + s + "`) FROM `mesures` where `date` BETWEEN '2001-12-14 00:00:00' AND '3000-12-31 23:59:59'; ";
                    }
                }
            } else if (mois.equals("00")) {
                if (jour.equals("00")) {
                    requete = "SELECT AVG(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-01-01 00:00:00' AND '" + annee + "-12-31 23:59:59'; ";
                }
            } else if (jour.equals("00")) {
                requete = "SELECT AVG(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-" + mois + "-01 00:00:00' AND '" + annee + "-" + mois + "-31 23:59:59'; ";
            } else {
                requete = "SELECT AVG(`" + s + "`) FROM `mesures` where `date` BETWEEN '" + annee + "-" + mois + "-" + jour + " 00:00:00' AND '" + annee + "-" + mois + "-" + jour + " 23:59:59'; ";
            }
            try {
                if (connexion()) {
                    preparedStatement = connection.prepareStatement(requete);
                    rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        donnee_float.add((float) rs.getInt(1));
                    }
                    deconnexion();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if (this.rs != null) {
                    try {
                        this.rs.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    this.rs = null;
                }
                if (this.preparedStatement != null) {
                    try {
                        this.preparedStatement.close();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                    this.preparedStatement = null;
                }
            }
        }
        return new Mesure(donnee_float.get(0), donnee_float.get(1)
                , donnee_float.get(2), donnee_float.get(3), donnee_float.get(4)
                , donnee_float.get(5), donnee_float.get(6), donnee_float.get(7));
    }

    public ObservableList<String> recuperation_annee() {
        annee_ObservableList = FXCollections.observableArrayList();
        annee_ObservableList.clear();
        try {
            System.out.println("\u001B[34m"+"Recuperation dans la bdd des années");
            requete = "SELECT `date` FROM `mesures`";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    String annee = rs.getString("date").substring(0, 4);
                    if (annee_ObservableList.size() == 0) {
                        annee_ObservableList.add(annee);
                    } else {
                        for (int i = 0; i < annee_ObservableList.size(); i++) {
                            if (!annee_ObservableList.contains(annee)) {
                                annee_ObservableList.add(annee);
                            }
                        }
                    }
                }
                deconnexion();
            }
            return annee_ObservableList;
        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
            return null;
        }


    }

    public ObservableList<String> recuperation_mois(String annee) {
        mois_ObservableList = FXCollections.observableArrayList();
        mois_ObservableList.clear();
        try {
            System.out.println("\u001B[34m"+"Recuperation dans la bdd des mois");
            requete = "SELECT `date` FROM `mesures` WHERE `date` BETWEEN '" + annee + "-01-01 00:00:00' AND '" + annee + "-12-30 00:00:00';";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    //recuperation des données de ce mois

                    String mois = rs.getString("date").substring(5, 7);
                    if (mois_ObservableList.size() == 0) {
                        mois_ObservableList.add(mois);
                    } else {
                        for (int i = 0; i < mois_ObservableList.size(); i++) {
                            if (!mois_ObservableList.contains(mois)) {
                                mois_ObservableList.add(mois);
                            }
                        }
                    }
                }
                deconnexion();
            }
            return mois_ObservableList;
        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
            return null;
        }

    }

    public ObservableList<String> recuperation_jour(String annee, String mois) {
        jour_ObservableList = FXCollections.observableArrayList();
        jour_ObservableList.clear();
        try {
            System.out.println("\u001B[34m"+"Recuperation dans la bdd des jours");
            requete = "SELECT `date` FROM `mesures` WHERE `date` BETWEEN '" + annee + "-" + mois + "-01 00:00:00' AND '" + annee + "-" + mois + "-30 00:00:00';";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    //recuperation des données de ce jour

                    String jour = rs.getString("date").substring(8, 10);
                    if (jour_ObservableList.size() == 0) {
                        jour_ObservableList.add(jour);
                    } else {
                        for (int i = 0; i < jour_ObservableList.size(); i++) {
                            if (!jour_ObservableList.contains(jour)) {
                                jour_ObservableList.add(jour);
                            }
                        }
                    }
                }
                deconnexion();
            }
            return jour_ObservableList;
        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
            return null;
        }

    }

    public void recuperation_donnee_jour(String annee, String mois, String jour, Controller_Main controller_main,String commande) {
        try {
            System.out.println("\u001B[34m"+"Recuperation dans la bdd des données d'un jour precis");
            requete = "SELECT * FROM `mesures` WHERE `date` BETWEEN '" + annee + "-" + mois + "-" + jour + " 00:00:00' AND '" + annee + "-" + mois + "-" + jour + " 23:59:59';";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    Gestion_Mesures.getDataSeries_Linechart().getData().add(new XYChart.Data<>(rs.getString("date").substring(11, 13) + rs.getString("date").substring(13, 16), rs.getFloat(commande)));
                }
                deconnexion();
            }

            if (!controller_main.graphique_LineChart.getData().contains(Gestion_Mesures.getDataSeries_Linechart())) {
                Gestion_Mesures.getDataSeries_Linechart().setName(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
                controller_main.graphique_LineChart.getData().add(Gestion_Mesures.getDataSeries_Linechart());
            }

        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
        }

    }


    public int recuperation_prix() {
        try {
            requete = "SELECT `prix`  FROM `mesures` WHERE   `id_mesures` = (SELECT MAX(`id_mesures`)  FROM `mesures`);";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getInt("prix");
                }
                deconnexion();
            }

        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
            return 0;
        }

    }

    public float[] recuperation_mesure() {
        try {
            float[] tableau_float = new float[100];
            requete = "SELECT *  FROM `mesures` WHERE   `id_mesures` = (SELECT MAX(`id_mesures`)  FROM `mesures`);";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    tableau_float = new float[]{
                            rs.getFloat("courant"), rs.getFloat("tension"),
                            rs.getFloat("facteur_puissance_k"), rs.getFloat("p_W"),
                            rs.getFloat("w_kWh"), rs.getFloat("s_VA"),
                            rs.getFloat("q_VAR"), rs.getFloat("prix"),
                            Float.parseFloat(rs.getString("longitude").replace(',','.')), Float.parseFloat(rs.getString("latitude").replace(',','.'))
                    };
                }
                deconnexion();
                return tableau_float;
            }

        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
        }
        return null;
    }

    //recuperation de données
    public ObservableList<String> recuperation_donnees(String table) {
        try {
            ObservableList<String> donneeObservableList = FXCollections.observableArrayList();
            System.out.println("\u001B[34m"+"-------------------------------------------------------");
            switch (table) {

                case "alarmes":
                    requete = "SELECT * FROM `" + table + "`";
                    if (connexion()) {
                        preparedStatement = connection.prepareStatement(requete);
                        rs = preparedStatement.executeQuery();
                        while (rs.next()) {
                            donneeObservableList.add(rs.getString("module_non_disponible"));
                            donneeObservableList.add(rs.getString("lora_indisponible_1h"));
                        }
                        deconnexion();
                    }

                    break;
                case "mesures":
                    requete = "SELECT * FROM `" + table + "`";
                    if (connexion()) {
                        preparedStatement = connection.prepareStatement(requete);
                        rs = preparedStatement.executeQuery();
                        while (rs.next()) {
                            donneeObservableList.add(rs.getString("date"));
                            donneeObservableList.add(rs.getString("courant (A)"));
                            donneeObservableList.add(rs.getString("tension (V)"));
                            donneeObservableList.add(rs.getString("facteur puissance (K)"));
                            donneeObservableList.add(rs.getString("puissance (W)"));
                            donneeObservableList.add(rs.getString("energie (kWh)"));
                            donneeObservableList.add(rs.getString("s (VA)"));
                            donneeObservableList.add(rs.getString("q (VAR)"));
                            donneeObservableList.add(rs.getString("prix"));
                            donneeObservableList.add(rs.getString("longitude"));
                            donneeObservableList.add(rs.getString("latitude"));
                        }
                        deconnexion();
                    }
                    break;
            }
            return donneeObservableList;
        } catch (SQLException e) {
            System.err.println();
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }
            return null;
        }

    }

    //écriture alarmes
    public boolean ecriture_alarmes(String module_connexion) {
        try {
            System.out.println("-------------------------------------------------------");
            requete = "INSERT INTO `alarmes`(`module_connexion`,`date`) VALUES ('" + module_connexion +"','"+ dateTimeFormatter.format(LocalDateTime.now())+"') ";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                preparedStatement.executeUpdate();
                deconnexion();
            }

            System.out.println("\u001B[34m"+"écriture de la ligne réussi");
            return true;
        } catch (SQLException e) {
            System.err.println("\u001B[34m"+"impossible d'écrire la ligne");
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }

        }
        return false;
    }

    //écriture mesure
    public boolean ecriture_mesures(Mesure mesure) {
        try {
            System.out.println("-------------------------------------------------------");

            requete = "INSERT INTO `mesures` (`date`,`courant (A)`,`tension (V)`,`facteur puissance (K)`,`puissance (W)`,`energie (kWh)`,`s (VA)`,`q (VAR)`,`prix`,`longitude`,`latitude`)" +
                    "VALUES ('" + dateTimeFormatter.format(LocalDateTime.now()) + "','" + mesure.getCourant() +
                    "', '" + mesure.getTension() + "', '" + mesure.getFacteur_puissance() + "', '" + mesure.getPuissance_W() +
                    "', '" + mesure.getEnergie_KWH() + "', '" + mesure.getS_VA() + "', '" + mesure.getQ_VAR() + "', '" + mesure.getPrix() +
                    "', '" + mesure.getLongitude() + "', '" + mesure.getLatitude() + "');";
            if (connexion()) {
                preparedStatement = connection.prepareStatement(requete);
                preparedStatement.executeUpdate();
                deconnexion();
            }
            System.out.println("\u001B[34m"+"écriture de la ligne réussi");
            return true;
        } catch (SQLException e) {
            System.err.println("\u001B[34m"+"impossible d'écrire la ligne");
            e.printStackTrace();
        } finally {
            if (this.rs != null) {
                try {
                    this.rs.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.rs = null;
            }
            if (this.preparedStatement != null) {
                try {
                    this.preparedStatement.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }
                this.preparedStatement = null;
            }

        }
        return false;
    }

    public static String getIdentifiant() {
        return identifiant;
    }

    public static void setIdentifiant(String identifiant) {
        Bdd.identifiant = identifiant;
    }

    public static String getMot_de_passe() {
        return mot_de_passe;
    }

    public static void setMot_de_passe(String mot_de_passe) {
        Bdd.mot_de_passe = mot_de_passe;
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Bdd.url = url;
    }

    public static ObservableList<String> getJour_ObservableList() {
        return jour_ObservableList;
    }

    public static ObservableList<String> getMois_ObservableList() {
        return mois_ObservableList;
    }

    public static ObservableList<String> getAnnee_ObservableList() {
        return annee_ObservableList;
    }

}

