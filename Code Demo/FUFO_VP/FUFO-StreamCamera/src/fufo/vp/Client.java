/**
 * Licensed to Open-Ones Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fufo.vp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import android.util.Log;


/**
 * @author khoinguyen67
 *
 */
public class Client {
    
    String ipClient = "192.168.157.1";
    int portClient = 4444;
    DatagramSocket socket;
    DatagramPacket fspacket;
    InetAddress serverAddr;
    int number = 0;
    public Client(){        
    }
    public Client(String ip, int port){
        ipClient = ip;
        portClient = port;
        
    }
    public void creatClient(){
        try{
             serverAddr = InetAddress.getByName(ipClient);
             socket = new DatagramSocket();                 
        }catch(SocketException e){
            Log.e("CameraTest", "Client: Errror socket", e);
        }catch(Exception e2){
            Log.e("CameraTest", "Client: Errror", e2);
        }
    }
    public void sendPackage(byte[] buffer){
        try{
            DatagramSocket socket = new DatagramSocket();
            
            /* chuan bi du lieu de gui */        
            byte[] b = buffer;      
            Log.d("CameraTest", "size frame: " + b.length + "'"); 
            fspacket = new DatagramPacket(b,b.length, serverAddr, portClient);  
            socket.send(fspacket);
            Log.d("CameraTest", "da gui");
            ++number; 
            Log.v("CameraTest", "sendNumber = "
                    + number);
        } catch (Exception e) {
            Log.e("CameraTest", "Client: Errror", e);
        }               
    }
}
