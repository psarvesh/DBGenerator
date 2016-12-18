package storage;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.whois.WhoisClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;
import com.maxmind.geoip.Location;

@Entity
public class URLInfo implements Delayed, Serializable{
	private static final long serialVersionUID = 1L;
	private boolean secure;
	private String hostName=null;
	private int portNo = 80;
	private String filePath=null;
	private String query=null;
	private String fragment=null;
	public String body=null;
	private boolean isHTML = false;
	private boolean isXML = false;
	private boolean hasBody = false;
	private long startTime = -1;
	private long crawlDelay = -1;
	private String contentType = null;
	private String creationDate = null;
	private String expirationDate=null;
	private String updateDate = null;
	private String geoCountry=null;
	private String geoRegion=null;
	private String geoCity=null;
	private String protocol=null;
	private String urlURL = null;

	//private ArrayList<String> linkList = new ArrayList<String>();


	@PrimaryKey
	private String url;

	@SecondaryKey(relate = Relationship.ONE_TO_ONE)
	private String checkSum;

	public URLInfo(){
		this.startTime = System.currentTimeMillis();
	}

	public URLInfo(String docURL){
		this();
		if(docURL == null || docURL.equals(""))
			return;
		docURL = docURL.trim();
		URL urlTemp = null;
		try{
			urlTemp=new URL(docURL);
		} catch(Exception e){
			System.out.println("URL Exception :" + docURL); 
			return;
		}
		if(urlTemp.getProtocol().equals("http"))
			setSecure(false);
		
		else if(urlTemp.getProtocol().equals("https"))
			setSecure(true);			
		else
			return;
		
		protocol=urlTemp.getProtocol();
		hostName=urlTemp.getHost();
		portNo=urlTemp.getPort();
		if(secure)
			portNo=-1;
		else if(portNo == -1){
			portNo = 80;
		}
			
		filePath=urlTemp.getPath();
		query=urlTemp.getQuery();
		fragment=urlTemp.getRef();
	
		try{
			urlURL = urlTemp.toString();
			url = new URI(protocol, null, hostName, portNo, filePath, query, fragment).toString();
		} catch(Exception e){
			System.out.println("URI Exception: "+docURL);
			StringBuilder sb = new StringBuilder(protocol+"://"+hostName);
			if(secure){
				sb.append(filePath);
			} else {
				sb.append(":" + portNo + filePath);
			}
			if(query != null){
				sb.append("?" + query);
			}
			if(fragment != null){
				sb.append("#" + fragment);
			}
			url = sb.toString();
			urlURL = docURL;
		}
	}

	public URLInfo(String hostName, String filePath){
		this.hostName = hostName;
		this.filePath = filePath;
		this.portNo = 80;
	}

	public URLInfo(String hostName,int portNo,String filePath){
		this.hostName = hostName;
		this.portNo = portNo;
		this.filePath = filePath;
	}
	/*
	 *   URL functions
	 */
	public String getHostName(){
		return hostName;
	}
	
	public String getProtocol(){
		return protocol;
	}
	
	public void setProtocol(String s){
		this.protocol=s;
	}
	
	public void setHostName(String s){
		hostName = s;
	}

	public int getPortNo(){
		return portNo;
	}

	public void setPortNo(int p){
		portNo = p;
	}

	public String getFilePath(){
		return filePath;
	}

	public void setFilePath(String fp){
		filePath = fp;
	}

	public String toString(){
		return url;
	}

	public String toURLString(){
		return urlURL;
	}

	/*
	 *  Delayed functions
	 */
	public void setDelay(long delay){
		this.startTime = delay;
	}

	@Override
	public int compareTo(Delayed obj) {
		if (this.startTime < ((URLInfo) obj).startTime) {
			return -1;
		}
		if (this.startTime > ((URLInfo) obj).startTime) {
			return 1;
		}
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(this.startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	/*
	 *  Body related operations
	 */

	public String getCheckSum(){
		return checkSum;
	}

	public void setHTML(){
		setHasBody(true);
		isHTML = true;
		setXML(false);
	}

	public void setXML(){
		setHasBody(true);
		isXML = true;
		setHTML(false);
	}

	public String getBody(){
		return body;
	}

	public void setBody(String body){
		this.body = body;
		setHasBody(true);
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(body.getBytes());

	        byte bodyBytes[] = md.digest();

	        //convert the byte to hex format method
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < bodyBytes.length; i++) {
	         sb.append(Integer.toString((bodyBytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        checkSum = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Check the encryption algorithm. Exception : " + e.getMessage());
		}
	}

	public ArrayList<URLInfo> parse(){
		ArrayList<URLInfo> tempList = new ArrayList<URLInfo>();
		if(isHTML) {
			Document doc=Jsoup.parse(body);
		    Elements links = doc.select("a[href]");
		    for (Element link : links) {
		    	String s = link.attr("href");
	            if(s.startsWith("http://")||s.startsWith("https://")){
					tempList.add(new URLInfo(s.trim()));
					//linkList.add(s.trim());
				} else {
					StringBuffer toAdd;
					if(secure) toAdd = new StringBuffer("https://");
					else toAdd = new StringBuffer("http://");
					toAdd.append(this.getHostName() + ":" + this.getPortNo());
					toAdd.append(this.getFilePath());
					if(toAdd.toString().endsWith("/")){
						if(s.startsWith("/")){
							String x = s.substring(1);
							toAdd.append(x);
						} else {
							toAdd.append(s);
						}
					} else {
						if(s.startsWith("/")){
							toAdd.append(s);
						} else {
							toAdd.append("/"+s);
						}
					}
					tempList.add(new URLInfo(toAdd.toString().trim()));
					//linkList.add(toAdd.toString().trim());
				}
		    }
		    return tempList;
	    } else {
        	return tempList;
        }
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	private String getWhoisServer(String whois) {

        String result = "";
        Pattern pattern=Pattern.compile("Whois Server:\\s(.*)");;
        Matcher matcher = pattern.matcher(whois);

        // get last whois server
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }
    
    private String queryWithWhoisServer(String domainName, String whoisServer) {

        String result = "";
        WhoisClient whois = new WhoisClient();
        try {

            whois.connect(whoisServer);
            result = whois.query(domainName);
            whois.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        } 

        return result;

    }

	public boolean updateWhois(){

		WhoisClient client = new WhoisClient();
		StringBuilder data = new StringBuilder();
		try {
			client.connect(WhoisClient.DEFAULT_HOST);
			String line = client.query("=" + hostName);
			data.append(line);
			client.disconnect();
			String whoisServerUrl = getWhoisServer(line);
             if (!whoisServerUrl.equals("")) {
                String whoisData2 =queryWithWhoisServer(hostName, whoisServerUrl);
                data.append(whoisData2);
             }
			String[] lines = data.toString().split(System.getProperty("line.separator"));
			for(String lineOfLines : lines){
				if(lineOfLines.contains("Date:")){
					String[] splitLine = lineOfLines.split(":");
					if(splitLine[0].toLowerCase().trim().startsWith("creation")){
					 	setCreationDate(splitLine[1].trim());
					} else if(splitLine[0].toLowerCase().trim().startsWith("updated")){
						setUpdateDate(splitLine[1].trim());
					}
					 else if(splitLine[0].toLowerCase().trim().startsWith("registry expiry")){
						setExpirationDate(splitLine[1].trim());
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 Geolocation related functions
	*/
	public void setGeoCountry(String country)
	{
		this.geoCountry=country;
	}

	public void setGeoCity(String city)
	{
		this.geoCity=city;
	}

	public void setGeoRegion(String region)
	{
		this.geoRegion=region;
	}

	public String getGeoRegion()
	{
		return this.geoRegion;
	}

	public String getGeoCity()
	{
		return this.geoCity;
	}

	public String getGeoCountry()
	{
		return this.geoCountry;
	}

	public boolean updateGeoLocation()
	{
	 try{
	 	 InetAddress address = InetAddress.getByName(new URL(this.toString()).getHost());
         String ip = address.getHostAddress();
 		 LookupService cl = new LookupService("GeoLiteCity.dat",LookupService.GEOIP_MEMORY_CACHE);
		 Location locationServices = cl.getLocation(ip);
		 this.geoCountry=locationServices.countryName;
		 this.geoRegion=regionName.regionNameByCode(locationServices.countryCode, locationServices.region);
		 this.geoCity=locationServices.city;
		 return true;
		 }
		 catch(Exception e)
		 {
		 	e.printStackTrace();
			return false;
		 }
	}

	/*
	 *  Database related operations
	 */
	public boolean checkDB() {
		return false;
	}

	public void storeInDB() {

	}
	/*
	 *  Set and get for booleans
	 */
	public boolean isHTML() {
		return isHTML;
	}

	public void setHTML(boolean isHTML) {
		this.isHTML = isHTML;
	}

	public boolean isXML() {
		return isXML;
	}

	public void setXML(boolean isXML) {
		this.isXML = isXML;
	}

	public boolean isHasBody() {
		return hasBody;
	}

	public void setHasBody(boolean hasBody) {
		this.hasBody = hasBody;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public long getCrawlDelay() {
		return crawlDelay;
	}

	public void setCrawlDelay(long crawlDelay) {
		this.crawlDelay = crawlDelay;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
