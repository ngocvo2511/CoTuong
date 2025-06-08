package com.example.cotuong.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerDiscoveryClient {

    public static String findServerIp() {
        String serverIp = null;
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000); // timeout 3 giây
            socket.setBroadcast(true);

            String message = "FIND_XIANGQI_SERVER";
            byte[] buffer = message.getBytes();

            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8888);
            socket.send(packet);

            // Nhận phản hồi từ server
            byte[] recvBuf = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(receivePacket);

            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Phản hồi từ server: " + response);

            if (response.startsWith("XIANGQI_SERVER|")) {
                String[] parts = response.split("\\|");
                serverIp = parts[1]; // IP server
                String port = parts[2]; // cổng websocket (nếu cần)
                System.out.println("Tìm thấy server tại: " + serverIp + ":" + port);
            }
        } catch (Exception e) {
            System.out.println("Không tìm thấy server.");
        }
        return serverIp;
    }
}
