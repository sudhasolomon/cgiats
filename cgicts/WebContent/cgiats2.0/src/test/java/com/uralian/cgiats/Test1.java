package com.uralian.cgiats;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class Test1 {

	public static void main(String args[]) throws IOException {

		File file = new File("D:\\ATS  Technologies.docx");
		if (file.exists()) {
			FileInputStream stream = new FileInputStream(file);
			ByteArrayInputStream bStream = new ByteArrayInputStream(stream.toString().getBytes());

			XWPFDocument docIn = new XWPFDocument(bStream);
			XWPFWordExtractor extractor = new XWPFWordExtractor(docIn);
			String docText = extractor.getText();
			System.out.println(docText);
		}
	}
}
