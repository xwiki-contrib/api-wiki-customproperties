package org.xwiki.wikicustomproperties.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.internal.mandatory.AbstractMandatoryDocumentInitializer;

/**
 * This is only creating a empty XWiki.XWikiExtendedServerClass for holding custom properties.
 * Customizing this class should be done by the consumer of this API.
 *
 * @version $Id$
 */
@Component
@Named("XWiki.XWikiExtendedServerClass")
@Singleton
public class ExtendedServerClassDocumentInitializer extends AbstractMandatoryDocumentInitializer
{
    /**
     * The name of the mandatory document.
     */
    public static final String DOCUMENT_NAME = "XWikiExtendedServerClass";

    /**
     * The space of the mandatory document.
     */
    public static final String DOCUMENT_SPACE = "XWiki";

    /**
     * Reference to the server class.
     */
    public static final EntityReference SERVER_CLASS = new EntityReference(DOCUMENT_NAME, EntityType.DOCUMENT,
        new EntityReference(DOCUMENT_SPACE, EntityType.SPACE));

    /**
     * Extended server class reference.
     */
    private DocumentReference reference;

    @Inject
    private Provider<XWikiContext> xcontextProvider;

    /**
     * Constructor.
     */
    public ExtendedServerClassDocumentInitializer()
    {
        // Since we can`t get the main wiki here, this is just to be able to use the Abstract class.
        // getDocumentReference() returns the actual main wiki document reference.
        super(DOCUMENT_SPACE, DOCUMENT_NAME);
    }

    @Override
    public EntityReference getDocumentReference() {
        if (this.reference == null) {
            synchronized (this) {
                if (this.reference == null) {
                    String mainWikiName = this.xcontextProvider.get().getMainXWiki();
                    this.reference = new DocumentReference(mainWikiName, DOCUMENT_SPACE, DOCUMENT_NAME);
                }
            }
        }

        return this.reference;
    }

    @Override
    public boolean updateDocument(XWikiDocument document)
    {
        boolean needsUpdate = false;

        // Check if the document is hidden
        if (!document.isHidden()) {
            document.setHidden(true);
            needsUpdate = true;
        }

        // Mark this document as Wiki Class.
        if (document.isNew()) {
            needsUpdate |= setClassDocumentFields(document, "Wiki Custom Properties Class");
            document.setContent(document.getContent() + "\n\nClass that should be customized to represent the custom"
                + " properties you when to store in the extended wiki descriptor property group.");
        }

        return needsUpdate;
    }
}
