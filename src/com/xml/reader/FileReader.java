/**
 * 
 */
package com.xml.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.itextpdf.io.source.OutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.pdfa.PdfADocument;

/**
 * @author Aditya
 *
 */
public class FileReader {
	public static final int windowOfRelevance = 10;
	public static final String question = "\n\nin above conversation, code/s mentioned has issue/s?\n\tIf Yes:\n\t1.Bad\t2.Very bad\n\tIf No:\n\t1.Good\t2.Very good\n";
	public static final String confidence = "\n\nHow confident are you?\n\t1.Low\n\t2.Average\n\t3.High\n";
	public static final String optional = "\nOptional!\ncan you highlight place/word/sentence which lead to your decision\n";
	
	public void reader(String fileName) {
		// open file " filename " 
		File data = new File(fileName);
		// read file
		int count = 0;
		int codeElementCounter = 0;
		String codeElements[] = null;
		String messages[] = null;
		String codeElement = "";
		int codeElementIndex [] [] = null ;
	    Pattern p = Pattern.compile("```");   // the pattern to search for
	    //Matcher m = p.matcher();
	    boolean codeTagOpened = false;
		File messageData = new File(System.getProperty("user.dir")+"/messages.data");
		if(!messageData.exists()) {
			try {
				messageData.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!(messageData.length()/1024 >=4581)) {
			FileWriter documentCollection =  null;
			try {
				documentCollection = new FileWriter(messageData);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
		    int i = 0;
			if(data.canRead())
			{
				BufferedReader in = null;
				try {
					//Reader reader = new java.io.FileReader(data);
				    in = new BufferedReader(new java.io.FileReader(data));
				    String read = "";
				    //Stream s = null;
	 			    //s = in.lines();
				    count = (int) in.lines().count();
				    messages = new String[count];
				    codeElementIndex =  new int [count][2];
				    in.close(); // close and reset
				    in = new BufferedReader(new java.io.FileReader(data));
				    read = in.readLine();
				    
				    while (read!=null) {
				    	//System.out.println("Reading :::" + read+"\n");
				        String[] splited = read.split(",::");
				        /*if (splited.length>0){
					        for(int l=0;l<splited.length;l++) {
					        	System.out.println("splited ::: "+l+"." +splited[l] +"\n");
					        }
				        }*/
				        if(messages[i]==null) {
				        	messages[i]="";
				        }
				        if(splited.length>1) {
				        	//documentCollection.write(splited[1]);
				        	
				        	messages[i] += splited[0]+ " : " +splited[1]; // new line 
				        	/*if(splited.length>2) {
				        		for(int k = 2;k<=splited.length;k++) {
				        			messages[i]+= " "+splited[k];
				        		}
				        	}*/
				        	//System.out.println("Read :::"+read);
				        	//System.out.println("Message ::: "+i+". "+ messages[i]+"\n");
				        	i++; // go for next line
				        }else {
				        	if(splited.length>0 || splited.length == 1) {
					        	if(i!=0 ) {
					        		if(splited[0]!=""|| splited[0]!=null) {
					        			messages[i-1] += splited[0] +"\n"; // represents same line
					        		}
					        	}else {
					        		if(splited[0]!=""|| splited[0]!=null) {
						        		messages[0] += splited[0]+"\n"; // represents same line
					        		}
					        	}
					        	//System.out.println("splited ::: "+0+"." +splited[0] +"\n");
					        	//System.out.println("Message ::: "+i+". "+ messages[i]+"\n");
				        	}
				        }
				    	read = in.readLine();
				    }
				}catch (ArrayIndexOutOfBoundsException e) {
					// TODO: handle exception
					System.out.println("Final count "+i);
					e.printStackTrace();
				} 
				catch (IOException e) {
				    System.out.println("There was a problem: " + e);
				    e.printStackTrace();
				} finally {
				    try {
				        in.close();
				    } catch (Exception e) {
				    }
				}
				try {
					if(messages != null) {
						System.out.println(messages.length + " " + documentCollection!=null );
					    // now try to find at least one match
						i = 0;
						codeElements = new String[messages.length];
						for(String str:messages) {
							
							if(str !=null) {
							    if (p.matcher(str).find()) {
							    	// line may contain code 
							    	//System.out.println("Found a match");

							    	codeTagOpened = !codeTagOpened;
						    		codeElement = str +"\n"+ codeElement;
						    		codeElements[codeElementCounter]=codeElement;
							    	if(codeTagOpened) {
							    		
							    		codeElementIndex[codeElementCounter][0]= i;
							    	}else {
							    		//System.out.println(codeElements[codeElementCounter]);
							    		codeElementIndex[codeElementCounter++][1]= i;
							    	}
							    	
							    }  
							    else {
							    	if(codeTagOpened) {
							    		codeElement +=str + "\n"; 
							    	}
							    	//System.out.println(codeElement);
							    }

								documentCollection.write(str+ "\n");
							
							}i++;
						}
					}
					
					createDocuments(messages,codeElementIndex,codeElements,codeElementCounter);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				finally {
					try {
						documentCollection.close();
				    } catch (Exception e) {
				    	System.out.println("Couldn't close file");
				    }
				}
				// get all time stamps
				// search for ''' and select 10 msgs above and below
				// create document which includes 21 documents
				// save them as txt files under documents
				// end
			}
		}// if message.data is empty
		else {
			System.out.println("Data is already extracted");
		}
	}
	
	public void createDocuments(String [] msessages, int codeElementIndex[][], String codeElements[], int codeElementLimit) {
		int messagesLimit = msessages.length;
		String documentString = "";
		int startOfDoc =0;
		int endOfDoc = 0;
		FileWriter documentWriter;
		for(int i = 0; i< codeElementLimit ;i++) {
			startOfDoc = min(codeElementIndex[i][0],codeElementIndex[i][0]-windowOfRelevance);
			endOfDoc = max(codeElementIndex[i][1],codeElementIndex[i][1]+windowOfRelevance);
			
			if(endOfDoc>messagesLimit) {
				endOfDoc = messagesLimit-1;
			}
			documentString = "";
//			System.out.println(i+".Start : "+startOfDoc+" End : "+endOfDoc +" Code started at :"+codeElementIndex[i][0]+ " Ends at "+ codeElementIndex[i][1]);
			for(int j = startOfDoc;j< endOfDoc;j++) {
				documentString += msessages[j]+"\n"; // append all strings
				//System.out.println(msessages[j]);
				//System.out.println(documentString);
				
			} 
			//push into document+i+.document
			//System.out.println(codeElements[i]);
			if(documentString!= null || documentString != "") {
				File file = new File(System.getProperty("user.dir")+"/documents/document_"+i+".data");
				File dir = new File(System.getProperty("user.dir")+"/documents");
				if(!dir.exists()) {
					dir.mkdir();
					// mostly files won't be created if we don't have directory
					// so create one
				
				}
				if(!file.exists()) {
					try {
						file.createNewFile();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				 documentWriter =  null;
				try {
					documentWriter = new FileWriter(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					documentString = documentString+question+confidence+optional;
					documentWriter.write(documentString);
					documentWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					try {
						documentWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	// returns minimum
	// used for verifying if if window relevance takes document collector beyond actual number of messages
	// for eg. Messages[].length = 54000 and last code element ends at line 53997, then 53997 + windowOfRelevance > 54000
	// this will create ArrayIndexOutOfBounds we can't let this happen so choose min_of 54000 and 53997 + windowOfRelevance 
	// output will be 54000 and no exception is thrown
	public int min(int x, int y) {
		if(y<0) {
			y=0;
		}
		if(x<0) {
			x =0;
		}
		
		if (x>y) {
			return y;
		}else {
			return x;
		}
	}
	// returns maximum
	// used for verifying if if window relevance takes document collector beyond actual number of messages
	// for eg. Messages[].length = 54000 and first code element starts at line 3, then 3 - windowOfRelevance < 0
	// this will create ArrayIndexOutOfBounds we can't let this happen, so choose max_of 0 and 3 - windowOfRelevance 
	// output will be 0 and no exception is thrown.	
	public int max(int x, int y) {
		if (x>y) {
			return x;
		}else {
			return y;
		}
	}
	
	
}
