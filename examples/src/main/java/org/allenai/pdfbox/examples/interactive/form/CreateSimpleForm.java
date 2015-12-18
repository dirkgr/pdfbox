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

package org.allenai.pdfbox.examples.interactive.form;

import java.io.IOException;

import org.allenai.pdfbox.pdmodel.PDDocument;
import org.allenai.pdfbox.pdmodel.common.PDRectangle;
import org.allenai.pdfbox.pdmodel.font.PDFont;
import org.allenai.pdfbox.pdmodel.font.PDType1Font;
import org.allenai.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.allenai.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.allenai.pdfbox.pdmodel.interactive.form.PDTextField;
import org.allenai.pdfbox.cos.COSName;
import org.allenai.pdfbox.pdmodel.PDPage;
import org.allenai.pdfbox.pdmodel.PDResources;

/**
 * An example of creating an AcroForm and a form field from scratch.
 * 
 * The form field is created with properties similar to creating
 * a form with default settings in Adobe Acrobat.
 * 
 */
public final class CreateSimpleForm
{
    private CreateSimpleForm()
    {
    }
    
    public static void main(String[] args) throws IOException
    {
        // Create a new document with an empty page.
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        
        // Adobe Acrobat uses Helvetica as a default font and 
        // stores that under the name '/Helv' in the resources dictionary
        PDFont font = PDType1Font.HELVETICA;
        PDResources resources = new PDResources();
        resources.put(COSName.getPDFName("Helv"), font);
        
        // Add a new AcroForm and add that to the document
        PDAcroForm acroForm = new PDAcroForm(document);
        document.getDocumentCatalog().setAcroForm(acroForm);
        
        // Add and set the resources and default appearance at the form level
        acroForm.setDefaultResources(resources);
        
        // Acrobat sets the font size on the form level to be
        // auto sized as default. This is done by setting the font size to '0'
        String defaultAppearanceString = "/Helv 0 Tf 0 g";
        acroForm.setDefaultAppearance(defaultAppearanceString);
        
        // Add a form field to the form.
        PDTextField textBox = new PDTextField(acroForm);
        textBox.setPartialName("SampleField");
        // Acrobat sets the font size to 12 as default
        // This is done by setting the font size to '12' on the
        // field level.
        defaultAppearanceString = "/Helv 12 Tf 0 g";
        textBox.setDefaultAppearance(defaultAppearanceString);
        
        // add the field to the acroform
        acroForm.getFields().add(textBox);
        
        // Specify the annotation associated with the field
        PDAnnotationWidget widget = textBox.getWidgets().get(0);
        PDRectangle rect = new PDRectangle(50, 750, 200, 50);
        widget.setRectangle(rect);
        
        // Add the annotation to the page
        page.getAnnotations().add(widget);
        
        // set the field value
        textBox.setValue("Sample field");

        document.save("target/SimpleForm.pdf");
        document.close();
    }
}
