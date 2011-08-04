package main.java.org.fetcher.crypto.Base32;

/**
 * In house Base32 encoding, adapted from: http://bitcollider.cvs.sourceforge.net/bitcollider/jbitcollider/plugins/org.bitpedia.collider.core/src/org/bitpedia/util/Base32.java?view=markup
 * 
 * @author Carl
 * 
 */
public final class Base32 {
    private static String _base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final char _padChar = '=';
    private static final int [] _base32Lookup = {
        0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, // '0', '1', '2', '3', '4', '5', '6', '7'
        0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // '8', '9', ':', ';', '<', '=', '>', '?'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, // 'X', 'Y', 'Z', '[', '\', ']', '^', '_'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, // '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, // 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, // 'p', 'q', 'r', 's', 't', 'u', 'v', 'w'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
    // 'x', 'y', 'z', '{', '|', '}', '~', 'DEL'
    };

    static public String encode(final byte [] bytes) {
        int i = 0, index = 0, digit = 0;
        int currByte, nextByte;
        final StringBuffer base32 = new StringBuffer((bytes.length + 7) * 8 / 5);
        while (i < bytes.length) {
            currByte = bytes[i] >= 0 ? bytes[i] : bytes[i] + 256;
            if (index > 3) {
                if (i + 1 < bytes.length) {
                    nextByte = bytes[i + 1] >= 0 ? bytes[i + 1] : bytes[i + 1] + 256;
                }
                else {
                    nextByte = 0;
                }
                digit = currByte & 0xFF >> index;
                index = (index + 5) % 8;
                digit <<= index;
                digit |= nextByte >> 8 - index;
                i++;
            }
            else {
                digit = currByte >> 8 - (index + 5) & 0x1F;
                index = (index + 5) % 8;
                if (index == 0) {
                    i++;
                }
            }
            base32.append(_base32Chars.charAt(digit));
        }
        if (base32.length() % 8 > 0) {
            int numPads = 8 - base32.length() % 8;
            while (numPads-- > 0) {
                base32.append(_padChar);
            }
        }
        return base32.toString();
    }
    static public byte [] decode(String base32) {
        if (base32.indexOf(_padChar) > 0) {
            base32 = base32.substring(0, base32.indexOf(_padChar));
        }
        int i, index, lookup, offset, digit;
        final byte [] bytes = new byte [base32.length() * 5 / 8];
        for (i = 0, index = 0, offset = 0; i < base32.length(); i++) {
            lookup = base32.charAt(i) - '0';
            /* Skip chars outside the lookup table */
            if (lookup < 0 || lookup >= _base32Lookup.length) {
                continue;
            }
            digit = _base32Lookup[lookup];
            /* If this digit is not in the table, ignore it */
            if (digit == 0xFF) {
                continue;
            }
            if (index <= 3) {
                index = (index + 5) % 8;
                if (index == 0) {
                    bytes[offset] |= digit;
                    offset++;
                    if (offset >= bytes.length) {
                        break;
                    }
                }
                else {
                    bytes[offset] |= digit << 8 - index;
                }
            }
            else {
                index = (index + 5) % 8;
                bytes[offset] |= digit >>> index;
                offset++;
                if (offset >= bytes.length) {
                    break;
                }
                bytes[offset] |= digit << 8 - index;
            }
        }
        return bytes;
    }
}
