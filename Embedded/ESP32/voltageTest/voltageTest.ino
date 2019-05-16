const int analogIn = A0;

/*
  ReadAnalogVoltage

  Reads an analog input on pin 0, converts it to voltage, and prints the result to the Serial Monitor.
  Graphical representation is available using Serial Plotter (Tools > Serial Plotter menu).
  Attach the center pin of a potentiometer to pin A0, and the outside pins to +5V and ground.

  This example code is in the public domain.

  http://www.arduino.cc/en/Tutorial/ReadAnalogVoltage
*/

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
}

// the loop routine runs over and over again forever:
void loop() {
 
float  VBAT =   50.0f * 2048.0f * float(analogRead(A0)) / 1024.0f ; 
 float VBAT2 = 3.7f  *  float(analogRead(A0))  ;

 // float VBAT = map(float(analogRead(A0)),0,1023,0,5000)/1000;
  //float VBAT = (127.0f/100.0f) * 3.30f * float(analogRead(A0)) / 4096.0f;  // LiPo battery
  //  Serial.print("Battery Voltage = "); 
    Serial.println(VBAT, 3);
    Serial.println(VBAT2, 3);
    Serial.println(" V");   

    delay(100);
}
