package org.xwiki.wikicustomproperties;

import org.xwiki.component.annotation.Role;

/**
 * Manage custom properties in the extended wiki descriptor property group.
 *
 * @version $Id$
 */
@Role
public interface WikiCustomPropertiesManager
{
    /**
     * Get the value of a property from the descriptor cache.
     *
     * @param wikiId Identifier of the wiki
     * @param propertyName Name of the property
     * @return value of the property
     * @throws WikiCustomPropertiesManagerException on error
     */
    Object getProperty(String wikiId, String propertyName) throws WikiCustomPropertiesManagerException;

    /**
     * Set the value of a property in the descriptor cache.
     *
     * @param wikiId Identifier of the wiki
     * @param propertyName Name of the property
     * @param value Value of the property
     * @throws WikiCustomPropertiesManagerException on error
     */
    void setProperty(String wikiId, String propertyName, Object value) throws WikiCustomPropertiesManagerException;

    /**
     * Persist the values currently stored in the descriptor cache in the database.
     * Only properties defined in the XWiki.XWikiExtendedServerClass will be persisted.
     *
     * @param wikiId Identifier of the wiki
     * @throws WikiCustomPropertiesManagerException on error
     */
    void saveProperties(String wikiId) throws WikiCustomPropertiesManagerException;
}
