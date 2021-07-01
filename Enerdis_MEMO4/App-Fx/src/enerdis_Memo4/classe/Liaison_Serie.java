package enerdis_Memo4.classe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jssc.*;

import java.text.DecimalFormat;

public class Liaison_Serie implements SerialPortEventListener {

    //Variable
    public SerialPort serialPort;
    private static ModBus modBus;
    private static boolean etat_port, reponse = false;
    private static byte reponse_byte;
    private static float reponse_float;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.########");
    private static final ObservableList<Byte> observableList_byte = FXCollections.observableArrayList();
    private static final ObservableList<Float> observableList_float = FXCollections.observableArrayList();
    private static String type;
    private static String longitude;
    private static String latitude;
    private final Convertisseur convertisseur;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Constructeur vide

    public Liaison_Serie() {
        convertisseur = new Convertisseur();
    }

    //event du serial port
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        //si port non ouvert ouvre le
        //defini le type de reception
        switch (type) {
            case "byte":
                //lis la tramme avec le type defini
                if (serialPortEvent.getEventValue() > 1) {
                    reponse = true;
                    Mesure.setReponse(true);
                    byte modbus_reponse_b = convertisseur.from_Array_to_byte(lecture_byte(7));
                    System.out.println("\u001B[33m"+"Reponse (valeur en byte): " + modbus_reponse_b + "\ntaille = " + serialPortEvent.getEventValue() + "\n*****************************\n");
                    reponse_byte = modbus_reponse_b;
                    observableList_byte.add(modbus_reponse_b);
                }
                break;
            case "float":
                //lis la tramme avec le type defini
                if (serialPortEvent.getEventValue() > 1) {
                    reponse = true;
                    Mesure.setReponse(true);
                    float modbus_reponse_f = convertisseur.from_Array_to_float(lecture_byte(9));
                    System.out.println("\u001B[33m"+"Reponse (valeur en float): " + modbus_reponse_f + "\ntaille = " + serialPortEvent.getEventValue() + "\n*****************************\n");
                    reponse_float = modbus_reponse_f;
                    observableList_float.add(modbus_reponse_f);
                }
                break;
            //cas utilise pour la detection de gps
            case "string":
                String s = lecture_String();
                String[] strings;
                if (s != null) {
                    if (s.startsWith("$GPGGA")) {
                        Gestion_Module.setReponse(true);
                        strings = s.split(",");
                        for (int i = 0; i < strings.length; i++) {
                            if (!strings[5].isEmpty() && !strings[3].isEmpty()) {
                                longitude = decimalFormat.format(Double.parseDouble(strings[4].substring(0,3)) + (Double.parseDouble(strings[4].substring(3))/60));
                                latitude = decimalFormat.format(Double.parseDouble(strings[2].substring(0,2))+ (Double.parseDouble(strings[2].substring(2))/60));
                                break;
                            }
                        }
                    }
                }
                break;
            //default
            default:
                System.out.println("default liaison serie");
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //list tout les ports disponible en les affichant et en les stockant dans un arraylist
    //peut etre recuperer comme ca :ArrayList<String> list_port =  liaison_serie.listePorts();

    public ObservableList<String> listePorts() {
        ObservableList<String> list_ports = FXCollections.observableArrayList();
        String nom_port;
        int nombre_Port = SerialPortList.getPortNames().length;
        for (int i = 0; i < nombre_Port; i++) {
            nom_port = SerialPortList.getPortNames()[i];
            list_ports.add(nom_port);
        }
        System.out.println("\u001B[33m"+"/////////////////////////////////////////\n\nPort trouver:");
        for (String s : list_ports) {
            System.out.println(s);
        }
        System.out.println("\u001B[33m"+"\n/////////////////////////////////////////");
        return list_ports;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //recupere le port et le configure

    public void configurationPort(String port, int baudrate) {
        try {
            int mask = SerialPort.MASK_RXCHAR;
            this.serialPort = new SerialPort(port);
            if (serialPort.isOpened()) {
                fermer_Port();
            } else {
                ouvre_Port();
            }
            setEtat_port(true);
            this.serialPort.setParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            this.serialPort.addEventListener(this, mask);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //ferme le port

    public void fermer_Port() {
        try {
            this.serialPort.closePort();
            setEtat_port(false);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //ouvre le port

    public void ouvre_Port() {
        try {
            this.serialPort.openPort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Lis les bytes sur le port
    public byte[] lecture_byte(int taille) {
        try {
            return this.serialPort.readBytes(taille);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Lis le String sur le port
    public String lecture_String() {
        try {
            return this.serialPort.readString();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Ecrit un byte sur le port

    public void ecriture(byte[] b) {
        try {
            for (byte s : b) {
                this.serialPort.writeByte(s);
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isEtat_port() {
        return etat_port;
    }

    public static void setEtat_port(boolean etat_port) {
        Liaison_Serie.etat_port = etat_port;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static String getLatitude() {
        return latitude;
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        Liaison_Serie.type = type;
    }

    public static ModBus getModBus() {
        return modBus;
    }

    public static void setModBus(ModBus modBus) {
        Liaison_Serie.modBus = modBus;
    }

    public static byte getReponse_byte() {
        return reponse_byte;
    }

    public static float getReponse_float() {
        return reponse_float;
    }

    public static boolean isReponse() {
        return reponse;
    }

    public static ObservableList<Byte> getObservableList_byte() {
        return observableList_byte;
    }

    public static ObservableList<Float> getObservableList_float() {
        return observableList_float;
    }
}
