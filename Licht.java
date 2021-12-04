import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;

public class Licht implements Runnable{

	final GpioPinDigitalInput  s1;
	final GpioPinDigitalOutput m1;
	boolean keyReleased = true;
	String e = ".";		//Licht ein/aus
	String i = "..";	//alle Lichter ein
	String t = "-";		//alle Lichter aus
	boolean investigate_pause=false;
	boolean pause_valid = false;
	long start;
	long stop;
	long pressTime; 					//contains duration of time as long as rollo-switch was pressed 
	long releaseTime;					//contains duration of time as long as rollo-switch was released 
	long timeStampPause;				//contains duration of pause, while nothing was pressed
	long start_pause;					//contains  timeStamp when pause was started
	boolean doRun = true; 
	private StringBuilder letter;
	final int TB = 200;					//contains timebase
	private Thread viewStp;
	long curT=0;
	long elapsedTime=0;
	String name;	


	public Licht(String name, Pin p1,Pin p2){
		InitBoard_Licht brd = new InitBoard_Licht(p1,p2);
		viewStp = new Thread(this);
		viewStp.start();
		s1 = brd.get_s1();           
		m1 = brd.get_m1();
		letter = new StringBuilder();
		this.name = name; 
		s_read();      
	}


	public void lampe_ein(){		       
		PinState m1_state = m1.getState();        
		if (m1_state == PinState.HIGH){        
			m1.setState(PinState.LOW);
			try {
				System.out.println("juo: " + name + " -> lampe_ein, waiting for 1000 ms because State of m1 is HIGH"); 
				Thread.sleep(1000);				
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("juo: " + name + " Lampe ein");
		m1.setState(PinState.LOW);             
	}



	public void lampe_aus(){
		PinState m1_state = m1.getState();        
		if (m1_state == PinState.LOW){        
			m1.setState(PinState.HIGH); 
			try {
				System.out.println("juo: " + name + " -> lampe_aus, waiting for 1000 ms because State of m1 is LOW"); 
				Thread.sleep(1000);				
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("juo: " + name + " Lampe aus");  
		m1.setState(PinState.HIGH); 
	}




	public void lampeAlle_aus(){
		System.out.println("juo: " + name + " Lampe ALLE aus");        
		m1.setState(false);
	}   
	
 

	public void s_read(){
		
		s1.addListener(new GpioPinListenerDigital() {
		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				char tone = ' ';
				if(event.getState().isLow()){
					
					start = System.currentTimeMillis();
					releaseTime = start-stop;
					investigate_pause = false;
					System.out.println("juo: negative edge set investigat_pause  false false false false false");
					if (releaseTime >= TB*3){						
						System.out.println("juo: " + name + " -> delete letter because button was released too long, releaseTime: "  + releaseTime + " ****************************************");                              								
					}
					if (releaseTime < TB/2){
						System.out.println("juo: " + name + " -> delete letter because button was released too short, releaseTime: " + releaseTime + " ****************************************");
					}  				                        	                  
					System.out.println("juo: " + name + " -> negative edge: " + letter);
				}
				else{
					if(event.getState().isHigh()){					
						stop = System.currentTimeMillis();
						pressTime = stop-start;
						start_pause       = stop;
						investigate_pause = true;
						System.out.println("juo: positive edge set investigat_pause true true true true true");
						if (pressTime < TB/2){
							System.out.println("juo: " + name + " -> delete letter because button was pressed too short, pressTime: " + pressTime + " ****************************************\n");
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
								}
							}
						}
						System.out.println("juo: " + name + " -> positive edge: " + letter);
					}
				}
			}   
		});	
	} 

	public void run() {     

		while (doRun){
						
			while (investigate_pause){
				
				timeStampPause = System.currentTimeMillis();
				
				if ((timeStampPause - start_pause) >= TB*3){
					pause_valid = true;
					investigate_pause = false;
				}
			}
			if (pause_valid){

				if (e.equals(letter.toString())){
					lampe_ein();
				}  
				if (i.equals(letter.toString())){
					lampe_aus();
				} 
				if (t.equals(letter.toString())){
					lampeAlle_aus(); 
				}
				pause_valid=false;
				investigate_pause=false;
				letter.delete(0,letter.length());				
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
