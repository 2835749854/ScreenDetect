import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
import java.util.concurrent.*;
import java.awt.geom.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class MainInterface extends JFrame implements ActionListener{

	JMenuBar bar;			//菜单选项
	JMenu menu;
	JMenuItem opencam,opentest,startstage1,startstage2,startstage3;
	JPanel back,videobk,shotpanel,curpanel,content;
	ImagePanel shotimg,curimg;
	JLabel shotlb,curlb,progress,test1,test2,test3;
	JButton starttest1,starttest2,starttest3,retest1,retest2,retest3,sampling;
	JTextArea result;
	JScrollPane scrollpane;
	Webcam webcam;
	WebcamPanel panel;
	CalcData calc = new CalcData();	//计算类
	ArrayList<ImagePanel> allpics = new ArrayList<ImagePanel>();		//保存程序起点后读取连播文件夹下图片并生成的panel
	ArrayList<BufferedImage> animashot = new ArrayList<BufferedImage>();	//保存测试三采样相机拍摄的图片
	ArrayList<PointLoc> point = new ArrayList<PointLoc>();		//存储采样图片的特征数据(圆心)
	ExecutorService exec = Executors.newCachedThreadPool();
	TestFrame tf = null;
	volatile boolean samFinished = false;		//采样进度
	volatile BufferedImage level3shot = null;			//第三阶段计算时延拍摄的图片

	public MainInterface(){
		super("屏幕质量检测");
		this.setSize(1275,720);
		this.setLocation(200,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		exec.execute(new CalcThread());
		exec.execute(new CalcTimeDelay());
		exec.execute(new Thread(new ReadPics()));
		init();

	}
	
	public void init(){
		bar = new JMenuBar();
		menu = new JMenu("操作");
		opencam = new JMenuItem("打开相机");
		opentest = new JMenuItem("测试面板");
		startstage1 = new JMenuItem("测试一");
		startstage2 = new JMenuItem("测试二");
		startstage3 = new JMenuItem("测试三");
		back = new JPanel();
		back.setLayout(null);
		back.setBackground(Color.white);
		add(back);
		menu.add(opencam);
		menu.add(opentest);
		menu.add(startstage1);
		menu.add(startstage2);
		menu.add(startstage3);
		bar.add(menu);
		setJMenuBar(bar);

		videobk = new JPanel();
		videobk.setBorder(BorderFactory.createRaisedBevelBorder());
		videobk.setBounds(0,0,670,670);
		videobk.setBackground(new Color(230,230,230));
		back.add(videobk);
		
		shotpanel = new JPanel();
		shotpanel.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		shotpanel.setBounds(690,0,280,200);
		shotpanel.setBackground(new Color(230,230,230));
		shotpanel.setLayout(null);
		back.add(shotpanel);

		curpanel = new JPanel();
		curpanel.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		curpanel.setBounds(980,0,280,200);
		curpanel.setBackground(new Color(230,230,230));
		curpanel.setLayout(null);
		back.add(curpanel);
		
		content = new JPanel();
		content.setLayout(null);
		content.setBounds(690,230,570,430);
		content.setBorder(BorderFactory.createLineBorder(Color.gray,1));
		content.setBackground(new Color(230,230,230));
		back.add(content);

		progress = new JLabel("监控面板");
		progress.setBounds(200,5,100,25);
		content.add(progress);

		test1 = new JLabel("测试一:");
		test1.setBounds(50,50,100,25);
		content.add(test1);

		starttest1 = new JButton("开始测试");
		starttest1.setBackground(Color.white);
		starttest1.setFocusable(false);
		starttest1.setBounds(200,50,100,25);
		content.add(starttest1);

		test2 = new JLabel("测试二:");
		test2.setBounds(50,100,100,25);
		content.add(test2);

		starttest2 = new JButton("开始测试");
		starttest2.setBackground(Color.white);
		starttest2.setFocusable(false);
		starttest2.setBounds(200,100,100,25);
		content.add(starttest2);

		test3 = new JLabel("测试三:");
		test3.setBounds(50,150,100,25);
		content.add(test3);

		starttest3 = new JButton("开始测试");
		starttest3.setBackground(Color.white);
		starttest3.setFocusable(false);
		starttest3.setEnabled(false);
		starttest3.setBounds(200,150,100,25);
		content.add(starttest3);

		sampling = new JButton("开始采样");
		sampling.setBackground(Color.white);
		sampling.setFocusable(false);
		sampling.setBounds(350,150,100,25);
		content.add(sampling);

		result = new JTextArea();
		result.setBounds(5,200,56,225);
		result.setEditable(false);
		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		scrollpane = new JScrollPane();
		scrollpane.setBounds(5,200,560,225);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		scrollpane.setViewportView(result);
		content.add(scrollpane);
		content.repaint();

		shotlb = new JLabel("拍摄图片");
		shotlb.setBounds(800,200,100,25);
		shotlb.setOpaque(false);
		back.add(shotlb);
		back.repaint();

		curlb = new JLabel("比对图片");
		curlb.setBounds(1080,200,100,25);
		curlb.setOpaque(false);
		back.add(curlb);
		back.repaint();

		opencam.addActionListener(this);
		opentest.addActionListener(this);
		startstage1.addActionListener(this);
		startstage2.addActionListener(this);
		startstage3.addActionListener(this);
		starttest1.addActionListener(this);
		starttest2.addActionListener(this);
		starttest3.addActionListener(this);
		sampling.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==opencam){
			webcam = Webcam.getDefault();
			webcam.setViewSize(WebcamResolution.VGA.getSize());
			panel = new WebcamPanel(webcam);
			//panel.setMirrored(true);			//镜像关闭
			panel.setImageSizeDisplayed(true);
			panel.setBounds(0,0,670,670);
			videobk.add(panel);
			videobk.repaint();
		}else if(e.getSource()==opentest){
			tf = new TestFrame("src/1.png");		//切换不同模式的图片

		}else if(e.getSource()==startstage1){
			tf.changeImg("src/1.png");

		}else if(e.getSource()==startstage2){		//显示动画的第一张图片，用于相机位置的调整
			tf.changeImg("src/circle.png");

		}else if(e.getSource()==startstage3){
			tf.changeImg("src/Anima/1.png");
		}else if(e.getSource()==starttest1){
			BufferedImage img = webcam.getImage();
			boolean isLine = detectLine(webcam.getImage());	//判断直线

			if(isLine){
				result.setText(result.getText()+"直线斜率测试通过！\n");
			}else{
				result.setText(result.getText()+"直线斜率测试未通过！详情请见DOS窗口 \n");
			}
			
		}else if(e.getSource()==starttest2){
			BufferedImage img = webcam.getImage();
			boolean isCircle = detectCircle(webcam.getImage()); //判断圆

			if(isCircle){
				result.setText(result.getText()+"圆的半径长度测试通过！\n");
			}else{
				result.setText(result.getText()+"圆的半径长度测试未通过！详情请见DOS窗口 \n");
			}

		}else if(e.getSource()==starttest3){	//开始第三阶段时延测试
			exec.execute(new Thread(new PlayThread()));
			exec.execute(new Thread(new ShotThread(false)));		//进行一次非采样拍摄，定时拍摄时间为500ms

			starttest3.setEnabled(false);
		}else if(e.getSource()==sampling){		//开始样本采样
			starttest3.setEnabled(true);
			result.setText(result.getText()+"正在采样和计算特征数据,请稍等……  \n");
			exec.execute(new Thread(new PlayThread(false,true)));	//启动连播线程，只需要采样一个完整周期的动画，所以关闭重复播放功能
			exec.execute(new Thread(new ShotThread()));				//启动拍摄线程，模式为采样拍摄

		}

	}

	public boolean detectLine(BufferedImage bi){		//第一阶段测试，判断是否为直线，返回检测结果，需要对拍照所得图片处理，参数传入的应为拍摄图片
		boolean isPassed = false;	//是否通过测试
		double k = 0.0,simplek = 0.0;

		if(shotimg!=null){		//若拍摄面板上已显示图片，则先移除已经显示的图片
			shotpanel.remove(shotimg);
			shotpanel.repaint();
		}
		shotimg = new ImagePanel(bi);
		shotimg.setBounds(0,0,280,200);
		shotpanel.add(shotimg);
		shotpanel.repaint();

		if(curimg!=null)		//直线测试图片只有一张，故只在比对图片面板上没有图片的时候添加
			curpanel.remove(curimg);	
		curimg = new ImagePanel("src/1.png");
		curimg.setBounds(0,0,280,200);
		curpanel.add(curimg);
		curpanel.repaint();

		BufferedImage processed = processingPic(bi);	//对拍摄图片进行处理
		k = calc.calcSlope(processed);	//将处理完成的图片交给计算类计算
		simplek = calc.calcSlope("src/1.png");	//计算样本图片的斜率
		System.out.println(k);		//打印查看样本斜率
		//System.out.println("CalcData "+calc.calcSlope(processed));
		if(Math.abs(k-simplek)<=CalcData.MAX_SLOPE_THRESHOLD)	//MAX_SLOPE_THRESHOLD为斜率误差最大阈值，差值低于这个值则认为斜率基本相同
			isPassed = true;
		return isPassed;
	}

	public boolean detectCircle(BufferedImage bi){		//判断是否为圆，并返回检测结果,当前为测试的第二阶段
		boolean isPassed = false;	
		double rdiff = 0.0;
		if(shotimg!=null){
			shotpanel.remove(shotimg);		//移除之前的图片
			shotpanel.repaint();
		}
		shotimg = new ImagePanel(bi);	
		shotimg.setBounds(0,0,280,200);
		shotpanel.add(shotimg);		//添加新拍摄的图片
		shotpanel.repaint();
		if(curimg!=null)
			curpanel.remove(curimg);
		curimg = new ImagePanel("src/circle.png");
		curimg.setBounds(0,0,280,200);
		curpanel.add(curimg);
		curpanel.repaint();

		BufferedImage processed = processingPic(bi);	//处理图片
		rdiff = calc.calcRDiff(processed);				//交由数据计算类CalcData计算
		if(rdiff<=CalcData.MAX_RDIFFERENCE_THRESHOLD)	//若小于最大半径差，则认为是圆
			isPassed = true;
		return isPassed;
	}
	/*
	*对相机拍摄的图片进行处理
	*由于比对样本是只有绝对黑和绝对白的图片
	*所以在此对不满足一定灰度阈值(方便计算)的RGB块进行置白处理
	*/
	public BufferedImage processingPic(BufferedImage before){ 
		int width = before.getWidth();
		int height = before.getHeight();							
		int minx = before.getMinX();
		int miny = before.getMinY();
		int []rgb = new int[3];
		BufferedImage after = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);		//结果图片
		for(int i = minx;i<width;i++){
			for(int j = miny;j<height;j++){
				int pixel = before.getRGB(i,j);
				rgb[0] = (pixel & 0xff0000)>>16;                                         
				rgb[1] = (pixel & 0xff00)>>8;
				rgb[2] = (pixel & 0xff);
				int gray = (rgb[0]*30+rgb[1]*59+rgb[2]*11+50)/100;		//灰度计算公式
				if(gray<=45){					//检测黑块，黑色像素的灰度相较于白色，容易发现
					after.setRGB(i,j,new Color(0,0,0).getRGB());
				}else{
					after.setRGB(i,j,new Color(255,255,255).getRGB());
				}
			}
		}
		return after;
	}

	public static void main(String []args){
		new MainInterface();
	}

	/**
	*读取一个文件夹下所有图片并生成panel，
	*并将生成的panel放入顺序表
	*/
	class ReadPics implements Runnable{				//在程序启动时读取动画的图片序列并放入缓存，加快动画播放的流程度(内存消耗大)
		@Override
		public void run(){
			String path = "src/Anima/";
			File file = null;
			BufferedImage bi = null;
			try{
				for(int i = 1;i<=48;i++){
					file = new File(path+i+".png");
					//System.out.println(path+i+".png");
					bi = ImageIO.read(file);
					allpics.add(new ImagePanel(bi));
				}
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}

	/**
	*播放线程
	*连播时启动此线程
	*/
	class PlayThread implements Runnable{
		boolean isRepeat = true,isSampling = false;		//是否重复播放,默认状态为重复播放
		int time = 20;			//播放时间间隔
		PlayThread(boolean repeat){
			isRepeat = repeat;
		}

		PlayThread(boolean repeat,boolean sampling){
			isRepeat = repeat;
			isSampling = sampling;
		}

		PlayThread(){}

		@Override
		public void run(){
			int i = 0,count = 0; //count为计数器
			if(isSampling)		//若采样模式一已打开，则将时间播放时间间隔更改为150毫秒
				time = 150;
			while(true){	
				try{
					Thread.sleep(time);		//连播播放时间间隔
				}catch(InterruptedException  ie){
					ie.printStackTrace(System.out);
				}
				tf.playImg(allpics.get(i));
				i = (i+1)%47;			//将i控制在范围内，实现循环播放
				//System.out.println("当前张数:"+i);
				if(++count==47&&!isRepeat)		//计数器达到最后一张图片位置且重复播放关闭的话，播放一遍即停止播放
					break;
			}
		}
	}

	class ShotThread implements Runnable{	//拍摄线程，默认模式是采样拍摄
		boolean isSampling = true;			//采样模式，若此值为真，则连续拍摄多张样本，若为假，则定时拍摄单张图片

		ShotThread(boolean sampling){
			isSampling = sampling;
		}

		ShotThread(){}

		@Override
		public void run(){
			int i = 0;
			try{
				if(isSampling){			//等待采样完成
					Thread.sleep(10);	//第一次拍摄在处于连播线程第一张图片显示后的10ms(采样连播时间间隔为150ms)
					while(++i<=101){
						animashot.add(webcam.getImage());		//开始拍摄，为尽快完成此线程的任务以及保证拍摄的有序性，在此只保存拍摄的图片，不进行图片处理
						System.out.println("拍摄了"+i);
						Thread.sleep(60);
					}
					samFinished = true;			//表示采样完成
				}else{
					Thread.sleep(500);
					level3shot = webcam.getImage();		//拍摄
				}
			}catch(InterruptedException ie){
				ie.printStackTrace(System.out);
			}
		}
	}

	class CalcThread extends Thread{	//线程一直运行，监控采样状态samFinished，一旦采样完成，立即开始计算特征数据
		CalcThread(){
			setDaemon(true);	//设置为后台线程，随主线程关闭而关闭
		}

		@Override
		public void run(){
			PointLoc prev;
			PointLoc cur;
			for(;;){
				if(samFinished){
					result.setText(result.getText()+"采样完成！开始处理采样图片！\n");
					for(int i = 0;i<animashot.size();i++){
						animashot.set(i,processingPic(animashot.get(i)));		//对采样图片进行二值化处理
						/*try{
							ImageIO.write(animashot.get(i),"png",new File("respic/"+i+".png"));		//检测图片处理结果用
						}catch(Exception e){
							e.printStackTrace(System.out);
						}*/
					}
					result.setText(result.getText()+"图片处理完成！开始计算特征数据！\n");
					for(int i = 0;i<animashot.size();i++){
						prev = i != 0 ? calc.calcRCenter(animashot.get(i-1)) : new PointLoc(-1,-1);
						cur = calc.calcRCenter(animashot.get(i));
						if(Math.abs(cur.getX()-prev.getX())>CalcData.MAX_JITTER_THRESHOLD){	//若x轴变化的值大于抖动阈值，则此坐标其添加到顺序表
							point.add(cur);
							/*System.out.println("之前的圆心坐标")
							System.out.println("圆心坐标: "+cur.getX()+","+cur.getY());*/
							System.out.print("已存！    当前: "+cur.getX()+","+cur.getY()+"\t");
							System.out.println("之前: "+prev.getX()+","+prev.getY());
						}else{
							System.out.print("未存！    当前: "+cur.getX()+","+cur.getY()+"\t");
							System.out.println("之前: "+prev.getX()+","+prev.getY());
						}
					}
					//System.out.println("共得到 : "+point.size()+" 个特征数据");
					result.setText(result.getText()+"共得到 : "+point.size()+" 个特征数据！\n");
					result.setText(result.getText()+"特征数据计算完成！可以开始计算时延！\n");
					samFinished = false;
				}
			}
		}
	}

	class CalcTimeDelay extends Thread{	//监控第三阶段时延计算图片拍摄的线程，若图片拍摄，则开始计算时延
		CalcTimeDelay(){
			setDaemon(true);		//设置为后台线程
		}

		@Override
		public void run(){
			for(;;){
				if(level3shot!=null){		//等待测试三拍摄图片
					if(shotimg!=null){		//若拍摄面板上已显示图片，则先移除已经显示的图片
						shotpanel.remove(shotimg);
						shotpanel.repaint();
					}
					shotimg = new ImagePanel(level3shot);
					shotimg.setBounds(0,0,280,200);
					shotpanel.add(shotimg);
					shotpanel.repaint();
					level3shot = processingPic(level3shot);		//对拍摄的图片进行处理(二值化)
					System.out.println("正在计算时延");
					PointLoc p = calc.calcRCenter(level3shot);		//计算圆心坐标
					int index = calc.getSimilarestIndex(point,p);		//用计算所得的圆心坐标与特征数据集比较，获取最相似的特征数据的位置
					System.out.println(index);					//查看位置
					System.out.println("屏幕时延为  "+(index*20-500)+"  ms");
					result.setText(result.getText()+"计算任务完成，(参考)屏幕时延为： "+Math.abs(index*20-500)+ "ms！ \n");
					level3shot = null;
				}
			}
		}
	}
}
