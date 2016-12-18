package storage;

import com.sleepycat.persist.model.PrimaryKey;

public class IDFObject {
	@PrimaryKey
	String word;
	
	int idf;
	
	public IDFObject(String word, int idf){
		this.word = word;
		this.idf = idf;
	}
	
	public int getIDF(){
		return this.idf;
	}
	
}
