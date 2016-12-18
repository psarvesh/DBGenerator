package storage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import org.ccil.cowan.tagsoup.jaxp.SAXParserImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Main {

	private static String title = null;
	
	public static void main(String[] args) {
		boolean isIndex = false;
		String dir = null, inputDir = null;
		String workDir = null;
		try{
			if(args.length == 4){
				workDir = args[3];
				inputDir = args[2];
				dir = args[1];
				int which = Integer.parseInt(args[0]);
				if(which == 0){
					isIndex = false;
				} else if(which == 1){
					isIndex = true;
				} else {
					return;
				}
			} else {
				System.out.println("Usage: java -jar jarfile args");
				System.out.println("args: <indexer/pr> <output-dir> <input db-dir> <indexer/pr-dir> ");
				System.exit(1);
			}
		} catch(Exception e){
			System.out.println("Usage: java -jar jarfile args");
			System.out.println("args: <indexer/pr> <output-dir> <input db-dir> <indexer/pr-dir> ");
			System.exit(1);
		}
		if(isIndex){
			OutputDBWrapper outDB = new OutputDBWrapper(dir);
			File file = new File(workDir);
			FileReader fr = null;
			BufferedReader br = null;
			if(file.exists()){
				if(file.isDirectory()){
					File[] listOfFiles = file.listFiles();
					for(int i = 0; i < listOfFiles.length; i++){
						if(listOfFiles[i].isFile()){
							try {
								fr = new FileReader(listOfFiles[i]);
								br = new BufferedReader(fr);
								String line;
								while((line = br.readLine())!=null){
									try{
										String[] split = line.split("\\s+");
										if(split.length != 2) continue;
										String word = args[0];
										double idf = Double.parseDouble(split[1]);
										outDB.storeIDF(new IDFObject(word, idf));
									} catch(Exception e){
										
									}
								}
							} catch (Exception e) {
								
							}
						}
					}
				} else {
					System.out.println(workDir + ": location does not exist!");
				} 
			} else {
				System.out.println(workDir + ": location does not exist!");
			}
		} else {
			OutputDBWrapper outDB = new OutputDBWrapper(dir);
			InputDBWrapper inDB = new InputDBWrapper(inputDir);
			File file = new File(workDir);
			FileReader fr = null;
			BufferedReader br = null;
			if(file.exists()){
				if(file.isDirectory()){
					File[] listOfFiles = file.listFiles();
					for(int i = 0; i < listOfFiles.length; i++){
						if(listOfFiles[i].isFile()){
							try {
								fr = new FileReader(listOfFiles[i]);
								br = new BufferedReader(fr);
								String line;
								while((line = br.readLine())!=null){
									try{
										String[] split = line.split("\\s+");
										if(split.length != 2) continue;
										String url = args[0];
										double pr = Double.parseDouble(split[1]);
										URLInfo urlInfo = inDB.peekDoc(url);
										if(urlInfo == null) continue;
										InputStream is = new ByteArrayInputStream(urlInfo.getBody().getBytes("UTF-8"));			
										SAXParserImpl.newInstance(null).parse(
												is,
												new DefaultHandler() {
													boolean isTitle = false;
													public void startElement(String uri, String localName,
															String name, Attributes a) {
														if (name.equalsIgnoreCase("title")){
															isTitle = true;
														}
													}
													
													public void characters(char ch[], int start, int length) throws SAXException{
														if(isTitle){
															title = new String(ch, start, length);
															isTitle = false;
														}
													}
												}
											);
										String region = urlInfo.getGeoRegion();
										String city = urlInfo.getGeoCity();
										String country = urlInfo.getGeoCountry();
										outDB.storePR(new PRObject(url, title, pr, region, city, country));
										title = null;
									} catch(Exception e){
										
									}
								}
							} catch (Exception e) {
								
							}
						}
					}
				} else {
					System.out.println(workDir + ": location does not exist!");
				} 
			} else {
				System.out.println(workDir + ": location does not exist!");
			}
			
		}
		
	}

}
