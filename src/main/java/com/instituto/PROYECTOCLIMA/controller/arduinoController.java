package com.instituto.PROYECTOCLIMA.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/test")
public class arduinoController {
    private SerialPort serialPort;
    private StringBuilder dataBuffer = new StringBuilder();
    private volatile boolean receivingData = false;

    // 🔹 ENDPOINT PRINCIPAL - Para ver en navegador
    @GetMapping("")
    public String paginaInicio() {
        return """
            <html>
            <head>
                <title>🔧 Test Arduino</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .button { 
                        background: #007bff; color: white; padding: 10px 15px; 
                        text-decoration: none; border-radius: 5px; margin: 5px;
                        display: inline-block;
                    }
                    .success { background: #28a745; }
                    .danger { background: #dc3545; }
                    .container { margin: 20px 0; padding: 15px; border: 1px solid #ddd; }
                </style>
            </head>
            <body>
                <h1>🔧 Test de Conexión Arduino</h1>
                
                <div class="container">
                    <h3>📡 Puertos Disponibles</h3>
                    <a class="button" href="/api/test/puertos">Ver Puertos</a>
                </div>
                
                <div class="container">
                    <h3>🔄 Conexión</h3>
                    <p><strong>Conectar a COM3:</strong> 
                    <a class="button success" href="/api/test/conectar/COM3">Conectar COM3</a></p>
                    
                    <p><strong>Conectar a COM4:</strong> 
                    <a class="button success" href="/api/test/conectar/COM4">Conectar COM4</a></p>
                    
                    <p><strong>Conectar a COM5:</strong> 
                    <a class="button success" href="/api/test/conectar/COM5">Conectar COM5</a></p>
                </div>
                
                <div class="container">
                    <h3>📊 Estado y Datos</h3>
                    <a class="button" href="/api/test/estado">Ver Estado</a>
                    <a class="button danger" href="/api/test/desconectar">Desconectar</a>
                </div>
                
                <div class="container">
                    <h3>💡 Instrucciones:</h3>
                    <ol>
                        <li>Haz clic en "Ver Puertos" para ver los puertos disponibles</li>
                        <li>Conecta al puerto correcto (COM3, COM4, etc.)</li>
                        <li>Los datos del Arduino aparecerán en la consola de Spring Boot</li>
                        <li>Usa "Ver Estado" para ver el estado de la conexión</li>
                    </ol>
                </div>
            </body>
            </html>
            """;
    }

    // 🔹 LISTAR PUERTOS
    @GetMapping(value = "/puertos", produces = MediaType.TEXT_PLAIN_VALUE)
    public String listarPuertos() {
        System.out.println("🔍 Listando puertos disponibles...");

        StringBuilder response = new StringBuilder();
        response.append("📡 PUERTOS DISPONIBLES:\n\n");

        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            response.append("❌ No se encontraron puertos seriales\n");
        } else {
            for (SerialPort port : ports) {
                response.append("→ ")
                        .append(port.getSystemPortName())
                        .append(" - ")
                        .append(port.getDescriptivePortName())
                        .append("\n");
            }
        }

        response.append("\n🔗 Usa: http://localhost:8080/api/test/conectar/COM3");
        return response.toString();
    }

    // 🔹 CONECTAR
    @GetMapping(value = "/conectar/{puerto}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String conectarArduino(@PathVariable String puerto) {
        System.out.println("🔄 Intentando conectar a: " + puerto);

        try {
            // Cerrar conexión anterior si existe
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }

            serialPort = SerialPort.getCommPort(puerto);
            serialPort.setBaudRate(9600);
            serialPort.setNumDataBits(8);
            serialPort.setNumStopBits(1);
            serialPort.setParity(SerialPort.NO_PARITY);

            if (serialPort.openPort()) {
                System.out.println("✅ Conectado exitosamente a: " + puerto);
                receivingData = true;

                // Configurar listener para ver datos en consola
                configurarListener();

                return "✅ CONEXIÓN EXITOSA\n\n" +
                        "Puerto: " + puerto + "\n" +
                        "Estado: Conectado\n" +
                        "Baud Rate: 9600\n\n" +
                        "📨 Los datos del Arduino aparecerán en la consola de Spring Boot\n\n" +
                        "🔗 <a href='/api/test'>Volver al inicio</a>";
            } else {
                System.out.println("❌ No se pudo conectar a: " + puerto);
                return "❌ ERROR: No se pudo conectar al puerto " + puerto + "\n\n" +
                        "🔗 <a href='/api/test/puertos'>Ver puertos disponibles</a>";
            }

        } catch (Exception e) {
            System.out.println("💥 Error conectando: " + e.getMessage());
            return "💥 ERROR: " + e.getMessage() + "\n\n" +
                    "🔗 <a href='/api/test'>Volver al inicio</a>";
        }
    }

    private void configurarListener() {
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    try {
                        byte[] buffer = new byte[serialPort.bytesAvailable()];
                        int bytesRead = serialPort.readBytes(buffer, buffer.length);
                        String rawData = new String(buffer, 0, bytesRead);

                        // Mostrar en consola
                        mostrarDatosEnConsola(rawData);

                    } catch (Exception e) {
                        System.out.println("❌ Error leyendo datos: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void mostrarDatosEnConsola(String rawData) {
        dataBuffer.append(rawData);
        String bufferContent = dataBuffer.toString();

        // Buscar líneas completas
        if (bufferContent.contains("\n")) {
            String[] lines = bufferContent.split("\\r?\\n");

            for (int i = 0; i < lines.length - 1; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    // 📨 Esto se verá en la consola de Spring Boot
                    System.out.println("📨 ARDUINO >>> " + line);
                }
            }

            dataBuffer = new StringBuilder();
            if (lines.length > 0) {
                dataBuffer.append(lines[lines.length - 1]);
            }
        }
    }

    // 🔹 ESTADO
    @GetMapping(value = "/estado", produces = MediaType.TEXT_PLAIN_VALUE)
    public String obtenerEstado() {
        boolean conectado = serialPort != null && serialPort.isOpen();

        String estado = "🔌 ESTADO DE CONEXIÓN\n\n" +
                "Conectado: " + (conectado ? "✅ SI" : "❌ NO") + "\n" +
                "Puerto: " + (conectado ? serialPort.getSystemPortName() : "No conectado") + "\n" +
                "Recibiendo datos: " + (receivingData ? "✅ SI" : "❌ NO") + "\n" +
                "Buffer actual: " + dataBuffer.toString() + "\n\n" +
                "🔗 <a href='/api/test'>Volver al inicio</a>";

        return estado;
    }

    // 🔹 DESCONECTAR
    @GetMapping(value = "/desconectar", produces = MediaType.TEXT_PLAIN_VALUE)
    public String desconectar() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            receivingData = false;
            System.out.println("🔌 Desconectado de Arduino");
            return "🔌 DESCONECTADO\n\nConexión cerrada exitosamente\n\n" +
                    "🔗 <a href='/api/test'>Volver al inicio</a>";
        }
        return "❌ No había conexión activa\n\n" +
                "🔗 <a href='/api/test'>Volver al inicio</a>";
    }

    // 🔹 DIAGNOSTICO COMPLETO DE PUERTOS
    @GetMapping(value = "/diagnostico", produces = MediaType.TEXT_PLAIN_VALUE)
    public String diagnosticoCompleto() {
        System.out.println("🔍 INICIANDO DIAGNÓSTICO COMPLETO...");

        StringBuilder diagnostico = new StringBuilder();
        diagnostico.append("=== DIAGNÓSTICO ARDUINO ===\\n\\n");

        SerialPort[] ports = SerialPort.getCommPorts();
        diagnostico.append("📡 PUERTOS ENCONTRADOS: ").append(ports.length).append("\\n\\n");

        if (ports.length == 0) {
            diagnostico.append("❌ NO SE ENCONTRARON PUERTOS SERIALES\\n");
            diagnostico.append("• Verifica que el Arduino esté conectado por USB\\n");
            diagnostico.append("• Reinicia el Arduino\\n");
            diagnostico.append("• Prueba en otro puerto USB\\n");
        } else {
            for (int i = 0; i < ports.length; i++) {
                SerialPort port = ports[i];
                diagnostico.append("→ ").append(port.getSystemPortName())
                        .append(" - ").append(port.getDescriptivePortName())
                        .append("\\n");

                // Intentar abrir y cerrar para probar
                boolean puedeAbrir = port.openPort();
                diagnostico.append("   Puede abrir: ").append(puedeAbrir ? "✅ SI" : "❌ NO").append("\\n");
                if (puedeAbrir) {
                    port.closePort();
                }
                diagnostico.append("\\n");
            }
        }

        // Información del sistema
        diagnostico.append("💻 INFORMACIÓN DEL SISTEMA:\\n");
        diagnostico.append("• OS: ").append(System.getProperty("os.name")).append("\\n");
        diagnostico.append("• Arquitectura: ").append(System.getProperty("os.arch")).append("\\n");
        diagnostico.append("• Versión Java: ").append(System.getProperty("java.version")).append("\\n");

        System.out.println(diagnostico.toString());
        return diagnostico.toString();
    }
}