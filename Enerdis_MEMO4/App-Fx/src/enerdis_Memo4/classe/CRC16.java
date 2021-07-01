package enerdis_Memo4.classe;

public class CRC16 {

    public CRC16() {
    }

    public byte[] calcul_Crc16(byte[] octets) {
        byte[] bytes = new byte[2];
        int stdPoly = 0xA001;
        int initialValue = 0xffff;
        for (int p = 0; p < octets.length; p++) {
            initialValue ^= (octets[p] & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((initialValue & 1) != 0) {
                    initialValue = (initialValue >> 1) ^ stdPoly;
                } else {
                    initialValue = initialValue >> 1;
                }
            }
        }
        bytes[1] = (byte) ((initialValue & 0xff00) >> 8);
        bytes[0] = (byte) (initialValue & 0xff);
        return bytes;
    }
}
