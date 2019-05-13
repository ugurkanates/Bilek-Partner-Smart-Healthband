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
int sample_count=1;
float val;
float sample_avg;
float tempAnalog;

WiFiMulti WiFiMulti;

 
uint32_t tsLastReport = 0;
float heartCurrent = 0;
const int analogIn = A0;
 
int RawValue= 0;
double Voltage = 0;
double tempC = 0;

void onBeatDetected()
{
Serial.println("Beat!");
}

void setup() {
  Serial.begin(115200);
  Serial.print("Initializing Pulse and 3-Eksen and WIFI and Bluetooth?");
  delay(10);
  WiFiMulti.addAP("internet_hayrati", "1234567890");
   Serial.println();
    Serial.println();
    Serial.print("Waiting for WiFi... ");
    while(WiFiMulti.run() != WL_CONNECTED) {
        Serial.print(".");
        delay(500);
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    delay(500);

  Wire.begin(SDA_PIN,SCL_PIN);
  mpu6050.begin();
  mpu6050.calcGyroOffsets(true);
  Serial.println("3Ekseninitledi");
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
   const uint16_t port = 1379;
   const char * host = "89.107.226.204"; // ip or dns
   Serial.print("Connecting to ");
   Serial.println(host);


  mpu6050.update();
  pox.update();

  float acX,acY,acZ,temp,heart,oksijen;
   sample_count++;

   val = analogRead(analogIn); //sicaklik
   
   sample_avg = sample_avg + (val - sample_avg) / sample_count;
  if (sample_count >= 4096){
     tempAnalog=(1.1 * sample_avg * 100.0) / 1024.0;
     //Serial.print(tempAnalog+3,DEC);
     //Serial.println("º");
     sample_count = 1;
    // delay(1000); 
   }
   Serial.print("analog deger");
   Serial.println(tempAnalog);
  if(millis() - timer > 1000){
    
    Serial.println("=======================================================");
    temp = mpu6050.getTemp();
    acX = mpu6050.getAccX();
    acY = mpu6050.getAccY();
    acZ = mpu6050.getAccZ();
    heart = pox.getHeartRate();
    if(heart > 50 && heart <= 150)
      heartCurrent = heart;
      
    oksijen=pox.getSpO2();
    
    Serial.print("temp : ");Serial.println(temp);
    Serial.print("accX : ");Serial.print(acX);
    Serial.print("\taccY : ");Serial.print(acY);
    Serial.print("\taccZ : ");Serial.println(acZ);
    Serial.print("Heart rate:");
    Serial.print(heartCurrent);
    Serial.print("bpm / SpO2:");
    Serial.print(oksijen);
    Serial.println("%");

 
   //   RawValue = analogRead(analogIn);
   // Voltage = (RawValue / 2048.0) * 3300; // 5000 to get millivots.
   // tempC = Voltage * 0.1;
 
    Serial.print("\t Temperature in C = ");
    Serial.print(tempAnalog,1);
       WiFiClient client;
    if (!client.connect(host, port)) {
        Serial.println("Connection failed.");
        Serial.println("Waiting 5 seconds before retrying...");
        delay(5000);
        return;
    }
    char package[256];
       sprintf(package,"B_%lf_%lf_%lf_%lf_%lf",acX,acY,acZ,temp,heart);
       client.println(package);
    /*client.print("B");    //Bilek Partner
    client.print("_");  
    client.print(acX);    //x
    client.print("_");    
    client.print(acY);//y
    client.print("_");
    client.print(acZ);    //z
    client.print("_");
    client.print(tempAnalog);    //temp
    client.print("_");
    client.print(heart);   //pulse
    client.print("-");*/
     Serial.println("Closing connection.");
    client.stop();
 
    Serial.println("=======================================================\n");
    timer = millis();
    
  }

       // This will send a request to the server
       

    Serial.println("Waiting 5 seconds before restarting...");
    delay(1000); // 5000 yyerine yaptim  

}
