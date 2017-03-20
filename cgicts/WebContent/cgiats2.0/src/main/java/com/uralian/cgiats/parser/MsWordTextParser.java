/*
 * MsWordTextParser.java Feb 29, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author Vlad Orzhekhovskiy
 */
public class MsWordTextParser extends AbstractTextParser {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.parser.TextParser#extractKeywords(java.io.InputStream)
	 */
	@Override
	public Map<String, Integer> extractKeywords(InputStream is) throws IOException {
		WordExtractor we = new WordExtractor(is);
		String text = we.getText();

		Map<String, Integer> keywords = new HashMap<String, Integer>();
		parseTextAddKeywords(text, keywords);

		return keywords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.parser.TextParser#parseText(java.io.InputStream)
	 */
	@Override
	public String parseText(InputStream is) {
		String str = null;
		// Below code commented to test for stopping an invalid header exception
		/*
		 * WordExtractor we = new WordExtractor(is); String text = we.getText();
		 * return text;
		 */
		try {
			// Siva Kurapati
			POIFSFileSystem fs = new POIFSFileSystem(is);
			HWPFDocument docIn = new HWPFDocument(fs);
			WordExtractor extractor = new WordExtractor(docIn);
			str = extractor.getText();
		} catch (InvalidOperationException ie) {
			WordExtractor we;
			try {
				ie.printStackTrace();
				// we = new WordExtractor(is);
				// str = we.getText();
				str = new String(IOUtils.toByteArray(is), "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException ie) {
			WordExtractor we;
			try {
				ie.printStackTrace();
				// we = new WordExtractor(is);
				// str = we.getText();
				str = new String(IOUtils.toByteArray(is), "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// e.printStackTrace();
			System.out.println("In catch of MsWordTextParser");

			/*
			 * WordExtractor we = new WordExtractor(is); str = we.getText();
			 */
		}
		return str;
	}
}