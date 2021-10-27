import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Dimension;
/**
*ͼ����壬���ڽ���ͼƬ�ļ�,�����仭����ʾ
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
			System.out.println("�ػ�Ĭ��ͼƬ");
		}
		else
		{
			g.drawImage(buffimg,0,0,500,400,this);
			System.out.println("�ػ����ͼƬ");
		}
	}



	@Override
	public void update(Graphics g)
	{}
}
