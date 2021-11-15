import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
*��ʾ����ͼƬ�Ĵ����࣬
*ʵ������ʾͼƬ����������
*/
public class TestFrame extends JFrame{			//���Դ���
	ImagePanel ip,preimg;				//ipΪ��ʼ������ʾ��ͼƬ��preimg������ʱ����һ��ͼƬ
	Dimension screenSize;
	public TestFrame(String filepath){		//��ʼ��������һ��ͼƬ
		screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		setUndecorated(true);
		setSize(screenSize.width, screenSize.height);
		ip = new ImagePanel(filepath);
		ip.setSize(screenSize.width, screenSize.height);
		this.add(ip);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}
			}
		});
	}

	public void changeImg(String filepath){		//�л�ͼƬ
		this.remove(ip);
		ip = new ImagePanel(filepath);
		ip.setSize(screenSize.width, screenSize.height);
		this.add(ip);
		this.repaint();
	}

	public void changeImg(Image img){		//�л�ͼƬ
		if(preimg!=null)
			this.remove(preimg);
		this.remove(ip);
		ip = new ImagePanel(img);
		ip.setSize(screenSize.width, screenSize.height);
		this.add(ip);
		this.repaint();
	}

	public void playImg(ImagePanel imgp) {		//����ͼƬ
		if(preimg!=null)
			this.remove(preimg);
		this.remove(ip);
		imgp.setSize(screenSize.width, screenSize.height);
		//System.out.println("������");
		this.add(imgp);
		//this.invalidate();
		this.repaint();
		preimg = imgp;
	}
}