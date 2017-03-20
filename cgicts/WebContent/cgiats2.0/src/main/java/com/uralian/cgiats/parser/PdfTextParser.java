/*
 * PdfTextParser.java Oct 4, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * @author Vlad Orzhekhovskiy
 */
public class PdfTextParser extends AbstractTextParser
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.parser.TextParser#extractKeywords(java.io.InputStream)
	 */
	@Override
	public Map<String, Integer> extractKeywords(InputStream is)
	    throws IOException
	{
		PDDocument doc = PDDocument.load(is);

		PDFTextStripper stripper = new PDFTextStripper();
		StringWriter out = new StringWriter();
		stripper.writeText(doc, out);

		Map<String, Integer> keywords = new HashMap<String, Integer>();
		parseTextAddKeywords(out.toString(), keywords);

		return keywords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.parser.TextParser#parseText(java.io.InputStream)
	 */
	@Override
	public String parseText(InputStream is) throws IOException
	{
		 PDFParser parser;
	        String parsedText = null;
	        PDFTextStripper pdfStripper = null;
	        PDDocument pdDoc = null;
	        COSDocument cosDoc = null;
	        try {
	            parser = new PDFParser(is);
	        } catch (IOException e) {
	            System.err.println("Unable to open PDF Parser. " + e.getMessage());
	            return null;
	        }
	        try {
	            parser.parse();
	            cosDoc = parser.getDocument();
	            pdfStripper = new PDFTextStripper();
	            pdDoc = new PDDocument(cosDoc);
	            parsedText = pdfStripper.getText(pdDoc);
	        } catch (Exception e) {
	            System.err
	                    .println("An exception occured in parsing the PDF Document."
	                            + e.getMessage());
	            e.printStackTrace();
	        } finally {
	            try {
	                if (cosDoc != null)
	                    cosDoc.close();
	                if (pdDoc != null)
	                    pdDoc.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return parsedText;
	}
}