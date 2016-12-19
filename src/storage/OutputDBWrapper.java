package storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	
	/**
	 * Sorted list return
	 */
	
	public HashMap<String, PRObject> getSortedPR(ArrayList<String> list){
		HashMap<PRObject, Double> mapPR = new HashMap<PRObject, Double>();
		for(String url: list){
			PRObject pro = this.peekPR(url);
			if(pro != null){
				mapPR.put(pro, pro.getPageRank());
			}
		}
		
		List<PRObject> mapKeys = new ArrayList<PRObject>(mapPR.keySet());
	    List<Double> mapValues = new ArrayList<Double>(mapPR.values());
	    Collections.sort(mapValues);

	    LinkedHashMap<PRObject, Double> sortedMap = new LinkedHashMap<PRObject, Double>();

	    Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Double val = valueIt.next();
	        Iterator<PRObject> keyIt = mapKeys.iterator();
	        while (keyIt.hasNext()) {
	            PRObject key = keyIt.next();
	            Double comp1 = mapPR.get(key);
	            Double comp2 = val;
	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    LinkedHashMap<String, PRObject> sortedReturn = new LinkedHashMap<String, PRObject>();
	    for(Map.Entry<PRObject, Double> entry : sortedMap.entrySet()){
	    	sortedReturn.put(entry.getKey().getURL(), entry.getKey());
	    }
		return sortedReturn;
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
