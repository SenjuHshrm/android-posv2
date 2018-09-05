package com.pylon.emarketpos.tasks;

import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.Toast;
import android.content.Intent;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.lang.*;
import android.util.*;

import com.pylon.emarketpos.Utils.Formatter;
import com.pylon.emarketpos.Utils.Utils;


public class PrintReceipt {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPos;
    volatile boolean stopWorker;
    private String value = "";
    public Context mContext;
    private static String header1 = "Republic of the Philippines\n";
    private static String header2 = "City Government of San Pablo\n";
    private static String header3 = "Province of Laguna\n\n";
    private static String header4 = "Electronic Market System\n";
    private static String divider = "\n********************************\n";
    private static String feedLine = "\n\n\n\n\n";
    private static InputStream assetInStream;
    private static Bitmap bitmap;
    public PrintReceipt(Context context){
        this.mContext = context;
    }
    public void PrintReceiptPrep(String type, String[] RecInfo, String TransactNum){
        if(type.equalsIgnoreCase("ambulant")){
            String Type = "Ambulant Payment";
            String printInfo = "\n" +
                    "Owner Name: " + RecInfo[2] + "\n" +
                    "Nature of Business: " + RecInfo[3] + "\n" +
                    "Amount paid: P " + RecInfo[4] + "\n" +
                    divider + "\n" +
                    "Date: \n " + getDateTime() + " / " + getTime() + "\n" +
                    "Acknowledgement Receipt No.: \n" + " " + TransactNum + "\n" +
                    "Collector: " + RecInfo[5] + "\n" +
                    feedLine;
            IntentPrint(Type, printInfo);
        }else{
            String Type = "Stall Payment";
            String printInfo = "\n" +
                    "Stall Number: " + RecInfo[1] + "\n" +
                    "Owner Name: " + RecInfo[6] + "\n" +
                    "Business: " + RecInfo[2] + "\n" +
                    "Amount paid: P " + RecInfo[3] + "\n" +
                    divider  + "\n" +
                    "Date: \n " + getDateTime() + " / " + getTime() + "\n" +
                    "Acknowledgement Receipt No.: \n" + " " + TransactNum + "\n" +
                    "Collector: " + RecInfo[4] + "\n" +
                    feedLine;
            IntentPrint(Type, printInfo);
        }

    }
    private void IntentPrint(String type, String txtToPrint){
        InitPrinter();
        try{
            assetInStream = null;
            assetInStream = mContext.getAssets().open("SPCLOGO-print.png");
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(assetInStream),150, 150, true);
            assetInStream.close();
            byte[] comm = Utils.decodeBitmap(bitmap);
            writeWithFormat(comm, new Formatter().get(), Formatter.rightAlign());
            writeWithFormat(header1.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(header2.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(header3.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(header4.getBytes(), new Formatter().bold().get(), Formatter.centerAlign());
            writeWithFormat(divider.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(type.getBytes(), new Formatter().height().get(), Formatter.centerAlign());
            writeWithFormat(divider.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(txtToPrint.getBytes(), new Formatter().small().get(), Formatter.leftAlign());
            outputStream.close();
            socket.close();
        }catch(Exception e){
            value+="2 There is something wrong with your device please contact your ADMIN \n";
            Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
        }
    }
    private void InitPrinter(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try{
            if(!bluetoothAdapter.isEnabled()){
                Intent enabledBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enabledBluetooth);
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0){
                for(BluetoothDevice device : pairedDevices){
                    if(device.getName().equals("InnerPrinter")){
                        bluetoothDevice = device;
                        break;
                    }
                }
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                beginListenForData();
            }else{
                value+="No Devices found";
                Toast.makeText(mContext,value,Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            value+="1 There is something wrong with your device please contact your ADMIN \n";
            Toast.makeText(mContext, value, Toast.LENGTH_SHORT).show();
        }
    }
    private void beginListenForData(){
        try{
            //final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPos = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable(){
                public void run(){
                    while(!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int bytesAvailable = inputStream.available();
                            if(bytesAvailable > 0){
                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);
                                for(int i = 0; i < bytesAvailable; i++){
                                    byte b = packetBytes[i];
                                    if(b == delimiter){
                                        byte[] encodedBytes = new byte[readBufferPos];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes,0,
                                                encodedBytes.length
                                        );
                                    }else{
                                        readBuffer[readBufferPos++] = b;
                                    }
                                }
                            }
                        }catch(IOException ex){
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private void writeWithFormat(byte[] buffer, byte[] pFormat, byte[] pAlignment){
        try{
            outputStream.write(pAlignment);
            outputStream.write(pFormat);
            outputStream.write(buffer, 0, buffer.length);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private String getDateTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }
    private String getTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
        return df.format(c);
    }
}