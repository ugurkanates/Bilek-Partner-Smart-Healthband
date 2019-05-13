#include <MPU6050_tockn.h>
#include <Wire.h>
#include "MAX30100_PulseOximeter.h"
#include <WiFi.h>
#include <WiFiMulti.h>
#define SDA_PIN 21
#define SCL_PIN 22
MPU6050 mpu6050(Wire);


long timer = 0;
PulseOximeter pox;
int sample_count=0;
float val;
float sample_avg;
float tempAnalog;
int kalp_sample_count = 0 ;
float heartPrint;

WiFiMulti WiFiMulti;
 
uint32_t tsLastReport = 0;
float heartCurrent = 70;
const int analogIn = A0;
 
int RawValue= 0;
double Voltage = 0;
double tempC = 0;



     WiFiClient client;
    const uint16_t port = 1379;
   const char * host = "89.107.226.204"; // ip or dns
   //Serial.print("Connecting to ");
  // Serial.println(host);




    
void onBeatDetected()
{
//Serial.println("Beat!");
}



void setup() {
  Serial.begin(9600);

  delay(10);
  
  WiFiMulti.addAP("sinanelveren", "sinanelveren");
    Serial.println();
    Serial.println();
    Serial.print("Waiting for WiFi... ");
   
    while(WiFiMulti.run() != WL_CONNECTED) {
        Serial.print(".");
        WiFiMulti.addAP("sinanelveren", "sinanelveren");
        delay(500);
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());





   
delay(1000);




    Serial.print("Initializing Pulse and 3-Eksen");


    Wire.begin(SDA_PIN,SCL_PIN);
    mpu6050.begin();
    mpu6050.calcGyroOffsets(true);
    Serial.println("Calibrated GYRO");
        //kalp init
        if (!pox.begin()) {
      Serial.println("FAILED");
      for(;;);
      } else {
      Serial.println("SUCCESS");
      }
      pox.setOnBeatDetectedCallback(onBeatDetected);

}

void loop() {

   
  mpu6050.update();
  pox.update();

  float acX,acY,acZ,temp,heart,oksijen;
   sample_count++;
   kalp_sample_count++;
   val = analogRead(analogIn); //sicaklik
   sample_avg = sample_avg + (val - sample_avg) / sample_count;
  if (sample_count >= 4096){
     tempAnalog=(1.1 * sample_avg * 100.0) / 1024.0;
     //Serial.print(tempAnalog+3,DEC);
     //Serial.println("º");
     sample_count = 0;
    // delay(1000); 
   }

    if(kalp_sample_count <= 1000){
        heart = pox.getHeartRate();
        
        if(heart > 50 && heart <= 150){
            heartCurrent +=  heart;
            heartCurrent = heartCurrent /2;
        }

   }
   else{
       //kalp_sample_count = 0;
       heartPrint = heartCurrent;
   }



  
   

  if(millis() - timer > 1000){

    kalp_sample_count = 0;


    Serial.println("=======================================================");
    temp = mpu6050.getTemp();
    acX = mpu6050.getAccX();
    acY = mpu6050.getAccY();
    acZ = mpu6050.getAccZ();
     // heartCurrent = heart;
      
    oksijen=pox.getSpO2();
    
    Serial.print("temp : ");Serial.println(temp);
    Serial.print("accX : ");Serial.print(acX);
    Serial.print("\taccY : ");Serial.print(acY);
    Serial.print("\taccZ : ");Serial.println(acZ);
    Serial.print("Heart rate:");
    Serial.print(heartPrint);
    Serial.print("bpm / SpO2:");
    Serial.print(oksijen);
    Serial.println("%");

   //   RawValue = analogRead(analogIn);
   // Voltage = (RawValue / 2048.0) * 3300; // 5000 to get millivots.
   // tempC = Voltage * 0.1;
 
    Serial.print("\t Temperature in C = ");
    Serial.print(tempAnalog,1);
 
    Serial.println("\n=======================================================\n\n\n");



 
  Serial.println("Connecting to host");
  //delay(1000);

       while (!client.connect(host, port)) {
        Serial.println("Connection failed.");
        //Serial.println("Waiting 5 seconds before retrying...");
        delay(5000);
       // return;
    }
/*

    char package[256];
       sprintf(package,"B_%lf_%lf_%lf_%lf_%lf",acX,acY,acZ,tempAnalog,heartPrint);
       client.println(package);
*/

     client.stop();


      
    timer = millis();
    
  }

}
