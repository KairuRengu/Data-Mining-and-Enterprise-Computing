import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class WebScraper{
    private WebDriver driver;
    private DataStorage dataStorage;
    private static String baseURL = "http://stats.nba.com";
    private static String scoreURN = "/scores/#!/";
    private static String gameURN = "/game/#!/";

    public WebScraper(WebDriver driver, DataStorage ds){
      this.driver = driver;
      this.dataStorage = ds;
    }

    public void collectGameScores(String filename){
      List<String> gameIDs = dataStorage.readGameIDs(filename);
      for (String gameID : gameIDs){
        scrapeGameScore(gameID);
      }
    }

    public void scrapeGameScore(String gameID){
      System.out.println("Scraping " + gameID);
      String URL = gameID;
      List<String> gameData = new ArrayList<String>();
      LocalDate date;
      int pointsA = 0;
      int pointsB = 0;

      driver.get(URL);
      try{
        Thread.sleep(1500);
      }catch(Exception e){

      }
      while (true){
        try{
          WebElement dateElement = driver.findElement(By.className("game-summary__date"));
          DateTimeFormatter f = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d, yyyy")
            .toFormatter();
          date = LocalDate.parse(dateElement.getText(), f);

          List<WebElement> teams = driver.findElements(By.className("nba-stat-table__caption"));
          gameData.add(teams.get(0).getText() + ",");
          gameData.add(teams.get(1).getText() + ",");

          List<WebElement> statContainers = driver.findElements(By.className("nba-stat-table"));
          int i = 0;
          for (WebElement containers : statContainers){
            List<WebElement> dataTable = containers.findElement(By.className("nba-stat-table__overflow"))
              .findElement(By.tagName("tfoot"))
              .findElements(By.tagName("td"));

            if (i == 0){
              pointsA = Integer.valueOf(dataTable.get(19).getText());
            }else{
              pointsB = Integer.valueOf(dataTable.get(19).getText());
            }
            gameData.set(i, gameData.get(i)
              + dataTable.get(19).getText() + ","
              + dataTable.get(20).getText() + ","
              + dataTable.get(4).getText() + ","
              + dataTable.get(13).getText());
            i++;
          }
          break;
        }catch (Exception e){

        }
      }
      gameData.set(0, gameData.get(0) + "," + String.valueOf(pointsA > pointsB));
      gameData.set(1, gameData.get(1) + "," + String.valueOf(pointsB > pointsA));
      dataStorage.writeBoxScore(date.toString(), gameData);
    }

    public void collectGameIds(LocalDate lastDay, LocalDate firstDay){
      LocalDate current = lastDay;
      String date;
      while (current.isAfter(firstDay) || current.equals(firstDay)){
        date = String.valueOf(current.getMonthValue()) + "/" + String.valueOf(current.getDayOfMonth()) + "/" + String.valueOf(current.getYear());
        scrapeGames(date);
        current = current.minusDays(1);
      }
    }

    public void scrapeGames(String date){
      System.out.println("Scraping " + date);
      String URL = baseURL + scoreURN + date;
      List<String> gameIDs = new ArrayList<String>();
      // System.out.println(URL);
      driver.get(URL);
      try{
        Thread.sleep(1500);
      }catch(Exception e){

      }
      while (true){
        try{
          WebElement scoreContainer = driver
              .findElement(By.id("scoresPage"))
              .findElement(By.className("linescores-container"));
          List<WebElement> scores = scoreContainer.findElements(By.className("linescores"));
          for (WebElement el : scores){
              WebElement link = el
                  .findElement(By.className("bottom-bar"))
                  .findElement(By.linkText("Box Score"));
              gameIDs.add(link.getAttribute("href"));
          }
          break;
        }catch(Exception e){

        }
      }
      dataStorage.writeGameIds(date, gameIDs);
    }
}
