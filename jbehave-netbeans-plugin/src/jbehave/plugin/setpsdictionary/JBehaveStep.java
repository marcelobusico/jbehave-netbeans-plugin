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

import org.openide.filesystems.FileObject;

public class JBehaveStep implements Comparable<JBehaveStep>
{
    private final String operator;
    private final String operation;
    private final FileObject fileObject;
    private final int lineNumber;
    private final boolean hasOperatorTrailingSpace;
    public JBehaveStep(String operator, String operation, boolean hasOperatorTrailingSpace)
    {
        this.operator = operator;
        this.operation = operation;
        this.hasOperatorTrailingSpace = hasOperatorTrailingSpace;
        fileObject = null;
        lineNumber = 0;
    }

    public JBehaveStep(String operator, String operation, FileObject fileObject, int lineNumber)
    {
        this.operator = operator;
        this.operation = operation;
        this.hasOperatorTrailingSpace = false;
        this.fileObject = fileObject;
        this.lineNumber = lineNumber;
    }

    public String getOperator()
    {
        return operator;
    }

    public String getOperation()
    {
        return operation;
    }

    public FileObject getFileObject()
    {
        return fileObject;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public boolean hasOperatorTrailingSpace()
    {
        return hasOperatorTrailingSpace;
    }

    @Override
    public int compareTo(JBehaveStep o)
    {
        if (o == null)
        {
            return 1;
        }
        if (o.operation == null)
        {
            return 1;
        }
        if (operation == null)
        {
            return -1;
        }

        if (operation.equals(o.operation))
        {
            if (fileObject.equals(o.fileObject))
            {
                return lineNumber - o.lineNumber;
            }
            else
            {
                return fileObject.getName().compareTo(o.fileObject.getName());
            }
        }

        return operation.compareTo(o.operation);
    }

}
