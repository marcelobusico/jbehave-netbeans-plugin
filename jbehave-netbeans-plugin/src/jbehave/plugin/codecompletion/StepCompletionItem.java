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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import jbehave.plugin.setpsdictionary.JBehaveStep;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

public class StepCompletionItem implements CompletionItem
{
    private static ImageIcon fieldIcon =
            new ImageIcon(ImageUtilities.loadImage("jbehave/plugin/jbehave-logo.png"));
    private static Color fieldColor = Color.decode("0x0000B2");
    private final JBehaveStep currentJBehaveStep;
    private final JBehaveStep suggestedJBehaveStep;
    private final String operatorText;
    private final int currentLinePosition;
    public StepCompletionItem(String operatorText, int currentLinePosition)
    {
        this.currentJBehaveStep = null;
        this.suggestedJBehaveStep = null;
        this.operatorText = operatorText;
        this.currentLinePosition = currentLinePosition;
    }

    public StepCompletionItem(JBehaveStep currentJBehaveStep, JBehaveStep suggestedJBehaveStep)
    {
        this.currentJBehaveStep = currentJBehaveStep;
        this.suggestedJBehaveStep = suggestedJBehaveStep;
        this.operatorText = null;
        this.currentLinePosition = -1;
    }

    @Override
    public void defaultAction(JTextComponent editor)
    {
        try
        {
            StyledDocument doc = (StyledDocument) editor.getDocument();

            String stepTextToInsert = "";
            if (suggestedJBehaveStep == null)
            {
                stepTextToInsert += operatorText.substring(currentLinePosition);
            }
            else
            {
                int charactersAlreadyWritten = currentJBehaveStep.getOperation().length();
                if (!currentJBehaveStep.hasOperatorTrailingSpace())
                {
                    stepTextToInsert += " ";
                }
                stepTextToInsert += suggestedJBehaveStep.getOperation().substring(charactersAlreadyWritten);
            }

            doc.insertString(editor.getCaretPosition(), stepTextToInsert, null);

            Completion.get().hideAll();
        }
        catch (Exception ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent ke)
    {
    }

    @Override
    public int getPreferredWidth(Graphics graphics, Font font)
    {
        String textToRender;
        if (suggestedJBehaveStep == null)
        {
            textToRender = operatorText;
        }
        else
        {
            textToRender = suggestedJBehaveStep.getOperation();
        }
        return CompletionUtilities.getPreferredWidth(textToRender, null, graphics, font);
    }

    @Override
    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected)
    {
        String textToRender;
        if (suggestedJBehaveStep == null)
        {
            textToRender = operatorText;
        }
        else
        {
            textToRender = suggestedJBehaveStep.getOperation();
        }
        CompletionUtilities.renderHtml(fieldIcon, textToRender, null, g, defaultFont,
                (selected ? Color.white : fieldColor), width, height, selected);
    }

    @Override
    public CompletionTask createDocumentationTask()
    {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask()
    {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent jtc)
    {
        return false;
    }

    @Override
    public int getSortPriority()
    {
        return 0;
    }

    @Override
    public CharSequence getSortText()
    {
        if (suggestedJBehaveStep == null)
        {
            return operatorText;
        }
        else
        {
            return suggestedJBehaveStep.getOperation();
        }
    }

    @Override
    public CharSequence getInsertPrefix()
    {
        if (suggestedJBehaveStep == null)
        {
            return operatorText;
        }
        else
        {
            return suggestedJBehaveStep.getOperation();
        }
    }

}
