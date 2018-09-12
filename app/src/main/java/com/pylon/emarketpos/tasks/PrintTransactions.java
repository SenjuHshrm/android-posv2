package com.pylon.emarketpos.tasks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.pylon.emarketpos.Utils.Formatter;
import com.pylon.emarketpos.Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class PrintTransactions {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPos;
    volatile boolean stopWorker;
    private static String header1 = "Republic of the Philippines\n";
    private static String header2 = "City Government of San Pablo\n";
    private static String header3 = "Province of Laguna\n\n";
    private static String header4 = "Electronic Market System\n";
    private static String divider = "\n********************************\n";
    private static String title = "Daily Collection";
    private static String feedLine = "\n\n\n\n\n";
    private static InputStream assetInStream;
    private static Bitmap bitmap;
    Context mContext;
    public PrintTransactions(Context ctx) {
        this.mContext = ctx;
    }
    public void PrintReceipt(String DevUser, ArrayList<String> arraylist, String total){
        int totalColl = arraylist.size();
        String printUser = "Collector: " + DevUser + "\n";
        String printTotal = "Total stall # collected: " + String.valueOf(totalColl) + "\n";
        String printTotalAmt = "Total collection: P " + total;
        IntentPrint(printUser, printTotal, printTotalAmt);

    }
    private void IntentPrint(String user, String total, String totalAmt){
        InitPrinter();
        try{
            assetInStream = null;
            assetInStream = mContext.getAssets().open("SPCLOGO-print.png");
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(assetInStream),150,150, true);
            assetInStream.close();
            byte[] comm = Utils.decodeBitmap(bitmap);
            writeWithFormat(comm, new Formatter().get(), Formatter.rightAlign());
            writeWithFormat(header1.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(header2.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(header3.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(header4.getBytes(), new Formatter().bold().get(), Formatter.centerAlign());
            writeWithFormat(divider.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat(title.getBytes(), new Formatter().height().get(), Formatter.centerAlign());
            writeWithFormat(divider.getBytes(), new Formatter().get(), Formatter.centerAlign());
            writeWithFormat("\n".getBytes(), new Formatter().get(), Formatter.rightAlign());
            writeWithFormat(user.getBytes(), new Formatter().get(), Formatter.leftAlign());
            writeWithFormat(getCurrDate().getBytes(), new Formatter().get(), Formatter.leftAlign());
            writeWithFormat(total.getBytes(), new Formatter().get(), Formatter.leftAlign());
            writeWithFormat(totalAmt.getBytes(), new Formatter().get(), Formatter.leftAlign());
            writeWithFormat(feedLine.getBytes(), new Formatter().get(), Formatter.leftAlign());
            outputStream.close();
            socket.close();

        } catch (Exception e) {

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
                socket = (BluetoothSocket) m.invoke(bluetoothDevice,1);
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                beginListenForData();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "This happens when you try to print with bluetooth turned off", Toast.LENGTH_LONG).show();
        }
    }
    private void beginListenForData() {
        try{
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPos = 0;
            readBuffer = new byte[2048];
            workerThread = new Thread(new Runnable() {
                @Override
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
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                    } else {
                                        readBuffer[readBufferPos++] = b;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
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
    private String getCurrDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy / hh:mm aa");
        String formattedDate = df.format(c);
        return "Date: \n " + formattedDate + "\n";
    }
}
