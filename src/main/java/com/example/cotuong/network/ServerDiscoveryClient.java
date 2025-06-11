package com.example.cotuong.network;

import java.net.*;
import java.util.Enumeration;

public class ServerDiscoveryClient {

    public static String findServerIp() {
        String serverIp = null;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            socket.setBroadcast(true);

            String message = "FIND_XIANGQI_SERVER";
            byte[] buffer = message.getBytes();

            // Gửi broadcast đến tất cả địa chỉ broadcast của các card mạng đang hoạt động
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) continue;

                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcast, 8888);
                        socket.send(packet);
                        System.out.println("Đã gửi broadcast tới: " + broadcast.getHostAddress());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // Nhận phản hồi từ server
            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Phản hồi từ server: " + response);

            if (response.startsWith("XIANGQI_SERVER|")) {
                String[] parts = response.split("\\|");
                serverIp = parts[1]; // IP server
                String port = parts[2]; // port (nếu cần)
                System.out.println("Tìm thấy server tại: " + serverIp + ":" + port);
            }

        } catch (Exception e) {
            System.out.println("Không tìm thấy server.");
        }
        return serverIp == null ? "" : serverIp;
    }
}
