package Serveur;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

public class Serveur{
    static ServerSocket  socket;	
    static int           port = 5000;
    static String        ip = "172.26.201.2";
    
    public static void main(String[] args)    {
    	
    	Robot robotino = new Robot(ip);
    	tControlRobotino mouvement = new tControlRobotino();		
    	robotino.addListener(new RobotListenerImpl());

    	try{
            socket = new ServerSocket(port);
            System.out.println("Serveur " + ip + ":" + port + " en attente");
            
            while (true){
                Socket client = socket.accept();
                boolean continuer = true;

				do{
					try{
						BufferedReader pLecture = new BufferedReader(
						        new InputStreamReader(client.getInputStream())
						       );
						
						String str = pLecture.readLine();
						if(!str.isEmpty()){
							DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
							Date date = new Date();

							System.out.println("[" + dateFormat.format(date) + "] " + client.getInetAddress() + ":" + client.getPort() + " \"" + str + "\"");
							StringTokenizer st = new StringTokenizer(str); 
							String go = st.nextToken();
							
							if(go.equals("go")){
								float x = Float.valueOf(st.nextToken());
								float y = Float.valueOf(st.nextToken());
								mouvement.run(robotino, y, -x, 0);	
							}
							
							if(go.equals("stop")){
								continuer = false;
								mouvement.run(robotino, 0, 0, 0);	
							}
							
							if(go.equals("tourner")){
								float o = Float.valueOf(st.nextToken());
								mouvement.run(robotino, 0, 0, o);	
							}
							
							if(go.equals("camera")){
								String onoff = st.nextToken();
								if(onoff.equals("on")){
									RobotListenerImpl.FluxOff();
								}
								if(onoff.equals("off")){
									RobotListenerImpl.FluxOn();
								}
							}
							
							if(go.equals("pincer")){
								Boolean serrer = Boolean.valueOf(st.nextToken());
								if(serrer) robotino.serrerPince();
								else robotino.ouvrirPince();
							}
													
						}						
					}catch(NullPointerException e){}
				}while(continuer);
				
                client.close();
            }
        }
        catch (IOException e) 	{
            e.printStackTrace();
        }
        
        try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private static class RobotListenerImpl implements RobotListener
    {
    	public DatagramSocket envoieImage;
    	InetAddress IPAddress;
    	static Boolean envoyer;
    	
    	public RobotListenerImpl(){
    		try {
				envoieImage = new DatagramSocket(5001);
				IPAddress  = InetAddress.getByName("172.26.201.2");
			} catch (Exception e) {
				 e.printStackTrace();
			}
    	}
    	
        @Override public void onImageReceived(Image img) {
        	//flag debut fin
        	if(envoyer){
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();        
					ImageIO.write(toBufferedImage(img), "bpm", baos);
					baos.flush();
					byte[] buffer = baos.toByteArray();
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, IPAddress, 9876);
					envoieImage.send(packet);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
        }
        
    	public BufferedImage toBufferedImage(Image img) {
    		if (img instanceof BufferedImage) {
    			return (BufferedImage) img;
    		}

    		// Create a buffered image with transparency
    		BufferedImage bimage = new BufferedImage(img.getWidth(null),
    				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    		// Draw the image on to the buffered image
    		Graphics2D bGr = bimage.createGraphics();
    		bGr.drawImage(img, 0, 0, null);
    		bGr.dispose();

    		// Return the buffered image
    		return bimage;
    	}
    	
    	public static void FluxOn(){	envoyer = true;   	}
    	public static void FluxOff(){	envoyer = false;	}
    	
        @Override public void onConnected(){}
        @Override public void onDisconnected(){}
        @Override public void onError(Error error){}
    }
}
