/////////////////////////////////
// Generated with a lot of love//
// with TUNIOT FOR ESP32     //
// Website: Easycoding.tn      //
/////////////////////////////////
#include <Wire.h>
#include "SSD1306.h"

/*Gebze Technical University*/

SSD1306  display(0x3C, 21,22);
//Download the esp8266-oled-ssd1306 library from http://easycoding.tn/index.php/resources/
//SDA -> D21
//SCL -> D22

void setup()
{
Serial.begin(9600);

display.init();
display.flipScreenVertically();


/*
  d

 // display.setTextAlignment(TEXT_ALIGN_LEFT);
  display.setFont(ArialMT_Plain_16);
  display.drawString(0, 0, "Hello World!");
  display.display();
  delay(1000);
 // display.setTextAlignment(TEXT_ALIGN_LEFT);
  //display.setFont(ArialMT_Plain_10);
  display.clear();
  display.drawString(0, 0, "espp");
  display.display();
  display.clear();
*/
}


void loop()
{
float h = 45.6;
float t = 60.3;
int i=0;
int count = 0;
while (1) {
count++;

  display.setFont(ArialMT_Plain_16);

display.setColor(WHITE);

//display.setCursor(10, 0);

display.clear();

char fst [] = " Bilek Partner\n";

display.drawString(0,0,fst);
 display.setFont(ArialMT_Plain_24);

char sec[15];
sprintf(sec,"   %d ºC",count);
char trd[15];
sprintf(trd,"   %d bpm ",count);

display.drawString(0,18,sec);
display.drawString(0,40,trd);
i++;
//Serial.print(char(i));
Serial.println(i);

display.display();
//printText();
delay(250);
//display.clear();

}


}
