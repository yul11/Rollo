package rollo;

import com.pi4j.io.gpio.RaspiPin;


public class Start_Rollo {

    public static void main(String[] args) throws InterruptedException {
        
        new Rollo("Rollo1", RaspiPin.GPIO_00, RaspiPin.GPIO_01, RaspiPin.GPIO_02);
        new Rollo("Rollo2", RaspiPin.GPIO_03, RaspiPin.GPIO_04, RaspiPin.GPIO_05);

   }
}
