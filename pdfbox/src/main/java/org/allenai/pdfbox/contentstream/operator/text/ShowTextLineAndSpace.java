/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.allenai.pdfbox.contentstream.operator.text;

import java.util.List;

import org.allenai.pdfbox.contentstream.operator.MissingOperandException;
import org.allenai.pdfbox.contentstream.operator.Operator;
import org.allenai.pdfbox.contentstream.operator.OperatorProcessor;
import org.allenai.pdfbox.cos.COSBase;

import java.io.IOException;

/**
 * ": Set word and character spacing, move to next line, and show text.
 *
 * @author Laurent Huault
 */
public class ShowTextLineAndSpace extends OperatorProcessor
{
    @Override
    public void process(Operator operator, List<COSBase> arguments) throws IOException
    {
        if (arguments.size() < 3)
        {
            throw new MissingOperandException(operator, arguments);
        }
        context.processOperator("Tw", arguments.subList(0,1));
        context.processOperator("Tc", arguments.subList(1,2));
        context.processOperator("'", arguments.subList(2,3));
    }

    @Override
    public String getName()
    {
        return "\"";
    }
}