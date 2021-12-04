import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;

public class InitBoard_Rollo {

	final GpioPinDigitalInput  s1;
	final GpioPinDigitalOutput m1;
	final GpioPinDigitalOutput m2;
	private GpioController gpio;	


	public InitBoard_Rollo(Pin p1, Pin p2, Pin p3){
		gpio = GpioFactory.getInstance();		
		s1 = gpio.provisionDigitalInputPin(p1,  "switch", PinPullResistance.PULL_UP);		
		m1 = gpio.provisionDigitalOutputPin(p2, "motor1",PinState.LOW);
		m2 = gpio.provisionDigitalOutputPin(p3, "motor2",PinState.LOW);
	}

	public GpioPinDigitalInput get_s1(){
		return this.s1;
	}
	
	public GpioPinDigitalOutput get_m1(){
		return this.m1;
	}
	
	public GpioPinDigitalOutput get_m2(){
		return this.m2;
	}	
}

