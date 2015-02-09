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

public class JBehaveSyntaxHint
{
    private final int startPosition;
    private final int endPosition;
    private final String message;
    public JBehaveSyntaxHint(int startPosition, int endPosition, String message)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.message = message;
    }

    public int getStartPosition()
    {
        return startPosition;
    }

    public int getEndPosition()
    {
        return endPosition;
    }

    public String getMessage()
    {
        return message;
    }

}
