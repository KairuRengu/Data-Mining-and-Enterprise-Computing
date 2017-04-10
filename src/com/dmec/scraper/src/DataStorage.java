import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

public class DataStorage{
  public static final String folderPath = "../data";
  public DataStorage(){

  }

  public void writeBoxScore(String date, List<String> gameData){
    System.out.println(gameData.get(0).subSequence(0,gameData.get(0).indexOf(',')) + "_"
    + gameData.get(1).subSequence(0,gameData.get(1).indexOf(',')));
    try{
      String teams = gameData.get(0).subSequence(0,gameData.get(0).indexOf(',')) + "_"
      + gameData.get(1).subSequence(0,gameData.get(1).indexOf(','));
      File f = new File("../data/BoxScores/" + date + "_" + teams + ".txt");
      f.createNewFile();
      FileWriter fr = new FileWriter(f);
      BufferedWriter w = new BufferedWriter(fr);
      w.write(gameData.get(0));
      w.newLine();
      w.write(gameData.get(1));

      w.close();
      fr.close();
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  public List<String> readGameIDs(String filename){
    List<String> gameIDs = new ArrayList<String>();
    try{
      File f = new File("../data/" + filename);
      FileReader fr = new FileReader(f);
      BufferedReader r = new BufferedReader(fr);
      String line;
      while ((line = r.readLine()) != null){
        if (line.charAt(0) != '#'){
          gameIDs.add(line);
        }
      }

      r.close();
      fr.close();
    } catch(Exception e){
      e.printStackTrace();
    }
    return gameIDs;
  }

  public void writeGameIds(String date, List<String> gameIDs){
    try{
      File f = new File("../data/Games.txt");
      f.createNewFile();
      FileWriter fr = new FileWriter(f, true);
      BufferedWriter w = new BufferedWriter(fr);

      w.write("#" + date);
      w.newLine();
      for (String gameID : gameIDs){
        w.write(gameID);
        w.newLine();
      }

      w.close();
      fr.close();
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}
