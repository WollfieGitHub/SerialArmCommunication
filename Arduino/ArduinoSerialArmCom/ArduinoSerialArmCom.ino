#include <HCPCA9685.h>

// Motor A connections
int enA = 9;
int in1 = 8;
int in2 = 7;
// Motor B connections
int enB = 3;
int in3 = 5;
int in4 = 4;



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

  // Set all the motor control pins to outputs
  pinMode(enA, OUTPUT);
  pinMode(enB, OUTPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);
  
  // Turn off motors - Initial state
  digitalWrite(in1, LOW);
  digitalWrite(in2, LOW);
  digitalWrite(in3, LOW);
  digitalWrite(in4, LOW);
  
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
  forward_with(parseDataMotorA(serialData), parseDataMotorB(serialData));  
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
  data.remove(data.indexOf("A"));
  data.remove(0, data.indexOf("S")+1);
  return data.toInt();
}

int parseDataMotorA(String data) {
  data.remove(data.indexOf("C"));
  data.remove(0, data.indexOf("A")+1);
  return data.toInt();
}

int parseDataMotorB(String data) {
  data.remove(0, data.indexOf("C")+1);
  return data.toInt();
}

void forward_with(int speed_a, int speed_b) {
  if(speed_a < 10 && speed_b < 10) {
    // Turn on motors
    digitalWrite(in1, LOW );
    digitalWrite(in2, LOW );
    digitalWrite(in3, LOW );
    digitalWrite(in4, LOW );
  } else {
    digitalWrite(in1, LOW );
    digitalWrite(in2, HIGH);
    digitalWrite(in3, LOW );
    digitalWrite(in4, HIGH);
    analogWrite(enA, speed_a);
    analogWrite(enB, speed_b);
  }

  Serial.print(speed_a);
  Serial.print(", ");
  Serial.println(speed_b);

  
}
