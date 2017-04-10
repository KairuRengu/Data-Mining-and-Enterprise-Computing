package org.jboss.as.quickstarts.rshelloworld;

import com.dmec.dtree.Dmec;

public class TaskService {
	
	 String test(int task) {
		 if (task ==0)
	        return "Hello!";
		 else
			 return "Goodbye!";
	    }
	 
	 String fight(String teamA, String teamB){
		 Dmec dmec = new Dmec();
		 String result = String.valueOf(dmec.doCARTPrediction("C:\\Users\\norr5300\\Downloads\\data_train.txt"));
		 return result;
	 }
	 
	 void cart(String teamA, String teamB){
		 System.out.println(teamA);
		 Dmec dmec = new Dmec();
		 dmec.doCART(teamA,teamB);//"Toronto Raptors", "Milwaukee Bucks");
	 }
}
