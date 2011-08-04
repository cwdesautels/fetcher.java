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
package main.java.org.fetcher;

import main.java.org.fetcher.crypto.CryptoEngine;
import main.java.org.fetcher.crypto.iCrypto;
import main.java.org.fetcher.json.JsonFactory;
import main.java.org.fetcher.json.iJson;
import main.java.org.fetcher.json.containers.BookmarkObject;
import main.java.org.fetcher.json.containers.BrowserObject;
import main.java.org.fetcher.json.containers.HistoryObject;
import main.java.org.fetcher.json.containers.TabObject;
import main.java.org.fetcher.network.Smuggler;
import main.java.org.fetcher.network.iSmuggler;
import main.java.org.fetcher.utils.Subscriber;
import main.java.org.fetcher.utils.WorkQueue;

/** See: {@link main.java.org.fetcher.iFetcher} */
public final class Fetcher {
    // Me
    private static Fetcher _instance = new Fetcher();
    private iFetcher _delegate;
    // My Thread(s)
    private final WorkQueue _queue;

    private Fetcher() {
        setService(new SyncFetcher());
        _delegate.setCryptoEngine(new CryptoEngine());
        _delegate.setJsonFactory(new JsonFactory());
        _delegate.setNetworkConnector(new Smuggler());
        _queue = new WorkQueue(3);
    }
    /** @return instance of this object */
    public static synchronized Fetcher getInstance() {
        return _instance;
    }
    /**
     * Sets the delegate for this object
     * 
     * @param fetcher sets the fetcher delegate
     * @return instance of this object
     */
    public Fetcher setService(final iFetcher fetcher) {
        if (fetcher instanceof iFetcher) {
            _delegate = fetcher;
        }
        return _instance;
    }
    /**
     * Allows use of a background work queue, code is expected to be thread safe<br>
     * 
     * @param data runnable object to be run
     * @return instance of this object
     */
    public Fetcher invokeLater(final Runnable data) {
        if (data instanceof Runnable) {
            _queue.enQueue(data);
        }
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.utils.iSubscribable}<br>
     * 
     * @return instance of this object
     */
    public Fetcher addSubscriber(final Subscriber listener) {
        _delegate.addSubscriber(listener);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.utils.iSubscribable}<br>
     * 
     * @return instance of this object
     */
    public Fetcher removeSubscriber(final Subscriber listener) {
        _delegate.removeSubscriber(listener);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher pull() {
        _delegate.pull();
        return _instance;
    }
    public Fetcher clearState() {
        _delegate.clearState();
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setJsonFactory(final iJson factory) {
        _delegate.setJsonFactory(factory);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setCryptoEngine(final iCrypto crypto) {
        _delegate.setCryptoEngine(crypto);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setNetworkConnector(final iSmuggler smuggler) {
        _delegate.setNetworkConnector(smuggler);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setLogin(final String accountName, final String passPhrase, final String syncKey) {
        _delegate.setLogin(accountName, passPhrase, syncKey);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setCustomWeaveServer(final String weaveUrl) {
        _delegate.setCustomWeaveServer(weaveUrl);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setDefaultWeaveServer() {
        _delegate.setDefaultWeaveServer();
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setRetryAttempts(final int n) {
        _delegate.setRetryAttempts(n);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public Fetcher setResyncInterval(final int minutes) {
        _delegate.setResyncInterval(minutes);
        return _instance;
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public BookmarkObject [] getBookmarks() {
        return _delegate.getBookmarks();
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public TabObject [] getTabs() {
        return _delegate.getTabs();
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public HistoryObject [] getHistory() {
        return _delegate.getHistory();
    }
    public String getWeaveServer() {
        return _delegate.getWeaveServer();
    }
    public int getRetryAttempts() {
        return _delegate.getRetryAttempts();
    }
    public int getResyncInterval() {
        return _delegate.getResyncInterval();
    }
    /**
     * See: {@link main.java.org.fetcher.iFetcher}<br>
     * 
     * @return instance of this object
     */
    public BrowserObject [] search(final String data) {
        return _delegate.search(data);
    }
}
