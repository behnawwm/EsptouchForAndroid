package com.espressif.esptouch.android.main;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * UDP sender and receiver.
 */
public class UDP {

    private String host;
    private int port;
    private DatagramSocket udpSocket;
    private InetAddress serverAddr;

    /**
     * UDP constructor.
     * @param host the ip address of remote host.
     * @param port the port of remote host.
     * @param timeoutMs the timeout for the socket in milliseconds.
     */
    public UDP(String host, int port, int timeoutMs){
        this.host = host;
        this.port = port;
        try {
            udpSocket = new DatagramSocket(port);
            udpSocket.setSoTimeout(timeoutMs);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            serverAddr = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts input Object to array of byte.
     * @param object the input object.
     * @return the array of byte.
     */
    public byte[] objectToBytes(Object object){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts the input array of byte in Object.
     * @param data the input array of byte.
     * @return the Object.
     */
    public Object bytesToObject(byte[] data){
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send Object.
     * @param obj the Object to send.
     * @return true if sent, false otherwise.
     */
    public boolean send(Object obj){
        byte[] data = objectToBytes(obj);
        if (data == null){
            return false;
        }
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, port);
        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Send data as byte array.
     * @param data the byte array.
     * @return true if sent, false otherwise.
     */
    public boolean send(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, port);
        try {
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Receive an Object.
     * @return the object received.
     */
    public Object receive(){
        byte[] data = new byte[8000];
        DatagramPacket packet = new DatagramPacket(data,data.length);
        try {
            udpSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bytesToObject(data);
    }

    /**
     * Receive data as byte array.
     * @param data the buffer where received data will be written.
     * @return the received data.
     */
    public byte[] receive(byte[] data){
        DatagramPacket packet = new DatagramPacket(data,data.length);
        try {
            udpSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    /**
     * Close the socket.
     */
    public void close(){
        udpSocket.close();
    }
}
