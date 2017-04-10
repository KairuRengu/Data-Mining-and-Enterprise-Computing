
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args){
        try {
          DataStorage ds = new DataStorage();
          WebDriver driver = new FirefoxDriver();
          WebScraper scraper = new WebScraper(driver, ds);
          driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
          scraper.collectGameIds(LocalDate.parse("2017-03-31"), LocalDate.parse("2017-03-25"));
          scraper.collectGameScores("Games.txt");
          // scraper.collectGameScores("Games-JAN.txt");
        } catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
