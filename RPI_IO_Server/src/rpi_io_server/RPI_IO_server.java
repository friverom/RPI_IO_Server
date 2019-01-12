/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_io_server;

import java.io.IOException;



/**
 *
 * @author federico
 */
public class RPI_IO_server {

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        
        Server test=new Server();
        
       // test.test(5);
        test.server_start();
        
    }
    
}
