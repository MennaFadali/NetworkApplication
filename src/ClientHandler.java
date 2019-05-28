import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private Socket socket;
    private String name;
    private int server;

    public ClientHandler(Socket client, DataInputStream dis, DataOutputStream dos, int num) {
        this.socket = client;
        this.dis = dis;
        this.dos = dos;
        this.server = num;

    }


    ArrayList<String> getMembers() throws IOException {
        ArrayList<String> ans = new ArrayList<>();

        int port = 1234;
        if (server == 1) {
            for (String cur : Server.clients.keySet())
                ans.add(cur);
            port = 6789;
        } else {
            for (String cur : Server2.clients.keySet())
                ans.add(cur);
        }
        Socket s = new Socket("localhost", port);
        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF("#");
        String respnse = din.readUTF();
        String[] tmp = respnse.split("#");
        for (String cur : tmp) {
            if (cur.length()>0) ans.add(cur);
        }

        return ans;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    void Chat(String source, String destination, int TTL, String message) throws IOException {
        int port = 6789;
        if (server == 1) {
            if (Server.clients.containsKey(destination)) {
                Server.clients.get(destination).dos.writeUTF(name + " : " + message);
                return;
            }
        } else if (Server2.clients.containsKey(destination)) {
            Server2.clients.get(destination).dos.writeUTF(name + " : " + message);
            return;
        } else port = 1234;
        Socket s = new Socket("localhost", port);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream d = new DataOutputStream(s.getOutputStream());
        d.writeUTF("#" + source + "#" + destination + "#" + message);
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = dis.readUTF();
                        if (message.equals("NF")) {
                            dos.writeUTF("The client " + destination + " is not connected to the network");
                        }
                        break;
                    } catch (Exception e) {
                    }
                }
            }
        });
        readMessage.start();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        String received;
        while (true) {
            try {
                received = dis.readUTF();
                if (received.equals("GetMemberList()")) {
                    dos.writeUTF(getMembers().toString());
                    continue;
                }
                if (received.equals("Quit")) {
                    this.socket.close();
                    if (server == 1)
                        Server.clients.remove(name);
                    else Server2.clients.remove(name);
                    System.out.println("Client " + name + " just disconnected");
                    break;
                }
                String[] tmp = received.split("#");
                String sendTo = tmp[0];
                Chat(name, sendTo, 2, tmp[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}