/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_io_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;





/**
 *
 * @author federico
 */
public class Server {

    ServerSocket serverSocket = null;
    Socket socket = null;
    RPI_IO_EXT rpio=null;
   
    public void server_start() throws IOException{
     
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
        //System.out.println("New client connected");
        new ServerWorker(socket,rpio).start();
    }

   }
    public void test(int t) throws InterruptedException, IOException {

        RPI_IO_EXT rpio = new RPI_IO_EXT();
        double value = 0;
        Calendar date = null;

        rpio.setLockRly(1, 10, 3, 8);

        for (int i = 0; i < t; i++) {
            rpio.out_on();

            rpio.setRly(1, 10, 5);
            rpio.setRly(2, 10, 4);
            rpio.setRly(3, 10, 5);
            rpio.setRly(4, 10, 5);
            rpio.setRly(5, 10, 5);
            rpio.setRly(6, 10, 5);
            rpio.setRly(7, 10, 5);
            rpio.setRly(8, 10, 5);
            value = rpio.getAnalogRead(2);
            System.out.println("Analog: " + value);
            date = rpio.getCalendarRTC();
            System.out.println("RTC date: " + date.getTime());

            Thread.sleep(500);

            rpio.out_off();
            rpio.resetRly(1, 10, 5);
            rpio.resetRly(2, 10, 4);
            rpio.resetRly(3, 10, 5);
            rpio.resetRly(4, 10, 5);
            rpio.resetRly(5, 10, 5);
            rpio.resetRly(6, 10, 5);
            rpio.resetRly(7, 10, 5);
            rpio.resetRly(8, 10, 5);
            value = rpio.getAnalogRead(2);
            System.out.println("Analog: " + value);
            
            

            Thread.sleep(500);
        }

    }
        
    

    
}
