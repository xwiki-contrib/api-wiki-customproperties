package org.xwiki.wikicustomproperties;

/**
 * Exception related to Wiki Custom Properties Manager.
 *
 * @version $Id$
 */
public class WikiCustomPropertiesManagerException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Construct an exception with the specified detail message.
     *
     * @param message The detailed message. This can later be retrieved by the Throwable.getMessage() method.
     */
    public WikiCustomPropertiesManagerException(String message)
    {
        super(message);
    }

    /**
     * Construct an exception with the specified detail message and cause.
     *
     * @param message The detailed message. This can later be retrieved by the Throwable.getMessage() method.
     * @param throwable the cause. This can be retrieved later by the Throwable.getCause() method. (A null value
     *        is permitted, and indicates that the cause is nonexistent or unknown)
     */
    public WikiCustomPropertiesManagerException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
