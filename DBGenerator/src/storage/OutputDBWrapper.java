package storage;

import java.io.File;

import com.sleepycat.je.*;
import com.sleepycat.persist.*;

public class OutputDBWrapper {
	
	private static Environment myEnv = null;
	private static EntityStore store;
	private EnvironmentConfig envConfig;
	private StoreConfig storeConfig;
	private PrimaryIndex<String, PRObject> prDB;
	private PrimaryIndex<String, IDFObject> idfDB; 
	public OutputDBWrapper(File envPath){
		try{
			envConfig = new EnvironmentConfig();
			storeConfig = new StoreConfig();
			
			envConfig.setAllowCreate(true);
			storeConfig.setAllowCreate(true);
			
			myEnv = new Environment(envPath, envConfig);
			store = new EntityStore(myEnv, "EntityStore", storeConfig);
			
			prDB = store.getPrimaryIndex(String.class, PRObject.class);
			idfDB = store.getPrimaryIndex(String.class, IDFObject.class);
		} catch(DatabaseException dbe){
			System.out.println("Database not created. Exception : " + dbe.getMessage());
		}
	}
	
	public OutputDBWrapper(String envPath){
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
			prDB = store.getPrimaryIndex(String.class, PRObject.class);
			idfDB = store.getPrimaryIndex(String.class, IDFObject.class);
		} catch(DatabaseException dbe){
			System.out.println("Database not created. Exception : " + dbe.getMessage());
		}
	}
	/**
	 * Methods to access pagerank object
	 */
	
	public void storePR(PRObject probj){
		prDB.put(probj);
	}
	
	public boolean containsPR(String url){
		return prDB.contains(url);
	}
	
	public PRObject peekPR(String url){
		if(prDB.contains(url)){
			return prDB.get(url);
		} else{
			return null;
		}
	}
	
	public boolean deletePR(String url){
		return prDB.delete(url);
	}

	/**
	 * Methods to access IDF object
	 */
	
	public void storeIDF(IDFObject idfobj){
		idfDB.put(idfobj);
	}
	
	public boolean containsIDFObj(String word){
		return idfDB.contains(word);
	}
	
	public IDFObject peekIDF(String word){
		if(idfDB.contains(word)){
			return idfDB.get(word);
		} else{
			return null;
		}
	}
	
	public boolean deleteIDF(String word){
		return idfDB.delete(word);
	}
	
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
