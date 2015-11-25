package org.xwiki.wikicustomproperties.script;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.script.service.ScriptService;
import org.xwiki.security.authorization.AccessDeniedException;
import org.xwiki.security.authorization.AuthorizationManager;
import org.xwiki.security.authorization.Right;
import org.xwiki.wiki.manager.WikiManagerException;
import org.xwiki.wikicustomproperties.WikiCustomPropertiesManager;

import com.xpn.xwiki.XWikiContext;

/**
 * Script service to manage custom properties in the extended wiki descriptor property group.
 *
 * @version $Id$
 */
@Component
@Named("wiki.customproperties")
@Singleton
public class WikiCustomPropertiesManagerScriptService implements ScriptService
{
    /**
     * The key under which the last encountered error is stored in the current execution context.
     */
    private static final String WIKICUSTOMPROPSERROR_KEY = "scriptservice.wiki.customproperties.error";

    @Inject
    private Execution execution;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private WikiCustomPropertiesManager wikiCustomPropertiesManager;

    @Inject
    private Provider<XWikiContext> xcontextProvider;

    /**
     * Get the value of a given custom property from the cached descriptor of the current wiki.
     *
     * @param propertyName name of the property
     * @return value of the property
     */
    public Object get(String propertyName)
    {
        return get(getCurrentWiki(), propertyName);
    }

    /**
     * Get the value of a given custom property from the cached descriptor of a given wiki.
     *
     * @param wikiId identifier of the wiki
     * @param propertyName name of the property
     * @return value of the property
     */
    public Object get(String wikiId, String propertyName)
    {
        try {
            return wikiCustomPropertiesManager.getProperty(wikiId, propertyName);
        } catch (Exception e) {
            setLastError(e);
        }
        return null;
    }

    /**
     * Set the value of a property in the cached descriptor of the current wiki.
     *
     * @param propertyName name of the property
     * @param value value of the property
     */
    public void set(String propertyName, Object value)
    {
        set(getCurrentWiki(), propertyName, value);
    }

    /**
     * Set the value of a property in the cached descriptor of a given wiki.
     *
     * @param wikiId identifier of the wiki
     * @param propertyName name of the property
     * @param value value of the property
     */
    public void set(String wikiId, String propertyName, Object value)
    {
        try {
            checkAccess(wikiId);
            wikiCustomPropertiesManager.setProperty(wikiId, propertyName, value);
        } catch (Exception e) {
            setLastError(e);
        }
    }

    /**
     * Save into the database the custom property values currently stored in the cached descriptor of the current wiki.
     * Only properties defined in the XWiki.XWikiExtendedServerClass will be persisted.
     * You need admin rights on the current wiki to be able to save the values.
     */
    public void save()
    {
        save(getCurrentWiki());
    }

    /**
     * Save into the database the custom property values currently stored in the cached descriptor of a given wiki.
     * Only properties defined in the XWiki.XWikiExtendedServerClass will be persisted.
     * You need admin rights on the given wiki to be able to save the values.
     *
     * @param wikiId identifier of the wiki
     */
    public void save(String wikiId)
    {
        try {
            checkAccess(wikiId);
            wikiCustomPropertiesManager.saveProperties(wikiId);
        } catch (Exception e) {
            setLastError(e);
        }
    }

    /**
     * Set the value of a property in the cached descriptor of the current wiki and save it into the database.
     * Only properties defined in the XWiki.XWikiExtendedServerClass will be persisted.
     * You need admin rights on the current wiki to be able to save the value.
     *
     * @param propertyName name of the property
     * @param value value to be set for the property
     */
    public void save(String propertyName, Object value)
    {
        set(getCurrentWiki(), propertyName, value);
        save(getCurrentWiki());
    }

    /**
     * Set the value of a property in the cached descriptor of a given wiki and save it into the database.
     * Only properties defined in the XWiki.XWikiExtendedServerClass will be persisted.
     * You need admin rights on the given wiki to be able to save the value.
     *
     * @param wikiId identifier of the wiki
     * @param propertyName name of the property
     * @param value value be set for property

     */
    public void save(String wikiId, String propertyName, Object value)
    {
        set(wikiId, propertyName, value);
        save(wikiId);
    }

    /**
     * Get the error generated while performing the previously called action.
     *
     * @return an eventual exception or {@code null} if no exception was thrown
     */
    public Exception getLastError()
    {
        return (Exception) this.execution.getContext().getProperty(WIKICUSTOMPROPSERROR_KEY);
    }

    /**
     * Store a caught exception in the context, so that it can be later retrieved using {@link #getLastError()}.
     *
     * @param e the exception to store, can be {@code null} to clear the previously stored exception
     * @see #getLastError()
     */
    private void setLastError(Exception e)
    {
        this.execution.getContext().setProperty(WIKICUSTOMPROPSERROR_KEY, e);
    }

    private String getCurrentWiki()
    {
        return xcontextProvider.get().getDatabase();
    }

    private void checkAccess(String wikiId) throws WikiManagerException, AccessDeniedException
    {
        authorizationManager.checkAccess(Right.ADMIN, xcontextProvider.get().getUserReference(),
            new WikiReference(wikiId));
    }
}
