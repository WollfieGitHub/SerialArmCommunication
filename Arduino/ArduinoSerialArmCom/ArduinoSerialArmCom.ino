#include <HCPCA9685.h>

/* I2C slave address for the device/module. For the HCMODU0097 the default I2C address
   is 0x40 */
#define  I2CAdd 0x40
/* Create an instance of the library */
HCPCA9685 HCPCA9685(I2CAdd);

int servo_indices[4] = {4, 5, 2, 3};
int servo_targets[4] = {90, 90, 90, 90};
int servo_currents[4] = {90, 90, 90, 90};

const float step_size = 0.05;

void setup() {
  /* Initialise the library and set it to 'servo mode' */
  HCPCA9685.Init(SERVO_MODE);
  /* Wake the device up */
  HCPCA9685.Sleep(false);

  Serial.begin(9600);
  Serial.setTimeout(15);

  for(int i = 0; i < 4; i++) {
    HCPCA9685.Servo(servo_indices[i], servo_currents[i]);
  }
}

void loop() {

  /*
  turn_servo(0, 0 , 90, true);
  turn_servo(1, 0 , 90, true);
  turn_servo(2, 0 , 90, true);
  turn_servo(3, 0 , 90, true);

  turn_servo(3, 90 , 0, false);
  turn_servo(2, 90 , 0, false);
  turn_servo(1, 90 , 0, false);
  turn_servo(0, 90 , 0, false);
  */

  for(int i = 0; i < 4; i++) {
    int servo_step = step_size * servo_targets[i] + (1 - step_size) * servo_currents[i];
    servo_currents[i] = servo_step;
    HCPCA9685.Servo(servo_indices[i], servo_step);
  }
  delay(10);
}

void turn_servo(int servo_num, int initial_angle, int final_angle, bool growing) {
  if (growing) {
    for(int i = initial_angle ; i <= final_angle  ; i++){
      HCPCA9685.Servo(servo_num, i);
      delay(10);
    }
  } else {
    for (int i = initial_angle ; i >= final_angle  ; i--) {
      HCPCA9685.Servo(servo_num, i);
      delay(10);
    }
  }
}

void turn_to(int servo_idx, int angle) {
  servo_targets[servo_idx] = angle;
}

String serialData;

void serialEvent(){
  serialData = Serial.readString();

  turn_to(0, parseDataGrip(serialData));
  turn_to(1, parseDataWrist(serialData));
  turn_to(2, parseDataElbow(serialData));
  turn_to(3, parseDataShoulder(serialData));
  
}

int parseDataGrip(String data){
  data.remove(data.indexOf("W"));
  data.remove(data.indexOf("G"), 1);
  return data.toInt();
}

int parseDataWrist(String data){
  data.remove(data.indexOf("E"));
  data.remove(0, data.indexOf("W")+1);
  return data.toInt();
}

int parseDataElbow(String data){
  data.remove(data.indexOf("S"));
  data.remove(0, data.indexOf("E")+1);
  return data.toInt();
}

int parseDataShoulder(String data) {
  data.remove(0, data.indexOf("S")+1);
  return data.toInt();
}
