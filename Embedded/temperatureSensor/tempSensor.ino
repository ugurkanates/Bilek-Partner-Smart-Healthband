int sample_count;
float val;
float sample_avg;
float temp;

void setup()
{
 Serial.begin(9600); // start serial communication
 analogReference(INTERNAL);
}

void loop()
{
 sample_count++;
 val = analogRead(A0);
 sample_avg = sample_avg + (val - sample_avg) / sample_count;

 if (sample_count >= 4096){
   temp=(1.1 * sample_avg * 100.0) / 1024.0;
   Serial.print(temp+3,DEC);
   Serial.println("º");
   sample_count = 0;
   delay(1000); 
 }
}
