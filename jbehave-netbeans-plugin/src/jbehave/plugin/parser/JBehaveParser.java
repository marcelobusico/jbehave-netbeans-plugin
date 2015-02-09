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
package jbehave.plugin.parser;

import java.util.List;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyledDocument;
import jbehave.plugin.search.JBehaveStepSearcher;
import jbehave.plugin.setpsdictionary.JBehaveStep;
import jbehave.plugin.setpsdictionary.StepsDictionary;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.text.NbDocument;

public class JBehaveParser extends Parser
{
    private static final String STEP_NOT_FOUND = "Step not found in current open projects.";
    private static final String DUPLICATED_STEP_FOUND = "More than one step was found in current open projects.";
    private JBehaveParserResult result;
    private JBehaveStepSearcher jBehaveStepSearcher;
    private StepsDictionary stepsDictionary;
    public JBehaveParser()
    {
        jBehaveStepSearcher = new JBehaveStepSearcher();
        stepsDictionary = StepsDictionary.getInstance();
    }

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event)
    {
        System.out.println("Parsing JBehave step...");
        String storyText = snapshot.getText().toString();

        StyledDocument document = (StyledDocument) snapshot.getSource().getDocument(false);

        result = new JBehaveParserResult(snapshot);

        String[] storyLines;
        if (storyText.contains(System.getProperty("line.separator")))
        {
            storyLines = storyText.split(System.getProperty("line.separator"));
        }
        else
        {
            storyLines = storyText.split("\n");
        }

        int lineNumber = 0;
        for (String storyLine : storyLines)
        {
            if (jBehaveStepSearcher.isCurrentLineAStep(storyLine))
            {
                JBehaveStep jBehaveStep =
                        jBehaveStepSearcher.getCurrentJBehaveStepInLine(
                        document, lineNumber);

                List<JBehaveStep> matchingJBehaveSteps =
                        stepsDictionary.getMatchingJBehaveSteps(jBehaveStep);

                if (matchingJBehaveSteps.isEmpty())
                {
                    result.getSyntaxErrors().add(createJBehaveSyntaxHint(
                            document, lineNumber, STEP_NOT_FOUND));
                }
                else if (matchingJBehaveSteps.size() > 1)
                {
                    result.getSyntaxWarnings().add(createJBehaveSyntaxHint(
                            document, lineNumber, DUPLICATED_STEP_FOUND));
                }
            }
            lineNumber++;
        }

        System.out.println("Parsing of JBehave step completed.");
    }

    private JBehaveSyntaxHint createJBehaveSyntaxHint(StyledDocument document,
            int lineNumber, String message)
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

        return new JBehaveSyntaxHint(lineStartOffset, lineEndOffset, message);
    }

    @Override
    public Result getResult(Task task)
    {
        return result;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void cancel()
    {
    }

    @Override
    public void addChangeListener(ChangeListener changeListener)
    {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener)
    {
    }

}
