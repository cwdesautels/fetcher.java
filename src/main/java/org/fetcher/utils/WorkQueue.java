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

public final class WorkQueue {
    private Node _head;
    private Node _tail;
    private final QueueWorker [] _worker;

    public WorkQueue(final int num) {
        _head = _tail = null;

        _worker = new QueueWorker [num];
        for (int i = 0; i < num; i++) {
            _worker[i] = new QueueWorker();
            _worker[i].setPriority(Thread.MAX_PRIORITY);
            _worker[i].start();
        }
    }
    private boolean isEmpty() {
        return _head == null;
    }
    public synchronized void enQueue(final Runnable data) {
        if (data instanceof Runnable) {
            final Node nn = new Node(data);
            if (isEmpty()) {
                _head = _tail = nn;
            }
            else {
                _tail._next = nn;
                _tail = nn;
            }
            notifyAll();
        }
    }
    private synchronized Runnable deQueue() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        final Runnable data = _head._data;
        if (_head._next instanceof Node) {
            _head = _head._next;
        }
        else {
            _head = _tail = null;
        }
        return data;
    }

    private final class Node {
        Runnable _data;
        Node _next;

        public Node(final Runnable data) {
            _data = data;
            _next = null;
        }
    }

    private final class QueueWorker extends Thread {
        public void run() {
            Runnable work = null;
            while (true) {
                try {
                    work = deQueue();
                    work.run();
                }
                catch (final InterruptedException ignored) {}
            }
        }
    }
}
