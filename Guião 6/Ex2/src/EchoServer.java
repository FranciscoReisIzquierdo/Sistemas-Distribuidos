import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class ClientThread implements Runnable{
    private Socket client;

    public ClientThread(Socket client) {
        this.client= client;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            PrintWriter out = new PrintWriter(this.client.getOutputStream());

            int soma = 0;
            int quantidade = 0;

            String line;
            while ((line = in.readLine()) != null) {
                quantidade++;
                soma += Integer.parseInt(line);
                out.println(soma);
                out.flush();
            }
            out.println(soma / quantidade);
            out.flush();

            this.client.shutdownOutput();
            this.client.shutdownInput();
            this.client.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}


public class EchoServer {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            Socket socket;

            while (true) {
                socket = ss.accept();
                Thread client= new Thread(new ClientThread(socket));
                client.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}