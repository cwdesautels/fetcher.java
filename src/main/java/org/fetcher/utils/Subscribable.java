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
package main.java.org.fetcher.utils;

import java.util.Vector;

/**
 * Subscribable indicates that the implementing class can be subscribed to
 * 
 * @author Carl
 * 
 */
public abstract class Subscribable implements iSubscribable {
    // Subscribers
    private final Vector _subscribers = new Vector();

    public void addSubscriber(final Subscriber listener) {
        if (!_subscribers.contains(listener)) {
            _subscribers.addElement(listener);
        }
    }
    public void removeSubscriber(final Subscriber listener) {
        if (_subscribers.contains(listener)) {
            _subscribers.removeElement(listener);
        }
    }
    public void changeNotify(final byte context) {
        for (int i = 0, len = _subscribers.size(); i < len; i++) {
            final Subscriber listener = (Subscriber) _subscribers.elementAt(i);
            listener.onChange(context);
        }
        System.out.println("Fetcher Ran - Context: [ 0x" + Integer.toString(context, 16) + " ]");
    }
}
