import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;

    public static void main(String[] args) throws Exception{
        Scanner userInput = new Scanner(System.in);
        System.out.println("Server IP?");
        String ip = userInput.nextLine();

        System.out.println("Port?");
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(ip, port);

        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        ServerListener listener = new ServerListener();
        Thread t = new Thread(listener);
        t.start();

        System.out.println("Enter your username.");
        String userName = userInput.nextLine();
        out.println(userName);

    }

    static class ServerListener implements Runnable{

        public void run(){

            try{ 
                //while(true){ to keep connection open on client side

                //}
            }
            catch(Exception ex){
                System.out.println("Exception caught in listener - " + ex);
            }
        }
    }

}