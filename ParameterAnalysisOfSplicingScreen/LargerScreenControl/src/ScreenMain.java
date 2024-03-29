import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
public class ScreenMain
{
	final void init(JFrame f,int width,int height,String path)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				//加载图像变化面板
				ImageSwitchCanvas isc = new ImageSwitchCanvas(path);
				isc.startShootTask();
				f.add(isc);
				
				//设置JFrame
				f.addKeyListener(
                    new KeyListener(){
                    public void keyReleased(KeyEvent e) 
				    {
                      if(e.getKeyCode() == KeyEvent.VK_ESCAPE ) 
				      {  
                         //f.dispose();
						 System.exit(0);
                      }
                    }
                    public void keyTyped(KeyEvent e) {}
                    public void keyPressed(KeyEvent e) {}
                }); 
				f.setUndecorated(true);//不显示JFrame边框
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocation(0,0);
				f.setSize(width,height);
				f.setVisible(true);
			}
		});
	}




	public static void main(String[] args) 
	{
		//获取屏幕大小
		Dimension screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		//启动主程序
		String path = "LargeScreenTestPic";
		new ScreenMain().init(new JFrame(),(int)width,(int)height,path);
	}
}
