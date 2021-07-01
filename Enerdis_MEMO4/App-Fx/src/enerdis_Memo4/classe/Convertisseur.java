package enerdis_Memo4.classe;

import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Convertisseur {
    public Convertisseur() {
    }

    public byte hex_to_byte(String s) {
        return new BigInteger(s, 16).byteValue();
    }

    public byte from_Array_to_byte(byte[] packet) {
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.get();
    }

    public float from_Array_to_float(byte[] packet) {
        System.out.println(Arrays.toString(packet));
        byte[] tableau_donnes = new byte[]{packet[3], packet[4], packet[5], packet[6]};
        ByteBuffer buffer = ByteBuffer.wrap(tableau_donnes);
        return buffer.getFloat();
    }

    public byte[] to_Array(float valeur) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putFloat(valeur);
        buffer.flip();
        return buffer.array();
    }

    public String int_to_hex(int i) {
        return String.format("%X", i);
    }
}
