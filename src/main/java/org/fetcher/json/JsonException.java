package main.java.org.fetcher.json;

/**
 * The JSONException is thrown by the JSON.org classes then things are amiss.
 * 
 * @author JSON.org
 * @version 2
 */
public final class JsonException extends Exception {
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     * 
     * @param message Detail about the reason for the exception.
     */
    public JsonException(final String message) {
        super(message);
    }
    public JsonException(final Throwable t) {
        super(t.getMessage());
        cause = t;
    }
    public Throwable getCause() {
        return cause;
    }
}
