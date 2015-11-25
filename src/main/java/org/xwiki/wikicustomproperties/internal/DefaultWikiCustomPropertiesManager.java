package org.xwiki.wikicustomproperties.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.wiki.descriptor.WikiDescriptor;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;
import org.xwiki.wiki.manager.WikiManagerException;
import org.xwiki.wiki.properties.WikiPropertyGroup;
import org.xwiki.wiki.properties.WikiPropertyGroupException;
import org.xwiki.wiki.properties.WikiPropertyGroupProvider;
import org.xwiki.wikicustomproperties.WikiCustomPropertiesManager;
import org.xwiki.wikicustomproperties.WikiCustomPropertiesManagerException;

/**
 * Default implementation for {@link WikiCustomPropertiesManager}.
 *
 * @version $Id$
 */
@Component
@Singleton
public class DefaultWikiCustomPropertiesManager implements WikiCustomPropertiesManager
{
    @Inject
    private WikiDescriptorManager wikiDescriptorManager;

    @Inject
    @Named(ExtendedWikiPropertyGroupProvider.GROUP_NAME)
    private WikiPropertyGroupProvider extendedWikiPropertyGroupProvider;

    @Override
    public Object getProperty(String wikiId, String propertyName) throws WikiCustomPropertiesManagerException
    {
        return getPropertyGroup(wikiId).get(propertyName);
    }

    @Override
    public void setProperty(String wikiId, String propertyName, Object value)
        throws WikiCustomPropertiesManagerException
    {
        getPropertyGroup(wikiId).set(propertyName, value);
    }

    @Override
    public void saveProperties(String wikiId) throws WikiCustomPropertiesManagerException
    {
        try {
            extendedWikiPropertyGroupProvider.save(getPropertyGroup(wikiId), wikiId);
        } catch (WikiPropertyGroupException e) {
            throw new WikiCustomPropertiesManagerException(String.format("Failed to save the property group [%s]",
                ExtendedWikiPropertyGroupProvider.GROUP_NAME), e);
        }
    }

    private WikiPropertyGroup getPropertyGroup(String wikiId) throws WikiCustomPropertiesManagerException
    {
        return getDescriptor(wikiId).getPropertyGroup(ExtendedWikiPropertyGroupProvider.GROUP_NAME);
    }

    private WikiDescriptor getDescriptor(String wikiId) throws WikiCustomPropertiesManagerException
    {
        try {
            return wikiDescriptorManager.getById(wikiId);
        } catch (WikiManagerException e) {
            throw new WikiCustomPropertiesManagerException(String.format("Failed to get the descriptor for [%s].",
                wikiId), e);
        }
    }
}
