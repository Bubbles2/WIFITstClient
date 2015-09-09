package matcom.dcf.wifitstclient;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    EditText welcomeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        //
        editTextAddress.setText("192.168.4.117");
        editTextPort.setText("8080");

        welcomeMsg = (EditText)findViewById(R.id.welcomemsg);
        welcomeMsg.setText("Donncha says HI");

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        //
        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });
    }

    OnClickListener buttonConnectOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            String tMsg = welcomeMsg.getText().toString();
            //tMsg="*";
            if(tMsg.equals("")){
                tMsg = null;
                Toast.makeText(MainActivity.this, "No Welcome Msg sent", Toast.LENGTH_SHORT).show();
            }

            MyClientTask myClientTask = new MyClientTask(editTextAddress
                    .getText().toString(), Integer.parseInt(editTextPort
                    .getText().toString()),
                    tMsg);
            myClientTask.execute();
        }
    };

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                //
                if(msgToServer != null){
                   // dataOutputStream.writeUTF(msgToServer);
                }
                 dataOutputStream.write(readTextFile());


                response = dataInputStream.readUTF();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }
//=========================
private int LoopCurrentIP = 0;

    public ArrayList<InetAddress> getConnectedDevices(String YourPhoneIPAddress) {
        ArrayList<InetAddress> ret = new ArrayList<InetAddress>();

        LoopCurrentIP = 0;

        String IPAddress = "";
        String[] myIPArray = YourPhoneIPAddress.split("\\.");
        InetAddress currentPingAddr;

        for (int i = 0; i <= 255; i++) {
            try {

                // build the next IP address
                currentPingAddr = InetAddress.getByName(myIPArray[0] + "." +
                        myIPArray[1] + "." +
                        myIPArray[2] + "." +
                        Integer.toString(LoopCurrentIP));

                // 50ms Timeout for the "ping"
                if (currentPingAddr.isReachable(50)) {

                    ret.add(currentPingAddr);
                }
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }

            LoopCurrentIP++;
        }
        return ret;
    }
    public byte[] readTextFile() {
        //
        FileInputStream fis=null;
        byte[] dat = null;
        //String yourFilePath = this.getFilesDir() + "/" + "Share2.txt";
        String yourFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/df/"+ "Share2.txt";;
        Log.i("DF - Files",yourFilePath);
        File myFile = new File(yourFilePath);
        try {
            fis = new FileInputStream(myFile);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line="";
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                Log.i("DF - Files", line);
            }
            line=sb.toString();
            if(!line.isEmpty() && line!=null) dat = line.getBytes();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dat;
    }
}