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

	JMenuBar bar;			//�˵�ѡ��
	JMenu menu;
	JMenuItem opencam,opentest,starttest;
	JPanel back,videobk,shotpanel,curpanel,content;
	ImagePanel shotimg,curimg;
	JLabel shotlb,curlb,progress,test1,test2,test3;
	JButton starttest1,starttest2,starttest3,retest1,retest2,retest3;
	Webcam webcam;
	WebcamPanel panel;
	CalcData calc = new CalcData();	//������

	public MainInterface(){
		super("��Ļ�������");
		this.setSize(1280,720);
		this.setLocation(200,200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		init();

	}
	
	public void init(){
		bar = new JMenuBar();
		menu = new JMenu("����");
		opencam = new JMenuItem("�����");
		opentest = new JMenuItem("�������");
		starttest = new JMenuItem("��ʼ����");
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

		progress = new JLabel("������");
		progress.setBounds(200,5,100,25);
		content.add(progress);

		test1 = new JLabel("����һ:");
		test1.setBounds(50,50,100,25);
		content.add(test1);


		shotlb = new JLabel("����ͼƬ");
		shotlb.setBounds(800,200,100,25);
		shotlb.setOpaque(false);
		back.add(shotlb);
		back.repaint();

		curlb = new JLabel("�ȶ�ͼƬ");
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
			//processAllTest();  //��Ҫʹ�ô˷���������
			BufferedImage img = webcam.getImage();
			boolean isLine = detectLine(webcam.getImage());	//�ж�ֱ��
			//boolean isCircle = detectCircle(webcam.getImage()); //�ж�Բ��ȥ��ע�ͼ���ʹ��
			System.out.println(isLine);
			//System.out.println(isCircle);
		}

	}

	public boolean detectLine(BufferedImage bi){		//��һ�׶β��ԣ��ж��Ƿ�Ϊֱ�ߣ����ؼ��������Ҫ����������ͼƬ���������������ӦΪ����ͼƬ
		boolean isPassed = false;	//�Ƿ�ͨ������
		double k = 0.0,simplek = 0.0;

		if(shotimg!=null){		//���������������ʾͼƬ�������Ƴ��Ѿ���ʾ��ͼƬ
			shotpanel.remove(shotimg);
			shotpanel.repaint();
		}
		shotimg = new ImagePanel(bi);
		shotimg.setBounds(0,0,280,200);
		shotpanel.add(shotimg);
		shotpanel.repaint();

		if(curimg==null){		//ֱ�߲���ͼƬֻ��һ�ţ���ֻ�ڱȶ�ͼƬ�����û��ͼƬ��ʱ������
			curimg = new ImagePanel("src/1.png");
			curimg.setBounds(0,0,280,200);
			curpanel.add(curimg);
			curpanel.repaint();
		}

		BufferedImage processed = processingPic(bi);	//������ͼƬ���д���
		k = calc.calcSlope(processed);	//��������ɵ�ͼƬ�������������
		simplek = calc.calcSlope("src/1.png");	//��������ͼƬ��б��
		System.out.println(k);		//��ӡ�鿴����б��
		//System.out.println("CalcData "+calc.calcSlope(processed));
		if(Math.abs(k-simplek)<=CalcData.MAX_SLOPE_THRESHOLD)	//MAX_SLOPE_THRESHOLDΪб����������ֵ����ֵ�������ֵ����Ϊб�ʻ�����ͬ
			isPassed = true;
		return isPassed;
	}

	public boolean detectCircle(BufferedImage bi){		//�ж��Ƿ�ΪԲ�������ؼ����,��ǰΪ���Եĵڶ��׶�
		boolean isPassed = false;	
		double rdiff = 0.0;

		shotpanel.remove(shotimg);		//�Ƴ�֮ǰ��ͼƬ
		shotpanel.repaint();

		shotimg = new ImagePanel(bi);	
		shotimg.setBounds(0,0,280,200);
		shotpanel.add(shotimg);		//�����������ͼƬ
		shotpanel.repaint();
		
		curpanel.remove(curimg);
		curimg = new ImagePanel("src/circle.png");
		curimg.setBounds(0,0,280,200);
		curpanel.add(curimg);
		curpanel.repaint();

		BufferedImage processed = processingPic(bi);	//����ͼƬ
		rdiff = calc.calcRDiff(processed);				//�������ݼ�����CalcData����
		if(rdiff<=CalcData.Max_RDIFFERENCE_THRESHOLD)	//��С�����뾶�����Ϊ��Բ
			isPassed = true;
		return isPassed;
	}
	/*
	*����������ͼƬ���д���
	*���ڱȶ�������ֻ�о��Ժں;��԰׵�ͼƬ
	*�����ڴ˶Բ�����һ���Ҷ���ֵ(�������)��RGB������ð״���
	*/
	public BufferedImage processingPic(BufferedImage before){ 
		int width = before.getWidth();
		int height = before.getHeight();							
		int minx = before.getMinX();
		int miny = before.getMinY();
		int []rgb = new int[3];
		BufferedImage after = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);		//���ͼƬ
		for(int i = minx;i<width;i++){
			for(int j = miny;j<height;j++){
				int pixel = before.getRGB(i,j);
				rgb[0] = (pixel & 0xff0000)>>16;                                         
				rgb[1] = (pixel & 0xff00)>>8;
				rgb[2] = (pixel & 0xff);
				int gray = (rgb[0]*30+rgb[1]*59+rgb[2]*11+50)/100;		//�Ҷȼ��㹫ʽ
				if(gray<=45){					//���ڿ飬��ɫ���صĻҶ�����ڰ�ɫ�����׷���
					after.setRGB(i,j,new Color(0,0,0).getRGB());
				}else{
					after.setRGB(i,j,new Color(255,255,255).getRGB());
				}
			}
		}
		return after;
	}
	/*
	*�����Ƕ����ֲ��Եļ���
	*������ʹ�ô˷�����������
	*/
	public void processAllTest(){		//���ж�ֱ�ߣ��ж�Բ������ʱ�Ӽ�������
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

class TestFrame extends JFrame{			//���Դ���
	ImagePanel ip;
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
}
class ImagePanel extends JPanel{	//����ͼƬ�Ͷ���Ƶ(ͼƬ��������)���ܵ���壬Ŀǰֻ����ʾͼƬ����������
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

	public void paintComponent(Graphics g){									//����ͼƬ�Ĵ�С
		super.paintComponent(g);
		g.drawImage(icon.getImage(),0,0,this.getWidth(),this.getHeight(),this);
	}
}