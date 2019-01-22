/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_io_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Federico
 */
public class Server {
    ServerSocket serverSocket = null;
    Socket socket = null;
    RPI_IO_EXT rpio=null;
   
    public void server_start() throws IOException, InterruptedException{
     
            RPI_IO_EXT rpio=new RPI_IO_EXT();
            serverSocket = new ServerSocket(30000);
            System.out.println("Waiting for connection on port 30000");
   

        while (true) {
            
            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        // new thread for a client
       // System.out.println("New client connected");
        new ServerWorker(socket,rpio).start();
    }

   }
    
}
