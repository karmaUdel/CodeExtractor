/**
 * 
 */
package com.xml.main;

import com.xml.extractor.CodeExtractor;
import com.xml.reader.FileReader;

/**
 * @author Aditya
 *
 */
public final class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CodeExtractor code = new CodeExtractor();
		code.main(args);
		FileReader reader = new FileReader();
		reader.reader(System.getProperty("user.dir")+"/data.csv"); 
	}
}
