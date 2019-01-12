/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_io_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import rpi_io.RPI_IO;




/**
 * This class creates and adds privilege controls on outputs of RPI_IO board.
 * Output relays now has owner (task) and level priority (level) to gain access
 * to them.
 * Analog inputs are now converted to their voltage value equivalent based on
 * parameters sets defined in file "CalibrationData.txt". Analog input type are 
 * 5V, 10V and 0..20mA. Zero and span can also be adjusted to calibrate each
 * input.
 * @author Federico Rivero
 */
public class RPI_IO_EXT extends RPI_IO{
    
    private static byte[] lockRly = new byte[8]; //relay owner
    private static byte[] lockLvl = new byte[8]; //lock level
    private static double[] analog_type = new double[8];//analog type
    private static double[] zero_cal = new double[8];//zero calibration
    private static double[] span_cal = new double[8];//span calibration
    
    public static final int MASTER=255; 
    
    /**
     * Class Constructor. 
     * Initializes analog control arrays
     */
    public RPI_IO_EXT() throws IOException {
        super();
        for(int i=0;i<8;i++){
            lockRly[i]=0;
            lockLvl[i]=0;
            analog_type[i]=1.0;
            zero_cal[i]=0;
            span_cal[i]=1.0;
        }
        //Read Analog settings from "CalibrationData.txt" file
        this.readAnalogSettings();
    }
    
    /**
     * Get voltage reading of input.
     * Analog types:
     * 5 volts. Returns actual voltage reading. 0.00 to 5.00 Volts
     * 10 volts. Returns actual voltage reading. 0.00 to 10.00 volts
     * 0..20 mA. Returns actual mA reading. 00.00 to 20.00 mA
     * @param chn Analog channel input from 1 to 8.
     * @return Double with analog value
     */
    public double getAnalogRead(int chn){
        
        double value=((super.getChannel(chn)*analog_type[chn-1]/4095)+zero_cal[chn-1])*span_cal[chn-1];
     //   double value=((2047*analog_type[chn-1]/4095)+zero_cal[chn-1])*span_cal[chn-1];
        return value;
        
    }
    /* 
     * This method get the owner of a relay output.  
     * @param rly int from 1 to 8
     * @return int owner
     */
    public int getLockRly(int rly) {
        
        return (int)lockRly[rly-1];
    }

    /**
     * Method to assign an owner and a priority level to a relay output.
     * Only MASTER task can override a lock.
     * @param rly int from 1 to 8
     * @param task int owner. from 1 to 255
     * @param lvl int priority level. from 1 to 10
     * @return int. 0 if lock operation succeed. -1 if not.
     */
    public int setLockRly(int rly, int task, int lvl) {
        if(lockRly[rly-1]==0){
            lockRly[rly-1]=(byte)task;
            lockLvl[rly-1]=(byte)lvl;
            return 0;
        } else {
            return -1;
        }
    }
    
    /**
     * Method to assign an owner and a priority level to a set of relay outputs.
     * Is an overloaded method of the setLockRly and locks "x" amount of 
     * continues relays.
     * @param rly int from 1 to 8
     * @param task int from 1 to 255
     * @param lvl int level. from 1 to 10
     * @param rlycount # of relays to lock starting from rly. from 1 to 8
     * @return int. 0 if operation succeed. -1 if not
     */
    public int setLockRly(int rly, int task, int lvl, int rlycount){
        // NOTE. This method should be revised. 
        // Should call setLockRly(rly,task,level) to set the lock
        // and test if operation succesfull on each relay.
        int data=0;
        
        if((rly+rlycount-1)>8){
            data=-1;
        } else {
            for (int i = 0; i < (rly + rlycount - 1); i++) {
                lockRly[i] = (byte) task;
                lockLvl[i] = (byte) lvl;
            }
        }
        return data;
    }

    /**
     * Method to get the priority level of owner.
     * 
     * @param rly int. from 1 to 8
     * @return int. priority level of owner
     */
    public int getLockLvl(int rly) {
        return (int)lockLvl[rly-1];
    }
    
    /**
     * Method to release Lock on relay output
     * @param rly int. from 1 to 8
     */
    public void releaseLock(int rly, int task){
        if (lockRly[rly - 1] == task) {
            lockRly[rly - 1] = 0;
            lockLvl[rly - 1] = 0;
        }
    }

    /**
     * Method to set ON an output relay.
     * Only the owner with level priority will be allow to set the output
     * MASTER task with level priority with equal or higher priority can
     * override output.
     * @param rly int. from 1 to 8
     * @param task int. Owner from 1 to 255
     * @param lvl int. from 1 to 10
     * @return int. 0 if succeed. -1 if not.
     */
    public int setRly(int rly, int task, int lvl){
        if(lockRly[rly-1]==task && lockLvl[rly-1]<=lvl){
            super.setRly(rly);
            return 0;
        } else if(task==MASTER && lvl>lockLvl[rly-1]) {
            super.setRly(rly);
            return 0;
        } else {
            return -1;
        }
    }
    
    /**
     * Method to reset an output relay.
     * Only the owner with level priority will be allow to set the output
     * MASTER task with level priority with equal or higher priority can
     * override output.
     * @param rly int. from 1 to 8
     * @param task int. Owner from 1 to 255
     * @param lvl int. from 1 to 10
     * @return int. 0 if succeed. -1 if not.
     */
    public int resetRly(int rly, int task, int lvl) {
        if (lockRly[rly - 1] == task && lockLvl[rly - 1] <= lvl) {
            super.resetRly(rly);
            return 0;
        } else if (task == MASTER && lvl > lockLvl[rly - 1]) {
            super.resetRly(rly);
            return 0;
        } else {
            return -1;
        }
    }
    /**
     * Reads calibration data from file and sets the analog arrays.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private void readAnalogSettings() throws FileNotFoundException, IOException{
    
        File file = new File("/home/pi/NetBeansProjects/RPI_IO_Server/CalibrationData.txt");
        boolean exists = file.exists();
        if(!exists){
            file.createNewFile();
            saveAnalogSettings();
        }
        
        BufferedReader reader = null;
        
        reader = new BufferedReader(new FileReader(file));
        String text = null;
        String[] data=null;
        int i=0;
        
        while ((text = reader.readLine()) != null) {
            data=text.split(";", 3);
            if(data.length==3){
                analog_type[i]=Double.parseDouble(data[0]);
                zero_cal[i]=Double.parseDouble(data[1]);
                span_cal[i]=Double.parseDouble(data[2]);
                i=i+1;
            }
                
        }
        reader.close();
    }
    
    /**
     * Writes the analog calibration data from analog arrays to file
     * @return
     * @throws IOException 
     */
    private String saveAnalogSettings() throws IOException{
     
        File file = new File("/home/pi/NetBeansProjects/RPI_IO_Server/CalibrationData.txt");
        file.createNewFile();
        BufferedWriter writer = null;
        
        writer = new BufferedWriter(new FileWriter(file));
        
        String data="";
        for(int i=0;i<8;i++){
            data=analog_type[i]+";"+zero_cal[i]+";"+span_cal[i];
            writer.newLine();
            writer.write(data);
        }
        writer.flush();
        writer.close();
        
    return "0";    
    }
    
    /**
     * Reads calibration data for channel.
     * Analog types:
     * 5 volts. Returns  5.00
     * 10 volts. Returns 10.00 
     * 0..20 mA. Returns 20.00 
     * @param chn int from 1 to 8
     * @return String. "type;zero;span"
     */
    public String getAnalogSettings(int chn){
        
        String data=""+analog_type[chn-1]+";"+zero_cal[chn-1]+";"+span_cal[chn-1];
        
        return data;
        
    }
    
    /**
     * Set calibration data for channel in analog data arrays
     * type:
     * 5.00 for 5 volts inputs
     * 10.00 for 10 volts inputs
     * 20.00 from 0 to 20 mA inputs
     * @param chn int from 1 to 8
     * @param data "type;zero;span"
     * @return
     * @throws IOException 
     */
    public String setAnalogSettings (int chn, String data) throws IOException{
        
        String[] parts=null;
        parts=data.split(";");
        
        analog_type[chn-1]=Double.parseDouble(parts[0]);
        zero_cal[chn-1]=Double.parseDouble(parts[1]);
        span_cal[chn-1]=Double.parseDouble(parts[2]);
        
        saveAnalogSettings();
        return "0";
    }
   
}
