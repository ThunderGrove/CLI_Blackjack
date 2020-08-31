import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientTwo {
    public static void main(String[] args) throws IOException {
        int port=8000;//Normally a unused port.
        String host="localhost";
        DataInputStream dis;
        DataOutputStream dos;
        Socket socket;
        Scanner scanner=new Scanner(System.in);
        String ic="";//Input container

        socket=new Socket(host,port);
        dis=new DataInputStream(socket.getInputStream());
        dos=new DataOutputStream(socket.getOutputStream());
        while(!socket.isClosed()){
            System.out.println(dis.readUTF());
            System.out.print("->");
            dos.writeUTF(scanner.nextLine());
            ic=dis.readUTF();
            if(ic.contains("Closing connection.")){
                System.out.println(ic);
                socket.close();
            }else{
                System.out.println(ic);
            }

        }
    }
}