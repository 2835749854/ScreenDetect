/**
*自定义菜单栏
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
*自定义菜单类
*
*这个类控制显示面板，坐标面板，文本域
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
	private JMenu[] menus = {new JMenu("文件"),new JMenu("相机")};
	private JMenuItem[] fileItem = {new JMenuItem("载入文件分析")};
	private JMenuItem[] cameraItem = {new JMenuItem("打开"),new JMenuItem("关闭"),new JMenuItem("拍摄"),new JMenuItem("打开定时拍摄")};
	
	
	JPanel showP = null;ImagePanel imgP = null;
	CoordinatePanel cp = null;HashMap<String,CustomCoordinate> allCoordinate = null;
	JScrollPane scrollPane = null;JTextArea jta = null;

	JDialog jd = new JDialog();//用于构建文件对话框
	FileDialog d1;
	CameraMenuOperation cma = null;
	WebcamPanel camera = null;
	
	//String newPath = null;//这是相机拍照返回的图片地址(相对)
	boolean cameraSwitch = false;//用于标记相机是否已经打开

    CustomMenuBar()//JFrame f为参数也可
	{
		//初始化菜单栏
		init();
		//初始化显示面板
		showP = new JPanel();showP.setBounds(0,0,500,400);
		imgP = new ImagePanel();
		showP.add(imgP);
		//初始化坐标面板
		cp = new CoordinatePanel();
		this.allCoordinate = cp.allCoordinate;
		//初始化带有文本域的滚动面板
		scrollPane = new JScrollPane();
		jta = new JTextArea();
		scrollPane.setBounds(0,400,500,155);
		scrollPane.setViewportView(jta);
		//预加载文件对话框
		d1 = new FileDialog(jd, "Open File", FileDialog.LOAD);//对话框对象随菜单创建一起创建，事件发生的时候显示出来
		//预加载相机操作类
		cma = new CameraMenuOperation();
	}



	void init()
	{
		
		/*
		*将所有菜单项添加到菜单栏
		*
		*/
		for(int i = 0;i<menus.length;i++)
		{
			this.add(menus[i]);
		}
		System.out.println("自定义完成添加");


		/*
		*给菜单项添加子项
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
		*为所有菜单子项添加监听
		*
		*/
		//文件子菜单
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
					//显示同步
					imgP.buffimg = ImageIO.read(new File(newPath));
					showP.add(imgP);
					imgP.repaint();
					System.out.println("完成repaint");

					//初始化处理服务模块并处理
					ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
					LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(newPath);
					String result = cas.analysis(allIndexInfo);
					jta.setText("");//清空文本
					jta.append(result);
						    
						
					//坐标模块同步数据
					Set<String> indexSet = allIndexInfo.keySet();//获得所有键的set集合
        			for(String key : indexSet)
     				{
            			//根据得到的键，获取对应的值
            			InfoOfIndex info = allIndexInfo.get(key);
						String indexR = key+"R",indexG = key+"G",indexB = key+"B";//这里注意命名的时候用的是11,21等编号01不能使用。所以这里用到了i+1,j+1
						CustomCoordinate ccR = allCoordinate.get(indexR);
						CustomCoordinate ccG = allCoordinate.get(indexG);
						CustomCoordinate ccB = allCoordinate.get(indexB);
						ccR.data = info.rSet;
						ccG.data = info.gSet;
						ccB.data = info.bSet;
						ccR.repaint();
						ccG.repaint();
						ccB.repaint();
						System.out.println(key+"屏绘制完成");
        			}
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		});




        //相机子菜单
		cameraItem[0].addActionListener(//打开相机
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

						cameraSwitch = true;//标记相机已经打开
					}
					catch (Exception inter)
					{
						inter.printStackTrace();
					}
				}
				else
				{
					System.out.println("相机已经打开！！！");
				}
			}
		});




		cameraItem[1].addActionListener(//关闭相机
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cameraSwitch==true)
				{
					
					try
					{
						camera.stop();
						cameraSwitch = false;//标记相机已经关闭
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
					System.out.println("相机已经关闭！！！");
				}
			}
		});



		cameraItem[2].addActionListener(//拍摄
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(cameraSwitch==true)//标记相机打开(才可以拍摄)
				{
					try 
					{
						String ip = "127.0.0.1"+"_";
						String path = "..//CameraImages//";
		    			// 1. 读取系统时间
        				Calendar calendar = Calendar.getInstance();
        				Date time = calendar.getTime();
            			// 2. 格式化系统时间
        				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        				String fileName = format.format(time); //获取系统当前时间并将其转换为string类型,fileName即文件名
            			// 3. 创建文件夹
        				String newPath = path + ip +fileName+".jpg";
        				File file = new File(newPath);
        				
				
				
						ImageIO.write(camera.getImage(), "JPG", file);
						System.out.println("图片保存至："+newPath);


						//显示同步
						camera.stop();
						cameraSwitch = false;//标记相机已经关闭
						showP.remove(camera);
						showP.add(imgP);
						imgP.buffimg = ImageIO.read(file);//初始化buffimg,imgp更新就不再使用默认buff
						imgP.repaint();
						System.out.println("完成repaint");

						//初始化处理服务模块并处理
						ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
						LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(newPath);
						String result = cas.analysis(allIndexInfo);
						jta.append(result);
						    
						
						//坐标模块同步数据
						Set<String> indexSet = allIndexInfo.keySet();//获得所有键的set集合
        				for(String key : indexSet)
     					{
            				//根据得到的键，获取对应的值
            				InfoOfIndex info = allIndexInfo.get(key);
							String indexR = key+"R",indexG = key+"G",indexB = key+"B";//这里注意命名的时候用的是11,21等编号01不能使用。所以这里用到了i+1,j+1
							CustomCoordinate ccR = allCoordinate.get(indexR);
							CustomCoordinate ccG = allCoordinate.get(indexG);
							CustomCoordinate ccB = allCoordinate.get(indexB);
							ccR.data = info.rSet;
							ccG.data = info.gSet;
							ccB.data = info.bSet;
							ccR.repaint();
							ccG.repaint();
							ccB.repaint();
							System.out.println(key+"屏绘制完成");
        				}
					}
					catch (IOException t)
					{
						t.printStackTrace();
					}
				}
				else
				{
					System.out.println("相机还没有打开！！！");
				}
			}
		});



		cameraItem[3].addActionListener(//打开定时拍摄任务
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// 创建定时任务
        		TimerTask timerTask = new TimingShootTask();
        		//创建定时器对象
        		Timer timer = new Timer();
        		/**相当于新开一个线程去执行定时器任务
        		 * 定时器执行定时器任务，3 秒后开始执行任务
        		 * 任务执行完成后，间隔 5 秒再次执行，循环往复
        		 */
        		timer.schedule(timerTask, 3000, 5000);
			}
		});
	}




	//根据图像绘制图像面板、并将结果添加到文本域
	synchronized void takeAction(boolean cameraSwitch,WebcamPanel camera,JPanel showP,ImagePanel imgP,JTextArea jta)
	{
		if(cameraSwitch==true)//标记相机打开(才可以拍摄)
		{
			try 
			{
				String ip = "127.0.0.1"+"_";
				String path = "..//CameraImages//";
		    	// 1. 读取系统时间
        		Calendar calendar = Calendar.getInstance();
        		Date time = calendar.getTime();
            	// 2. 格式化系统时间
        		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        		String fileName = format.format(time); //获取系统当前时间并将其转换为string类型,fileName即文件名
            	// 3. 创建文件夹
        		String newPath = path + ip +fileName+".jpg";
        		File file = new File(newPath);
        				
				
				
				ImageIO.write(camera.getImage(), "JPG", file);
				System.out.println("图片保存至："+newPath);


				//显示同步
				//camera.stop();
				//cameraSwitch = false;//标记相机已经关闭
				showP.remove(camera);
				showP.add(imgP);
				imgP.buffimg = ImageIO.read(file);//初始化buffimg,imgp更新就不再使用默认buff
				imgP.repaint();
				System.out.println("完成repaint");

				//初始化处理服务模块并处理
				ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
				LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(newPath);
				String result = cas.analysis(allIndexInfo);
				jta.append(result);
						    
						
				//坐标模块同步数据
				Set<String> indexSet = allIndexInfo.keySet();//获得所有键的set集合
        		for(String key : indexSet)
     			{
            		//根据得到的键，获取对应的值
            		InfoOfIndex info = allIndexInfo.get(key);
					String indexR = key+"R",indexG = key+"G",indexB = key+"B";//这里注意命名的时候用的是11,21等编号01不能使用。所以这里用到了i+1,j+1
					CustomCoordinate ccR = allCoordinate.get(indexR);
					CustomCoordinate ccG = allCoordinate.get(indexG);
					CustomCoordinate ccB = allCoordinate.get(indexB);
					ccR.data = info.rSet;
					ccG.data = info.gSet;
					ccB.data = info.bSet;
					ccR.repaint();
					ccG.repaint();
					ccB.repaint();
					System.out.println(key+"屏绘制完成");
        		}
			}
			catch (IOException t)
			{
				t.printStackTrace();
			}
		}
		else
		{
			System.out.println("相机还没有打开！！！");
		}
	}




    /**
    *定时拍摄任务
    *
    **/
	class TimingShootTask extends TimerTask 
	{
		public void run()
		{
			takeAction(cameraSwitch,camera,showP,imgP,jta);
			System.out.println("定时任务完成一次执行！");
		}
	}
}
