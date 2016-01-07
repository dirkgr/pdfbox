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

package org.allenai.pdfbox.preflight.process.reflect;

import java.io.IOException;
import java.util.List;

import org.allenai.pdfbox.cos.COSBase;
import org.allenai.pdfbox.cos.COSObject;
import org.allenai.pdfbox.cos.COSStream;
import org.allenai.pdfbox.pdmodel.graphics.PDXObject;
import org.allenai.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.allenai.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.allenai.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.allenai.pdfbox.preflight.PreflightConfiguration;
import org.allenai.pdfbox.preflight.PreflightConstants;
import org.allenai.pdfbox.preflight.PreflightPath;
import org.allenai.pdfbox.preflight.ValidationResult;
import org.allenai.pdfbox.preflight.content.PreflightContentStream;
import org.allenai.pdfbox.preflight.exception.ValidationException;
import org.allenai.pdfbox.preflight.graphic.ColorSpaceHelper;
import org.allenai.pdfbox.preflight.graphic.ColorSpaceHelperFactory;
import org.allenai.pdfbox.preflight.process.AbstractProcess;
import org.allenai.pdfbox.preflight.utils.COSUtils;
import org.allenai.pdfbox.preflight.utils.ContextHelper;
import org.allenai.pdfbox.cos.COSDictionary;
import org.allenai.pdfbox.cos.COSName;
import org.allenai.pdfbox.pdmodel.PDPage;
import org.allenai.pdfbox.pdmodel.PDResources;
import org.allenai.pdfbox.preflight.PreflightContext;

public class SinglePageValidationProcess extends AbstractProcess
{

    @Override
    public void validate(PreflightContext context) throws ValidationException
    {
        PreflightPath vPath = context.getValidationPath();
        if (vPath.isEmpty())
        {
            return;
        }
        if (!vPath.isExpectedType(PDPage.class)) 
        {
            addValidationError(context, new ValidationResult.ValidationError(PreflightConstants.ERROR_PDF_PROCESSING_MISSING, "Page validation required at least a PDPage"));
        } 
        else 
        {
            PDPage page = (PDPage) vPath.peek();
            validateActions(context, page);
            validateAnnotation(context, page);
            validateColorSpaces(context, page);
            validateResources(context, page);
            validateGraphicObjects(context, page);
            validateGroupTransparency(context, page);
            // TODO
            // add MetaData validation ?
            validateContent(context, page);
        }
    }

    /**
     * This method checks additional actions contained in the given Page object.
     * 
     * @param context the preflight context.
     * @param page the page to check.
     * @throws ValidationException
     */
    protected void validateActions(PreflightContext context, PDPage page) throws ValidationException
    {
        ContextHelper.validateElement(context, page.getCOSObject(), PreflightConfiguration.ACTIONS_PROCESS);
    }

    /**
     * Check that all ColorSpace present in the Resource dictionary are conforming to the ISO 19005:2005-1
     * specification.
     * 
     * @param context the preflight context.
     * @param page the page to check.
     */
    protected void validateColorSpaces(PreflightContext context, PDPage page)
    {
        PDResources resources = page.getResources();
        if (resources != null)
        {
            PreflightConfiguration config = context.getConfig();
            ColorSpaceHelperFactory colorSpaceFactory = config.getColorSpaceHelperFact();
            for (COSName name : resources.getColorSpaceNames())
            {
                try
                {
                    PDColorSpace pdCS = resources.getColorSpace(name);
                    ColorSpaceHelper csHelper = colorSpaceFactory.getColorSpaceHelper(context, pdCS,
                            ColorSpaceHelperFactory.ColorSpaceRestriction.NO_RESTRICTION);
                    csHelper.validate();
                }
                catch (IOException e)
                {
                    // fixme: this code was previously in PDResources
                    // LOG.error("error while creating a colorspace", exception);
                }
            }
        }
    }

    /**
     * Check that all XObject references in the PDResource of the page and in the Thumb entry are confirming to the
     * PDF/A specification.
     * 
     * @param context the preflight context.
     * @param page the page to check.
     * @throws ValidationException
     */
    protected void validateGraphicObjects(PreflightContext context, PDPage page) throws ValidationException
    {
        COSBase thumbBase = page.getCOSObject().getItem(PreflightConstants.PAGE_DICTIONARY_VALUE_THUMB);
        if (thumbBase != null)
        {
            try
            {
                if (thumbBase instanceof COSObject)
                {
                    thumbBase = ((COSObject) thumbBase).getObject();
                }
                PDXObject thumbImg = PDImageXObject.createThumbnail((COSStream) thumbBase);
                ContextHelper.validateElement(context, thumbImg, PreflightConfiguration.GRAPHIC_PROCESS);
            }
            catch (IOException e)
            {
                context.addValidationError(new ValidationResult.ValidationError(PreflightConstants.ERROR_GRAPHIC_INVALID, "Unable to read Thumb image : "
                        + e.getMessage()));
            }
        }
    }

    protected void validateResources(PreflightContext context, PDPage page) throws ValidationException
    {
        ContextHelper.validateElement(context, page.getResources(), PreflightConfiguration.RESOURCES_PROCESS);
    }

    /**
     * 
     * @param context the preflight context.
     * @param page the page to check.
     * @throws ValidationException
     */
    protected void validateContent(PreflightContext context, PDPage page) throws ValidationException
    {
        // TODO add this wrapper in the config object ?
        try
        {
            PreflightContentStream csWrapper = new PreflightContentStream(context, page);
            csWrapper.validatePageContentStream();
        }
        catch (IOException e)
        {
            context.addValidationError(new ValidationResult.ValidationError(PreflightConstants.ERROR_UNKOWN_ERROR, e.getMessage(), e));
        }
    }

    /**
     * 
     * @param context the preflight context.
     * @param page the page to check.
     * @throws ValidationException
     */
    protected void validateAnnotation(PreflightContext context, PDPage page) throws ValidationException
    {
        try
        {
            List<?> lAnnots = page.getAnnotations();
            for (Object object : lAnnots)
            {
                if (object instanceof PDAnnotation)
                {
                    COSDictionary cosAnnot = ((PDAnnotation) object).getCOSObject();
                    ContextHelper.validateElement(context, cosAnnot, PreflightConfiguration.ANNOTATIONS_PROCESS);
                }
            }
        }
        catch (ValidationException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            // TODO IOException probably due to Encrypt
            throw new ValidationException("Unable to access Annotation", e);
        }
    }

    /**
     * Check that the group dictionary doesn't have a Transparency attribute
     * 
     * @param context the preflight context.
     * @param page the page to check.
     * @throws ValidationException
     */
    protected void validateGroupTransparency(PreflightContext context, PDPage page) throws ValidationException
    {
        COSBase baseGroup = page.getCOSObject().getItem(PreflightConstants.XOBJECT_DICTIONARY_KEY_GROUP);
        COSDictionary groupDictionary = COSUtils.getAsDictionary(baseGroup, context.getDocument().getDocument());
        if (groupDictionary != null)
        {
            String sVal = groupDictionary.getNameAsString(COSName.S);
            if (PreflightConstants.XOBJECT_DICTIONARY_VALUE_S_TRANSPARENCY.equals(sVal))
            {
                context.addValidationError(new ValidationResult.ValidationError(PreflightConstants.ERROR_GRAPHIC_TRANSPARENCY_GROUP,
                        "Group has a transparency S entry or the S entry is null"));
            }
        }
    }

}