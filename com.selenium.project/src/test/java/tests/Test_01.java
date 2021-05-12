package tests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.opencsv.CSVWriter;

public class Test_01 {
	
	WebDriver driver;
	List<String> imageUrl = new ArrayList<String>();
	List<String> hotelNames = new ArrayList<String>();
	List<String> hotelPhones = new ArrayList<String>();
	List<String> hotelAddress = new ArrayList<String>();
	List<String> hotelArea = new ArrayList<String>();
	
	@Test(priority = 0)
	public void test1() throws IOException {
		
		System.out.println("This is test");
		System.setProperty("webdriver.chrome.driver",".\\chromedriver.exe"); 
		ChromeOptions options = new ChromeOptions();
		//options.addArguments("--headless");
	    //driver=new ChromeDriver(options);  
	    driver=new ChromeDriver();
	    driver.get("https://downtowndallas.com/experience/stay/");
	    driver.manage().window().maximize();
	    
	    List<WebElement> list = driver.findElements(By.xpath("//section[@class='places']//a"));
	    List<String> hotelLink = new ArrayList<String>();
	    
		for(WebElement l : list) {
			hotelNames.add(l.getText());			
			String link = l.getAttribute("href");
			hotelLink.add(link);		   
		}
		
		WebElement phone, address, area;
		for(int i = 0; i<hotelLink.size(); i++) {
			driver.get(hotelLink.get(i));
			try {
			address = driver.findElement(By.xpath("//div[@class='place-info-address']/a[not(contains(@href,'https'))]"));
			hotelAddress.add(address.getText());
			area = driver.findElement(By.xpath("//div[@class='place-info-address']/a[contains(@href,'https')]"));
			hotelArea.add(address.getText());
			phone = driver.findElement(By.xpath("//div[@class='place-info']/div//a[contains(@href,'tel:')]"));
			hotelPhones.add(phone.getText());
			}
			catch (Exception e) {
				hotelPhones.add("");
			}
		    String imageInfo = driver.findElement(By.xpath("//div[@class='place-info-image']/img")).getAttribute("src");
		    imageUrl.add(imageInfo);
		}
		saveImage();
		writeCSV();
		
	}
	
	@AfterTest
	public void tearDown() {
		driver.close();
		driver.quit();
	}
	
	
	private void saveImage() {
		BufferedImage image =null;
		int j = 0;
        try{
        	for(String i : imageUrl) {
        		 URL url =new URL(i);
                 image = ImageIO.read(url);
                 ImageIO.write(image, "jpg",new File("./Images/"+hotelNames.get(j++)+".jpg"));
        	}
        }catch(IOException e){
            e.printStackTrace();
        }
	}
	
	private void writeCSV() throws IOException {
	    try {
	    	File file = new File("./Data.csv");
	        FileWriter outputfile = new FileWriter(file);
	        CSVWriter writer = new CSVWriter(outputfile);
	        List<String[]> data = new ArrayList<String[]>();
	        data.add(new String[] { "Name", "ImageUrl", "Phone", "Address" , "Area"});
	        int k = 0;
	        for(String hName : hotelNames) {
        		data.add(new String[] { hName , imageUrl.get(k), hotelPhones.get(k), hotelAddress.get(k), hotelArea.get(k)});
	        	k++;
	        }
	        writer.writeAll(data);
	        writer.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
