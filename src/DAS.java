import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class DAS {

    private static final int BROADCAST_INTERVAL_MS = 1000; // Interval for broadcasting

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java DAS <port> <number>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            int number = Integer.parseInt(args[1]);
            runDAS(port, number);
        } catch (NumberFormatException e) {
            System.err.println("Both <port> and <number> must be integers.");
        }
    }

    private static void runDAS(int port, int number) {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println("Master mode activated on port " + port);
            runMaster(socket, port, number);
        } catch (SocketException e) {
            System.out.println("Master socket unavailable. Running in slave mode.");
            runSlave(port, number);
        }
    }

    private static void runMaster(DatagramSocket socket, int port, int initialNumber) {
        List<Integer> receivedNumbers = new ArrayList<>();
        receivedNumbers.add(initialNumber);

        try {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                int receivedNumber;

                try {
                    receivedNumber = Integer.parseInt(message);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number received: " + message);
                    continue;
                }

                if (receivedNumber == -1) {
                    System.out.println("Received -1. Broadcasting and shutting down.");
                    broadcast(socket, port, "-1");
                    break;
                } else if (receivedNumber == 0) {
                    double average = calculateAverage(receivedNumbers);
                    System.out.println("Calculated average: " + average);
                    broadcast(socket, port, String.valueOf(average));
                } else {
                    System.out.println("Received: " + receivedNumber);
                    receivedNumbers.add(receivedNumber);
                }
            }
        } catch (IOException e) {
            System.err.println("Error in master mode: " + e.getMessage());
        } finally {
            socket.close();
            System.out.println("Master socket closed.");
        }
    }

    private static void runSlave(int port, int number) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = String.valueOf(number);
            byte[] data = message.getBytes();
            InetAddress localAddress = InetAddress.getLocalHost();

            DatagramPacket packet = new DatagramPacket(data, data.length, localAddress, port);
            socket.send(packet);
            System.out.println("Slave sent: " + message + " to port " + port);
        } catch (IOException e) {
            System.err.println("Error in slave mode: " + e.getMessage());
        }
    }

    private static void broadcast(DatagramSocket socket, int port, String message) {
        try {
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddress, port);

            socket.setBroadcast(true);
            socket.send(packet);
            System.out.println("Broadcasted: " + message);
        } catch (IOException e) {
            System.err.println("Error broadcasting message: " + e.getMessage());
        }
    }

    private static double calculateAverage(List<Integer> numbers) {
        int sum = 0;
        int count = 0;

        for (int num : numbers) {
            if (num != 0) {
                sum += num;
                count++;
            }
        }

        return count > 0 ? (double) sum / count : 0.0;
    }
}
