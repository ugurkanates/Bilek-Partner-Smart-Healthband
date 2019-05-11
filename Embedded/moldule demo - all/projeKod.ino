#include <SoftwareSerial.h>// import the serial library

int sample_count;
float val;
float sample_avg;
float temp;

#include <MPU6050_tockn.h>
#include <Wire.h>
long timer = 0;

MPU6050 mpu6050(Wire);

//bluetooth
SoftwareSerial mySerial(10, 11); // RX, TX


void setup()
{
 Serial.begin(9600); // start serial communication
 analogReference(INTERNAL);
  Wire.begin();
  mpu6050.begin();
  mpu6050.calcGyroOffsets(true);

  //bluetooth
  Serial.begin(4800);
  
  Serial.println("Type AT commands!"); // put your setup code here, to run once:
  
  mySerial.begin(9600);
  
  Serial.println("Bluetooth sends sensor's data");
}


//sicaklik, ivme,
void loop()
{
 mpu6050.update();
 sample_count++;
 val = analogRead(A0); //sicaklik
 sample_avg = sample_avg + (val - sample_avg) / sample_count;

/*
 if (sample_count >= 350){
   temp=(1.1 * sample_avg * 100.0) / 1024.0;
   Serial.print(temp+3,DEC);
   Serial.println("ºC");
   sample_count = 0;
 }
*/
  if(millis() - timer > 1000){

   temp=(1.1 * sample_avg * 100.0) / 1024.0;
   Serial.print(temp+3,DEC);
   Serial.println("ºC");
   sample_count = 0;
   float tempString  = mpu6050.getTemp();
   float acx= mpu6050.getAccX();
   float acy= mpu6050.getAccY();
   float acz= mpu6050.getAccZ();
    mySerial.println("  : Gebze Technical University, Bilek Partner ");
    mySerial.println("=======================================================");
    mySerial.print("Sicaklik Sensoru :  ");mySerial.println(tempString);
    mySerial.print("X eksenindeki ivme degeri : ");mySerial.println(acx);
    mySerial.print("Y eksenindeki ivme degeri : ");mySerial.println(acy);
    mySerial.print("Z eksenindeki ivme degeri : ");mySerial.println(acz);

  
    mySerial.println("=======================================================\n");
    timer = millis();

     
    
     delay(500);
    
  }
 
}
