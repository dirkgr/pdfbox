/*****************************************************************************
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 ****************************************************************************/

package org.allenai.pdfbox.preflight.font;

import org.allenai.pdfbox.preflight.exception.ValidationException;
import org.allenai.pdfbox.cos.COSDictionary;
import org.allenai.pdfbox.preflight.PreflightContext;
import org.allenai.pdfbox.preflight.font.container.FontContainer;
import org.allenai.pdfbox.preflight.font.descriptor.FontDescriptorHelper;

public abstract class FontValidator<T extends FontContainer>
{
    protected T fontContainer;
    protected PreflightContext context;
    protected FontDescriptorHelper<T> descriptorHelper;

    public FontValidator(PreflightContext context, COSDictionary dict, T fContainer)
    {
        super();
        this.context = context;
        this.fontContainer = fContainer;
        this.context.addFontContainer(dict, fContainer);
    }

    public abstract void validate() throws ValidationException;

    protected void checkEncoding()
    {
        // nothing to check for PDF/A-1b
    }

    protected void checkToUnicode()
    {
        // nothing to check for PDF/A-1b
    }

    public T getFontContainer()
    {
        return fontContainer;
    }

}