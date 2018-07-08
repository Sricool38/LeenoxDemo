package LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.testng.annotations.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
public class Login {
	public WebDriver driver;
	public static String User,Pass;
	public static Map<String,String> homepagemap = new HashMap();
	public static Map<String,String> pdpmap = new HashMap();
	public static Map<String,String> activemap = new HashMap();
	@BeforeClass
	public void beforeMethod() throws Exception {
		driver = new ChromeDriver();
		File Cred = new File (System.getProperty("user.dir")+"/"+"Cred.xls");
		FileInputStream fs = new FileInputStream(Cred);
		HSSFWorkbook workbook = new HSSFWorkbook(fs);
	    HSSFSheet sheet = workbook.getSheet("Sheet1");
	    HSSFRow row = sheet.getRow(0);
	        int col_num_user = -1; 
	        int col_num_pass = -1;
	        for(int i=0; i < row.getLastCellNum(); i++)
	        {
	            if(row.getCell(i).getStringCellValue().trim().equals("UserName"))
	                col_num_user = i;
	            if(row.getCell(i).getStringCellValue().trim().equals("Password"))
	            	col_num_pass = i;
	        }
	        row = sheet.getRow(1);
	        HSSFCell cell = row.getCell(col_num_user);
	        User = cell.getStringCellValue();
	        HSSFCell cell1 = row.getCell(col_num_pass);
	        Pass = cell1.getStringCellValue();
		driver.get("https://www.liidaveqa.com/");
		driver.manage().window().maximize();
		Assert.assertEquals(driver.getTitle(), "Homepage | LennoxPROs.com");
	}
	
	@Test
	 public void Step1_login() throws Exception {
		System.out.println("Login to the page");
		driver.findElement(By.xpath("//a[contains(.,'Sign In')]")).click();
		Assert.assertEquals(driver.getTitle(), "Login | LennoxPROs.com");
		WebDriverWait wait = new WebDriverWait(driver, 20);
		By username = By.id("j_username");
		WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(username));
		element.sendKeys(User);
		driver.findElement(By.id("j_password")).sendKeys(Pass);
		driver.findElement(By.id("loginSubmit")).click();
		By welcome = By.xpath("//div[contains(text(),'Welcome,')]");
		wait.until(ExpectedConditions.presenceOfElementLocated(welcome));

	}
	
	@Test
	 public void Step2_homePageDetails() throws Exception {
			System.out.println("Getting the HomePageDetails");
			
			String productDesc = driver.findElement(By.cssSelector(".product-name")).getText();
			homepagemap.put("ProdDes",productDesc);
			System.out.println("Product Details :"+productDesc);
			WebDriverWait wait = new WebDriverWait(driver, 20);
			By Price =By.cssSelector("p.your-price");
			String productPrice = wait.until(ExpectedConditions.presenceOfElementLocated(Price)).getText().substring(12, 19);			
			homepagemap.put("ProdPrice",productPrice);
			System.out.println("productPrice :"+productPrice);
		}
	
	@Test
	public void Step3_NavigateToPDP() throws Exception{
		System.out.println("Navigating to PDP Page");
		driver.findElement(By.cssSelector(".product-name")).click();
		String PDPproductDesc = driver.findElement(By.cssSelector(".product-shop h1")).getText();
		pdpmap.put("ProdDes",PDPproductDesc);
		System.out.println("PDP Product Details :"+PDPproductDesc);
		
		String PDPproductPrice = driver.findElement(By.xpath("//*[contains(text(),'Your price:')]")).getText().substring(12, 19);
		pdpmap.put("ProdPrice",PDPproductPrice);
		System.out.println("PDPproductPrice :"+PDPproductPrice);
	}
	
	@Test
	public  void Step4_ValidateDetailsinPDP() throws Exception{
		System.out.println("Comparing the details of both the pages");
		boolean result = mapsAreEqual(homepagemap,pdpmap);
		Assert.assertTrue("Homepage and PDP details are not equal",result);
	}
	
	@Test
	public void Step5_NavigateToActivePage() throws Exception{	
		JavascriptExecutor js = (JavascriptExecutor) driver;
		 js.executeScript("window.scrollBy(0,400)");
		driver.findElement(By.cssSelector(".button-primary.button-large.btn-ajax")).click();
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".button-default.view-cart"))).click();
		String APproductDesc = driver.findElement(By.cssSelector("div.product-name a")).getText(); 
		System.out.println("Active Page Product Details :"+APproductDesc);
		String APPrice = driver.findElement(By.cssSelector(".totalprice-content span.cart-price")).getText();
		System.out.println("Total Price in Active page :"+APPrice);
		activemap.put("ProdDes", APproductDesc);
		activemap.put("ProdPrice", APPrice);
	}
	
	@Test
	public  void Step6_ValidateDetailsinActivePage() throws Exception{
		System.out.println("Comparing the details of both the pages");
		boolean result = mapsAreEqual(homepagemap,activemap);
		Assert.assertTrue("Homepage and ActivePage details are not equal",result);
	}
	
	public boolean mapsAreEqual(Map<String, String> homepagemap, Map<String, String> pdpmap) {
	try{
        for (String key : homepagemap.keySet())
        {
            if (!pdpmap.get(key).equalsIgnoreCase(homepagemap.get(key))) {
                return false;
            }

        } 
        for (String key : homepagemap.keySet())
        {
            if (!pdpmap.containsKey(key)) {
                return false;
            }
        } 
    } catch (NullPointerException np) {
        return false;
    }
    return true;
	}

	@AfterClass
	public void AfterMethod() throws Exception{
		System.out.println("Closing the driver");
		driver.quit();
	}
}
