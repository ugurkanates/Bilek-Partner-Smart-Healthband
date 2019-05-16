#include <MPU6050_tockn.h>
#include <Wire.h>
#include "MAX30100_PulseOximeter.h"
#define SDA_PIN 21
#define SCL_PIN 22
MPU6050 mpu6050(Wire);


#include <WiFi.h>

#define WIFI_SSID "sinanelveren"
#define WIFI_PASSWORD "sinanelveren"


#include <Wire.h>
#include "SSD1306.h"


long timer = 0;
PulseOximeter pox;
int sample_count=0;
float val;
float sample_avg;
float tempAnalog;
int kalp_sample_count = 0 ;
float heartPrint;
 
uint32_t tsLastReport = 0;
float heartCurrent = 70;
const int analogIn = A0;
const int analogout = A3;
char package[256];
char wifiPackage[5][256]; 
int RawValue= 0;
double Voltage = 0;
double tempC = 0;
int volt = 0;

int wifiCounter = 0;
int ledCount = 0;


//Wifi client
const uint16_t port = 1379;
const char * host = "89.107.226.204"; // ip or dns


//OLED
SSD1306  display(0x3C, 21,22);


const int ledPin = 12;  // 16 corresponds to GPIO16

// setting PWM properties
const int freq = 2000;
const int ledChannel = 0;
const int resolution = 8;


void onBeatDetected()
{
Serial.println("Beat!");
}

void setup() {
    Serial.begin(9600);

    display.init();
    display.flipScreenVertically();
    display.setFont(ArialMT_Plain_16);
    display.setColor(WHITE);
    display.clear();
    screenOled(heartPrint, tempAnalog);
    
  
    Serial.print("Waiting for WiFi... ");
    WiFi.mode(WIFI_STA);
      
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("connecting");
       
    while (WiFi.status() != WL_CONNECTED) {
      Serial.print(".");
      wifiCounter++;
      delay(500);
      if(wifiCounter >= 5){
        break;
      }
    }
    wifiCounter = 0;

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());

    delay(100);
 
    // configure buzzer PWM functionalitites
    ledcSetup(ledChannel, freq, resolution);
    // attach the channel to the GPIO to be controlled
    ledcAttachPin(ledPin, ledChannel);

     buzzer();
     buzzer();

     Serial.print("Connecting to ");
     Serial.println(host);
  
 
    Serial.print("Initializing Pulse and 3-Eksen");
  
    Wire.begin(SDA_PIN,SCL_PIN);
    mpu6050.begin();
    mpu6050.calcGyroOffsets(true);
    Serial.println("GYRO is Calibrated");
    
     //kalp init
     setPulse();

}

void loop() {
  mpu6050.update();
  pox.update();

  float acX,acY,acZ,temp,heart,oksijen, gX, gY, gZ;
  sample_count++;
  kalp_sample_count++;
  val = analogRead(analogIn); //sicaklik
  sample_avg = sample_avg + (val - sample_avg) / sample_count;
 
  if (sample_count >= 4096){
     tempAnalog=(1.1 * sample_avg * 100.0) / 1024.0;

     sample_count = 0;
    
   }


   if(kalp_sample_count <= 100){
        heart = pox.getHeartRate();
        
        if(heart > 50 && heart <= 150){
            heartCurrent +=  heart;
            heartCurrent = heartCurrent /2;
            heartPrint = heartCurrent;
        }
   }
   else{
       heartPrint = heartCurrent;
   }
   
   

  if(millis() - timer > 400){
   // buzzer();
    kalp_sample_count = 0;



    Serial.println("\n=======================================================");
    temp = mpu6050.getTemp();
    acX = mpu6050.getAccX();
    acY = mpu6050.getAccY();
    acZ = mpu6050.getAccZ();
    gX =  mpu6050.getGyroAngleX();
    gY =  mpu6050.getGyroAngleY();
    gZ =  mpu6050.getGyroAngleZ();
    volt = getVolt();
    
    sprintf(package,"B_%lf_%lf_%lf_%lf_%lf_%lf_%lf_%lf_%d_%lf",acX,acY,acZ,gX,gY,gZ, heartPrint,tempAnalog,volt, temp);Serial.println(package);
/*    Serial.print("temp : ");Serial.println(temp);
    Serial.print("accX : ");Serial.print(acX);
    Serial.print("\taccY : ");Serial.print(acY);
    Serial.print("\taccZ : ");Serial.println(acZ);

    Serial.print("  Gx : ");
    Serial.print(gX);
    Serial.print("  Gy : ");
    Serial.print(gY);
    Serial.print("  Gz : ");
    Serial.print(gZ);
*/    
    Serial.print("  Heart rate:");
    Serial.println(heartPrint);


    Serial.print("\t Temperature in C = ");
    Serial.print(tempAnalog,1);
 
    timer = millis();

   
    sprintf(wifiPackage[wifiCounter],"%s", package);
    wifiCounter++;
    if(wifiCounter == 5){
        wifitest(acX, acY, acZ, heartPrint, tempAnalog);
        wifiCounter = 0;
    }
    
  }


}

void wifitest(float x,float y, float z, float h, float t){
 
  //buzzer();
  int res =0;
  delay(10);
   Serial.println();
    Serial.println();


    WiFiClient client;
    if(WiFi.status() == WL_CONNECTED){
      if ( !client.connect(host, port)) {
          Serial.println("Connection failed.");
          Serial.println("Waiting 5 seconds before retrying...");
          delay(5000);
          setPulse();
          return;
      }
    }
    
  //  sprintf(package,"B_%lf_%lf_%lf_%lf_%lf",x,y,z,h,t);
   
    res = getVolt();
    if(res == 1 || res == 2){
      buzzer();
      buzzer();
      buzzer();
      sprintf(package,"B_%lf_%lf_%lf_%lf_%lf_%d",x,y,z,h,t,res);
       Serial.println(package);
    }
      for(int i = 0; i<5; i++ ){
       client.println(wifiPackage[i]);      
      }
  
     Serial.println("Closing connection.");
    if(WiFi.status() == WL_CONNECTED )
      client.stop();
 
    Serial.println("\n***************************************\n");
 

  Serial.print("HEART: ");
  Serial.print(h);
  Serial.print("   TEMP: ");
  Serial.println(t);


  Serial.println("disconnected\n");


screenOled(heartPrint, tempAnalog);



  delay(100);

  setPulse();
  

}

void setPulse(){


//    pox.resume();
 if (!pox.begin()) {
    Serial.println("FAILED");
   // for(;;);
    } else {
    Serial.println("SUCCESS");
    }
    pox.setOnBeatDetectedCallback(onBeatDetected);  
}




void screenOled(float heart, float temp){

  int h = heart;
  int t = temp;
  
  ledCount++;
  
  display.setFont(ArialMT_Plain_16);
  
  display.setColor(WHITE);
  
  
  display.clear();
  
  char fst [] = " Bilek Partner\n";
  
  display.drawString(0,0,fst);
  display.setFont(ArialMT_Plain_24);
  
  char sec[15];
  sprintf(sec,"   %d ºC",t);
  char trd[15];
  sprintf(trd,"   %d bpm ",h);
  
  display.drawString(0,18,sec);
  display.drawString(0,40,trd);
  
  
  display.display();

  Serial.println(h);
  Serial.println(t);

  Serial.println("\n_________________________________________*___OLED__*_______________________\n\n");
}



int buzzer(){
  
    for (int i = 0; i<1; i++){
        ledcWrite(ledChannel, 100);
        delay(100);
         
        ledcWrite(ledChannel, 0);
        delay(5);
        
  //      ledcWrite(ledChannel, 100);
  //      delay(200);
        
        
   //     ledcWrite(ledChannel, 0);
   //     delay(1000);
    }
  
}

int getVolt(){
  float v = (float) analogRead(analogout)*5.0/ 1024.0;
  float res = v *10;
  if(res >=4.1 && res< 4.2)
    return 1;
  else if(res >= 3.2 && res <3.3)
  return 2;

  else 
    return 0;
    }
