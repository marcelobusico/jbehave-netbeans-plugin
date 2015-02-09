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

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.util.Exceptions;

public class JBehaveHintProcessor
{
    private static final String HINTS_LAYER = "JBehave-Hints";
    public void processDocument(Document document, JBehaveParserResult jBehaveParserResult)
    {
        try
        {
            List<ErrorDescription> errors = new ArrayList<ErrorDescription>();

            for (JBehaveSyntaxHint jBehaveSyntaxHint : jBehaveParserResult.getSyntaxErrors())
            {
                ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
                        Severity.ERROR,
                        jBehaveSyntaxHint.getMessage(),
                        document,
                        document.createPosition(jBehaveSyntaxHint.getStartPosition()),
                        document.createPosition(jBehaveSyntaxHint.getEndPosition()));

                errors.add(errorDescription);
            }

            for (JBehaveSyntaxHint jBehaveSyntaxHint : jBehaveParserResult.getSyntaxWarnings())
            {
                ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
                        Severity.WARNING,
                        jBehaveSyntaxHint.getMessage(),
                        document,
                        document.createPosition(jBehaveSyntaxHint.getStartPosition()),
                        document.createPosition(jBehaveSyntaxHint.getEndPosition()));

                errors.add(errorDescription);
            }

            HintsController.setErrors(document, HINTS_LAYER, errors);
        }
        catch (BadLocationException ex)
        {
            Exceptions.printStackTrace(ex);
        }
    }

}
