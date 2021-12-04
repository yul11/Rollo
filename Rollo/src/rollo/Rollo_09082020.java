import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;

public class Rollo implements Runnable {

	final GpioPinDigitalInput  s1;
	final GpioPinDigitalOutput m1;
	final GpioPinDigitalOutput m2;
	String e = ".";
	String i = "..";
	String t = "-";
	long start;
	long stop;
	long pressTime;  
	long releaseTime; 
	boolean doRun = true; 
	private StringBuilder letter;
	final int TB = 200;
	private Thread viewStp;
	boolean keyReleased = false;
	long curT=0;
	long elapsedTime=0;
	String name;


	public Rollo(String name, Pin p1,Pin p2,Pin p3){
		InitBoard brd = new InitBoard(p1,p2,p3);
		viewStp = new Thread(this);
		viewStp.start();
		s1 = brd.get_s1();           
		m1 = brd.get_m1(); 
		m2 = brd.get_m2();
		letter = new StringBuilder();
		s_read();
		this.name = name;       
	}


	public void motorDown(){		       
		PinState m2_state = m2.getState();        
		if (m2_state == PinState.HIGH){        
			m2.setState(PinState.LOW);
			try {
				System.out.println("juo: " + name + " -> motorDown, waiting for 3000 ms because State of m2 is HIGH"); 
				Thread.sleep(3000);				
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("juo: " + name + " down"); 
		m1.setState(true);             
	}

	public void motorUp(){		      
		PinState m1_state = m1.getState();        
		if (m1_state == PinState.HIGH){        
			m1.setState(PinState.LOW); 
			try {
				System.out.println("juo: " + name + " -> motorUp, waiting for 3000 ms because State of m1 is HIGH"); 
				Thread.sleep(3000);				
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("juo: " + name + " up");  
		m2.setState(true); 
	}

	public void motorStop(){
		System.out.println("juo: " + name + " stop");        
		m1.setState(false);
		m2.setState(false);  
	}    




	public void s_read(){
		
		s1.addListener(new GpioPinListenerDigital() {

			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

				char tone = ' ';
				keyReleased = false;






				if(event.getState().isLow()){
					
					start = System.currentTimeMillis();
					releaseTime = start-stop;

					if (releaseTime >= TB*3){	
						System.out.println("juo: " + name + " -> delete letter because button was released too long, releaseTime: "  + releaseTime + " ****************************************");                              
						if (letter.length()>0){
							letter.delete(0,letter.length());
						}
					}
					if (releaseTime < TB/2){
						System.out.println("juo: " + name + " -> delete letter because button was released too short, releaseTime: " + releaseTime + " ****************************************");
						if (letter.length()>0){
							letter.delete(0,letter.length());
						}                            
					}  				
                         	                  
					System.out.println("juo: " + name + " -> negative edge: " + letter);
				}






				else{

					if(event.getState().isHigh()){	
																		
						stop = System.currentTimeMillis();
						pressTime = stop-start;
						
						
						if (pressTime < TB/2){
							System.out.println("juo: " + name + " -> delete letter because button was pressed too short, pressTime: " + pressTime + " ****************************************\n");
							if (letter.length()>0)
								letter.delete(0,letter.length());
						}
						else{  

                                  
							if (pressTime >= TB/2 && pressTime < TB){ 
								tone = '.';
								letter.append(tone);
							}
							else{
								if (pressTime >= TB && pressTime < TB*3){
									tone = '-';
									letter.append(tone);
								}
								else{
									System.out.println("juo: " + name + " -> delete letter because button was pressed too long, pressTime: " + pressTime + " ****************************************");
									if (letter.length()>0){
										letter.delete(0,letter.length());
									}                                                     
								}
							}
						}

						System.out.println("juo: " + name + " -> positive edge: " + letter);
						keyReleased = true;
					}
				}
			}   
		});
	}

	public void run() {     

		while (doRun){

			if (keyReleased){

				if (e.equals(letter.toString())){
					motorDown();
				}  
				if (i.equals(letter.toString())){
					motorUp();
				} 
				if (t.equals(letter.toString())){
					motorStop(); 
				}
				keyReleased=false;
			}



			try {
				Thread.sleep(TB*5);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}      
}

