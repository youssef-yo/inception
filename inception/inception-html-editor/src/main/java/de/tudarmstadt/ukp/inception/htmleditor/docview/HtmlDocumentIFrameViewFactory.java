/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.htmleditor.docview;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.rendering.DocumentViewFactory;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.inception.htmleditor.config.HtmlAnnotationEditorSupportAutoConfiguration;
import de.tudarmstadt.ukp.inception.io.html.HtmlFormatSupport;

/**
 * <p>
 * This class is exposed as a Spring Component via
 * {@link HtmlAnnotationEditorSupportAutoConfiguration#htmlDocumentIFrameViewFactory}.
 * </p>
 */
public class HtmlDocumentIFrameViewFactory
    implements DocumentViewFactory
{
    public static final String ID = "iframe:cas+html";

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public boolean accepts(AnnotationDocument aContext)
    {
        return HtmlFormatSupport.ID.equals(aContext.getDocument().getFormat());
    }

    @Override
    public Component createView(String aId, IModel<AnnotationDocument> aDoc)
    {
        return new HtmlDocumentIFrameView(aId, aDoc);
    }
}
