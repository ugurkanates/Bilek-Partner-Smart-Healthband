String blueToothVal;           //value sent over via bluetooth
String lastValue;              //stores last state of device (on/off)
 
void setup()
{
 Serial.begin(9600); 
 pinMode(13,OUTPUT);
}
 
 
void loop()
{
  while(Serial.available())
  {//while there is data available on the serial monitor
    blueToothVal+=char(Serial.read());//store string from serial command
  }
  if (blueToothVal=="1")
  {//if value from bluetooth serial is 1
    digitalWrite(13,HIGH);            //switch on LED
    if (lastValue!="1")
      Serial.println(F("LED is on")); //print LED is on
    lastValue=blueToothVal;
    blueToothVal="";
    Serial.println(F("Gebze Technical University")); //print info
  }
  else if (blueToothVal=="0")
  {//if value from bluetooth serial is 0
    digitalWrite(13,LOW);             //turn off LED
    if (lastValue!="0")
      Serial.println(F("LED is off")); //print LED is off
    lastValue=blueToothVal;
    blueToothVal="";
    Serial.println(F("Gebze Technical University")); //print info
  }
  
  delay(1000);
}
