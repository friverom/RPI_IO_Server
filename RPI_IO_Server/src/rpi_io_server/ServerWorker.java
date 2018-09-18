/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_io_server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author federico
 */
public class ServerWorker extends Thread{
 
    private Socket socket = null;
    private InputStream in = null;
    private BufferedReader input = null;
    private PrintWriter output = null;
    private boolean stopFlag=true;
    private RPI_IO_EXT rpio=null;
    IO_Protocol protocol=null;
    
    public ServerWorker(Socket socket, RPI_IO_EXT rpio){
        this.socket=socket;
        this.rpio=rpio;
        this.protocol=new IO_Protocol(rpio);
    }
    
    public void run() {
       
        try {
            in=socket.getInputStream();
            input=new BufferedReader(new InputStreamReader(in));
            output=new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        String request="";
        try {
            request=input.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        String reply=null;
        try {
            reply = this.protocol.processRequest(request);
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        output.println(reply);
       
        
    }
    
    
}
