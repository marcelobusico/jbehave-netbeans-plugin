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
package jbehave.plugin.search;

import jbehave.plugin.setpsdictionary.JBehaveStep;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import jbehave.plugin.setpsdictionary.StepsDictionary;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

public class JBehaveStepSearcher
{
    public JBehaveStepSearcher()
    {
    }

    public boolean isCurrentLineAStep(StyledDocument document, int offset)
    {
        int currentLine = NbDocument.findLineNumber(document, offset);
        String currentStepTextTrimmed = getStepText(document, currentLine).trim();

        return isCurrentLineAStep(currentStepTextTrimmed);
    }

    public boolean isCurrentLineAStep(String stepLine)
    {
        if (stepLine.isEmpty())
        {
            return false;
        }
        if (stepLine.startsWith("Given "))
        {
            return true;
        }
        else if (stepLine.startsWith("When "))
        {
            return true;
        }
        else if (stepLine.startsWith("Then "))
        {
            return true;
        }
        else
        {
            return stepLine.startsWith("And ");
        }
    }

    public JBehaveStep getCurrentJBehaveStep(StyledDocument document, int offset)
    {
        int currentLine = NbDocument.findLineNumber(document, offset);

        return getCurrentJBehaveStepInLine(document, currentLine);
    }

    public JBehaveStep getCurrentJBehaveStepInLine(StyledDocument document, int currentLine)
    {
        String currentStepText = getStepText(document, currentLine);
        String currentStepTextTrimmed = currentStepText.trim();

        if (currentStepTextTrimmed.isEmpty())
        {
            return null;
        }

        String operator;
        String operation = "";
        boolean hasOperatorTrailingSpace = true;
        if (currentStepText.startsWith("Given"))
        {
            operator = "@Given";
            if (currentStepTextTrimmed.length() > 6)
            {
                operation = currentStepTextTrimmed.substring("Given ".length());
            }
            if (currentStepText.length() == 5)
            {
                hasOperatorTrailingSpace = false;
            }
        }
        else if (currentStepText.startsWith("When"))
        {
            operator = "@When";
            if (currentStepTextTrimmed.length() > 5)
            {
                operation = currentStepTextTrimmed.substring("When ".length());
            }
            if (currentStepText.length() == 4)
            {
                hasOperatorTrailingSpace = false;
            }
        }
        else if (currentStepText.startsWith("Then"))
        {
            operator = "@Then";
            if (currentStepTextTrimmed.length() > 5)
            {
                operation = currentStepTextTrimmed.substring("Then ".length());
            }
            if (currentStepText.length() == 4)
            {
                hasOperatorTrailingSpace = false;
            }
        }
        else if (currentStepText.startsWith("And"))
        {
            operator = getOperatorFromLine(document, currentLine);
            if (currentStepTextTrimmed.length() > 4)
            {
                operation = currentStepTextTrimmed.substring("And ".length());
            }
            if (currentStepText.length() == 3)
            {
                hasOperatorTrailingSpace = false;
            }
        }
        else
        {
            return null;
        }

        if (operator == null || operator.isEmpty())
        {
            return null;
        }

        return new JBehaveStep(operator, operation, hasOperatorTrailingSpace);
    }

    public void navigateToStep(StyledDocument document, int offset)
    {
        JBehaveStep currentJBehaveStepInStory
                = getCurrentJBehaveStep(document, offset);

        if (currentJBehaveStepInStory != null)
        {
            findStep(currentJBehaveStepInStory, true);
        }
    }

    public String getStepText(StyledDocument document, int lineNumber)
    {
        try
        {
            int lineStartOffset = NbDocument.findLineOffset(document, lineNumber);
            int lineEndOffset;
            try
            {
                lineEndOffset = NbDocument.findLineOffset(document, lineNumber + 1) - 1;
            }
            catch (Exception ex)
            {
                lineEndOffset = document.getLength();
            }
            String currentStepText = document.getText(lineStartOffset,
                    lineEndOffset - lineStartOffset);

            log(currentStepText);

            return currentStepText;
        }
        catch (BadLocationException ex)
        {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    private String getOperatorFromLine(StyledDocument document, int currentLine)
    {
        try
        {
            int currentLineStartOffset = NbDocument.findLineOffset(document, currentLine);
            int currentLineEndOffset;
            try
            {
                currentLineEndOffset = NbDocument.findLineOffset(document, currentLine + 1) - 1;
            }
            catch (Exception ex)
            {
                currentLineEndOffset = document.getLength();
            }

            String currentStepText = document.getText(currentLineStartOffset,
                    currentLineEndOffset - currentLineStartOffset).trim();

            String operator = "";

            if (currentStepText.startsWith("And")
                    || currentStepText.isEmpty()
                    || currentStepText.startsWith("!--")
                    || currentStepText.startsWith("|"))
            {
                if (currentLine > 0)
                {
                    operator = getOperatorFromLine(document, currentLine - 1);
                }
            }
            else
            {
                if (currentStepText.startsWith("Given "))
                {
                    operator = "@Given";
                }
                else if (currentStepText.startsWith("When "))
                {
                    operator = "@When";
                }
                else if (currentStepText.startsWith("Then "))
                {
                    operator = "@Then";
                }
            }

            return operator;
        }
        catch (BadLocationException ex)
        {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    private void findStep(JBehaveStep jbehaveStepInStory, boolean askForReloadIfNotFound)
    {
        int matchesCount = findStepInDictionary(jbehaveStepInStory);

        if (matchesCount == 0)
        {
            if (askForReloadIfNotFound)
            {
                String message
                        = "There is no step implementation matching with the declared step.\n\n"
                        + "Would you like to reaload the JBehave Steps Dictionary and try looking again?";

                int res = JOptionPane.showConfirmDialog(
                        null, message, "JBehave Navigator", JOptionPane.YES_NO_OPTION);

                if (res == JOptionPane.YES_OPTION)
                {
                    StepsDictionary.getInstance().rebuildDictionary();
                    findStep(jbehaveStepInStory, false);
                }
            }
            else
            {
                String message
                        = "There is no step implementation matching with the declared step.";

                JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (matchesCount > 1)
        {
            String message
                    = "There are " + matchesCount
                    + " steps implementation matching with the declared step.";
            JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private int findStepInDictionary(JBehaveStep jbehaveStepInStory)
    {
        StepsDictionary dictionary = StepsDictionary.getInstance();

        List<JBehaveStep> matchingSteps
                = dictionary.getMatchingJBehaveSteps(jbehaveStepInStory);

        for (JBehaveStep jBehaveStep : matchingSteps)
        {
            openFileInEditor(jBehaveStep.getFileObject(), jBehaveStep.getLineNumber());
        }

        return matchingSteps.size();
    }

    private void openFileInEditor(FileObject fileObject, int lineNumber)
    {
        DataObject dataObject = null;
        try
        {
            dataObject = DataObject.find(fileObject);
        }
        catch (DataObjectNotFoundException ex)
        {
            Exceptions.printStackTrace(ex);
        }

        if (dataObject != null)
        {
            LineCookie lc = dataObject.getLookup().lookup(LineCookie.class);
            if (lc == null)
            {
                return;
            }
            Line l = lc.getLineSet().getOriginal(lineNumber);
            l.show(Line.ShowOpenType.OPEN, Line.ShowVisibilityType.FOCUS);
        }
    }

    private void log(String text)
    {
        //LOG is disabled.
//        System.out.println(text);
    }

}
