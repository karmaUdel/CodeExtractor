/**
 * 
 */
package com.xml.extractor;

//import com.xml.db.DbConnection;
import java.io.File;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
/**
 * @author Aditya Karmarkar
 *
 */
public class CodeExtractor {
	//static DbConnection db ;
	/**
	 * @param args
	 */
	public CodeExtractor() {
		// TODO Auto-generated constructor stub
		//db = new DbConnection();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// getConnection
		
		//db.getConnection();
		// pass all the files
		File folder;
		String path = System.getProperty("user.dir")+"/Slack-Data";
		File[] listOfFiles = null;
		try {
			//path =  args[0];
			folder = new File(path);
			if(folder.exists()){
				listOfFiles = folder.listFiles();
			}
			else {
				System.out.println("Files do not exists");
			}
		}catch(Exception e) {
			path = "F:/UniversityofDelaware/Slack-Data";
			folder = new File(path);
		}
		if(listOfFiles!=null) {
			for (int i=0;i<listOfFiles.length;i++) {
				if(listOfFiles[i].isFile()) {
					extract(path,listOfFiles[i].getName());
				}
			}
		}
		else{
			extract(path,"racket");
		}
	}
	
	public static boolean extract(String path, String name) {
		boolean flag = true;
		try {
				
			 	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        File stylesheet = new File("SlackDataTransformer.xsl");
		        File xmlSource = new File(path+"/"+name);
				//System.out.println("Print file xsl "+ stylesheet);
				//System.out.println("Print file source "+ xmlSource);
				
				DocumentBuilder builder = factory.newDocumentBuilder();
		        Document document = builder.parse(xmlSource);
				//System.out.println("Document Builder ");
		        StreamSource stylesource = new StreamSource(stylesheet);
		        Transformer transformer = TransformerFactory.newInstance()
		                .newTransformer(stylesource);
				System.out.println("Transforming ");
		        Source source = new DOMSource(document);
				System.out.println("Generating file " + System.getProperty("user.dir"));
				
		        File out=new File(System.getProperty("user.dir")+"/data.csv");
				System.out.println(out.getPath());
		        FileWriter fw = new FileWriter(out, true);
				Result outputTarget = null;
				if(!out.exists()) {
					out.createNewFile();
				}
				if(!((float)out.length()/1024 >=7371)) {
						
					
					if(!out.exists()) {
						out.createNewFile();
						outputTarget = new StreamResult(fw);
						//System.out.println("Generated file ");
					}
					Result s = null;
					if(outputTarget==null) {
						outputTarget = new StreamResult(fw);
						transformer.transform(source, outputTarget);
					}else {
			        transformer.transform(source, outputTarget);
					}System.out.println("Print file output"+ outputTarget);
					flag =true;
				}
				/*else {
					flag =false;
					System.out.println("I'm not making file");
				}*/
		}
		catch(Exception e){
			flag =false;
			e.printStackTrace();
		}
		finally {
			
			return flag;
		}
		
	}

}
