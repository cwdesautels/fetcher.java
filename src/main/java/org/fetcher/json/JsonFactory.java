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
package main.java.org.fetcher.json;

import java.util.Vector;

import main.java.org.fetcher.json.JSONME.JSONArray;
import main.java.org.fetcher.json.JSONME.JSONObject;
import main.java.org.fetcher.json.containers.BookmarkObject;
import main.java.org.fetcher.json.containers.CryptoObject;
import main.java.org.fetcher.json.containers.HistoryObject;
import main.java.org.fetcher.json.containers.TabObject;
import main.java.org.fetcher.json.containers.WeaveObject;


public final class JsonFactory implements iJson {
    public HistoryObject createHistory(final String data) throws JsonException {
        HistoryObject obj = null;
        JSONObject outer = null;
        JSONArray holder = null;
        JSONObject inner = null;

        outer = new JSONObject(data);
        if (outer != null) {
            holder = outer.getJSONArray("visits");
            if (holder != null) {
                inner = holder.getJSONObject(0);
                if (inner != null) {
                    obj = new HistoryObject();
                    obj._id = outer.getString("id");
                    obj._url = outer.getString("histUri");
                    final String title = outer.getString("title");
                    obj._type = "history";
                    obj._lastVisit = Long.parseLong(inner.getString("date"));
                    if (title instanceof String && !title.equals("") && !title.equals("null")) {
                        obj._title = title;
                    }
                    else {
                        obj._title = obj._url;
                    }
                }
            }
        }
        return obj;
    }
    public BookmarkObject createBookmark(final String data) throws JsonException {
        BookmarkObject obj = null;
        JSONObject outer = null;
        JSONArray holder = null;

        outer = new JSONObject(data);
        if (outer != null) {
            final String type = outer.getString("type");
            if (type.equals("bookmark")) {
                obj = new BookmarkObject();
                obj._id = outer.getString("id");
                final String title = outer.getString("title");
                obj._url = outer.getString("bmkUri");
                obj._type = type;
                obj._parent = outer.getString("parentid");
                if (title instanceof String && !title.equals("") && !title.equals("null")) {
                    obj._title = title;
                }
                else {
                    obj._title = obj._url;
                }
            }
            else if (type.equals("folder")) {
                holder = outer.getJSONArray("children");
                if (holder != null) {
                    obj = new BookmarkObject();
                    obj._id = outer.getString("id");
                    obj._title = outer.getString("title");
                    obj._type = type;
                    obj._parent = outer.getString("parentid");
                    final int len = holder.length();
                    obj._children = new String [len];
                    for (int i = 0; i < len; i++) {
                        obj._children[i] = holder.getString(i);
                    }
                }
            }
        }
        return obj;
    }
    public Vector createTabList(final String data) throws JsonException {
        final Vector obj = new Vector();
        TabObject temp = null;
        JSONArray holder = null;
        JSONArray urlHolder = null;
        JSONObject outer = null;
        JSONObject inner = null;
        String id, client;

        outer = new JSONObject(data);
        if (outer != null) {
            id = outer.getString("id");
            client = outer.getString("clientName");
            holder = outer.getJSONArray("tabs");
            if (holder != null) {
                for (int i = 0, len = holder.length(); i < len; i++) {
                    inner = holder.getJSONObject(i);
                    if (inner != null) {
                        urlHolder = inner.getJSONArray("urlHistory");
                        if (urlHolder != null) {
                            temp = new TabObject();
                            temp._id = id;
                            final String title = inner.getString("title");
                            temp._url = urlHolder.getString(0);
                            temp._type = "tab";
                            temp._client = client;
                            temp._icon = inner.getString("icon");
                            temp._lastUsed = Long.parseLong(inner.getString("lastUsed"));
                            if (title instanceof String && !title.equals("") && !title.equals("null")) {
                                temp._title = title;
                            }
                            else {
                                temp._title = temp._url;
                            }
                            obj.addElement(temp);
                        }
                    }
                }
            }
        }
        return obj;
    }
    public Vector createWeaveObjectList(String data) throws JsonException {
        data = jsonWeaveFormat(data);
        final Vector obj = new Vector();
        WeaveObject temp = null;
        JSONArray holder = null;
        JSONObject outer = null;
        JSONObject inner = null;

        holder = new JSONArray(data);
        if (holder != null) {
            for (int i = 0, len = holder.length(); i < len; i++) {
                outer = holder.getJSONObject(i);
                if (outer != null) {
                    inner = outer.getJSONObject("payload");
                    if (inner != null) {
                        temp = new WeaveObject();
                        temp._id = outer.getString("id");
                        temp._cipher = inner.getString("ciphertext");
                        temp._iv = inner.getString("IV");
                        temp._hmac = inner.getString("hmac");
                        obj.addElement(temp);
                    }
                }
            }
        }
        return obj;
    }
    public WeaveObject createWeaveObject(String data) throws JsonException {
        data = jsonWeaveFormat(data);
        WeaveObject obj = null;
        JSONObject outer = null;
        JSONObject inner = null;

        outer = new JSONObject(data);
        if (outer != null) {
            inner = outer.getJSONObject("payload");
            if (inner != null) {
                obj = new WeaveObject();
                obj._id = outer.getString("id");
                obj._cipher = inner.getString("ciphertext");
                obj._iv = inner.getString("IV");
                obj._hmac = inner.getString("hmac");
            }
        }
        return obj;
    }
    public CryptoObject createWeaveKey(final String data) throws JsonException {
        CryptoObject obj = null;
        JSONObject outer = null;
        JSONArray inner = null;

        outer = new JSONObject(data);
        if (outer != null) {
            inner = outer.getJSONArray("default");
            if (inner != null) {
                obj = new CryptoObject();
                obj._id = outer.getString("id");
                obj._key = inner.getString(0);
                obj._hmac = inner.getString(1);
            }
        }
        return obj;
    }
    public String jsonWeaveFormat(final String data) {
        final StringBuffer sb = new StringBuffer();
        sb.append(data);
        for (int i = 0, len = sb.length(); i < len; i++, len = sb.length()) {
            if (sb.charAt(i) == '"') {
                if (i + 1 < len && sb.charAt(i + 1) == '{') {
                    sb.deleteCharAt(i);
                }
                else if (i - 1 > 0 && sb.charAt(i - 1) == '}') {
                    sb.deleteCharAt(i);
                }
            }
            else if (sb.charAt(i) == '\\') {
                if (i + 1 < len && sb.charAt(i + 1) == '"') {
                    sb.deleteCharAt(i);
                }
            }
        }
        return sb.toString();
    }
}
