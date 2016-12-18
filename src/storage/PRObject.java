package storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class PRObject{
	@PrimaryKey
	String url;
	
	String title;
	String region;
	String city;
	String country;
	double pageRank;
	

	public PRObject(String url, String title, double pr, String region, String city, String country){
		this.url = url;
		this.title = title;
		this.region = region;
		this.city = city;
		this.country = country;
		this.pageRank = pr;
	}

	public String getURL(){
		return this.url;
	}
	
	public String getTitle(){
		return this.title;	
	}

	public double getPageRank(){
		return this.pageRank;
	}
	
	public String getRegion(){
		return this.region;
	}
	
	public String getCity(){
		return this.city;
	}
	
	public String getCountry(){
		return this.country;
	}

}
