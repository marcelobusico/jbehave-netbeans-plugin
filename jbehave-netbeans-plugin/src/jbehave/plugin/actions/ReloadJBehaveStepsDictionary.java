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
import javax.swing.JOptionPane;
import jbehave.plugin.setpsdictionary.StepsDictionary;
import org.openide.cookies.EditorCookie;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Source",
        id = "jbehave.plugin.actions.ReloadJBehaveStepsDictionary")
@ActionRegistration(
        displayName = "#CTL_ReloadJBehaveStepsDictionary")
@ActionReferences(
        {
    @ActionReference(path = "Menu/GoTo", position = 436),
    @ActionReference(path = "Editors/text/jbehave-story/Popup", position = 1150)
})
@Messages("CTL_ReloadJBehaveStepsDictionary=Reload JBehave Steps Dictionary...")
public final class ReloadJBehaveStepsDictionary implements ActionListener
{
    private final EditorCookie context;
    public ReloadJBehaveStepsDictionary(EditorCookie context)
    {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev)
    {
        StepsDictionary.getInstance().rebuildDictionary();
        JOptionPane.showMessageDialog(null,
                "JBehave steps dictionary has been reloaded using current open projects.",
                "JBehave Navigator", JOptionPane.INFORMATION_MESSAGE);
    }

}
