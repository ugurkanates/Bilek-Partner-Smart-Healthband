// the number of the LED pin
const int ledPin = 12;  // 16 corresponds to GPIO16

// setting PWM properties
const int freq = 2000;
const int ledChannel = 0;
const int resolution = 8;

int maximumRange = 50;
int minimumRange = 0;
int olcum = 0;
 
void setup(){
  // configure LED PWM functionalitites
  ledcSetup(ledChannel, freq, resolution);
  
  // attach the channel to the GPIO to be controlled
  ledcAttachPin(ledPin, ledChannel);
}
 
void loop(){
  // increase the LED brightness

 
   
  melodi();
  delay(20000);
 /* 
   for(int dutyCycle = 0; dutyCycle <= 255; dutyCycle++){   
    // changing the LED brightness with PWM

    
  }*/
  
/*
  // decrease the LED brightness
  for(int dutyCycle = 255; dutyCycle >= 0; dutyCycle-=20){
    // changing the LED brightness with PWM
    ledcWrite(ledChannel, dutyCycle);   
    delay(15);
  }
  */
} 
int melodi(){
  
    for (int i = 0; i<5; i++){
        ledcWrite(ledChannel, 100);
        delay(100);
         
        ledcWrite(ledChannel, 0);
        delay(150);
    
        
        ledcWrite(ledChannel, 100);
        delay(200);
        
        
        ledcWrite(ledChannel, 0);
        delay(1000);
    }
       
    
  
}


  
