/**
*�Զ���˵���
*
*
**/
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JDialog;
/**
*�Զ���˵���
*
*����������ʾ��壬������壬�ı���
*
*/
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;


class CustomMenuBar extends JMenuBar 
{
	private JMenu[] menus = {new JMenu("�ļ�"),new JMenu("���")};
	private JMenuItem[] fileItem = {new JMenuItem("�����ļ�����")};
	private JMenuItem[] cameraItem = {new JMenuItem("��"),new JMenuItem("�ر�"),new JMenuItem("����"),new JMenuItem("�򿪶�ʱ����")};
	
	
	JPanel showP = null;ImagePanel imgP = null;
	CoordinatePanel cp = null;HashMap<String,CustomCoordinate> allCoordinate = null;
	JScrollPane scrollPane = null;JTextArea jta = null;

	JDialog jd = new JDialog();//���ڹ����ļ��Ի���
	FileDialog d1;
	CameraMenuOperation cma = null;
	WebcamPanel camera = null;
	
	//String newPath = null;//����������շ��ص�ͼƬ��ַ(���)
	boolean cameraSwitch = false;//���ڱ������Ƿ��Ѿ���

    CustomMenuBar()//JFrame fΪ����Ҳ��
	{
		//��ʼ���˵���
		init();
		//��ʼ����ʾ���
		showP = new JPanel();showP.setBounds(0,0,500,400);
		imgP = new ImagePanel();
		showP.add(imgP);
		//��ʼ���������
		cp = new CoordinatePanel();
		this.allCoordinate = cp.allCoordinate;
		//��ʼ�������ı���Ĺ������
		scrollPane = new JScrollPane();
		jta = new JTextArea();
		scrollPane.setBounds(0,400,500,155);
		scrollPane.setViewportView(jta);
		//Ԥ�����ļ��Ի���
		d1 = new FileDialog(jd, "Open File", FileDialog.LOAD);//�Ի��������˵�����һ�𴴽����¼�������ʱ����ʾ����
		//Ԥ�������������
		cma = new CameraMenuOperation();
	}



	void init()
	{
		
		/*
		*�����в˵�����ӵ��˵���
		*
		*/
		for(int i = 0;i<menus.length;i++)
		{
			this.add(menus[i]);
		}
		System.out.println("�Զ���������");


		/*
		*���˵����������
		*
		*/
		for(int i = 0;i<fileItem.length;i++)
		{
			menus[0].add(fileItem[i]);
		}

		for(int i = 0;i<cameraItem.length;i++)
		{
			menus[1].add(cameraItem[i]);
		}


		/*
		*Ϊ���в˵�������Ӽ���
		*
		*/
		//�ļ��Ӳ˵�
		fileItem[0].addActionListener(
		new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					d1.setVisible(true);


					String newPath = d1.getDirectory() + d1.getFile();
					System.out.println(newPath);
					//��ʾͬ��
					imgP.buffimg = ImageIO.read(new File(newPath));
					showP.add(imgP);
					imgP.repaint();
					System.out.println("���repaint");

					//��ʼ���������ģ�鲢����
					ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
					LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(newPath);
					String result = cas.analysis(allIndexInfo);
					jta.setText("");//����ı�
					jta.append(result);
						    
						
					//����ģ��ͬ������
					Set<String> indexSet = allIndexInfo.keySet();//������м���set����
        			for(String key : indexSet)
     				{
            			//���ݵõ��ļ�����ȡ��Ӧ��ֵ
            			InfoOfIndex info = allIndexInfo.get(key);
						String indexR = key+"R",indexG = key+"G",indexB = key+"B";//����ע��������ʱ���õ���11,21�ȱ��01����ʹ�á����������õ���i+1,j+1
						CustomCoordinate ccR = allCoordinate.get(indexR);
						CustomCoordinate ccG = allCoordinate.get(indexG);
						CustomCoordinate ccB = allCoordinate.get(indexB);
						ccR.data = info.rSet;
						ccG.data = info.gSet;
						ccB.data = info.bSet;
						ccR.repaint();
						ccG.repaint();
						ccB.repaint();
						System.out.println(key+"���������");
        			}
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		});




        //����Ӳ˵�
		cameraItem[0].addActionListener(//�����
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cameraSwitch==false)
				{
					
					try
					{
						camera = cma.startACamera();
						showP.remove(imgP);
						showP.add(camera);
						camera.updateUI();

						cameraSwitch = true;//�������Ѿ���
					}
					catch (Exception inter)
					{
						inter.printStackTrace();
					}
				}
				else
				{
					System.out.println("����Ѿ��򿪣�����");
				}
			}
		});




		cameraItem[1].addActionListener(//�ر����
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cameraSwitch==true)
				{
					
					try
					{
						camera.stop();
						cameraSwitch = false;//�������Ѿ��ر�
						showP.remove(camera);
						showP.add(imgP);
						imgP.repaint();
						
					}
					catch (Exception inter)
					{
						inter.printStackTrace();
					}
				}
				else
				{
					System.out.println("����Ѿ��رգ�����");
				}
			}
		});



		cameraItem[2].addActionListener(//����
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cameraSwitch==true)//��������(�ſ�������)
				{
					try 
					{
						String ip = "127.0.0.1"+"_";
						String path = "..//CameraImages//";
		    			// 1. ��ȡϵͳʱ��
        				Calendar calendar = Calendar.getInstance();
        				Date time = calendar.getTime();
            			// 2. ��ʽ��ϵͳʱ��
        				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        				String fileName = format.format(time); //��ȡϵͳ��ǰʱ�䲢����ת��Ϊstring����,fileName���ļ���
            			// 3. �����ļ���
        				String newPath = path + ip +fileName+".jpg";
        				File file = new File(newPath);
        				
				
				
						ImageIO.write(camera.getImage(), "JPG", file);
						System.out.println("ͼƬ��������"+newPath);


						//��ʾͬ��
						camera.stop();
						cameraSwitch = false;//�������Ѿ��ر�
						showP.remove(camera);
						showP.add(imgP);
						imgP.buffimg = ImageIO.read(file);//��ʼ��buffimg,imgp���¾Ͳ���ʹ��Ĭ��buff
						imgP.repaint();
						System.out.println("���repaint");

						//��ʼ���������ģ�鲢����
						ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
						LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(newPath);
						String result = cas.analysis(allIndexInfo);
						jta.append(result);
						    
						
						//����ģ��ͬ������
						Set<String> indexSet = allIndexInfo.keySet();//������м���set����
        				for(String key : indexSet)
     					{
            				//���ݵõ��ļ�����ȡ��Ӧ��ֵ
            				InfoOfIndex info = allIndexInfo.get(key);
							String indexR = key+"R",indexG = key+"G",indexB = key+"B";//����ע��������ʱ���õ���11,21�ȱ��01����ʹ�á����������õ���i+1,j+1
							CustomCoordinate ccR = allCoordinate.get(indexR);
							CustomCoordinate ccG = allCoordinate.get(indexG);
							CustomCoordinate ccB = allCoordinate.get(indexB);
							ccR.data = info.rSet;
							ccG.data = info.gSet;
							ccB.data = info.bSet;
							ccR.repaint();
							ccG.repaint();
							ccB.repaint();
							System.out.println(key+"���������");
        				}
					}
					catch (IOException t)
					{
						t.printStackTrace();
					}
				}
				else
				{
					System.out.println("�����û�д򿪣�����");
				}
			}
		});



		cameraItem[3].addActionListener(//�򿪶�ʱ��������
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// ������ʱ����
        		TimerTask timerTask = new TimingShootTask();
        		//������ʱ������
        		Timer timer = new Timer();
        		/**�൱���¿�һ���߳�ȥִ�ж�ʱ������
        		 * ��ʱ��ִ�ж�ʱ������3 ���ʼִ������
        		 * ����ִ����ɺ󣬼�� 5 ���ٴ�ִ�У�ѭ������
        		 */
        		timer.schedule(timerTask, 3000, 5000);
			}
		});
	}




	//����ͼ�����ͼ����塢���������ӵ��ı���
	synchronized void takeAction(boolean cameraSwitch,WebcamPanel camera,JPanel showP,ImagePanel imgP,JTextArea jta)
	{
		if(cameraSwitch==true)//��������(�ſ�������)
		{
			try 
			{
				String ip = "127.0.0.1"+"_";
				String path = "..//CameraImages//";
		    	// 1. ��ȡϵͳʱ��
        		Calendar calendar = Calendar.getInstance();
        		Date time = calendar.getTime();
            	// 2. ��ʽ��ϵͳʱ��
        		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        		String fileName = format.format(time); //��ȡϵͳ��ǰʱ�䲢����ת��Ϊstring����,fileName���ļ���
            	// 3. �����ļ���
        		String newPath = path + ip +fileName+".jpg";
        		File file = new File(newPath);
        				
				
				
				ImageIO.write(camera.getImage(), "JPG", file);
				System.out.println("ͼƬ��������"+newPath);


				//��ʾͬ��
				//camera.stop();
				//cameraSwitch = false;//�������Ѿ��ر�
				showP.remove(camera);
				showP.add(imgP);
				imgP.buffimg = ImageIO.read(file);//��ʼ��buffimg,imgp���¾Ͳ���ʹ��Ĭ��buff
				imgP.repaint();
				System.out.println("���repaint");

				//��ʼ���������ģ�鲢����
				ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
				LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(newPath);
				String result = cas.analysis(allIndexInfo);
				jta.append(result);
						    
						
				//����ģ��ͬ������
				Set<String> indexSet = allIndexInfo.keySet();//������м���set����
        		for(String key : indexSet)
     			{
            		//���ݵõ��ļ�����ȡ��Ӧ��ֵ
            		InfoOfIndex info = allIndexInfo.get(key);
					String indexR = key+"R",indexG = key+"G",indexB = key+"B";//����ע��������ʱ���õ���11,21�ȱ��01����ʹ�á����������õ���i+1,j+1
					CustomCoordinate ccR = allCoordinate.get(indexR);
					CustomCoordinate ccG = allCoordinate.get(indexG);
					CustomCoordinate ccB = allCoordinate.get(indexB);
					ccR.data = info.rSet;
					ccG.data = info.gSet;
					ccB.data = info.bSet;
					ccR.repaint();
					ccG.repaint();
					ccB.repaint();
					System.out.println(key+"���������");
        		}
			}
			catch (IOException t)
			{
				t.printStackTrace();
			}
		}
		else
		{
			System.out.println("�����û�д򿪣�����");
		}
	}




    /**
    *��ʱ��������
    *
    **/
	class TimingShootTask extends TimerTask 
	{
		public void run()
		{
			takeAction(cameraSwitch,camera,showP,imgP,jta);
			System.out.println("��ʱ�������һ��ִ�У�");
		}
	}
}
