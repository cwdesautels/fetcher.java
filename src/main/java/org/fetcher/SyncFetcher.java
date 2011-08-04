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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import main.java.org.fetcher.crypto.CryptoException;
import main.java.org.fetcher.crypto.iCrypto;
import main.java.org.fetcher.json.JsonException;
import main.java.org.fetcher.json.iJson;
import main.java.org.fetcher.json.containers.BookmarkObject;
import main.java.org.fetcher.json.containers.BrowserObject;
import main.java.org.fetcher.json.containers.CryptoObject;
import main.java.org.fetcher.json.containers.HistoryObject;
import main.java.org.fetcher.json.containers.TabObject;
import main.java.org.fetcher.json.containers.WeaveObject;
import main.java.org.fetcher.network.ConnectionTimeoutException;
import main.java.org.fetcher.network.LoginFailedException;
import main.java.org.fetcher.network.NoConnectionException;
import main.java.org.fetcher.network.iSmuggler;
import main.java.org.fetcher.utils.Subscribable;


/**
 * Fetcher generates usable data for the application
 * 
 * @author Carl
 * 
 */
public final class SyncFetcher extends Subscribable implements iFetcher {
    // Modules
    private iCrypto _crypto;
    private iJson _json;
    private iSmuggler _smuggler;
    private final Conductor _conductor;
    // Relevant Info
    private String _syncUsername;
    private String _syncLogin;
    private byte [] _syncKey;
    private int _retryAttempts;
    private int _reSyncInterval;
    // Weave Server
    private String _customPrefix;
    // Encryption Keys
    private byte [] _cryptoKey;
    // Data
    private Vector _tabs;
    private Vector _history;
    private Vector _bookmarks;
    // My States
    private volatile boolean _loggedIn;
    private volatile boolean _customSyncServer;

    public SyncFetcher() {
        _smuggler = null;
        _crypto = null;
        _json = null;
        _conductor = new Conductor();

        clearState();
    }
    // ***********
    // Public Methods
    // *******************
    // ***********
    // Public Getters Methods
    // *******************
    public void clearState() {
        _loggedIn = _customSyncServer = false;
        _syncUsername = _syncLogin = _customPrefix = null;
        _syncKey = _cryptoKey = null;
        _tabs = new Vector();
        _history = new Vector();
        _bookmarks = new Vector();

        setDefaultWeaveServer();
        setResyncInterval(_defaultResyncInterval);
        setRetryAttempts(_defaultRetryAttempts);
    }
    public String getWeaveServer() {
        return _customPrefix;
    }
    public int getRetryAttempts() {
        return _retryAttempts;
    }
    public int getResyncInterval() {
        return _reSyncInterval;
    }
    public BookmarkObject [] getBookmarks() {
        synchronized (_bookmarks) {
            final BookmarkObject [] arr = new BookmarkObject [_bookmarks.size()];
            _bookmarks.copyInto(arr);
            return arr;
        }
    }
    public TabObject [] getTabs() {
        synchronized (_tabs) {
            final TabObject [] arr = new TabObject [_tabs.size()];
            _tabs.copyInto(arr);
            return arr;
        }
    }
    public HistoryObject [] getHistory() {
        synchronized (_history) {
            final HistoryObject [] arr = new HistoryObject [_history.size()];
            _history.copyInto(arr);
            return arr;
        }
    }
    public BrowserObject [] search(final String data) {
        final Vector results = new Vector();
        synchronized (_bookmarks) {
            contains(_bookmarks, data, results);
        }
        synchronized (_tabs) {
            contains(_tabs, data, results);
        }
        synchronized (_history) {
            contains(_history, data, results);
        }
        final BrowserObject [] arr = new BrowserObject [results.size()];
        results.copyInto(arr);
        return arr;
    }
    public void setDefaultWeaveServer() {
        _customPrefix = null;
        _customSyncServer = false;
    }
    public void setCustomWeaveServer(final String weaveUrl) {
        if (weaveUrl instanceof String && weaveUrl.startsWith("http")) {
            _customPrefix = weaveUrl.endsWith("/") ? weaveUrl : weaveUrl + '/';
            _customSyncServer = true;
        }
        else {
            changeNotify(Error_Invalid_Url);
        }
    }
    public void setRetryAttempts(final int n) {
        _retryAttempts = n < 0 ? -n : n;
    }
    public void setResyncInterval(final int m) {
        _reSyncInterval = m < 0 ? -m : m;
    }
    public void setJsonFactory(final iJson factory) {
        _json = factory;
    }
    public void setCryptoEngine(final iCrypto crypto) {
        _crypto = crypto;
    }
    public void setNetworkConnector(final iSmuggler smuggler) {
        _smuggler = smuggler;
    }
    public void setLogin(final String accountName, final String passPhrase, final String syncKey) {
        if (modulesSet()) {
            try {
                _syncKey = _crypto.syncKeyDecode(syncKey);
                _syncUsername = _crypto.syncUsernameEncode(accountName);
                _syncLogin = buildSyncLogin(_syncUsername, passPhrase);
                _loggedIn = true;
            }
            catch (final Exception e) {
                changeNotify(Error_Invalid_Credentials);
            }
        }
    }
    public void pull() {
        if (canRun() && modulesSet()) {
            _conductor.startSync();
        }
    }
    // ***********
    // Private Methods
    // *******************
    private boolean matches(final BrowserObject obj, final String data) {
        boolean rc = false;
        if (obj._title instanceof String) {
            rc = obj._title.indexOf(data) > -1;
        }
        if (!rc && obj._type instanceof String) {
            rc = obj._type.indexOf(data) > -1;
        }
        if (!rc && obj._url instanceof String) {
            rc = obj._url.indexOf(data) > -1;
        }
        return rc;
    }
    private void contains(final Vector v, final String data, final Vector dst) {
        for (int i = 0, len = v.size(); i < len; i++) {
            final BrowserObject obj = (BrowserObject) v.elementAt(i);
            if (matches(obj, data)) {
                dst.addElement(obj);
            }
        }
    }
    private boolean canRun() {
        if (_loggedIn) {
            return true;
        }
        else {
            changeNotify(Error_Login_Required);
            return false;
        }
    }
    private boolean modulesSet() {
        if (_smuggler instanceof iSmuggler && _crypto instanceof iCrypto && _json instanceof iJson) {
            return true;
        }
        else {
            changeNotify(Error_Modules_Not_Set);
            return false;
        }
    }
    private void buildSyncHistory(final String json) throws JsonException, CryptoException, IOException {
        final Vector rawHistoryList = _json.createWeaveObjectList(json);
        final Vector tempList = new Vector();
        for (int i = 0, len = rawHistoryList.size(); i < len; i++) {
            final WeaveObject wObj = (WeaveObject) rawHistoryList.elementAt(i);
            final HistoryObject bObj = _json.createHistory(parseWeave(wObj));
            if (bObj instanceof HistoryObject) {
                tempList.addElement(bObj);
            }
        }
        if (tempList.size() > 0) {
            synchronized (_history) {
                _history = tempList;
            }
        }
    }
    private void buildSyncBookmarks(final String json) throws JsonException, CryptoException, IOException {
        final Vector rawBookmarkList = _json.createWeaveObjectList(json);
        final Vector tempList = new Vector();
        for (int i = 0, len = rawBookmarkList.size(); i < len; i++) {
            final WeaveObject wObj = (WeaveObject) rawBookmarkList.elementAt(i);
            final BookmarkObject bObj = _json.createBookmark(parseWeave(wObj));
            if (bObj instanceof BookmarkObject) {
                tempList.addElement(bObj);
            }
        }
        if (tempList.size() > 0) {
            synchronized (_bookmarks) {
                _bookmarks = tempList;
            }
        }
    }
    private void buildSyncTabs(final String json) throws JsonException, CryptoException, IOException {
        final Vector rawTabList = _json.createWeaveObjectList(json);
        final Vector tempList = new Vector();
        for (int i = 0, len = rawTabList.size(); i < len; i++) {
            final WeaveObject wObj = (WeaveObject) rawTabList.elementAt(i);
            final Vector bObj = _json.createTabList(parseWeave(wObj));
            if (bObj instanceof Vector) {
                for (int j = 0, size = bObj.size(); j < size; j++) {
                    tempList.addElement(bObj.elementAt(j));
                }
            }
        }
        if (tempList.size() > 0) {
            synchronized (_tabs) {
                _tabs = tempList;
            }
        }
    }
    private void buildSyncKeys(final String json) throws JsonException, IOException, CryptoException {
        final WeaveObject keys = _json.createWeaveObject(json);

        final byte [] encryptionKey = _crypto.syncEncryptionKey(_syncKey, _syncUsername);
        final byte [] cipher = _crypto.base64Decode(keys._cipher);
        final byte [] encryptionIv = _crypto.base64Decode(keys._iv);

        final byte [] payload = _crypto.AESDecrypt(cipher, encryptionKey, encryptionIv);
        final CryptoObject key = _json.createWeaveKey(new String(payload, CharEncoding));

        _cryptoKey = _crypto.base64Decode(key._key);
    }
    private String parseWeave(final WeaveObject obj) throws CryptoException, IOException {
        final byte [] iv = _crypto.base64Decode(obj._iv);
        final byte [] cipher = _crypto.base64Decode(obj._cipher);
        final byte [] payload = _crypto.AESDecrypt(cipher, _cryptoKey, iv);
        return new String(payload, CharEncoding);
    }
    private String buildSyncLogin(final String username, final String password) throws IOException {
        final String syncLogin = new String(username + ':' + password);
        return _crypto.base64Encode(syncLogin.getBytes(CharEncoding));
    }

    private final class Conductor {
        private volatile boolean _reSyncing;
        private volatile boolean _running;
        private volatile boolean _runningInfo;
        private volatile boolean _runningTabs;
        private volatile boolean _runningBookmarks;
        private volatile boolean _runningHistory;
        private String _weavePrefix;
        private Timer _stopWatch;

        public Conductor() {
            _reSyncing = _running = _runningInfo = _runningTabs = _runningBookmarks = _runningHistory = false;
            _weavePrefix = null;
            _stopWatch = null;
        }
        public void startSync() {
            if (!_running) {
                _running = true;

                if (_stopWatch instanceof Timer) {
                    _stopWatch.cancel();
                }
                _stopWatch = new Timer();

                try {
                    if (_customSyncServer) {
                        setWeaveServer(_customPrefix);
                    }
                    else {
                        setWeaveServer(getWeaveNode());
                    }
                    getCryptoKeys();
                }
                catch (final LoginFailedException e) {
                    errorNotify(Error_Incorrect_Username_Or_Password, e);
                    _loggedIn = false;
                }
                catch (final ConnectionTimeoutException e) {
                    errorNotify(Error_Connection_Timeout, e);
                    reStart();
                }
                catch (final NoConnectionException e) {
                    errorNotify(Error_Connection_Failed, e);
                    reStart();
                }
                catch (final Exception e) {
                    errorNotify(Unspecified_Error, e);
                }
            }
            else {
                changeNotify(Error_Already_Fetching);
            }
        }
        private void getBookmarksTask() {
            _runningBookmarks = _running = true;
            Fetcher.getInstance().invokeLater(new Runnable() {
                public void run() {
                    getBookmarks();
                    _runningBookmarks = false;
                    imDone();
                }
            });
        }
        private void getHistoryTask() {
            _runningHistory = _running = true;
            Fetcher.getInstance().invokeLater(new Runnable() {
                public void run() {
                    getHistory();
                    _runningHistory = false;
                    imDone();
                }
            });
        }
        private void getTabsTask() {
            _runningTabs = _running = true;
            Fetcher.getInstance().invokeLater(new Runnable() {
                public void run() {
                    getTabs();
                    _runningTabs = false;
                    imDone();
                }
            });
        }
        private void reStart() {
            _stopWatch.schedule(new TimerTask() {
                public void run() {
                    _running = false;
                    startSync();
                }
            }, _fiveMinutes);
        }
        private void retryBookmarks() {
            _stopWatch.schedule(new TimerTask() {
                public void run() {
                    getBookmarksTask();
                }
            }, _fiveMinutes);
        }
        private void retryHistory() {
            _stopWatch.schedule(new TimerTask() {
                public void run() {
                    getHistoryTask();
                }
            }, _fiveMinutes);
        }
        private void retryTabs() {
            _stopWatch.schedule(new TimerTask() {
                public void run() {
                    getTabsTask();
                }
            }, _fiveMinutes);
        }
        private void errorNotify(final byte context, final Exception e) {
            System.out.println(e);
            _running = false;
            changeNotify(context);
        }
        private void imDone() {
            if (!_runningInfo && !_runningTabs && !_runningBookmarks && !_runningHistory) {
                _running = false;
            }
        }
        private void setWeaveServer(final String weaveUrl) {
            _weavePrefix = weaveUrl + _weaveVersion + _syncUsername;
        }
        private void startResyncTimer(final long delay) {
            if (!_reSyncing) {
                _reSyncing = true;
                _stopWatch.schedule(new TimerTask() {
                    public void run() {
                        _reSyncing = false;
                        pull();
                    }
                }, delay);
            }
        }
        private String getWeaveNode() throws NoConnectionException, IOException, LoginFailedException, ConnectionTimeoutException {
            return _smuggler.attemptConnection(_syncPrefix + _syncUsername + _syncSuffix, null, _retryAttempts);
        }
        private void getCryptoKeys() throws IOException, NoConnectionException, LoginFailedException, ConnectionTimeoutException {
            try {
                buildSyncKeys(_smuggler.attemptConnection(_weavePrefix + _weaveSuffixCrypto, _syncLogin, _retryAttempts));
                changeNotify(Event_Login_Successful);
                startResyncTimer((long) 60000 * (long) _reSyncInterval);
                getInfo();
            }
            catch (final CryptoException e) {
                errorNotify(Error_Incorrect_SyncKey, e);
                _loggedIn = false;
            }
            catch (final JsonException e) {
                errorNotify(Error_Bad_Crypto_Data, e);
                _loggedIn = false;
            }
        }
        private void getInfo() throws NoConnectionException, IOException, LoginFailedException, ConnectionTimeoutException {
            _runningInfo = true;
            getCollections(_smuggler.attemptConnection(_weavePrefix + _weaveSuffixInfo, _syncLogin, _retryAttempts));
            _runningInfo = false;
            imDone();
        }
        private void getCollections(final String collections) {
            if (collections.indexOf("tabs") > 0) {
                getTabsTask();
            }
            else {
                changeNotify(Event_No_Tabs);
            }
            if (collections.indexOf("bookmarks") > 0) {
                getBookmarksTask();
            }
            else {
                changeNotify(Event_No_Bookmarks);
            }
            if (collections.indexOf("history") > 0) {
                getHistoryTask();
            }
            else {
                changeNotify(Event_No_History);
            }
        }
        private void getHistory() {
            try {
                buildSyncHistory(_smuggler.attemptConnection(_weavePrefix + _weaveSuffixHistory, _syncLogin, _retryAttempts));
                changeNotify(Event_Browser_History);
            }
            catch (final NoConnectionException e) {
                errorNotify(Error_Connection_Failed, e);
                retryHistory();
            }
            catch (final ConnectionTimeoutException e) {
                errorNotify(Error_Connection_Timeout, e);
                retryHistory();
            }
            catch (final LoginFailedException e) {
                errorNotify(Error_Incorrect_Username_Or_Password, e);
            }
            catch (final Exception e) {
                errorNotify(Error_Bad_History_Data, e);
            }
        }
        private void getTabs() {
            try {
                buildSyncTabs(_smuggler.attemptConnection(_weavePrefix + _weaveSuffixTabs, _syncLogin, _retryAttempts));
                changeNotify(Event_Browser_Tabs);
            }
            catch (final NoConnectionException e) {
                errorNotify(Error_Connection_Failed, e);
                retryTabs();
            }
            catch (final ConnectionTimeoutException e) {
                errorNotify(Error_Connection_Timeout, e);
                retryTabs();
            }
            catch (final LoginFailedException e) {
                errorNotify(Error_Incorrect_Username_Or_Password, e);
            }
            catch (final Exception e) {
                errorNotify(Error_Bad_Tab_Data, e);
            }
        }
        private void getBookmarks() {
            try {
                buildSyncBookmarks(_smuggler.attemptConnection(_weavePrefix + _weaveSuffixBookmarks, _syncLogin, _retryAttempts));
                changeNotify(Event_Browser_Bookmarks);
            }
            catch (final NoConnectionException e) {
                errorNotify(Error_Connection_Failed, e);
                retryBookmarks();
            }
            catch (final ConnectionTimeoutException e) {
                errorNotify(Error_Connection_Timeout, e);
                retryBookmarks();
            }
            catch (final LoginFailedException e) {
                errorNotify(Error_Incorrect_Username_Or_Password, e);
            }
            catch (final Exception e) {
                errorNotify(Error_Bad_Bookmark_Data, e);
            }
        }
    }
}
