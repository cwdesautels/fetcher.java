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

public class BookmarkObject extends BrowserObject implements Persistable {
    // Bookmarks
    public String _parent; // The Id of a _type folder in the bookmark collection
    public String [] _children;

    public BookmarkObject() {
        super();
        _parent = null;
        _children = null;
    }
    public String toJson() {
        final StringBuffer sb = new StringBuffer();
        sb.append('{');
        sb.append(super.toJson());
        sb.append(",\"parent\":");
        sb.append(_parent instanceof String ? '"' + _parent + '"' : "null");
        sb.append(",\"children\":[");
        if (_children != null) {
            for (int i = 0, len = _children.length; i < len; i++) {
                sb.append('"' + _children[i] + '"');
                if (i + 1 < len) {
                    sb.append(',');
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
