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

public interface iCrypto {
    public final static String _hmacInput = "Sync-AES_256_CBC-HMAC256";
    public final static char _cryptoSuffix = 0x01;
    public final static char _hmacSuffix = 0x02;

    // ***********
    // Sync Specific Methods
    // *******************
    public byte [] syncEncryptionKey(byte [] keyData, String username) throws CryptoException, IOException;
    public byte [] syncHMACKey(byte [] keyData, byte [] encryptionKeyData, String username) throws CryptoException, IOException;
    public byte [] syncKeyDecode(String data);
    public String syncKeyEncode(byte [] data);
    public String syncUsernameEncode(String username) throws IOException;
    // ***********
    // Encoding Methods
    // *******************
    public String base64Encode(byte [] data) throws IOException;
    public byte [] base64Decode(String data) throws IOException;
    public String base32Encode(byte [] data);
    public byte [] base32Decode(String data);
    // ***********
    // Encryption Methods
    // *******************
    public byte [] AESEncrypt(byte [] plainText, byte [] keyData, byte [] ivData) throws CryptoException;
    public byte [] AESDecrypt(byte [] cipherText, byte [] keyData, byte [] ivData) throws CryptoException;
    // ***********
    // Hashing Methods
    // *******************
    public byte [] HMAC_SHA256(byte [] keyData, byte [] data) throws CryptoException;
    public byte [] SHA1Digest(byte [] data);
}
