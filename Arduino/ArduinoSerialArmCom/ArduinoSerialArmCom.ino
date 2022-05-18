#include <HCPCA9685.h>

#define motor1_pin1 8
#define motor1_pin2 9
#define motor1_speed_pin 5

#define motor2_pin1 10
#define motor2_pin2 11
#define motor2_speed_pin 6

int engineSpeed = 210;

/* I2C slave address for the device/module. For the HCMODU0097 the default I2C address
   is 0x40 */
#define  I2CAdd 0x40
/* Create an instance of the library */
HCPCA9685 HCPCA9685(I2CAdd);

const int NB_SERVOS = 5;

int servo_indices[NB_SERVOS] = {0, 1, 2, 3, 4};
int servo_targets[NB_SERVOS] = {90, 90, 90, 90, 90};
int servo_currents[NB_SERVOS] = {90, 90, 90, 90, 90};

const float step_size = 0.05;

void setup() {

  //DEFINE PIN MODES AND SERVO
  pinMode(motor1_pin1, OUTPUT);
  pinMode(motor1_pin2, OUTPUT);
  pinMode(motor1_speed_pin, OUTPUT);

  pinMode(motor2_pin1, OUTPUT);
  pinMode(motor2_pin2, OUTPUT);
  pinMode(motor2_speed_pin, OUTPUT);
  
  /* Initialise the library and set it to 'servo mode' */
  HCPCA9685.Init(SERVO_MODE);
  /* Wake the device up */
  HCPCA9685.Sleep(false);

  Serial.begin(9600);
  Serial.setTimeout(15);

  for(int i = 0; i < NB_SERVOS; i++) {
    HCPCA9685.Servo(servo_indices[i], servo_currents[i]);
  }
}

void loop() {

  for(int i = 0; i < NB_SERVOS; i++) {
    int servo_step = step_size * servo_targets[i] + (1 - step_size) * servo_currents[i];
    servo_currents[i] = servo_step;
    HCPCA9685.Servo(servo_indices[i], servo_step);
  }
  delay(10);
}

void turn_to(int servo_idx, int angle) {
  servo_targets[servo_idx] = angle;
}

String serialData;

void serialEvent(){
  serialData = Serial.readString();

  turn_to(0, parseDataBase(serialData));
  turn_to(1, parseDataShoulder(serialData));
  turn_to(2, parseDataElbow(serialData));
  turn_to(3, parseDataWrist(serialData));
  turn_to(4, parseDataGrip(serialData));

  Serial.println(parseDataGrip(serialData));  
}

int parseDataBase(String data) {
  data.remove(data.indexOf("G"));
  data.remove(data.indexOf("B"), 1);
  return data.toInt();
}

int parseDataGrip(String data){
  data.remove(data.indexOf("W"));
  data.remove(0, data.indexOf("G")+1);
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
