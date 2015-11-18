package com.gameout.network;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

public class GameoutClient {
    private static final String SERVER_IP = "195.154.123.213";
    private static final int TCP_PORT = 9875;
    private static final int UDP_PORT = 9876;

    private InetAddress ipAddress;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;

    public GameoutClient() throws IOException {
        ipAddress = InetAddress.getByName(SERVER_IP);
        tcpSocket = new Socket(ipAddress, TCP_PORT);
        udpSocket = new DatagramSocket();
    }

    public void sendMessageTCP(String message) throws IOException {
        OutputStream output = tcpSocket.getOutputStream();
        output.write(GameoutUtils.stringToBytes(message));

        BufferedReader input = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        System.out.println("FROM TCP SERVER:" + input.readLine());

        tcpSocket.close();
    }

    public void sendMessageUDP(String message) throws IOException {
        byte[] sendData = message.getBytes(Charset.forName("UTF-8"));
        byte[] receiveData = new byte[1024];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, UDP_PORT);
        udpSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM UDP SERVER:" + modifiedSentence);
        udpSocket.close();
    }
}
