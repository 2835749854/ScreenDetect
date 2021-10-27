import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Dimension;
/**
*图像面板，用于接受图片文件,并将其画出显示
*
*
**/
class ImagePanel extends JPanel
{
	BufferedImage defimg = null;
	BufferedImage buffimg = null;
	ImagePanel()
	{
		try
		{
			String defpath = "..//CameraImages//init//Login.jpg";
			defimg = ImageIO.read(new File(defpath));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		

		//setBounds(0,0,500,400);
		setLocation(0,0);
		setPreferredSize(new Dimension(500,400));
	}



	@Override
	public void paint(Graphics g)
	{
		if(buffimg == null)
		{
		    g.drawImage(defimg,0,0,500,400,this);
			System.out.println("重绘默认图片");
		}
		else
		{
			g.drawImage(buffimg,0,0,500,400,this);
			System.out.println("重绘加载图片");
		}
	}



	@Override
	public void update(Graphics g)
	{}
}
