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
				//����ͼ��仯���
				ImageSwitchCanvas isc = new ImageSwitchCanvas(path);
				isc.startShootTask();
				f.add(isc);
				
				//����JFrame
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
				f.setUndecorated(true);//����ʾJFrame�߿�
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocation(0,0);
				f.setSize(width,height);
				f.setVisible(true);
			}
		});
	}




	public static void main(String[] args) 
	{
		//��ȡ��Ļ��С
		Dimension screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		//����������
		String path = "LargeScreenTestPic";
		new ScreenMain().init(new JFrame(),(int)width,(int)height,path);
	}
}