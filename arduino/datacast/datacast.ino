#include <SoftwareSerial.h>

#define RX 3
#define TX 2

#define LED 13
#define POT 0

#include <MeetAndroid.h>

MeetAndroid meetAndroid;

SoftwareSerial BTSerial(RX, TX);


void setup()
{
  pinMode(LED, OUTPUT);
  pinMode(POT, INPUT); // There is no other mode for pins A0-A5, so no config needed
  
  digitalWrite(LED, LOW);
  // Software serial HC-05 setup (Easier for frecuent flashes and debug ;))
//  BTSerial.begin(57600);
//  BTSerial.flush();
  delay(500);
  
  // Regular Serial is for debug 
  Serial.begin(57600);
  //Serial.println("Setup Done. Go on!");
}

void loop()
{
  /*
  if (BTSerial.available()){
    char command = BTSerial.read();
    BTSerial.flush();
    Serial.println(command);
  }*/
  
  int pot_status;
  pot_status = analogRead(POT);
  /*
  BTSerial.write(pot_status);
  */
  meetAndroid.receive();
  meetAndroid.send(pot_status);

  delay(100);
}
