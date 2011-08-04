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

import main.java.org.fetcher.crypto.iCrypto;
import main.java.org.fetcher.json.iJson;
import main.java.org.fetcher.json.containers.BookmarkObject;
import main.java.org.fetcher.json.containers.BrowserObject;
import main.java.org.fetcher.json.containers.HistoryObject;
import main.java.org.fetcher.json.containers.TabObject;
import main.java.org.fetcher.network.iSmuggler;
import main.java.org.fetcher.utils.iSubscribable;

/**
 * Formal communication interface of a fetcher module.
 * This class contains the generic methods for communicating
 * between browser synchronization services. Fetcher expects
 * its modules to be set and implemented.
 * <p>
 * See: <br>
 * {@link main.java.org.fetcher.crypto.iCrypto} <br>
 * {@link main.java.org.fetcher.json.iJson} <br>
 * {@link main.java.org.fetcher.network.iSmuggler} <br>
 * {@link main.java.org.berrysync.data.storage.iStorage}
 */
public interface iFetcher extends iSubscribable {
    public static final long _fiveMinutes = 300000;
    // Sync Server
    public static String _syncPrefix = "https://auth.services.mozilla.com/user/1.0/";
    public static String _syncSuffix = "/node/weave";
    // Weave Server
    public static String _weaveVersion = "1.1/";
    public static String _weaveSuffixInfo = "/info/collections";
    public static String _weaveSuffixCrypto = "/storage/crypto/keys";
    public static String _weaveSuffixHistory = "/storage/history?full=1&sort=newest&limit=1000";
    public static String _weaveSuffixBookmarks = "/storage/bookmarks?full=1&sort=index";
    public static String _weaveSuffixTabs = "/storage/tabs?full=1&sort=index";
    // Not Used
    public static String _weaveSuffixMeta = "/storage/meta/global";
    public static String _weaveSuffixPrefs = "/storage/prefs?full=1";
    public static String _weaveSuffixPasswords = "/storage/passwords?full=1";
    // Defaults
    public static final int _defaultRetryAttempts = 3; // Connection attempt count
    public static final int _defaultResyncInterval = 30;// Minutes between resyncing
    public static String CharEncoding = "UTF-8";

    /**
     * Pulls data from the defined server.
     * Uses: <li>{@link main.java.org.fetcher.network.iSmuggler} <li>{@link main.java.org.fetcher.json.iJson} <li>{@link main.java.org.fetcher.crypto.iCrypto} <br>
     * <br>
     * Notifies subcribers with status codes, See:<br>
     * <br> <li>{@link main.java.org.fetcher.utils.Subscriber}
     */
    public void pull();
    /** Reverts User settings to default */
    public void clearState();
    /**
     * Sets Users authentication and login information <br>
     * <br>
     * Notifies subcribers with status codes, See:<br>
     * <br>
     * <li>{@link main.java.org.fetcher.utils.Subscriber}<br>
     * <br>
     * 
     * @param accountName sync account name
     * @param passPhrase sync account password
     * @param syncKey sync account key
     */
    public void setLogin(String accountName, String passPhrase, String syncKey);
    /**
     * Sets a custom synchronization server url <br>
     * <br>
     * Notifies subcribers with status codes, See:<br>
     * <br>
     * <li>{@link main.java.org.fetcher.utils.Subscriber}<br>
     * <br>
     * 
     * @param weaveUrl custom sync server url
     */
    public void setCustomWeaveServer(String weaveUrl);
    /** Reverts to the default synchronization server url */
    public void setDefaultWeaveServer();
    public String getWeaveServer();
    /**
     * Sets the number of times a network connect will be attempted before stopping
     * 
     * @param num number of time a connection will attempted
     */
    public void setRetryAttempts(int num);
    public int getRetryAttempts();
    /**
     * Sets the delay in minutes from succesful login to the next automatic {@link pull()}
     * 
     * @param minutes time between resynchronization
     */
    public void setResyncInterval(int minutes);
    public int getResyncInterval();
    /**
     * Sets the json object factory module.
     * See: <li>{@link main.java.org.fetcher.json.iJson}<br>
     * <br>
     * 
     * @param factory json module
     */
    public void setJsonFactory(iJson factory);
    /**
     * Sets the encryption engine module.
     * See: <li>{@link main.java.org.fetcher.crypto.iCrypto}<br>
     * <br>
     * 
     * @param crypto encryption module
     */
    public void setCryptoEngine(iCrypto crypto);
    /**
     * Sets the network connection engine.
     * See: <li>{@link main.java.org.fetcher.network.iSmuggler}<br>
     * <br>
     * 
     * @param smuggler network connection module
     */
    public void setNetworkConnector(iSmuggler smuggler);
    // Public Getters
    /**
     * See: {@link main.java.org.fetcher.json.containers.BookmarkObject}
     * 
     * @return list of browser bookmarks, can be an empty list
     */
    public BookmarkObject [] getBookmarks();
    /**
     * See: {@link main.java.org.fetcher.json.containers.TabObject}
     * 
     * @return list of browser tabs, can be an empty list
     */
    public TabObject [] getTabs();
    /**
     * See: {@link main.java.org.fetcher.json.containers.HistoryObject}
     * 
     * @return list of browser history, can be an empty list
     */
    public HistoryObject [] getHistory();
    /**
     * Gets the list of search results, where param is matched against these parameters: <li>url <li>type <li>title<br>
     * <br>
     * See: {@link main.java.org.fetcher.json.containers.BrowserObject}<br>
     * <br>
     * 
     * @param serach paramter to match all browser objects against
     * @return list of matching browser objects, can be an empty list
     */
    public BrowserObject [] search(String search);
}
