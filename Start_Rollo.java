import com.pi4j.io.gpio.RaspiPin;


public class Start_Rollo {

    public static void main(String[] args) throws InterruptedException {
 
        new Rollo("Rollo1", RaspiPin.GPIO_00, RaspiPin.GPIO_06, RaspiPin.GPIO_21);                                                //Eingang Pin 11
        new Rollo("             Rollo2", RaspiPin.GPIO_01, RaspiPin.GPIO_22, RaspiPin.GPIO_23);                                   //Eingang Pin 12
        
        
        new Licht("                             Licht1", RaspiPin.GPIO_02, RaspiPin.GPIO_24);   
        new Licht("                                             Licht2", RaspiPin.GPIO_03, RaspiPin.GPIO_25); 
        new Licht("                                                             Licht3", RaspiPin.GPIO_04, RaspiPin.GPIO_26);    
        new Licht("                                                                             Licht4", RaspiPin.GPIO_05, RaspiPin.GPIO_27);    
           
  

   }
}
