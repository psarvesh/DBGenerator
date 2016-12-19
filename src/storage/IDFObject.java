package storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class IDFObject {
	@PrimaryKey
	String word;
	
	double idf;
	
	public IDFObject(String word, double idf2){
		this.word = word;
		this.idf = idf2;
	}
	
	public String getWord(){
		return this.word;
	}
	
	public double getIDF(){
		return this.idf;
	}
	
}
