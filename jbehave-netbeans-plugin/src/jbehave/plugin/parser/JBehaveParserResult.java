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

import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;

public class JBehaveParserResult extends Parser.Result
{
    private final List<JBehaveSyntaxHint> syntaxErrors;
    private final List<JBehaveSyntaxHint> syntaxWarnings;
    JBehaveParserResult(Snapshot snapshot)
    {
        super(snapshot);
        syntaxErrors = new LinkedList<JBehaveSyntaxHint>();
        syntaxWarnings = new LinkedList<JBehaveSyntaxHint>();
    }

    @Override
    protected void invalidate()
    {
    }

    public List<JBehaveSyntaxHint> getSyntaxErrors()
    {
        return syntaxErrors;
    }

    public List<JBehaveSyntaxHint> getSyntaxWarnings()
    {
        return syntaxWarnings;
    }

}
