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
package main.java.org.fetcher.json.containers;

import net.rim.device.api.util.Persistable;

public class WeaveObject extends SyncObject implements Persistable {
    public String _id;
    public String _cipher;
    public String _iv;
    public String _hmac;

    public WeaveObject() {
        _id = _cipher = _iv = _hmac = null;
    }
    public String toJson() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{\"id\":\"");
        sb.append(_id);
        sb.append("\",\"cipher\":\"");
        sb.append(_cipher);
        sb.append("\",\"iv\":\"");
        sb.append(_iv);
        sb.append("\",\"hmac\":\"");
        sb.append(_hmac);
        sb.append("\"}");
        return sb.toString();
    }
}
