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
package jbehave.plugin.setpsdictionary;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import jbehave.plugin.codecompletion.StepCompletionItem;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

public class StepsDictionary
{
    private static final StepsDictionary INSTANCE = new StepsDictionary();
    private HashMap<String, Set<JBehaveStep>> stepsByOperator;
    private StepsDictionary()
    {
        stepsByOperator = new HashMap<String, Set<JBehaveStep>>();
        rebuildDictionary();
    }

    public static StepsDictionary getInstance()
    {
        return INSTANCE;
    }

    public final void rebuildDictionary()
    {
        stepsByOperator.clear();
        for (FileObject curRoot : GlobalPathRegistry.getDefault().getSourceRoots())
        {
            scanFileObject(curRoot);
        }
    }

    public List<StepCompletionItem> getMatchingSuggestedSteps(JBehaveStep currentJBehaveStep)
    {
        LinkedList<StepCompletionItem> result = new LinkedList<StepCompletionItem>();

        if (currentJBehaveStep != null)
        {
            Set<JBehaveStep> steps = stepsByOperator.get(currentJBehaveStep.getOperator());

            for (JBehaveStep suggestedJBehaveStep : steps)
            {
                if (suggestedJBehaveStep.getOperation().startsWith(currentJBehaveStep.getOperation()))
                {
                    result.add(new StepCompletionItem(currentJBehaveStep, suggestedJBehaveStep));
                }
            }
        }

        return result;
    }

    public List<JBehaveStep> getMatchingJBehaveSteps(JBehaveStep currentJBehaveStep)
    {
        LinkedList<JBehaveStep> result = new LinkedList<JBehaveStep>();

        if (currentJBehaveStep != null)
        {
            Set<JBehaveStep> stepsInDictionaryForCurrentOperator
                    = stepsByOperator.get(currentJBehaveStep.getOperator());

            for (JBehaveStep stepInDictionary : stepsInDictionaryForCurrentOperator)
            {
                if (stepInDictionary.getOperation().equals(currentJBehaveStep.getOperation()))
                {
                    result.add(stepInDictionary);
                }
                else if (stepInDictionary.getOperation().contains("$"))
                {
                    if (isOperationInTestMatchingWithOperationInLine(
                            stepInDictionary.getOperation(), currentJBehaveStep.getOperation()))
                    {
                        result.add(stepInDictionary);
                    }
                }
            }
        }

        return result;
    }

    private void scanFileObject(FileObject currentFileObject)
    {
        for (FileObject childFileObject : currentFileObject.getChildren())
        {
            scanFileObject(childFileObject);
        }

        scanJavaFile(currentFileObject);
    }

    private void scanJavaFile(FileObject fileObject)
    {
        try
        {
            if (isCandidate(fileObject))
            {
                List<String> fileLines = fileObject.asLines();
                int lineNumber = 0;
                for (String fileLine : fileLines)
                {
                    processJavaFileLine(fileLine, fileObject, lineNumber);
                    lineNumber++;
                }
            }
        }
        catch (Exception ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }

    private boolean isCandidate(FileObject fileObject)
    {
        String filePath = fileObject.getPath();

        boolean hasJavaExtension = filePath.endsWith(".java");
        boolean isSourceFile = filePath.contains("/src/main/java/") || filePath.contains("/src/test/java/");

        return hasJavaExtension && isSourceFile;
    }

    private void processJavaFileLine(String fileLine, FileObject fileObject, int lineNumber)
    {
        try
        {
            String operator;
            if (fileLine.contains("@When"))
            {
                operator = "@When";
            }
            else if (fileLine.contains("@Then"))
            {
                operator = "@Then";
            }
            else if (fileLine.contains("@Given"))
            {
                operator = "@Given";
            }
            else
            {
                return;
            }

            int operationStartIndex = fileLine.indexOf("(") + 1;
            String subOperationString = fileLine.substring(operationStartIndex);

            int operationEndIndex = subOperationString.lastIndexOf(")");

            String operationInStep;
            if (operationEndIndex > 0)
            {
                operationInStep = subOperationString.substring(0, operationEndIndex);
            }
            else
            {
                operationInStep = subOperationString;
            }
            operationInStep = operationInStep.replace("\"", "");

            log("Operation In Line: " + operationInStep);

            if (!stepsByOperator.containsKey(operator))
            {
                stepsByOperator.put(operator, new TreeSet<JBehaveStep>());
            }

            Set<JBehaveStep> steps = stepsByOperator.get(operator);
            steps.add(new JBehaveStep(operator, operationInStep, fileObject, lineNumber));
        }
        catch (Exception ex)
        {
            log("Invalid line, ommiting it.");
        }
    }

    private boolean isOperationInTestMatchingWithOperationInLine(
            String operationInStep, String operationInStory)
    {
        String beginOfOperationInStep = operationInStep.substring(
                0, operationInStep.indexOf("$"));

        String operationInStoryWithTrailingSpace = operationInStory + " ";

        if (!operationInStoryWithTrailingSpace.startsWith(beginOfOperationInStep))
        {
            return false;
        }

        String[] wordsInOperation = operationInStep.split(" ");
        for (String word : wordsInOperation)
        {
            if (!word.startsWith("$"))
            {
                if (!operationInStory.contains(word))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private void log(String text)
    {
        //LOG is disabled.
//        System.out.println(text);
    }

}
