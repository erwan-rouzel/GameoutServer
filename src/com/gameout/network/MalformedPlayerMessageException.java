package com.gameout.network;

/**
 * Created by erwan on 21/11/2015.
 */
public class MalformedPlayerMessageException extends Exception {
    public byte[] receivedMessage;

    public MalformedPlayerMessageException(byte[] message) {
        receivedMessage = message;
    }

    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < receivedMessage.length; i++) {
            result += "\nreceivedMessage["+ i +"]=" + receivedMessage[i];
        }
        return "Received message:" + result + "\n" + super.toString();
    }
}
