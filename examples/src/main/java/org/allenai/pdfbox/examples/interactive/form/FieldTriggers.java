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

import java.io.File;
import java.io.IOException;

import org.allenai.pdfbox.pdmodel.PDDocument;
import org.allenai.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.allenai.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.allenai.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.allenai.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.allenai.pdfbox.pdmodel.interactive.form.PDField;
import org.allenai.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/**
 * Show usage of different field triggers.
 * 
 * This sample adds a JavaScript to be executed on the different field triggers available.
 * 
 * This sample builds on the form generated by @link CreateSimpleForm so you need to run that first.
 * 
 */
public final class FieldTriggers
{
    private FieldTriggers()
    {
    }
    
    public static void main(String[] args) throws IOException
    {
        // Load the PDF document created by SimpleForm.java
        PDDocument document = PDDocument.load(new File("target/SimpleForm.pdf"));
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

        // Get the field and the widget associated to it.
        // Note: there might be multiple widgets
        PDField field = acroForm.getField("SampleField");
        PDAnnotationWidget widget = field.getWidgets().get(0);
        
        // Some of the actions are available to the widget, some are available to the form field.
        // See Table 8.44 and Table 8.46 in the PDF 1.7 specification
        
        // Actions for the widget
        PDAnnotationAdditionalActions annotationActions = new PDAnnotationAdditionalActions();

        // Create an action when entering the annotations active area
        PDActionJavaScript jsEnterAction = new PDActionJavaScript();
        jsEnterAction.setAction("app.alert(\"On 'enter' action\")");
        annotationActions.setE(jsEnterAction);

        // Create an action when exiting the annotations active area
        PDActionJavaScript jsExitAction = new PDActionJavaScript();
        jsExitAction.setAction("app.alert(\"On 'exit' action\")");
        annotationActions.setX(jsExitAction);

        // Create an action when the mouse button is pressed inside the annotations active area
        PDActionJavaScript jsMouseDownAction = new PDActionJavaScript();
        jsMouseDownAction.setAction("app.alert(\"On 'mouse down' action\")");
        annotationActions.setD(jsMouseDownAction);

        // Create an action when the mouse button is released inside the annotations active area
        PDActionJavaScript jsMouseUpAction = new PDActionJavaScript();
        jsMouseUpAction.setAction("app.alert(\"On 'mouse up' action\")");
        annotationActions.setU(jsMouseUpAction);
        
        // Create an action when the annotation gets the input focus
        PDActionJavaScript jsFocusAction = new PDActionJavaScript();
        jsFocusAction.setAction("app.alert(\"On 'focus' action\")");
        annotationActions.setFo(jsFocusAction);        

        // Create an action when the annotation loses the input focus
        PDActionJavaScript jsBlurredAction = new PDActionJavaScript();
        jsBlurredAction.setAction("app.alert(\"On 'blurred' action\")");
        annotationActions.setBl(jsBlurredAction);  
                
        widget.setActions(annotationActions);
        
        // Actions for the field
        PDFormFieldAdditionalActions fieldActions = new PDFormFieldAdditionalActions();

        // Create an action when the user types a keystroke in the field
        PDActionJavaScript jsKeystrokeAction = new PDActionJavaScript();
        jsKeystrokeAction.setAction("app.alert(\"On 'keystroke' action\")");
        fieldActions.setK(jsKeystrokeAction);  

        // Create an action when the field is formatted to display the current value
        PDActionJavaScript jsFormattedAction = new PDActionJavaScript();
        jsFormattedAction.setAction("app.alert(\"On 'formatted' action\")");
        fieldActions.setF(jsFormattedAction);  

        // Create an action when the field value changes
        PDActionJavaScript jsChangedAction = new PDActionJavaScript();
        jsChangedAction.setAction("app.alert(\"On 'change' action\")");
        // fieldActions.setV(jsChangedAction);  

        // Create an action when the field value changes
        PDActionJavaScript jsRecalculateAction = new PDActionJavaScript();
        jsRecalculateAction.setAction("app.alert(\"On 'recalculate' action\")");
        fieldActions.setC(jsRecalculateAction);  
        
        // Set the Additional Actions entry for the field
        // Note: this is a workaround as if there is only one widget the widget
        // and the form field may share the same dictionary. Now setting the 
        // fields Additional Actions entry directly will overwrite the settings done for
        // the widget.
        // https://issues.apache.org/jira/browse/PDFBOX-3036
        
        field.getActions().getCOSObject().addAll(fieldActions.getCOSObject());
        
        document.save("target/FieldTriggers.pdf");
        document.close();
    }
}
