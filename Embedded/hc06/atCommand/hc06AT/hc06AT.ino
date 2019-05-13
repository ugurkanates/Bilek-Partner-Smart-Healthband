#include <SoftwareSerial.h>// import the serial library

SoftwareSerial mySerial(10, 11); // RX, TX

int ledpin=13; // led on D13 will show blink on / off

String BluetoothData; // the data given from Computer

void setup() {
  
  Serial.begin(4800);
  
  Serial.println("Type AT commands!"); // put your setup code here, to run once:
  
  mySerial.begin(9600);
  
  Serial.println("Bluetooth On please press 1 or 0 blink LED ..");
  
  pinMode(ledpin,OUTPUT);
    
}

void loop() {
/*
  if (Serial.available())
    mySerial.write(Serial.read());

  if (mySerial.available())
    Serial.write(mySerial.read());
*/
  while (Serial.available()){
      BluetoothData += char(Serial.read());
  }
  if (BluetoothData != ""){
      mySerial.println(BluetoothData);
      mySerial.println("  : Gebze Technical University, Bilek Partner ");
      BluetoothData = "";
  }


  //check here for led
  if(BluetoothData=="1") { // if number 1 pressed ….
  
      digitalWrite(ledpin,1);
  
      mySerial.println("LED  On D13 ON ! ");
  
    }
  
    if (BluetoothData=="0") { // if number 0 pressed ….
  
    digitalWrite(ledpin,0);
    
    mySerial.println("LED  On D13 Off ! ");
    
    }

    
  // put your main code here, to run repeatedly:

  while (mySerial.available()){
      BluetoothData += char(mySerial.read());
  }
  if (BluetoothData != ""){
      mySerial.println(BluetoothData);
      mySerial.println("  : Gebze Technical University, Bilek Partner ");
      BluetoothData = "";
  }
/*  
  if (mySerial.available()) {
  
    BluetoothData=mySerial.read();
  
    if(BluetoothData=="1") { // if number 1 pressed ….
  
      digitalWrite(ledpin,1);
  
      Serial.println("LED  On D13 ON ! ");
  
    }
  
    if (BluetoothData=="0") { // if number 0 pressed ….
  
    digitalWrite(ledpin,0);
    
    Serial.println("LED  On D13 Off ! ");
    
    }
  
  }
*/  
  delay(100);// prepare for next data …

}
