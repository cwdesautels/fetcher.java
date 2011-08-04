/**
 * Copyright (C) 2011 by Carlin Desautels <carl.desautels@yahoo.com>

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package main.java.org.fetcher.crypto;

import java.io.IOException;

import main.java.org.fetcher.iFetcher;


public abstract class CryptoBase implements iCrypto {
    // ***********
    // Sync Specific Methods
    // *******************
    public byte [] syncEncryptionKey(final byte [] keyData, final String username) throws CryptoException, IOException {
        return HMAC_SHA256(keyData, (_hmacInput + username + _cryptoSuffix).getBytes(iFetcher.CharEncoding));
    }
    public byte [] syncHMACKey(final byte [] keyData, final byte [] encryptionKeyData, final String username) throws CryptoException, IOException {
        return HMAC_SHA256(keyData, (encryptionKeyData + _hmacInput + username + _hmacSuffix).getBytes(iFetcher.CharEncoding));
    }
    public byte [] syncKeyDecode(final String data) {
        final StringBuffer sb = new StringBuffer();
        char c;
        for (int i = 0; i < data.length(); i++) {
            c = data.charAt(i);
            if (c != '-') {
                c = Character.toLowerCase(c);
                if (c == '8') {
                    sb.append('l');
                }
                else if (c == '9') {
                    sb.append('o');
                }
                else {
                    sb.append(c);
                }
            }
        }
        return base32Decode(sb.toString());
    }
    public String syncKeyEncode(final byte [] data) {
        final StringBuffer sb = new StringBuffer();
        final String temp = base32Encode(data);
        char c;
        for (int i = 0, len = temp.length(); i < len; i++) {
            c = temp.charAt(i);
            c = Character.toLowerCase(c);
            if (c == 'l') {
                sb.append('8');
            }
            else if (c == 'o') {
                sb.append('9');
            }
            else if (c != '=') {
                sb.append(c);
            }
            if (i == 0 || i == 5 || i == 10 || i == 15 || i == 20) {
                sb.append('-');
            }
        }
        return sb.toString();
    }
    public String syncUsernameEncode(String username) throws IOException {
        username = username.toLowerCase();
        final byte [] data = SHA1Digest(username.getBytes(iFetcher.CharEncoding));
        final String str = base32Encode(data);
        return str.toLowerCase();
    }
}
