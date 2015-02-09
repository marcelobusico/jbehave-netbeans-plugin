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
package jbehave.plugin.codecompletion;

import jbehave.plugin.setpsdictionary.StepsDictionary;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import jbehave.plugin.setpsdictionary.JBehaveStep;
import jbehave.plugin.search.JBehaveStepSearcher;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.text.NbDocument;

@MimeRegistration(mimeType = "text/jbehave-story", service = CompletionProvider.class)
public class JBehaveStepCompletion implements CompletionProvider
{
    private JBehaveStepSearcher jBehaveStepSearcher = new JBehaveStepSearcher();
    private StepsDictionary stepsDictionary = StepsDictionary.getInstance();
    @Override
    public CompletionTask createTask(int queryType, final JTextComponent jtc)
    {
        return new AsyncCompletionTask(new AsyncCompletionQuery()
        {
            @Override
            protected void query(CompletionResultSet completionResultSet, Document document, int caretOffset)
            {
                JTextComponent editor = EditorRegistry.lastFocusedComponent();
                StyledDocument styledDocument = (StyledDocument) editor.getDocument();

                JBehaveStep currentJBehaveStep = jBehaveStepSearcher.getCurrentJBehaveStep(
                        styledDocument, editor.getCaretPosition());

                if (currentJBehaveStep == null)
                {
                    int currentLine = NbDocument.findLineNumber(styledDocument, editor.getCaretPosition());
                    String currentStepText = jBehaveStepSearcher.getStepText(styledDocument, currentLine);
                    
                    if ("Given".startsWith(currentStepText))
                    {
                        completionResultSet.addItem(
                                new StepCompletionItem("Given ", currentStepText.length()));
                    }
                    if ("When".startsWith(currentStepText))
                    {
                        completionResultSet.addItem(
                                new StepCompletionItem("When ", currentStepText.length()));
                    }
                    if ("Then".startsWith(currentStepText))
                    {
                        completionResultSet.addItem(
                                new StepCompletionItem("Then ", currentStepText.length()));
                    }
                    if ("And".startsWith(currentStepText))
                    {
                        completionResultSet.addItem(
                                new StepCompletionItem("And ", currentStepText.length()));
                    }
                }
                else
                {
                    completionResultSet.addAllItems(stepsDictionary.getMatchingSuggestedSteps(currentJBehaveStep));
                }

                completionResultSet.finish();
            }

        });
    }

    @Override
    public int getAutoQueryTypes(JTextComponent jtc, String typedText)
    {
        return 0;
    }

}
