import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
*显示测试图片的窗口类，
*实现了显示图片和连播功能
*/
public class TestFrame extends JFrame{			//测试窗口
	ImagePanel ip,preimg;				//ip为初始化后显示的图片，preimg是连播时的上一张图片
	Dimension screenSize;
	public TestFrame(String filepath){		//初始化并附上一张图片
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

	public void changeImg(String filepath){		//切换图片
		this.remove(ip);
		ip = new ImagePanel(filepath);
		ip.setSize(screenSize.width, screenSize.height);
		this.add(ip);
		this.repaint();
	}

	public void changeImg(Image img){		//切换图片
		if(preimg!=null)
			this.remove(preimg);
		this.remove(ip);
		ip = new ImagePanel(img);
		ip.setSize(screenSize.width, screenSize.height);
		this.add(ip);
		this.repaint();
	}

	public void playImg(ImagePanel imgp) {		//播放图片
		if(preimg!=null)
			this.remove(preimg);
		this.remove(ip);
		imgp.setSize(screenSize.width, screenSize.height);
		//System.out.println("播放中");
		this.add(imgp);
		//this.invalidate();
		this.repaint();
		preimg = imgp;
	}
}