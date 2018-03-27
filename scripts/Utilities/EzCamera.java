package scripts.Utilities;

import java.awt.Point;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;
import org.tribot.script.Script;

public class EzCamera {
	
	private static double		angleDegreesPerPixelRatio = 0.4,
								rotationDegreesPerPixelRatio = .2857;
	
	private static ACamera		acamera = null;
	
	public static boolean 		useMiddleMouse = false;
	
	public EzCamera(Script script){
		acamera = new ACamera(script);
	}
	
	public static boolean turnToTile(Positionable tile){
		if(acamera != null){
			acamera.turnToTile(tile);
			return true;
		}
		return mmCameraTo(tile);
	}
	
	public static int getIdealCameraAngle(Positionable tile){
		RSTile myPos = Player.getPosition();
		int distance = myPos.distanceTo(tile);
		if(distance > 17){
			return 40;
		} else if(distance <= 8){
			return 100;
		} else if(distance <=12){
			int offset = 12 - distance;
			return General.random(59, 70-offset);
		} else {
			int offset = 17 - distance;
			return General.random(48,60-offset);
		}
	}
	public static int getIdealCameraRotation(Positionable tile){
		return Camera.getTileAngle(tile);
	}
	
	public static boolean mmCameraTo(Positionable tile){
		if(tile == null)
			return false;
		int rotation = getIdealCameraRotation(tile);
		int angle = getIdealCameraAngle(tile);
		return mmCameraTo(rotation,angle);
	}
	
	public static int differenceBetweenRadians(int start, int end){
		int rotationDifference = start - end;
		if(rotationDifference > 180){
			rotationDifference -= 360;
		} else if(rotationDifference < -180){
			rotationDifference += 360;
		}
		return rotationDifference;
	}
	
	public static boolean mmCameraTo(int rotation, int angle){
		int currentRotation = Camera.getCameraRotation();
		int currentAngle = Camera.getCameraAngle();
		int rotationDifference = differenceBetweenRadians(currentRotation,rotation);
		int angleDifference = angle - currentAngle;
		if(Math.abs(rotationDifference) < 5 && Math.abs(angleDifference) < 5){
			return true;
		}
		double pixelsX = rotationDifference / rotationDegreesPerPixelRatio;
		double pixelsY = angleDifference / angleDegreesPerPixelRatio;
		if(!Projection.isInViewport(Mouse.getPos())){
			Mouse.moveBox(60, 60, 400, 300);
			General.sleep(100,200);
		}
		Point myMouse = Mouse.getPos();
		int destX = (int)myMouse.getX() + (int)pixelsX;
		int destY = (int)myMouse.getY() + (int)pixelsY;
		if(destX < 0){
			rotationDifference = 360 - rotationDifference;
			pixelsX = rotationDifference / rotationDegreesPerPixelRatio;
			destX = (int)myMouse.getX() + (int)pixelsX;
		}
		if(destX > 765){
			rotationDifference = 360 - rotationDifference;
			pixelsX = rotationDifference / rotationDegreesPerPixelRatio;
			destX = (int)myMouse.getX() + (int)pixelsX;
		}
		if(destY < 0){
			destY = (int)myMouse.getY() + (int)pixelsY;
		}
		if(destY > 500){
			destY = (int)myMouse.getY() - (int)pixelsY;
		}
		Point destination = new Point(destX,destY); 
//		General.println("Rotation difference: " + rotationDifference);
//		General.println("Mouse y change: " + pixelsY);
		Mouse.drag(myMouse, destination, 2);
		return Math.abs(Camera.getCameraAngle() - angle) < 5 && Math.abs(Camera.getCameraRotation() - rotation) < 10 ;
	}
	
	public static boolean mmMaxAngle(){
		return mmCameraTo(Camera.getCameraRotation(),100);
	}
	
	public static boolean isMiddleMouseEnabled(){
		RSVarBit middleMouse = RSVarBit.get(4134);
		return middleMouse != null && middleMouse.getValue() == 0;
	}
	
	public static boolean enableMiddleMouse(){
		return EzOptions.openTab() && EzOptions.pressButton("Toggle Mouse Camera");
	}
	
	public static boolean setCameraAngle(int angle){
		return setCamera(Camera.getCameraRotation(),angle);
	}

	public static boolean setCameraRotation(int rota) {
		return setCamera(rota,Camera.getCameraAngle());
	}
	
	public static boolean setCamera(int rotation, int angle){
		if(useMiddleMouse){
			if(!isMiddleMouseEnabled()){
				if(!enableMiddleMouse())
					return false;
			} 
			return mmCameraTo(rotation,angle);
		}
		if(acamera != null){
			acamera.setCamera(angle, rotation);
			return true;
		} 
		Camera.setCameraAngle(angle);
		Camera.setCameraRotation(rotation);
		return true;
	}

}
