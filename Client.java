import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Client extends Application {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;

    public static void main(String[] args) throws Exception {
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
        Application.launch(args);

        System.out.println("Enter your username.");
        String userName = userInput.nextLine();
        out.println(userName);

        // this thread listens and sends things to the server
        String line = userInput.nextLine().trim();

        while (!line.equals("/quit")) {
            out.println(line);
            line = userInput.nextLine().trim();
        }

        out.println("QUIT");
        out.close();
        userInput.close();
        socketIn.close();
        socket.close();

    }

    static class ServerListener implements Runnable {

        public void run() {

            try {
                String incoming = "";

                while ((incoming = socketIn.readLine()) != null) {
                    System.out.println(incoming);
                }
            } catch (Exception ex) {
                System.out.println("Exception caught in listener - " + ex);
            } finally {
                System.out.println("Listener exiting");
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Button btOk = new Button("OK");
        Scene scene = new Scene(btOk, 200, 250);
        primaryStage.setTitle("MyJavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}