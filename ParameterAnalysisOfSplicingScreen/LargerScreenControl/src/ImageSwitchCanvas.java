import javax.swing.*;
import java.util.Timer;
import java.util.LinkedList;
import java.util.TimerTask;
import java.util.ListIterator;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Canvas;
class ImageSwitchCanvas extends Canvas
{
	double width = 0;
	double height = 0;
	int sizeImgs = 2;
	int indexImg = 0;
	boolean flag = true;
	LinkedList<BufferedImage> imgAll = new LinkedList<BufferedImage>();
	ListIterator<BufferedImage> imgIterator = null;
	ImageSwitchCanvas(String path)
	{
		try 
		{
			//��ȡ��Ļ��С
			Dimension screenSize   =   Toolkit.getDefaultToolkit().getScreenSize();
			width = screenSize.getWidth();
			height = screenSize.getHeight();
			
			//��ʼ���ļ����е�����ͼ��
			File file = new File(path);//��Ŀ¼�ļ���path���ļ�Ŀ¼��file���ļ�(��)����
			if (file.isDirectory())//����ʾ�˳���·�������ļ��Ƿ���һ��Ŀ¼ 
			{
				File[] list = file.listFiles();//���ļ�(��)�����������ļ��ŵ��ļ�����������
				for (int i = 0; i < list.length; i++) 
				{
					if (list[i].isDirectory()==false) //
					{
						BufferedImage buffimg = ImageIO.read(list[i]);
						imgAll.add(buffimg);
					}
					System.out.println("Find File:" + list[i].getName());
				}
				sizeImgs = imgAll.size();
				imgIterator = imgAll.listIterator();
				System.out.println("���ͼƬ��������");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	//��ʱ����ͼƬ�����
	/*void updateImg(ListIterator<BufferedImage> imgIterator)
	{
		//ListIterator<BufferedImage> imgIterator = allImg.listIterator();
		BufferedImage img = imgIterator.next();
		if(img!=null)
		{
			imageUpdate(img,JPanel.ALLBITS,0,0,(int)width,(int)height);
			updateUI();
		}
		else
		{
			System.out.println("ͼ��Ϊ�գ�");
		}
	}*/




	//�任���Ʊ���ͼ
	@Override
	public void paint(Graphics g)
	{
		try
		{
			if((flag==true)&&(indexImg<sizeImgs-1))
			{
				BufferedImage img = imgIterator.next();
				indexImg++;
				System.out.println(indexImg);
		    	g.drawImage(img,0,0,(int)width,(int)height,this);
			}
			else if((flag==true)&&(indexImg==sizeImgs-1))
			{
				BufferedImage img = imgIterator.next();
				flag = false;
		    	g.drawImage(img,0,0,(int)width,(int)height,this);
			}
			else if((flag==false)&&(indexImg>0))
			{
				BufferedImage img = imgIterator.previous();
				indexImg--;
				System.out.println(indexImg);
		    	g.drawImage(img,0,0,(int)width,(int)height,this);
			}
			else if((flag==false)&&(indexImg==0))
			{
				BufferedImage img = imgIterator.previous();
				flag = true;
		    	g.drawImage(img,0,0,(int)width,(int)height,this);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}




	@Override
	public void update(Graphics g)
	{
		paint(g);
	}




	//������ʱ����
	void startShootTask()
	{
		// ������ʱ����
        TimerTask timerTask = new TimingShootTask();
        //������ʱ������
        Timer timer = new Timer();
        /**�൱���¿�һ���߳�ȥִ�ж�ʱ������
         * ��ʱ��ִ�ж�ʱ������1 ���ʼִ������
         * ����ִ����ɺ󣬼�� 3 ���ٴ�ִ�У�ѭ������
         */
        timer.schedule(timerTask, 1000, 5000);
	}




	//��ʱ����
	class TimingShootTask extends TimerTask
	{
		public void run()
		{
			//updateImg(imgIterator);
			repaint();
			System.out.println("���һ�θ���");
		}
	}
}