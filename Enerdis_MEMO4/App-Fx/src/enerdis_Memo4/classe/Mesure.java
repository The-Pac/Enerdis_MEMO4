package enerdis_Memo4.classe;

import enerdis_Memo4.Controller.Controller_Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class Mesure {
    private static boolean reponse;
    private float tension, facteur_puissance, courant, puissance_W, q_VAR, s_VA, energie_KWH, prix;
    private String latitude, longitude,module;
    private ModBus modBus;
    private Commande commande;
    private byte[] trames_Byte_Tableau;
    private CRC16 crc16;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Mesure() {
        modBus = new ModBus();
        crc16 = new CRC16();
        commande = new Commande();
    }

    public Mesure(String module, float prix, float tension, float facteur_puissance, float courant, float puissance_W,
                  float q_VAR, float s_VA, float puissance_KWH, String latitude, String longitude) {
        this.module = module;
        this.prix = prix;
        this.tension = tension;
        this.facteur_puissance = facteur_puissance;
        this.courant = courant;
        this.puissance_W = puissance_W;
        this.q_VAR = q_VAR;
        this.s_VA = s_VA;
        this.energie_KWH = puissance_KWH;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Mesure(float prix, float tension, float facteur_puissance, float courant, float puissance_W,
                  float q_VAR, float s_VA, float puissance_KWH) {
        this.prix = prix;
        this.tension = tension;
        this.facteur_puissance = facteur_puissance;
        this.courant = courant;
        this.puissance_W = puissance_W;
        this.q_VAR = q_VAR;
        this.s_VA = s_VA;
        this.energie_KWH = puissance_KWH;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean Recuperation_Mesure(int id_esclave, String port, String type_commande) {
        Commande commande = new Commande();
        switch (type_commande) {
            case "Numero Esclave":
                commande = commande.getNumero_Esclave();
                break;
            case "tension (V)":
                commande = commande.getTension();
                break;
            case "courant (A)":
                commande = commande.getCourant();
                break;
            case "facteur puissance (K)":
                commande = commande.getFacteur_puissance();
                break;
            case "puissance (W)":
                commande = commande.getPuissance_W();
                break;
            case "q (VAR)":
                commande = commande.getQ_VAR();
                break;
            case "s (VA)":
                commande = commande.getS_VA();
                break;
            case "energie (kWh)":
                commande = commande.getEnergie_KWH();
                break;
        }

        //init du tableau
        int mode = 0x0003, registre = commande.getRegister(), taille = commande.getTaille();
        byte[] id_byte = new byte[]{(byte) (id_esclave & 0x00ff)};
        byte[] mode_byte = new byte[]{(byte) (mode & 0x00ff)};
        byte[] registre_byte = new byte[]{(byte) ((registre & 0xff00) >> 8), (byte) (registre & 0x00ff)};
        byte[] taille_byte = new byte[]{(byte) ((taille & 0xff00) >> 8), (byte) (taille & 0x00ff)};
        trames_Byte_Tableau = new byte[]{id_byte[0], mode_byte[0], registre_byte[0], registre_byte[1], taille_byte[0], taille_byte[1]};

        //init crc16 tableau
        byte[] crc_tableau = crc16.calcul_Crc16(trames_Byte_Tableau);

        //init tableau final trame + crc16
        byte[] trame_final = new byte[]{trames_Byte_Tableau[0], trames_Byte_Tableau[1], trames_Byte_Tableau[2], trames_Byte_Tableau[3], trames_Byte_Tableau[4]
                , trames_Byte_Tableau[5], crc_tableau[0], crc_tableau[1],};

        System.out.println("\u001B[33m"+"*****************************\nenvoie depuis le " + port + " trame " + Arrays.toString(trames_Byte_Tableau) + " crc16: " + Arrays.toString(crc_tableau) + " la commande : " + commande.getValeur());

        //phase modbus
        modBus = new ModBus();
        modBus.connecterMaitre(port);
        Liaison_Serie.setType(commande.getType());
        modBus.ecriture(trame_final);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (reponse) {
            modBus.fermerLiaisonSerie();
            return true;
        } else {
            modBus.fermerLiaisonSerie();
            return false;
        }
    }

    public Mesure Recuperation_des_Mesure(int id_esclave, String port, Controller_Main controller) {
        Liaison_Serie.getObservableList_float().clear();
        Liaison_Serie.getObservableList_byte().clear();
        for (Commande list_commande : commande.liste_commande()) {
            //init du tableau
            int mode = 0x0003, registre = list_commande.getRegister(), taille = list_commande.getTaille();
            byte[] id_byte = new byte[]{(byte) (id_esclave & 0x00ff)};
            byte[] mode_byte = new byte[]{(byte) (mode & 0x00ff)};
            byte[] registre_byte = new byte[]{(byte) ((registre & 0xff00) >> 8), (byte) (registre & 0x00ff)};
            byte[] taille_byte = new byte[]{(byte) ((taille & 0xff00) >> 8), (byte) (taille & 0x00ff)};
            trames_Byte_Tableau = new byte[]{id_byte[0], mode_byte[0], registre_byte[0], registre_byte[1], taille_byte[0], taille_byte[1]};

            //init crc16 tableau
            byte[] crc_tableau = crc16.calcul_Crc16(trames_Byte_Tableau);

            //init tableau final trame + crc16
            byte[] trame_final = new byte[]{trames_Byte_Tableau[0], trames_Byte_Tableau[1], trames_Byte_Tableau[2], trames_Byte_Tableau[3], trames_Byte_Tableau[4]
                    , trames_Byte_Tableau[5], crc_tableau[0], crc_tableau[1],};

            System.out.println("\u001B[33m"+"*****************************\nenvoie depuis le " + port + " trame " + Arrays.toString(trames_Byte_Tableau) + " crc16: " + Arrays.toString(crc_tableau) + " la commande : " + list_commande.getValeur());

            //phase modbus
            modBus = new ModBus();
            modBus.connecterMaitre(port);
            Liaison_Serie.setType(list_commande.getType());
            modBus.ecriture(trame_final);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            modBus.fermerLiaisonSerie();
        }
        if (Liaison_Serie.getObservableList_float().size() == 7){
            return new Mesure(String.valueOf(trames_Byte_Tableau[0]), Float.parseFloat(controller.label_prix.getText().replace(',', '.')), Liaison_Serie.getObservableList_float().get(0), Liaison_Serie.getObservableList_float().get(1),
                    Liaison_Serie.getObservableList_float().get(2), Liaison_Serie.getObservableList_float().get(3) * 1000, Liaison_Serie.getObservableList_float().get(4) * 1000, Liaison_Serie.getObservableList_float().get(5)* 1000,
                    Liaison_Serie.getObservableList_float().get(6), Liaison_Serie.getLatitude(), Liaison_Serie.getLongitude());
        }
        return null;
    }

    public static void setReponse(boolean reponse) {
        Mesure.reponse = reponse;
    }

    public float getTension() {
        return tension;
    }

    public float getFacteur_puissance() {
        return facteur_puissance;
    }

    public float getCourant() {
        return courant;
    }

    public float getPuissance_W() {
        return puissance_W;
    }

    public float getQ_VAR() {
        return q_VAR;
    }

    public float getS_VA() {
        return s_VA;
    }

    public float getEnergie_KWH() {
        return energie_KWH;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public float getPrix() {
        return prix;
    }
}
