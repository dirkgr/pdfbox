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

import org.allenai.pdfbox.cos.COSBase;
import org.allenai.pdfbox.preflight.font.container.CIDType2Container;
import org.allenai.pdfbox.preflight.font.descriptor.CIDType2DescriptorHelper;
import org.allenai.pdfbox.pdmodel.font.PDCIDFontType2;
import org.allenai.pdfbox.preflight.PreflightContext;

public class CIDType2FontValidator extends DescendantFontValidator<CIDType2Container>
{
    public CIDType2FontValidator(PreflightContext context, PDCIDFontType2 font)
    {
        super(context, font, new CIDType2Container(font));
    }

    @Override
    protected void checkCIDToGIDMap(COSBase ctog)
    {
        checkCIDToGIDMap(ctog, true);
    }

    @Override
    protected void createFontDescriptorHelper()
    {
        this.descriptorHelper = new CIDType2DescriptorHelper(context, font, fontContainer);
    }
}