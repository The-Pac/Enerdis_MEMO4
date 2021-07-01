package enerdis_Memo4.classe;

import enerdis_Memo4.Controller.Controller_Pre_Chargement;
import jssc.SerialPort;
import jssc.SerialPortEvent;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Rn2483 extends Liaison_Serie {
    private static String retour;

    public Rn2483() {
        configurationPort("/dev/ttyAMA0", SerialPort.BAUDRATE_57600);
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        retour = new String(lecture_byte(serialPortEvent.getEventValue()));
        System.out.println(retour);
    }

    public boolean seConnecter() {
        try {
            System.out.println("\u001B[35m" + "/////////////////////////////////////////");
            System.out.println("\u001B[35m" + "LorA\n");
            envoyerTrame("sys reset");
            envoyerTrame("mac set appkey 3050EF78828329867369222B113820C2");
            envoyerTrame("mac set appeui 70B3D57ED003CB9D");
            envoyerTrame("mac save");
            envoyerTrame("mac join otaa");

            System.out.println("\nsleep de 10 secondes");
            Thread.sleep(10000);

            if (retour != null) {
                if (retour.trim().equals("accepted")) {
                    System.out.println("\u001B[35m" + "requete accepté");
                    System.out.println("\u001B[35m" + "Module RN2484 connecté");
                    Controller_Pre_Chargement.setCheck_lora(true);
                    return true;
                } else {
                    System.out.println("\u001B[35m" + "requete refusé");
                    System.out.println("\u001B[35m" + "Module RN2483 non connecté");
                    Thread.sleep(60 * 1000);
                    seConnecter();
                    return false;
                }
            } else {
                Thread.sleep(60 * 1000);
                seConnecter();
            }
            System.out.println("\u001B[35m" + "/////////////////////////////////////////\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifierConnecter() {
        envoyerTrame("mac get status");
        if (retour != null) {
            if (retour.trim().equals("00000000")) {
                System.out.println("\u001B[35m" + "Module RN2483 non connecte");
                return false;
            } else {
                System.out.println("\u001B[35m" + "Module RN2484 connecte");
                return true;
            }
        } else {
            System.out.println("\u001B[35m" + "retour  = " + retour);
            return false;
        }
    }

    public void envoyerPayload(byte[] tabByte) {

        String payload = bytesToHex(tabByte);
        envoyerTrame("mac tx uncnf 1 " + payload);

    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private void envoyerTrame(String trame) {

        System.out.println("\u001B[35m" + "***** envoie trame LoRa: " + trame + " *****");
        trame = trame + "\r\n";
        ecriture(trame.getBytes());
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public byte[] floatToByte(float[] value) {
        byte[] tabByte = new byte[value.length * 4];
        for (int i = 0; i < value.length; i++) {
            ByteBuffer.wrap(tabByte, i * 4, 4).putFloat(value[i]);
        }
        return tabByte;
    }
}
