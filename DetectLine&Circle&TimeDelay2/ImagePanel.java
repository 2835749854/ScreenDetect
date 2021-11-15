import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ImagePanel extends JPanel{	//��ʾһ��ͼƬ�������
	String wholepath;
	ImageIcon icon;
	ImagePanel(String wholepath){		//��ʼ������ΪͼƬ��ַ					
		this.wholepath = wholepath;
		icon = new ImageIcon(wholepath);
	}
	
	ImagePanel(Image img){		//��ʼ������ΪImage��������(BufferedImage)
		icon = new ImageIcon();
		icon.setImage(img);
	}

	public void paintComponent(Graphics g){									//����ͼƬ�Ĵ�С
		super.paintComponent(g);
		g.drawImage(icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
}
