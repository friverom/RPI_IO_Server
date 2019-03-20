/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_io_server;

import java.io.IOException;

/**
 *
 * @author Federico
 */
public class IO_Protocol {
    RPI_IO_EXT rpio = null;
    String request =null;
    
    public IO_Protocol(RPI_IO_EXT rpio){
        this.rpio=rpio;
        
    }
    /**
     * Method that process the request received.
     * The request is a String with the following format:
     * "task,level,command data". 
     * @param request String "task,level,command data"
     * @return String "command error" if request format is wrong
     * or * for command OK or / if command error
     * @throws IOException 
     */
    public String processRequest(String request) throws IOException{
        
        this.request=request;
        String[] parts=null;
        String reply=null;
        
        String task=null;
        String level=null;
        String command=null;
        String data=null;
        
        parts=this.request.split(",");
        
        if(parts.length<3){
            return "command error";
        }
        
        task=parts[0];
        level=parts[1];
        command=parts[2];
        
        if(parts.length>3){
            data=parts[3];
        }
        
        reply=task+","+level+","+this.processCommand(task,level,command);
        return reply;
        
        
        
    }
    
    private String processCommand(String tsk, String lvl, String command) throws IOException{
        
        String[] parts=null;
        int partsLenght=0;
        int task=Integer.parseInt(tsk);
        int level=Integer.parseInt(lvl);
        int resp=0;
        String reply=null;
                
        parts=command.split(" ");
        partsLenght=parts.length;
        
        switch(parts[0]){
            
            //Set Lock Command 1 relay
            case "01":
                if(partsLenght!=2)
                    break;
                resp=rpio.setLockRly(Integer.parseInt(parts[1]),task, level);
                if(resp==0){
                    reply="*";
                    break;
                } else {
                    reply="/";
                    break;
                }
            //Set lock command multiple relays
            case "02":
                if(partsLenght!=3)
                    break;
                resp=rpio.setLockRly(Integer.parseInt(parts[1]), task, level,Integer.parseInt(parts[2]));
                if(resp==0){
                    reply="*";
                    break;
                } else {
                    reply="/";
                    break;
                }
            
            //Get Lock relay
            case "03":
                if(partsLenght!=2)
                    break;
                resp=rpio.getLockRly(Integer.parseInt(parts[1]));
                if(resp!=-1){
                    reply=String.valueOf(resp);
                    break;
                } else {
                    reply="/";
                    break;
                }
            //Get lock level    
            case "04":
                if(partsLenght!=2)
                    break;
                resp=rpio.getLockLvl(Integer.parseInt(parts[1]));
                if(resp!=-1){
                    reply=String.valueOf(resp);
                    break;
                } else {
                    reply="/";
                    break;
                }
            //Release Lock on Relay    
            case "05":
                if(partsLenght!=2)
                    break;
                rpio.releaseLock(Integer.parseInt(parts[1]),task);
                reply="*"; 
                break;
            //Set On relay    
            case "06":
                if(partsLenght!=2)
                    break;
                resp=rpio.setRly(Integer.parseInt(parts[1]), task, level);
                if(resp==0){
                    reply="*";
                    break;
                } else {
                    reply="/";
                    break;
                }
            //Reset relay    
            case "07":
                if(partsLenght!=2)
                    break;
                resp=rpio.resetRly(Integer.parseInt(parts[1]), task, level);
                if(resp==0){
                    reply="*";
                    break;
                } else {
                    reply="/";
                    break;
                }
            
            //Get Analog settings for channel    
            case "08":
                if(partsLenght!=2)
                    break;
                reply=rpio.getAnalogSettings(Integer.parseInt(parts[1]));
                break;
            
            // Set Analog settings for channel    
            case "09":
                if(partsLenght!=3)
                    break;
                rpio.setAnalogSettings(Integer.parseInt(parts[1]), parts[2]);
                if(resp==0){
                    reply="*";
                    break;
                } else {
                    reply="/";
                    break;
                }
            //Reads Analog conversion for channel    
            case "10":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.getAnalogRead(Integer.parseInt(parts[1]));
                break;
            
            //Reads Relay port byte
            case "11":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.getRlyStatus();
                break;
            
            //Reads Input port byte    
            case "12":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.getInputs();
                break;
            
            //Read Input port bit
            case "13":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.getInput(Integer.parseInt(parts[1]));
                break;
                
            case "22":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.setRPI_on();
                break;
                
            case "23":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.setRPI_off();
                break;
                
            case "24":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.getControlReg();
                break;
                
            case "25":
                if(partsLenght!=2)
                    break;
                reply=""+rpio.setOutputPort(Integer.parseInt(parts[1]));
                break;
                
          
        }
        return reply;
        
    }
    
}
