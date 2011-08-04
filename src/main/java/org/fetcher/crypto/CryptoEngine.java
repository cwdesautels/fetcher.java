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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import main.java.org.fetcher.iFetcher;
import main.java.org.fetcher.crypto.Base32.Base32;
import net.rim.device.api.crypto.AESCBCDecryptorEngine;
import net.rim.device.api.crypto.AESCBCEncryptorEngine;
import net.rim.device.api.crypto.AESKey;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.PKCS5FormatterEngine;
import net.rim.device.api.crypto.PKCS5UnformatterEngine;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.util.DataBuffer;

public final class CryptoEngine extends CryptoBase {
    // ***********
    // Encoding Methods
    // *******************
    public String base64Encode(final byte [] data) throws IOException {
        return new String(Base64OutputStream.encode(data, 0, data.length, false, false), iFetcher.CharEncoding);
    }
    public byte [] base64Decode(final String data) throws IOException {
        final byte [] temp = data.getBytes(iFetcher.CharEncoding);
        return Base64InputStream.decode(temp, 0, temp.length);
    }
    public String base32Encode(final byte [] data) {
        return Base32.encode(data);
    }
    public byte [] base32Decode(final String data) {
        return Base32.decode(data);
    }
    // ***********
    // Encryption Methods
    // *******************
    public byte [] AESEncrypt(final byte [] plainText, final byte [] keyData, final byte [] ivData) throws CryptoException {
        final AESKey key = new AESKey(keyData);
        final InitializationVector iv = new InitializationVector(ivData);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            final BlockEncryptor encryptor = new BlockEncryptor(new PKCS5FormatterEngine( // Padding last uneven cipher block
                new AESCBCEncryptorEngine(key, iv)), outputStream);// AES encryption with initialization vector
            encryptor.write(plainText);
            encryptor.close();
            outputStream.close();
            return outputStream.toByteArray();
        }
        catch (final Exception e) {
            throw new CryptoException(e);
        }
    }
    public byte [] AESDecrypt(final byte [] cipherText, final byte [] keyData, final byte [] ivData) throws CryptoException {
        final AESKey key = new AESKey(keyData);
        final InitializationVector iv = new InitializationVector(ivData);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(cipherText);
        BlockDecryptor decryptor = null;
        try {
            decryptor = new BlockDecryptor(new PKCS5UnformatterEngine( // Unpadding last uneven cipher block
                new AESCBCDecryptorEngine(key, iv)), inputStream); // AES decryption with initialization vector
            final byte [] temp = new byte [1];
            final DataBuffer db = new DataBuffer();
            while (true) {
                final int bytesRead = decryptor.read(temp);
                if (bytesRead > 0) {
                    db.write(temp, 0, bytesRead);
                }
                else {
                    break;
                }
            }
            return db.toArray();
        }
        catch (final Exception e) {
            throw new CryptoException(e);
        }
    }
    // ***********
    // Hashing Methods
    // *******************
    public byte [] HMAC_SHA256(final byte [] keyData, final byte [] data) throws CryptoException {
        final HMACKey key = new HMACKey(keyData);
        final SHA256Digest digest = new SHA256Digest();
        try {
            final HMAC hmac = new HMAC(key, digest);
            hmac.update(data);
            return hmac.getMAC();
        }
        catch (final Exception e) {
            throw new CryptoException(e);
        }
    }
    public byte [] SHA1Digest(final byte [] data) {
        final SHA1Digest digest = new SHA1Digest();
        digest.update(data);
        return digest.getDigest();
    }
}
