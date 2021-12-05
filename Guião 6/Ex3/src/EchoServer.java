import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ClientThread implements Runnable{
    private Socket client;
    private somaAndMedia all;

    public ClientThread(Socket client, somaAndMedia all) {
        this.client= client;
        this.all= all;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            PrintWriter out = new PrintWriter(this.client.getOutputStream());

            int soma = 0;

            String line;
            while ((line = in.readLine()) != null) {
                all.soma(Integer.parseInt(line));
                soma += Integer.parseInt(line);
                out.println(soma);
                out.flush();
            }
            out.println(all.getAllMedia());
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

class somaAndMedia{
    private Lock lock;
    private int quantidade= 0;
    private int soma= 0;

    public somaAndMedia(){
        lock= new ReentrantLock();
    }

    public void soma(int num){
        try{
            this.lock.lock();
            this.soma+= num;
            this.quantidade++;
        }
        finally { this.lock.unlock(); }
    }

    public int getAllMedia(){
        try {
            this.lock.lock();
            return this.soma / this.quantidade;
        }
        finally { this.lock.unlock(); }
    }
}


public class EchoServer {

    public static void main(String[] args) {
        somaAndMedia all= new somaAndMedia();

        try {
            ServerSocket ss = new ServerSocket(12345);
            Socket socket;

            while (true) {
                socket = ss.accept();
                Thread client= new Thread(new ClientThread(socket, all));
                client.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}