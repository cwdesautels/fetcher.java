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

/**
 * Interface that defines the implementation of the Observer design pattern
 * 
 * @author Carl
 * 
 */
public interface iSubscribable extends Events {
    /**
     * Add a {@link main.java.org.fetcher.utils.Subscriber} to this object
     * 
     * @param subscriber Subscriber to be added
     */
    public void addSubscriber(Subscriber listener);
    /**
     * Remove a {@link main.java.org.fetcher.utils.Subscriber} from this object
     * 
     * @param subscriber Subscriber to be removed
     */
    public void removeSubscriber(Subscriber listener);
    /**
     * Notifies each of this objects {@link main.java.org.fetcher.utils.Subscriber}'s by calling their onChange method
     * 
     * @param context Event context
     */
    public void changeNotify(byte context);
}
