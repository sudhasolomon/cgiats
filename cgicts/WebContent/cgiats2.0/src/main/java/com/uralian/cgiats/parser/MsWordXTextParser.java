package com.uralian.cgiats.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jfree.util.Log;
import org.jsoup.Jsoup;

/**
 * 
 * 
 * @author cxreboll
 */
public class MsWordXTextParser extends AbstractTextParser
{
	/**
	 * This is the Open Word XML document inside of all docx files.
	 */
	private static final String DOCUMENT = "word/document.xml";
private static final Logger LOG=Logger.getLogger(MsWordXTextParser.class);
	@Override
	public Map<String, Integer> extractKeywords(InputStream is)
	    throws IOException
	{
		// Docx files are Zip files.
		ZipInputStream zipIn = new ZipInputStream(is);
		ZipEntry ze = null;
		String strUnzipped = null;
		StringBuilder builder = null;

		// Looking for document.xml inside of docx file
		while ((ze = zipIn.getNextEntry()) != null)
		{
			
			if (ze.getName().toString().equals(DOCUMENT))
			{
				// Once we found it then lets start reading byte by byte.
				byte[] bytes = new byte[1024];
				long loops = ze.getSize();
				builder = new StringBuilder();
				int readBytes = 0;
				// Reading by 1MB chunks.
				while (readBytes < loops)
				{
					readBytes += zipIn.read(bytes, 0, 1024);
					strUnzipped = new String(bytes, "UTF-8");
					builder.append(strUnzipped);
					bytes = new byte[1024];
				}
				break;
			}
		}

		Map<String, Integer> keywords = new HashMap<String, Integer>();
		if (builder != null)
		{
			String parsedText = Jsoup.parse(builder.toString()).text();
			Scanner scanner = new Scanner(parsedText);
			while (scanner.hasNextLine())
			{
				String text = scanner.nextLine();
				parseTextAddKeywords(text, keywords);
			}
		}

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
		// Docx files are Zip files.
//		ZipInputStream zipIn = new ZipInputStream(is);
//		ZipEntry ze = null;
//		String strUnzipped = null;
//		StringBuilder builder = null;
		String parsedText = null;
	
		// This code is to Parse the Docx document Wrote by Raghavendra
		try{
			LOG.info("XWPFDocument is started");
			XWPFDocument docIn = new XWPFDocument(is);  
			LOG.info("XWPFDocument is completed");
			XWPFWordExtractor extractor = new XWPFWordExtractor(docIn);  
			LOG.info("XWPFWordExtractor is completed");
			parsedText= extractor.getText();
			LOG.info("parsed Text is over");
		}catch(InvalidOperationException ie){
			WordExtractor we;
			try {
				we = new WordExtractor(is);
				parsedText = we.getText();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
	}catch(IOException ie){
		ie.printStackTrace();
		WordExtractor we;
		try {
			we = new WordExtractor(is);
			parsedText = we.getText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
}catch(Exception e){
//		e.printStackTrace();
		System.out.println("In catch of MsWordTextParser");
		
		WordExtractor we = new WordExtractor(is);
		parsedText = we.getText();
	}
		// Upto here to parse the docx
		
	/* System.out.println("(ze = zipIn.getNextEntry()) != null :: "+(ze = zipIn.getNextEntry()) != null);
		System.out.println("While condition check >>"+(ze = zipIn.getNextEntry()) != null);
		// Looking for document.xml inside of docx file
		while ((ze = zipIn.getNextEntry()) != null)
		{
			System.out.println("ze>>"+ze);
			if(ze!=null){
				System.out.println("ze.getName() 93>>"+ze.getName());
				if(ze.getName()!=null && !ze.getName().trim().equals("")){
					System.out.println("ze.getName() 95>>"+ze.getName().toString());
				}
			}
			
			if (ze!=null && ze.getName()!=null && !ze.getName().trim().equals("") && ze.getName().toString().equals(DOCUMENT))
			{
				System.out.println("ze.getName().toString() 101>>"+ze.getName().toString());
				// Once we found it then lets start reading byte by byte.
				byte[] bytes = new byte[1024];
				System.out.println("ze.getSize() 104>>"+ze.getSize());
				long loops = ze.getSize();
				builder = new StringBuilder();
				int readBytes = 0;
				// Reading by 1MB chunks.
				while (readBytes < loops)
				{
					readBytes += zipIn.read(bytes, 0, 1024);
					strUnzipped = new String(bytes, "UTF-8");
					builder.append(strUnzipped);
					bytes = new byte[1024];
				}
				break;
			}else{
				// Once we found it then lets start reading byte by byte.
				byte[] bytes = new byte[1024];
				System.out.println("ze.getSize() 120>>"+ze.getSize());
				long loops = ze!=null && ze.getSize()>0 ?ze.getSize():0;
				builder = new StringBuilder();
				int readBytes = 0;
				// Reading by 1MB chunks.
				while (readBytes < loops)
				{
					readBytes += zipIn.read(bytes, 0, 1024);
					strUnzipped = new String(bytes, "UTF-8");
					builder.append(strUnzipped);
					bytes = new byte[1024];
				}
				break;
			}
		}
		System.out.println("builder :: "+builder);
		String parsedText = null;
		if(builder!=null && builder.toString()!=null && !builder.toString().trim().equals(""))
		parsedText=Jsoup.parse(builder.toString()).text();
		System.out.println(Jsoup.parse(builder.toString()).text());
		System.out.println(builder.toString());
		System.out.println("parsedText 140:: "+builder);*/
		return parsedText;
	}
}