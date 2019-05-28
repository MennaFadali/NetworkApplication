import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OtherHandler implements Runnable {
    int server;
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    String[] code;

    public OtherHandler(int server, Socket socket, DataInputStream dis, DataOutputStream dos, String[] code) {
        this.server = server;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
        this.code = code;
    }


    @Override
    public void run() {
        if (server == 1) {
            if (Server.clients.containsKey(code[1])) {
                try {
                    Server.clients.get(code[1]).getDos().writeUTF(code[0] + " : " + code[2]);
                    dos.writeUTF("Done");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    dos.writeUTF("NF");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } else {
            if (Server2.clients.containsKey(code[1])) {
                try {
                    Server2.clients.get(code[1]).getDos().writeUTF(code[0] + " : " + code[2]);
                    dos.writeUTF("Done");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    dos.writeUTF("NF");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}