package storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class PRObject{
	@PrimaryKey
	private String url = null;
	
	private String title = null;
	private String region = null;
	private String city = null;
	private String country = null;
	private double pageRank = 0.0;
	
	public PRObject(String url){
		this.url = url;
	}
	
	public PRObject(String url, String title, double pr, String region, String city, String country){
		this.url = url;
		this.title = title;
		this.region = region;
		this.city = city;
		this.country = country;
		this.pageRank = pr;
	}

	public void setURL(String url){
		this.url = url;
	}
	
	public void setTitle(String title){
		this.title = title;	
	}

	public void setPageRank(double pageRank){
		this.pageRank = pageRank;
	}
	
	public void setRegion(String region){
		this.region = region;
	}
	
	public void setCity(String city){
		this.city = city;
	}
	
	public void getCountry(String country){
		this.country = country;
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
