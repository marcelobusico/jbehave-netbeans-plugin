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
package jbehave.plugin.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import jbehave.plugin.search.JBehaveStepSearcher;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.cookies.EditorCookie;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Source",
        id = "jbehave.plugin.actions.GoToJBehaveTestStep")
@ActionRegistration(
        iconBase = "jbehave/plugin/jbehave-logo.png",
        displayName = "#CTL_GoToJBehaveTestStep")
@ActionReferences(
        {
    @ActionReference(path = "Menu/GoTo", position = 407),
    @ActionReference(path = "Editors/text/jbehave-story/Popup", position = 1125, separatorAfter = 1175),
    @ActionReference(path = "Shortcuts", name = "DS-G")
})
@Messages("CTL_GoToJBehaveTestStep=Go To JBehave Test Step...")
public final class GoToJBehaveTestStep implements ActionListener
{
    private final EditorCookie context;
    private JBehaveStepSearcher jBehaveStepSearcher;
    public GoToJBehaveTestStep(EditorCookie context)
    {
        this.context = context;
        jBehaveStepSearcher = new JBehaveStepSearcher();
    }

    @Override
    public void actionPerformed(ActionEvent ev)
    {
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        StyledDocument document = (StyledDocument) editor.getDocument();

        jBehaveStepSearcher.navigateToStep(document, editor.getCaretPosition());
    }

}
