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

	JMenuBar bar;			//�˵�ѡ��
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
	CalcData calc = new CalcData();	//������
	ArrayList<ImagePanel> allpics = new ArrayList<ImagePanel>();		//������������ȡ�����ļ�����ͼƬ�����ɵ�panel
	ArrayList<BufferedImage> animashot = new ArrayList<BufferedImage>();	//���������������������ͼƬ
	ArrayList<PointLoc> point = new ArrayList<PointLoc>();		//�洢����ͼƬ����������(Բ��)
	ExecutorService exec = Executors.newCachedThreadPool();
	TestFrame tf = null;
	volatile boolean samFinished = false;		//��������
	volatile BufferedImage level3shot = null;			//�����׶μ���ʱ�������ͼƬ

	public MainInterface(){
		super("��Ļ�������");
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
		menu = new JMenu("����");
		opencam = new JMenuItem("�����");
		opentest = new JMenuItem("�������");
		startstage1 = new JMenuItem("����һ");
		startstage2 = new JMenuItem("���Զ�");
		startstage3 = new JMenuItem("������");
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

		progress = new JLabel("������");
		progress.setBounds(200,5,100,25);
		content.add(progress);

		test1 = new JLabel("����һ:");
		test1.setBounds(50,50,100,25);
		content.add(test1);

		starttest1 = new JButton("��ʼ����");
		starttest1.setBackground(Color.white);
		starttest1.setFocusable(false);
		starttest1.setBounds(200,50,100,25);
		content.add(starttest1);

		test2 = new JLabel("���Զ�:");
		test2.setBounds(50,100,100,25);
		content.add(test2);

		starttest2 = new JButton("��ʼ����");
		starttest2.setBackground(Color.white);
		starttest2.setFocusable(false);
		starttest2.setBounds(200,100,100,25);
		content.add(starttest2);

		test3 = new JLabel("������:");
		test3.setBounds(50,150,100,25);
		content.add(test3);

		starttest3 = new JButton("��ʼ����");
		starttest3.setBackground(Color.white);
		starttest3.setFocusable(false);
		starttest3.setEnabled(false);
		starttest3.setBounds(200,150,100,25);
		content.add(starttest3);

		sampling = new JButton("��ʼ����");
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
			//panel.setMirrored(true);			//����ر�
			panel.setImageSizeDisplayed(true);
			panel.setBounds(0,0,670,670);
			videobk.add(panel);
			videobk.repaint();
		}else if(e.getSource()==opentest){
			tf = new TestFrame("src/1.png");		//�л���ͬģʽ��ͼƬ

		}else if(e.getSource()==startstage1){
			tf.changeImg("src/1.png");

		}else if(e.getSource()==startstage2){		//��ʾ�����ĵ�һ��ͼƬ���������λ�õĵ���
			tf.changeImg("src/circle.png");

		}else if(e.getSource()==startstage3){
			tf.changeImg("src/Anima/1.png");
		}else if(e.getSource()==starttest1){
			BufferedImage img = webcam.getImage();
			boolean isLine = detectLine(webcam.getImage());	//�ж�ֱ��

			if(isLine){
				result.setText(result.getText()+"ֱ��б�ʲ���ͨ����\n");
			}else{
				result.setText(result.getText()+"ֱ��б�ʲ���δͨ�����������DOS���� \n");
			}
			
		}else if(e.getSource()==starttest2){
			BufferedImage img = webcam.getImage();
			boolean isCircle = detectCircle(webcam.getImage()); //�ж�Բ

			if(isCircle){
				result.setText(result.getText()+"Բ�İ뾶���Ȳ���ͨ����\n");
			}else{
				result.setText(result.getText()+"Բ�İ뾶���Ȳ���δͨ�����������DOS���� \n");
			}

		}else if(e.getSource()==starttest3){	//��ʼ�����׶�ʱ�Ӳ���
			exec.execute(new Thread(new PlayThread()));
			exec.execute(new Thread(new ShotThread(false)));		//����һ�ηǲ������㣬��ʱ����ʱ��Ϊ500ms

			starttest3.setEnabled(false);
		}else if(e.getSource()==sampling){		//��ʼ��������
			starttest3.setEnabled(true);
			result.setText(result.getText()+"���ڲ����ͼ�����������,���Եȡ���  \n");
			exec.execute(new Thread(new PlayThread(false,true)));	//���������̣߳�ֻ��Ҫ����һ���������ڵĶ��������Թر��ظ����Ź���
			exec.execute(new Thread(new ShotThread()));				//���������̣߳�ģʽΪ��������

		}

	}

	public boolean detectLine(BufferedImage bi){		//��һ�׶β��ԣ��ж��Ƿ�Ϊֱ�ߣ����ؼ��������Ҫ����������ͼƬ�������������ӦΪ����ͼƬ
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

		if(curimg!=null)		//ֱ�߲���ͼƬֻ��һ�ţ���ֻ�ڱȶ�ͼƬ�����û��ͼƬ��ʱ�����
			curpanel.remove(curimg);	
		curimg = new ImagePanel("src/1.png");
		curimg.setBounds(0,0,280,200);
		curpanel.add(curimg);
		curpanel.repaint();

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
		if(shotimg!=null){
			shotpanel.remove(shotimg);		//�Ƴ�֮ǰ��ͼƬ
			shotpanel.repaint();
		}
		shotimg = new ImagePanel(bi);	
		shotimg.setBounds(0,0,280,200);
		shotpanel.add(shotimg);		//����������ͼƬ
		shotpanel.repaint();
		if(curimg!=null)
			curpanel.remove(curimg);
		curimg = new ImagePanel("src/circle.png");
		curimg.setBounds(0,0,280,200);
		curpanel.add(curimg);
		curpanel.repaint();

		BufferedImage processed = processingPic(bi);	//����ͼƬ
		rdiff = calc.calcRDiff(processed);				//�������ݼ�����CalcData����
		if(rdiff<=CalcData.MAX_RDIFFERENCE_THRESHOLD)	//��С�����뾶�����Ϊ��Բ
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

	public static void main(String []args){
		new MainInterface();
	}

	/**
	*��ȡһ���ļ���������ͼƬ������panel��
	*�������ɵ�panel����˳���
	*/
	class ReadPics implements Runnable{				//�ڳ�������ʱ��ȡ������ͼƬ���в����뻺�棬�ӿ춯�����ŵ����̶�(�ڴ����Ĵ�)
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
	*�����߳�
	*����ʱ�������߳�
	*/
	class PlayThread implements Runnable{
		boolean isRepeat = true,isSampling = false;		//�Ƿ��ظ�����,Ĭ��״̬Ϊ�ظ�����
		int time = 20;			//����ʱ����
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
			int i = 0,count = 0; //countΪ������
			if(isSampling)		//������ģʽһ�Ѵ򿪣���ʱ�䲥��ʱ��������Ϊ150����
				time = 150;
			while(true){	
				try{
					Thread.sleep(time);		//��������ʱ����
				}catch(InterruptedException  ie){
					ie.printStackTrace(System.out);
				}
				tf.playImg(allpics.get(i));
				i = (i+1)%47;			//��i�����ڷ�Χ�ڣ�ʵ��ѭ������
				//System.out.println("��ǰ����:"+i);
				if(++count==47&&!isRepeat)		//�������ﵽ���һ��ͼƬλ�����ظ����ŹرյĻ�������һ�鼴ֹͣ����
					break;
			}
		}
	}

	class ShotThread implements Runnable{	//�����̣߳�Ĭ��ģʽ�ǲ�������
		boolean isSampling = true;			//����ģʽ������ֵΪ�棬���������������������Ϊ�٣���ʱ���㵥��ͼƬ

		ShotThread(boolean sampling){
			isSampling = sampling;
		}

		ShotThread(){}

		@Override
		public void run(){
			int i = 0;
			try{
				if(isSampling){			//�ȴ��������
					Thread.sleep(10);	//��һ�������ڴ��������̵߳�һ��ͼƬ��ʾ���10ms(��������ʱ����Ϊ150ms)
					while(++i<=101){
						animashot.add(webcam.getImage());		//��ʼ���㣬Ϊ������ɴ��̵߳������Լ���֤����������ԣ��ڴ�ֻ���������ͼƬ��������ͼƬ����
						System.out.println("������"+i);
						Thread.sleep(60);
					}
					samFinished = true;			//��ʾ�������
				}else{
					Thread.sleep(500);
					level3shot = webcam.getImage();		//����
				}
			}catch(InterruptedException ie){
				ie.printStackTrace(System.out);
			}
		}
	}

	class CalcThread extends Thread{	//�߳�һֱ���У���ز���״̬samFinished��һ��������ɣ�������ʼ������������
		CalcThread(){
			setDaemon(true);	//����Ϊ��̨�̣߳������̹߳رն��ر�
		}

		@Override
		public void run(){
			PointLoc prev;
			PointLoc cur;
			for(;;){
				if(samFinished){
					result.setText(result.getText()+"������ɣ���ʼ�������ͼƬ��\n");
					for(int i = 0;i<animashot.size();i++){
						animashot.set(i,processingPic(animashot.get(i)));		//�Բ���ͼƬ���ж�ֵ������
						/*try{
							ImageIO.write(animashot.get(i),"png",new File("respic/"+i+".png"));		//���ͼƬ��������
						}catch(Exception e){
							e.printStackTrace(System.out);
						}*/
					}
					result.setText(result.getText()+"ͼƬ������ɣ���ʼ�����������ݣ�\n");
					for(int i = 0;i<animashot.size();i++){
						prev = i != 0 ? calc.calcRCenter(animashot.get(i-1)) : new PointLoc(-1,-1);
						cur = calc.calcRCenter(animashot.get(i));
						if(Math.abs(cur.getX()-prev.getX())>CalcData.MAX_JITTER_THRESHOLD){	//��x��仯��ֵ���ڶ�����ֵ�������������ӵ�˳���
							point.add(cur);
							/*System.out.println("֮ǰ��Բ������")
							System.out.println("Բ������: "+cur.getX()+","+cur.getY());*/
							System.out.print("�Ѵ棡    ��ǰ: "+cur.getX()+","+cur.getY()+"\t");
							System.out.println("֮ǰ: "+prev.getX()+","+prev.getY());
						}else{
							System.out.print("δ�棡    ��ǰ: "+cur.getX()+","+cur.getY()+"\t");
							System.out.println("֮ǰ: "+prev.getX()+","+prev.getY());
						}
					}
					//System.out.println("���õ� : "+point.size()+" ����������");
					result.setText(result.getText()+"���õ� : "+point.size()+" ���������ݣ�\n");
					result.setText(result.getText()+"�������ݼ�����ɣ����Կ�ʼ����ʱ�ӣ�\n");
					samFinished = false;
				}
			}
		}
	}

	class CalcTimeDelay extends Thread{	//��ص����׶�ʱ�Ӽ���ͼƬ������̣߳���ͼƬ���㣬��ʼ����ʱ��
		CalcTimeDelay(){
			setDaemon(true);		//����Ϊ��̨�߳�
		}

		@Override
		public void run(){
			for(;;){
				if(level3shot!=null){		//�ȴ�����������ͼƬ
					if(shotimg!=null){		//���������������ʾͼƬ�������Ƴ��Ѿ���ʾ��ͼƬ
						shotpanel.remove(shotimg);
						shotpanel.repaint();
					}
					shotimg = new ImagePanel(level3shot);
					shotimg.setBounds(0,0,280,200);
					shotpanel.add(shotimg);
					shotpanel.repaint();
					level3shot = processingPic(level3shot);		//�������ͼƬ���д���(��ֵ��)
					System.out.println("���ڼ���ʱ��");
					PointLoc p = calc.calcRCenter(level3shot);		//����Բ������
					int index = calc.getSimilarestIndex(point,p);		//�ü������õ�Բ���������������ݼ��Ƚϣ���ȡ�����Ƶ��������ݵ�λ��
					System.out.println(index);					//�鿴λ��
					System.out.println("��Ļʱ��Ϊ  "+(index*20-500)+"  ms");
					result.setText(result.getText()+"����������ɣ�(�ο�)��Ļʱ��Ϊ�� "+Math.abs(index*20-500)+ "ms�� \n");
					level3shot = null;
				}
			}
		}
	}
}
