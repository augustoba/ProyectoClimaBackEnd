package com.instituto.PROYECTOCLIMA.arduino;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class ArduinoUSBConnection {
    private SerialPort serialPort;
    private static final int BAUD_RATE = 9600;

    public boolean connect(String portName) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(BAUD_RATE);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("Conectado al puerto: " + portName);

            // Listener para datos entrantes
            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                        return;

                    byte[] buffer = new byte[serialPort.bytesAvailable()];
                    int bytesRead = serialPort.readBytes(buffer, buffer.length);
                    String data = new String(buffer, 0, bytesRead);
                    processData(data);
                }
            });

            return true;
        } else {
            System.err.println("Error al conectar con: " + portName);
            return false;
        }
    }

    private void processData(String data) {
        // Procesa los datos recibidos
        System.out.println("Dato recibido: " + data);

        // Ejemplo: "TEMP:25.5" → extraer valor
        if (data.startsWith("TEMP:")) {
            try {
                String tempStr = data.substring(5).trim();
                float temperatura = Float.parseFloat(tempStr);
                System.out.println("Temperatura: " + temperatura + "°C");

                // Aquí puedes guardar en BD, enviar a API, etc.
                saveToDatabase(temperatura);

            } catch (NumberFormatException e) {
                System.err.println("Error parseando temperatura: " + data);
            }
        }
    }

    private void saveToDatabase(float temperature) {
        // Tu lógica para guardar en base de datos
        System.out.println("Guardando en BD: " + temperature);
    }

    public void disconnect() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Desconectado");
        }
    }

    // Método para listar puertos disponibles
    public static void listAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Puertos disponibles:");
        for (SerialPort port : ports) {
            System.out.println("- " + port.getSystemPortName() + " : " + port.getDescriptivePortName());
        }
    }

    public static void main(String[] args) {
        // Listar puertos disponibles
        listAvailablePorts();

        ArduinoUSBConnection arduino = new ArduinoUSBConnection();

        // Conectar (ajusta el nombre del puerto)
        String portName = "COM3"; // Windows
        // String portName = "/dev/ttyUSB0"; // Linux
        // String portName = "/dev/cu.usbmodem14101"; // Mac

        if (arduino.connect(portName)) {
            System.out.println("Escuchando datos... Presiona Enter para salir");

            try {
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
            }

            arduino.disconnect();
        }
    }
}

