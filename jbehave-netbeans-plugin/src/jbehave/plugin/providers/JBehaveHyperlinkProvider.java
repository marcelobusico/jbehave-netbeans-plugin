/**
 * Copyright 2013 Marcelo Busico <marcelobusico@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jbehave.plugin.providers;

import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import jbehave.plugin.search.JBehaveStepSearcher;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.openide.text.NbDocument;

@MimeRegistration(mimeType = "text/jbehave-story", service = HyperlinkProvider.class)
public class JBehaveHyperlinkProvider implements HyperlinkProvider
{
    private JBehaveStepSearcher jBehaveStepSearcher;
    public JBehaveHyperlinkProvider()
    {
        jBehaveStepSearcher = new JBehaveStepSearcher();
    }

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset)
    {
        StyledDocument document = (StyledDocument) doc;

        return jBehaveStepSearcher.isCurrentLineAStep(document, offset);
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset)
    {
        StyledDocument document = (StyledDocument) doc;

        int currentLine = NbDocument.findLineNumber(document, offset);

        int lineStartOffset = NbDocument.findLineOffset(document, currentLine);
        int lineEndOffset;
        try
        {
            lineEndOffset = NbDocument.findLineOffset(document, currentLine + 1) - 1;
        }
        catch (Exception ex)
        {
            lineEndOffset = document.getLength();
        }

        return new int[]
        {
            lineStartOffset, lineEndOffset
        };
    }

    @Override
    public void performClickAction(Document doc, int offset)
    {
        StyledDocument document = (StyledDocument) doc;
        jBehaveStepSearcher.navigateToStep(document, offset);
    }

}
