int sample_count;
float val;
float sample_avg;
float temp;

#include <MPU6050_tockn.h>
#include <Wire.h>
long timer = 0;

MPU6050 mpu6050(Wire);

void setup()
{
 Serial.begin(9600); // start serial communication
 analogReference(INTERNAL);
  Wire.begin();
  mpu6050.begin();
  mpu6050.calcGyroOffsets(true);
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
   
    Serial.println("=======================================================");
    Serial.print("temp : ");Serial.println(mpu6050.getTemp());
    Serial.print("accX : ");Serial.print(mpu6050.getAccX());
    Serial.print("\taccY : ");Serial.print(mpu6050.getAccY());
    Serial.print("\taccZ : ");Serial.println(mpu6050.getAccZ());
  
    Serial.print("gyroX : ");Serial.print(mpu6050.getGyroX());
    Serial.print("\tgyroY : ");Serial.print(mpu6050.getGyroY());
    Serial.print("\tgyroZ : ");Serial.println(mpu6050.getGyroZ());
  
  
    Serial.println("=======================================================\n");
    timer = millis();
    
  }
 
}
