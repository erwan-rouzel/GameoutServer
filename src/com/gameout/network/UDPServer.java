/*
public class UDPServer implements Runnable {
    public void run() {
        while (true) {
            DatagramPacket packet = socket.receive();
            new Thread(new Responder(socket, packet)).start();
        }
    }
}
*/