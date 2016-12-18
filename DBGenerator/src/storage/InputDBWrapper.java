package storage;

import java.io.File;
import java.util.ArrayList;

import com.sleepycat.je.*;
import com.sleepycat.persist.*;

public class InputDBWrapper {
	
	private static Environment myEnv = null;
	private static EntityStore store;
	private EnvironmentConfig envConfig;
	private StoreConfig storeConfig;
	
	private PrimaryIndex<String, URLInfo> docDB;
	private SecondaryIndex<String, String, URLInfo> seenCheckDB;
	
	public InputDBWrapper(File envPath){
		try{
			envConfig = new EnvironmentConfig();
			storeConfig = new StoreConfig();
			
			envConfig.setAllowCreate(true);
			storeConfig.setAllowCreate(true);
			
			myEnv = new Environment(envPath, envConfig);
			store = new EntityStore(myEnv, "EntityStore", storeConfig);
			
			docDB = store.getPrimaryIndex(String.class, URLInfo.class);
			seenCheckDB = store.getSecondaryIndex(docDB, String.class, "checkSum");
		} catch(DatabaseException dbe){
			System.out.println("Database not created. Exception : " + dbe.getMessage());
		}
	}
	
	public InputDBWrapper(String envPath){
		File file = new File(envPath);
		if(file.exists()){
			if(file.isDirectory()){
				
			} else {
				file.mkdir();
				file.setReadable(true);
				file.setWritable(true);
			} 
		} else {
			file.mkdir();
			file.setReadable(true);
			file.setWritable(true);
		}
		try{
			envConfig = new EnvironmentConfig();
			storeConfig = new StoreConfig();
			envConfig.setAllowCreate(true);
			storeConfig.setAllowCreate(true);
			myEnv = new Environment(file, envConfig);
			store = new EntityStore(myEnv, "EntityStore", storeConfig);
			
			docDB = store.getPrimaryIndex(String.class, URLInfo.class);
			seenCheckDB = store.getSecondaryIndex(docDB, String.class, "checkSum");
		} catch(DatabaseException dbe){
			System.out.println("Database not created. Exception : " + dbe.getMessage());
		}
	}
	
	
	/**
	 * For urlDoc
	 */
	
	public void storeDoc(URLInfo urlDoc){
		docDB.put(urlDoc);
	}
	
	public boolean containsDoc(String url){
		return docDB.contains(url);
	}
	
	public URLInfo peekDoc(String url){
		if(docDB.contains(url))
			return docDB.get(url);
		else 
			return null;
	}
	
	public boolean deleteDoc(String url){
		return docDB.delete(url);
	}
	
	public boolean containsCheckSum(String key) {
		return seenCheckDB.contains(key);
	}

	public ArrayList<String> getAllURLs(){
		EntityCursor<String> cursor = null;
		ArrayList<String> urls = new ArrayList<String>();
		try{
			cursor = docDB.keys();
			for(String key : cursor){
				urls.add(key);
			}
			cursor.close();
			cursor=null;
		}
		finally {
			if(cursor!=null)
				cursor=null;
		}
		return urls;
	}
	/**
	 *  
	 */
	public void close(){
		if (store != null) {
		    try {
		        store.close();
		    } catch(DatabaseException dbe) {
		       
		    }
		}
		if (myEnv != null) {
		    try {
		        // Finally, close environment.
		        myEnv.close();
		    } catch(DatabaseException dbe) {
		        
		    }
		} 
	}
	
	@Override
	protected void finalize() throws Throwable{
		this.close();
	}
}