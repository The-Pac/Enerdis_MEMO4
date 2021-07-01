package enerdis_Memo4.classe;


import java.util.ArrayList;

public class Commande {
    private String valeur, type;
    private int register, taille;
    private Commande numero_Esclave, tension, courant, facteur_puissance, puissance_W, q_VAR, s_VA, energie_KWH;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Commande() {
        numero_Esclave = new Commande(0x1018, 0x01, "Numero Esclave", "byte");
        tension = new Commande(0x2000, 0x02, "tension (V)", "float");
        courant = new Commande(0x2060, 0x02, "courant (A)", "float");
        facteur_puissance = new Commande(0x20E0, 0x02, "facteur puissance (K)", "float");
        puissance_W = new Commande(0x2080, 0x02, "puissance (W)", "float");
        q_VAR = new Commande(0x20A0, 0x02, "q (VAR)", "float");
        s_VA = new Commande(0x20C0, 0x02, "s (VA)", "float");
        energie_KWH = new Commande(0x3000, 0x02, "energie (kWh)", "float");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Commande(int register, int taille, String valeur, String type) {
        this.register = register;
        this.taille = taille;
        this.valeur = valeur;
        this.type = type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public ArrayList<Commande> liste_commande() {
        ArrayList<Commande> liste_Commande = new ArrayList<>();
        liste_Commande.add(tension);
        liste_Commande.add(facteur_puissance);
        liste_Commande.add(courant);
        liste_Commande.add(puissance_W);
        liste_Commande.add(q_VAR);
        liste_Commande.add(s_VA);
        liste_Commande.add(energie_KWH);
        return liste_Commande;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int getRegister() {
        return register;
    }

    public int getTaille() {
        return taille;
    }

    public String getValeur() {
        return valeur;
    }

    public String getType() {
        return type;
    }

    public Commande getNumero_Esclave() {
        return numero_Esclave;
    }

    public Commande getTension() {
        return tension;
    }

    public Commande getCourant() {
        return courant;
    }

    public Commande getFacteur_puissance() {
        return facteur_puissance;
    }

    public Commande getPuissance_W() {
        return puissance_W;
    }

    public Commande getQ_VAR() {
        return q_VAR;
    }

    public Commande getS_VA() {
        return s_VA;
    }

    public Commande getEnergie_KWH() {
        return energie_KWH;
    }
}
