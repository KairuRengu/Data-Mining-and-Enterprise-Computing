package org.jboss.as.quickstarts.rshelloworld;

public class TaskService {
	
	 String test(int task) {
		 if (task ==0)
	        return "Hello!";
		 else
			 return "Goodbye!";
	    }
	 
	 String fight(String teamA, String teamB){
		 return teamA;
	 }
}
