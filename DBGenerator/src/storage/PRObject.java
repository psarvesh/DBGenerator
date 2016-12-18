package storage;

import com.sleepycat.persist.model.PrimaryKey;

public class PRObject{
	@PrimaryKey
	String url;
	
	String title;

	int pageRank;

	public PRObject(String url, String title, int pr){
		this.url = url;
		this.title = title;
		this.pageRank = pr;
	}

	public String getTitle(){
		return this.title;	
	}

	public int getPageRank(){
		return this.pageRank;
	}

}
