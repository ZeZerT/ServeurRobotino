package Serveur;

import java.awt.Image;

import rec.robotino.com.Bumper;
import rec.robotino.com.Camera;
import rec.robotino.com.Com;
import rec.robotino.com.Gripper;
import rec.robotino.com.Motor;
import rec.robotino.com.OmniDrive;


public class Robot implements Runnable
{
    protected final String hostname;
    protected final Com com;
    protected final Motor motor1;
    protected final Motor motor2;
    protected final Motor motor3;
    protected final OmniDrive omniDrive;
    protected final Bumper bumper;
    protected final Gripper gripper;
    protected final Camera camera;
    protected final float[] startVector = new float[]
    {
        200.0f, 0.0f
    };

    public Robot(String hostname)
    {
        this.hostname = hostname;
        com 		= new Com();
        motor1 		= new Motor();
        motor2 		= new Motor();
        motor3 		= new Motor();
        omniDrive	= new OmniDrive();
        bumper 		= new Bumper();
        gripper 	= new Gripper();
        camera 		= new Camera();
        init();
        com.setAddress(hostname);
        com.connect();
    }

    public void run(){}

    protected void init()
    {
        motor1.setComId(com.id());        motor1.setMotorNumber(0);
        motor2.setComId(com.id());        motor2.setMotorNumber(1);
        motor3.setComId(com.id());        motor3.setMotorNumber(2);

        omniDrive.setComId(com.id());     
        bumper.setComId(com.id());
    }

    protected void avancer(float x, float y, float o) throws InterruptedException
    {
        if(com.isConnected() && false == bumper.value())
        {
            omniDrive.setVelocity(x, y, o);
        }
    }
    
    protected void serrerPince(){   gripper.close();			}
    protected void ouvrirPince(){   gripper.open();				}
    protected void allumerCamera(){	camera.setStreaming(true);	}
    protected void eteindreCamera(){camera.setStreaming(false); }   
}
