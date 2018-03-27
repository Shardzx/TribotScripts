package scripts.Utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class EzPaint {

	private static Image paintImage;

	public static Image loadImageFile(String path){
		paintImage = null;
		
		new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try 
                {
                	paintImage = ImageIO.read(new File(path));
					this.finalize();
				} 
                catch (Throwable e) 
                {
					e.printStackTrace();
				}
            }
        }).run();
		
		return paintImage;
	}
	public static Image downloadImageFile(String path){
		paintImage = null;
		
		new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try 
                {
                	URL url = new URL(path);
                	paintImage = ImageIO.read(url);
					this.finalize();
				} 
                catch (Throwable e) 
                {
					e.printStackTrace();
				}
            }
        }).run();
		
		return paintImage;
	}

}
