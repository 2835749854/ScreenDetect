import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
import java.util.concurrent.TimeUnit;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class MainInterface extends JFrame implements ActionListener{

	JMenuBar bar;			//菜单选项
	JMenu menu;
	JMenuItem opencam,opentest,starttest;
	JPanel back,videobk,shotpanel,curpanel,content;
	ImagePanel shotimg,curimg;
	JLabel shotlb,curlb,progress,test1,test2,test3;
	JButton starttest1,starttest2,starttest3,retest1,retest2,retest3;
	Webcam webcam;
	WebcamPanel panel;
	CalcData calc = new CalcData();	//计算类

	public MainInterface(){
		super("屏幕质量检测");
		this.setSize(1280,720);
		this.setLocation(200,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		init();

	}
	
	public void init(){
		bar = new JMenuBar();
		menu = new JMenu("操作");
		opencam = new JMenuItem("打开相机");
		opentest = new JMenuItem("测试面板");
		starttest = new JMenuItem("开始测试");
		back = new JPanel();
		back.setLayout(null);
		back.setBackground(Color.white);
		add(back);
		menu.add(opencam);
		menu.add(opentest);
		menu.add(starttest);
		bar.add(menu);
		setJMenuBar(bar);

		videobk = new JPanel();
		videobk.setBounds(0,0,670,670);
		videobk.setBackground(new Color(230,230,230));
		back.add(videobk);
		
		shotpanel = new JPanel();
		shotpanel.setBounds(690,0,280,200);
		shotpanel.setBackground(new Color(230,230,230));
		shotpanel.setLayout(null);
		back.add(shotpanel);

		curpanel = new JPanel();
		curpanel.setBounds(980,0,280,200);
		curpanel.setBackground(new Color(230,230,230));
		curpanel.setLayout(null);
		back.add(curpanel);
		
		content = new JPanel();
		content.setBounds(690,230,570,430);
		content.setBackground(new Color(230,230,230));
		back.add(content);

		progress = new JLabel("监控面板");
		progress.setBounds(200,5,100,25);
		content.add(progress);

		test1 = new JLabel("测试一:");
		test1.setBounds(50,50,100,25);
		content.add(test1);


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
		starttest.addActionListener(this);
		

	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==opencam){
			webcam = Webcam.getDefault();
			webcam.setViewSize(WebcamResolution.VGA.getSize());
			panel = new WebcamPanel(webcam);
			panel.setMirrored(true);
			panel.setImageSizeDisplayed(true);
			panel.setBounds(0,0,670,670);
			videobk.add(panel);
			videobk.repaint();
		}else if(e.getSource()==opentest){
			new TestFrame("src/circle.png");

		}else if(e.getSource()==starttest){
			//processAllTest();  //不要使用此方法！！！
			BufferedImage img = webcam.getImage();
			boolean isLine = detectLine(webcam.getImage());	//判断直线
			//boolean isCircle = detectCircle(webcam.getImage()); //判断圆，去掉注释即可使用
			System.out.println(isLine);
			//System.out.println(isCircle);
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

		if(curimg==null){		//直线测试图片只有一张，故只在比对图片面板上没有图片的时候添加
			curimg = new ImagePanel("src/1.png");
			curimg.setBounds(0,0,280,200);
			curpanel.add(curimg);
			curpanel.repaint();
		}

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

		shotpanel.remove(shotimg);		//移除之前的图片
		shotpanel.repaint();

		shotimg = new ImagePanel(bi);	
		shotimg.setBounds(0,0,280,200);
		shotpanel.add(shotimg);		//添加新拍摄的图片
		shotpanel.repaint();
		
		curpanel.remove(curimg);
		curimg = new ImagePanel("src/circle.png");
		curimg.setBounds(0,0,280,200);
		curpanel.add(curimg);
		curpanel.repaint();

		BufferedImage processed = processingPic(bi);	//处理图片
		rdiff = calc.calcRDiff(processed);				//交由数据计算类CalcData计算
		if(rdiff<=CalcData.Max_RDIFFERENCE_THRESHOLD)	//若小于最大半径差，则认为是圆
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
	/*
	*以下是对三种测试的集中
	*不建议使用此方法，有问题
	*/
	public void processAllTest(){		//将判断直线，判断圆，计算时延集中运行
		try{
			TimeUnit.SECONDS.sleep(5);
			BufferedImage img = webcam.getImage();
			boolean isLine = detectLine(img);
			TimeUnit.SECONDS.sleep(2);
			img = webcam.getImage();
			boolean isCircle = detectCircle(img);
		}catch(InterruptedException ie){
			ie.printStackTrace(System.out);
		}

	}

	public static void main(String []args){
		new MainInterface();
	}
}

class TestFrame extends JFrame{			//测试窗口
	ImagePanel ip;
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
}
class ImagePanel extends JPanel{	//具有图片和短视频(图片快速连播)功能的面板，目前只可显示图片，不可连播
	String wholepath;
	ImageIcon icon;
	ImagePanel(String wholepath){							
		this.wholepath = wholepath;
		icon = new ImageIcon(wholepath);
	}
	
	ImagePanel(Image img){
		icon = new ImageIcon();
		icon.setImage(img);
	}

	public void paintComponent(Graphics g){									//调整图片的大小
		super.paintComponent(g);
		g.drawImage(icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
}