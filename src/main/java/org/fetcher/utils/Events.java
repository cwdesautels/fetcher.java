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
 * The event class contains the event context constants that {@link main.java.org.fetcher.utils.Subscribable} and {@link main.java.org.fetcher.utils.Subscriber} use
 * 
 * @author Carl
 * 
 */
public interface Events {
    public final static byte Event_Debug = 0x00;
    // Success Code
    public final static byte Event_Login_Successful = 0x10;
    // Have Browser Objects
    public final static byte Event_No_Tabs = 0x20;
    public final static byte Event_No_History = 0x21;
    public final static byte Event_No_Bookmarks = 0x22;
    public final static byte Event_Browser_Tabs = 0x23;
    public final static byte Event_Browser_History = 0x24;
    public final static byte Event_Browser_Bookmarks = 0x25;
    // Generic Errors
    public final static byte Error_Login_Required = 0x30;
    public final static byte Error_Invalid_Credentials = 0x31;
    public final static byte Error_Already_Fetching = 0x32;
    public final static byte Error_Modules_Not_Set = 0x33;
    public final static byte Unspecified_Error = 0x34;
    public final static byte Error_Save_Failed = 0x35;
    public final static byte Error_Load_Failed = 0x36;
    // Network Errors
    public final static byte Error_Invalid_Url = 0x40;
    public final static byte Error_Connection_Timeout = 0x41;
    public final static byte Error_Connection_Failed = 0x42;
    public final static byte Error_Incorrect_Username_Or_Password = 0x43;
    public final static byte Error_Incorrect_SyncKey = 0x44;
    // Data Errors
    public final static byte Error_Bad_Crypto_Data = 0x50;
    public final static byte Error_Bad_Tab_Data = 0x51;
    public final static byte Error_Bad_Bookmark_Data = 0x52;
    public final static byte Error_Bad_History_Data = 0x53;
}
