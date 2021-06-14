import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Client extends Application {
    private static Socket socket;
    private static BufferedReader socketIn;
    private static PrintWriter out;

    public static Canvas canvas = new Canvas(800, 500); 
    public static GraphicsContext gc = canvas.getGraphicsContext2D();
    public static Whiteboard w;
    public Welcome welcome = new Welcome(this);

    public static StackPane pane = new StackPane();
    public static Scene scene = new Scene(pane, 800, 500);
    public static Scanner userInput = new Scanner(System.in);
    
    private static int playerNum = -1;

    public static void main(String[] args) throws Exception {
        System.out.println("Server IP?");
        String ip = userInput.nextLine();

        System.out.println("Port?");
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(ip, port);

        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        w = new Whiteboard(out, scene, pane, gc, canvas);

        ServerListener listener = new ServerListener();
        Thread t = new Thread(listener);
        t.start();
        System.out.println("Enter your username.");
        String userName = userInput.nextLine();
        out.println(userName);

        Thread client = new Thread(new ClientListener());
        client.start();

        Application.launch(args);

    }

    public void start(Stage primaryStage) {
        welcome.start(primaryStage);

    }
    public void startWhiteboard(Stage primaryStage){
        w.start(primaryStage);
    }
    public void openDirections() {
        Platform.runLater(()->{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Instructions");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setResizable(true);

            alert.setHeaderText("Welcome to our Collaborative Whiteboard!");
            alert.setContentText("If you'd like to play a game of Pictionary, just click the 'PLAY!' button.\nIf there are other clients on the whiteboard, then the game will start.\nYou can also click the 'Whiteboard' button for free drawing.\nType '/quit' in the terminal to quit.\nHave fun!");

            ButtonType close = new ButtonType("Close");
            alert.getButtonTypes().setAll(close);

            alert.showAndWait();
        });
    }

    static class ServerListener implements Runnable {

        public void run() {

            try {
                String incoming = "";

                while ((incoming = socketIn.readLine()) != null) {

                    if(incoming.startsWith("COORDINATE1")){
                        gc.beginPath();
                        w.draw(incoming);
                    }
                    else if(incoming.startsWith("COORDINATE")){
                        w.draw(incoming);
                    }
                    else if(incoming.startsWith("START")){
                        playerNum = 2;
                        Whiteboard.setWord(incoming.substring(incoming.indexOf("T", 3) + 1));
                        w.pictionary(playerNum); //for text field to appeaar
                    }

                }
            } catch (Exception ex) {
                System.out.println("Exception caught in listener - " + ex);
            } finally {
                System.out.println("Listener exiting");
            }
        }

    }

    static class ClientListener implements Runnable{
        public void run(){
            String line = userInput.nextLine().trim();
        
            while (!line.equals("/quit")) {
                
                if (line.toLowerCase().equals("/pictionary")) {
                    String word = "";
                    try{
                        if (playerNum == -1){
                            playerNum = 1;

                            int randomWord = (int) (Math.random() * "pictionary_idea.txt".length());
                            word = Files.readAllLines(Paths.get("pictionary_ideas.txt")).get(randomWord);

                            Whiteboard.setWord(word);
<<<<<<< HEAD
                            w.pictionary(playerNum);
                            // Platform.runLater(arg0); Needs a runnable as a parameter
=======
                            w.pictionary(playerNum); //for word to appear on player side
>>>>>>> 22b97577e95278285b1bc1e1c3dd276a58484f3e

                            out.println("START" + word);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }     
                }
                //https://stackoverflow.com/questions/2312756/how-to-read-a-specific-line-using-the-specific-line-number-from-a-file-in-java
                
                out.println(line);
                line = userInput.nextLine().trim();

            }
            try { 
                out.println("QUIT");
                out.close();
                userInput.close();
                socketIn.close();
                socket.close();
            }
            catch(Exception e){};
        }
    }

}