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

package org.apache.pdfbox.tools.pdfdebugger.streampane.tooltip;

import java.awt.Color;

/**
 * @author Khyrul Bashar
 * A class that provide tooltip for G and g.
 */
final class GToolTip extends ColorToolTip
{
    /**
     * Constructor.
     * @param rowText String instance.
     */
    GToolTip(String rowText)
    {
        createMarkUp(rowText);
    }

    private void createMarkUp(String rowText)
    {
        float[] colorValues = extractColorValues(rowText);
        if (colorValues != null)
        {
            Color color = new Color(colorValues[0], colorValues[0], colorValues[0]);
            setToolTipText(getMarkUp(colorHexValue(color)));
        }
    }
}
