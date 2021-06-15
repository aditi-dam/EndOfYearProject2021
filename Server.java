import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// import javafx.stage.Stage;

public class Server {
    public static final int PORT = 54323;
    private static final ArrayList<ClientData> clientArrayList = new ArrayList<>();
    private static final List<ClientData> clientList = Collections.synchronizedList(clientArrayList); 

    public static void main(String[] args) throws Exception{
        ExecutorService pool = Executors.newFixedThreadPool(100); //instead of creating/deleting threads spontaneously, threads can be created and then reused with thread pooling

        try(ServerSocket serverSocket = new ServerSocket(PORT)){ 
            //waits for client to join
            System.out.println("Server started");
            System.out.println("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Port: " + serverSocket.getLocalPort());

            while(true) {

                if(clientArrayList.size() < 2){ 
                    Socket socket = serverSocket.accept();

                    System.out.printf ("Connected to %s:%d on local port %d\n", socket.getInetAddress(),
                        socket.getPort(), socket.getLocalPort());

                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    String name = socket.getInetAddress().getHostAddress();
                    ClientData cd = new ClientData(socket, input, out, name);

                    clientList.add(cd);

                    pool.execute(new ClientHandler(cd));
                }
            }
    
        }
    }

    static class ClientHandler implements Runnable{
        ClientData cd;

        public ClientHandler(ClientData cd){
            this.cd = cd;
        }

        private void broadcast(String str, ClientData skip){
            //#3: send the message to everyone connected (including the person that sent it)
            try{
                for(ClientData c : clientList){
                    if(!(c.equals(skip))){
                        c.getOut().println(str); 
                        c.getOut().flush();
                    }
                }
            }
            catch(Exception ex){
                System.out.println("broadcast - caught exception:" + ex);
                ex.printStackTrace();
            }
        }

        public void run(){
            try{
                BufferedReader in = cd.getInput();
                String userName = in.readLine().trim(); 
                cd.setUserName(userName);
                broadcast(String.format("WELCOME %s. Are you ready to play pictionary?\nBefore you start, here are a list of commands that may be useful.\n1. /directions: prints out the directions\n2. /pictionary: play pictionary\n3. /quit: quit game\n", cd.getUserName()), cd); 
 

                String incoming = "";
                
                while((incoming = in.readLine()) != null){ 

                    if(incoming.startsWith("QUIT")){
                        break;
                    }
                    else if(incoming.startsWith("COORDINATE")){ //#2: If the server is receiving coordinates, broadcast it to all the clients
                        broadcast(incoming, cd);
                    }
                    else if(incoming.startsWith("START")){
                        broadcast(incoming, cd);
                    }

                }

            }
            catch(Exception e){
                if(e instanceof SocketException){
                    System.out.println("Caught socket ex for " + cd.getName());
                }
                System.out.println("caught exception:" + e);
                e.printStackTrace();
            }
            finally{
                clientList.remove(cd);

                System.out.println(cd.getName() + " has left");
                broadcast(String.format("EXIT %s", cd.getUserName()), cd);

                try{
                    cd.getSocket().close();
                }
                catch(Exception ex){}
            }
        }
    }

}