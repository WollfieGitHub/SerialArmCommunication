package fr.wollfie.serial_arm_com.io;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

public class ArduinoSerial {

    private static Instant lastPacketTime = Instant.now();
    private static final Duration LIMITER = Duration.ofMillis(50);
    SerialPort activePort;
    SerialPort[] ports = SerialPort.getCommPorts();


    public void showAllPort() {
        int i = 0;
        for(SerialPort port : ports) {
            System.out.print(i + ". " + port.getDescriptivePortName() + " ");
            System.out.println(port.getPortDescription());
            i++;
        }
    }

    public void start(String descriptor) {
        activePort = SerialPort.getCommPort(descriptor);

        if (activePort.openPort())
            System.out.println(activePort.getPortDescription() + " port opened.");
        System.out.println(activePort.getPortDescription() + " Connected");

        activePort.addDataListener(new SerialPortDataListener() {

            @Override
            public void serialEvent(SerialPortEvent event) {
                int size = event.getSerialPort().bytesAvailable();
                byte[] buffer = new byte[size];
                event.getSerialPort().readBytes(buffer, size);
                for(byte b:buffer)
                    System.out.print((char)b);
            }

            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
        });
    }

    public void write(String data) {
        if (LIMITER.toMillis() < Duration.between(lastPacketTime, Instant.now()).toMillis()) {
            System.out.println(data);
            byte[] toWrite = data.getBytes(StandardCharsets.UTF_8);
            activePort.writeBytes(toWrite, toWrite.length);
            lastPacketTime = Instant.now();
        }
    }
}
