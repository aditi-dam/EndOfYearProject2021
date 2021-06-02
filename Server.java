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

public class Server {
    public static final int PORT = 54323;
    private static final ArrayList<ClientData> clientArrayList = new ArrayList<>();
    private static final List<ClientData> clientList = Collections.synchronizedList(clientArrayList); 
    //clientList and clientArrayList refer to the same thing, but clientList has a wrapper around clientArrayList that is thread safe

    public static void main(String[] args) throws Exception{
        ExecutorService pool = Executors.newFixedThreadPool(100); //instead of creating/deleting threads spontaneously, threads can be created and then reused with thread pooling

        try(ServerSocket serverSocket = new ServerSocket(PORT)){ 
            //waits for client to join
            System.out.println("Server started");
            System.out.println("Local IP: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Port: " + serverSocket.getLocalPort());

            //sets up client and adds it to the list
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

        private void broadcast(String msg){
            //send the message to everyone connected (stored in clientList)
            try{
                System.out.println("Broadcasting -- " + msg);
                for(ClientData c : clientList){
                    c.getOut().println(msg); //get the printwriter and write out the message
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
                String userName = in.readLine().trim(); //first thing user sends is name
                cd.setUserName(userName);
                broadcast(String.format("WELCOME %s", cd.getUserName())); //broadcast person's name

                String incoming = "";
                
                while((incoming = in.readLine()) != null){ 

                    if(incoming.startsWith("QUIT")){
                        break;
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
                broadcast(String.format("EXIT %s", cd.getUserName()));

                try{
                    cd.getSocket().close();
                }
                catch(Exception ex){}
            }
        }
    }

}