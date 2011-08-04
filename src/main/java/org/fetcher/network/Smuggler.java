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
package main.java.org.fetcher.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;

import main.java.org.fetcher.iFetcher;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.system.RadioInfo;

/**
 * Smuggler asynchronously performs HTTP requests to the sync server
 * 
 * @author Carl
 * 
 */
public final class Smuggler implements iSmuggler {
    /** Ordered list of connection protocols */
    private final int [] _preferredTypes = {
        TransportInfo.TRANSPORT_TCP_WIFI, TransportInfo.TRANSPORT_TCP_CELLULAR, TransportInfo.TRANSPORT_WAP, TransportInfo.TRANSPORT_WAP2, TransportInfo.TRANSPORT_MDS, TransportInfo.TRANSPORT_BIS_B
    };
    private final ConnectionFactory _factory;

    public Smuggler() {
        _factory = new ConnectionFactory();
        _factory.setPreferredTransportTypes(_preferredTypes);
        _factory.setConnectionTimeout(1000 * 30);
        _factory.setTimeoutSupported(true);
    }
    // ***********
    // Public Methods
    // *******************
    public String attemptConnection(final String url, final String login, final int attempts) throws IOException, NoConnectionException, LoginFailedException, ConnectionTimeoutException {
        String response = null;
        Connection connection = null;
        for (int i = 0; response == null && i <= attempts; i++) {
            System.out.println("Attempting..." + url);
            try {
                if (hasConnectivity()) {
                    final ConnectionDescriptor cd = _factory.getConnection(url);
                    if (cd != null) {
                        connection = cd.getConnection();
                        if (login != null) {
                            response = httpGetRequestAuth(connection, login);
                        }
                        else {
                            response = httpGetRequest(connection);
                        }
                    }
                }
                Thread.sleep(1000);
            }
            catch (final InterruptedIOException e) {
                throw new ConnectionTimeoutException("Timed Out");
            }
            catch (final InterruptedException ignored) {}
        }
        if (response == null) {
            throw new NoConnectionException("Could not establish a connection");
        }
        else {
            return response;
        }
    }
    // ***********
    // Private Methods
    // *******************
    private boolean hasConnectivity() {
        return RadioInfo.getSignalLevel() != RadioInfo.LEVEL_NO_COVERAGE;
    }
    private String httpGetRequest(final Connection connection) throws IOException, LoginFailedException, NoConnectionException {
        return httpGetRequestResponse(httpGetSetup(connection));
    }
    private String httpGetRequestAuth(final Connection connection, final String login) throws IOException, LoginFailedException, NoConnectionException {
        final HttpConnection httpconn = httpGetSetup(connection);
        httpconn.setRequestProperty("Authorization", "Basic " + login);
        return httpGetRequestResponse(httpconn);
    }
    private HttpConnection httpGetSetup(final Connection connection) throws IOException {
        final HttpConnection httpconn = (HttpConnection) connection;
        httpconn.setRequestMethod(HttpConnection.GET);
        return httpconn;
    }
    private String httpGetRequestResponse(final HttpConnection httpconn) throws IOException, LoginFailedException, NoConnectionException {
        InputStream is = null;
        try {
            final int code = httpconn.getResponseCode();
            if (code == 200) {
                is = httpconn.openInputStream();
                final byte [] data = IOUtilities.streamToBytes(is);
                return new String(data, iFetcher.CharEncoding);
            }
            else if (code == 401) {
                throw new LoginFailedException(httpconn.getResponseMessage());
            }
            else {
                throw new NoConnectionException(httpconn.getResponseMessage());
            }
        }
        finally {
            if (is != null) {
                is.close();
            }
            httpconn.close();
        }
    }
}
