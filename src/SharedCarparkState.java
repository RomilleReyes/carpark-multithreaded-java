import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class SharedCarparkState{
	
	private SharedCarparkState mySharedObj;
	private String myThreadName;
	private static double mySharedVariable;
	private boolean accessing=false; // true a thread has a lock, false otherwise
	private int threadsWaiting=0; // number of waiting writers
	
	
	//initiate variables
	private ArrayList<String> mySharedQueue;
	

// Constructor	
	
	SharedCarparkState(double SharedVariable, ArrayList<String> queue) {
		mySharedVariable = SharedVariable;
		mySharedQueue = queue;
	}

//Attempt to aquire a lock
	
	  public synchronized void acquireLock() throws InterruptedException{
	        Thread me = Thread.currentThread(); // get a ref to the current thread
	        System.out.println(me.getName()+" is attempting to acquire a lock!");	
	        ++threadsWaiting;
		    while (accessing) {  // while someone else is accessing or threadsWaiting > 0
		      System.out.println(me.getName()+" waiting to get a lock as someone else is accessing...");
		      //wait for the lock to be released - see releaseLock() below
		      wait();
		    }
		    // nobody has got a lock so get one
		    --threadsWaiting;
		    accessing = true;
		    System.out.println(me.getName()+" got a lock!"); 
		  }

		  // Releases a lock to when a thread is finished
		  
		  public synchronized void releaseLock() {
			  //release the lock and tell everyone
		      accessing = false;
		      notifyAll();
		      Thread me = Thread.currentThread(); // get a ref to the current thread
		      System.out.println(me.getName()+" released a lock!");
		  }
	
	
    /* The processInput method */

	public synchronized String processInput(String myThreadName, String theInput) {
    		System.out.println(myThreadName + " received "+ theInput);
    		String theOutput = null;
    		// Check what the client said
    		if (theInput.equalsIgnoreCase("Do my action!")) {
    			//Correct request
    			if (myThreadName.equals("EntranceClient1")) {
    				
    				
    				/*  Add 20 to the variable
    					multiply it by 5
    					divide by 3.
    				 
    				mySharedVariable = mySharedVariable + 20;
       				mySharedVariable = mySharedVariable * 5;
       				mySharedVariable = mySharedVariable / 3;
       				*/
       				
       				//System.out.println("THis is EntranceClient1");
   				//System.out.println(myThreadName + " made the SharedVariable " + mySharedVariable);
    				//theOutput = "Do action completed.  Shared Variable now = " + mySharedVariable;
    				if (ISCarparkFull() == false) {
    					mySharedVariable = mySharedVariable + 1;
        				System.out.println("Car entered from Entrance 1");
        				System.out.println("The number of cars in the carpark is now " + mySharedVariable);
        				theOutput = "Car has entered from Entrance 1";
    				}
    				else {
    					mySharedQueue.add(myThreadName);
    					theOutput =  "The carpark is full. Please wait." + " The line is " + mySharedQueue.size() + " long";
    					//System.out.println("The Carpark is full. Please wait");
    					
    					/*
    					try {
    						
							acquireLock();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
						}
						*/
    					
    				}
    				
    				
    				
    			}
    			else if (myThreadName.equals("EntranceClient2")) {
    				/*	Subtract 5 from the variable
    					Multiply it by 10 
    					Divide by 2.5
    					
       				mySharedVariable = mySharedVariable - 5;
       				mySharedVariable = mySharedVariable * 10;
       				mySharedVariable = mySharedVariable / 2.5;
    					
       				System.out.println("THis is EntranceClient2");
    				System.out.println(myThreadName + " made the SharedVariable " + mySharedVariable);
    				theOutput = "Do action completed.  Shared Variable now = " + mySharedVariable;
    				*/
    				mySharedVariable =+ 1;
    				System.out.println("Car entered from Entrance 2");
    				System.out.println("The number of cars in the carpark is now " + mySharedVariable);
    				
    				theOutput = "EntranceClient2 is done";

    			}
       			else if (myThreadName.equals("ExitClient1")) {
       				/*	Subtract 50
						Divide by 2
						Multiply by 33
       				 
       				mySharedVariable = mySharedVariable - 50;
       				mySharedVariable = mySharedVariable / 2;
       				mySharedVariable = mySharedVariable * 33;
       				
       				System.out.println("THis is ExitClient1");
       				System.out.println(myThreadName + " made the SharedVariable " + mySharedVariable);
    				theOutput = "Do action completed.  Shared Variable now = " + mySharedVariable;
    				*/
       				if(ISCarparkEmpty() == true) {
       					System.out.println("Carpark is empty");
       					theOutput = "The carpark is empty";
       				}
       				else{
       					mySharedVariable = mySharedVariable - 1;
        				System.out.println("Car leaving from Exit 1");
        				System.out.println("The number of cars in the carpark is now " + mySharedVariable);
        				//releaseLock();
        				theOutput = "A car is leaving, the next car may now enter";

       				}
       				
       				

       			}
       			else if (myThreadName.equals("ExitClient2")) {
    				/*	Multiply by 20
						Divide by 10
						Subtract 1
    				 
       				mySharedVariable = mySharedVariable * 20;
       				mySharedVariable = mySharedVariable / 10;
       				mySharedVariable = mySharedVariable - 1;
       				System.out.println("THis is ExitClient2");
    				System.out.println(myThreadName + " made the SharedVariable " + mySharedVariable);
    				theOutput = "Do action completed.  Shared Variable now = " + mySharedVariable;
    				*/
       				
       				mySharedVariable =- 1;
    				System.out.println("Car entered from Entrance 1");
    				System.out.println("The number of cars in the carpark is now " + mySharedVariable);
    				
    				theOutput = "ExitClient2 done";
       			}
       			else {System.out.println("Error - thread call not recognised.");}
    		}
    		else { //incorrect request
    			theOutput = myThreadName + " received incorrect request - only understand \"Do my action!\"";
		
    		}
 
     		//Return the output message to the ActionServer
    		System.out.println(theOutput);
    		return theOutput;
    	}	
	public static boolean ISCarparkFull() {
		if (mySharedVariable == 5) {
			return true;
		}
		else {
			return false;
		}
	}
	public static boolean ISCarparkEmpty() {
		if(mySharedVariable == 0) {
			return true;
		}
		else {
			return false;
		}
	}
}