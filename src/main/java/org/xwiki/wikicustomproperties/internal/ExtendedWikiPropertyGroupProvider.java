package org.xwiki.wikicustomproperties.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.wiki.internal.descriptor.document.WikiDescriptorDocumentHelper;
import org.xwiki.wiki.manager.WikiManagerException;
import org.xwiki.wiki.properties.WikiPropertyGroup;
import org.xwiki.wiki.properties.WikiPropertyGroupException;
import org.xwiki.wiki.properties.WikiPropertyGroupProvider;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.PropertyClass;

/**
 * Implementation of {@link WikiPropertyGroupProvider} for the properties stored in XWikiExtendedServerClass object.
 *
 * @version $Id$
 */
@Component
@Named(ExtendedWikiPropertyGroupProvider.GROUP_NAME)
@Singleton
public class ExtendedWikiPropertyGroupProvider implements WikiPropertyGroupProvider
{
    /**
     * Name of the property group.
     */
    public static final String GROUP_NAME = "extended";

    private static final String ERROR_MESSAGE_NO_DESCRIPTOR_DOCUMENT = "Unable to load descriptor "
        + "document for wiki [%s].";

    @Inject
    private Provider<XWikiContext> xcontextProvider;

    @Inject
    private WikiDescriptorDocumentHelper wikiDescriptorDocumentHelper;

    @Override
    public WikiPropertyGroup get(String wikiId) throws WikiPropertyGroupException
    {
        WikiPropertyGroup group = new WikiPropertyGroup(GROUP_NAME);

        try {
            XWikiDocument descriptorDocument = wikiDescriptorDocumentHelper.getDocumentFromWikiId(wikiId);
            // Get the object
            BaseObject object = descriptorDocument.getXObject(ExtendedServerClassDocumentInitializer.SERVER_CLASS);
            if (object != null) {
                for (Object property : object.getFieldList()) {
                    if (property instanceof BaseProperty) {
                        BaseProperty<?> prop = ((BaseProperty<?>) property);
                        group.set(prop.getName(), prop.getValue());
                    }
                }
            }
        } catch (WikiManagerException e) {
            throw new WikiPropertyGroupException(String.format(ERROR_MESSAGE_NO_DESCRIPTOR_DOCUMENT, wikiId), e);
        }

        return group;
    }

    @Override
    public void save(WikiPropertyGroup group, String wikiId) throws WikiPropertyGroupException
    {
        XWikiContext context = xcontextProvider.get();
        XWiki xwiki = context.getWiki();

        try {
            XWikiDocument descriptorDocument = wikiDescriptorDocumentHelper.getDocumentFromWikiId(wikiId);
            BaseObject object = descriptorDocument.getXObject(ExtendedServerClassDocumentInitializer.SERVER_CLASS,
                true, context);
            BaseClass xclass = object.getXClass(context);
            boolean dirty = false;
            for (Object property : xclass.getFieldList()) {
                PropertyClass pclass = (PropertyClass) property;
                Object value = group.get(pclass.getName());
                if (value != null) {
                    object.set(pclass.getName(), value, context);
                    dirty = true;
                }
            }

            if (dirty) {
                // The document must have a creator
                if (descriptorDocument.getCreatorReference() == null) {
                    descriptorDocument.setCreatorReference(context.getUserReference());
                }
                // The document must have an author
                if (descriptorDocument.getAuthorReference() == null) {
                    descriptorDocument.setAuthorReference(context.getUserReference());
                }
                xwiki.saveDocument(descriptorDocument, String.format("Changed property group [%s].", GROUP_NAME),
                    context);
            }
        } catch (WikiManagerException e) {
            throw new WikiPropertyGroupException(String.format(ERROR_MESSAGE_NO_DESCRIPTOR_DOCUMENT, wikiId), e);
        } catch (XWikiException e) {
            throw new WikiPropertyGroupException("Unable to save descriptor document.", e);
        }
    }

}
