import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImagePanel extends JPanel{	//显示一张图片的面板类
	String wholepath;
	ImageIcon icon;
	ImagePanel(String wholepath){		//初始化参数为图片地址					
		this.wholepath = wholepath;
		icon = new ImageIcon(wholepath);
	}
	
	ImagePanel(Image img){		//初始化参数为Image及其子类(BufferedImage)
		icon = new ImageIcon();
		icon.setImage(img);
	}

	public void paintComponent(Graphics g){									//调整图片的大小
		super.paintComponent(g);
		g.drawImage(icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
}
