import java.awt.image.*;
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.util.concurrent.*;

/*
*����������Ǽ��㣬���а����˿��������������һЩ�㷨
*��������Ӧ��ȫȨ���ɱ������
*/
public class CalcData {
	public static final double MAX_SLOPE_THRESHOLD = 0.05,MAX_RDIFFERENCE_THRESHOLD = 3,MAX_JITTER_THRESHOLD = 5;	//���б�ʲ���ֵ,���뾶��������ֵ����󶶶������ֵ
	ArrayList<Double> rarray = new ArrayList<Double>();		//����Բ�İ˸�����뾶
	
	/*
	*���淽�����ڼ���б�ʣ���ȡһ�Ŷ�ֵ����ͼ��˫�����ֱ�ߵ������յ㣬
	*Ȼ���������յ����б�ʣ���������Ķ�ֵ��ͼ���Բ��Ǻڰף�ֻҪ��˫ɫͼƬ�����ף����������ܼ������Ӧ�������յ�
	*/
	public double calcSlope(String filepath){		//����б�ʣ�������ͼƬ��·��
		double k = 0.0;
		BufferedImage bi = null;
		int []rgb = new int[3];
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		boolean hadFirst = false,hadEnd = false;		//����¼�Ƿ��ҵ���һ��������һ����
		File file = new File(filepath);
		try{
			bi = ImageIO.read(file);
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		int width = bi.getWidth();
		int height = bi.getHeight();							
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		for(int j = miny;j<height;j++){
			for(int i = minx;i<width-1;i++){
				int pixel = bi.getRGB(i,j);			//�����ȡͼƬRGB
				int pixel_2 = bi.getRGB(i+1,j);
				if(pixel!=pixel_2&&!hadFirst){		//���RGB�����ı䣬�����һ�û�ҵ���һ���㣬��ôRGB�����ı�ĵ���Ϊ���
					xstart = i;ystart = j;
					hadFirst = true;
					System.out.println(xstart+"  "+ystart);
				}
				int pixel_3 = bi.getRGB(i,height-j-1);	//�����ȡͼƬRGB
				int pixel_4 = bi.getRGB(i+1,height-j-1);
				if(pixel_3!=pixel_4&&!hadEnd){		//���RGB�����ı䣬�����һ�û�ҵ��յ㣬��ôRGB�����ı�ĵ���Ϊ�յ�
					xend = i;yend = height-j-1;
					hadEnd = true;
					System.out.println(xend+"   "+yend);	
				}
				if(hadFirst&&hadEnd)		//�������յ��Ѿ�ȫ���ҵ���ʣ�µ�RGB���ɲ��ü��㣬ֱ������ѭ��
					break;
			}
		}
		k = (double)(ystart-yend)/(xstart-xend);	//����б��
		return k;
	}
	
	/*
	*�˷��������淽���Ĳ�ͬ�����汾��ԭ����ͬ
	*�������һ���ѷ��뻺���ͼƬ��
	*������ķ������ŵ��ǣ����Զ�ͼƬ����һ���������ټ���б��
	*/
	public double calcSlope(BufferedImage bi){	//����б�ʣ��������ѷ��뻺���ͼƬ
		double k = 0.0;
		int []rgb = new int[3];
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		boolean hadFirst = false,hadEnd = false;
		int width = bi.getWidth();
		int height = bi.getHeight();							
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		for(int j = miny;j<height;j++){
			for(int i = minx;i<width-1;i++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i+1,j);
				if(pixel!=pixel_2&&!hadFirst){
					xstart = i;ystart = j;
					hadFirst = true;
					System.out.println(xstart+"  "+ystart);
				}
				int pixel_3 = bi.getRGB(i,height-j-1);
				int pixel_4 = bi.getRGB(i+1,height-j-1);
				if(pixel_3!=pixel_4&&!hadEnd){
					xend = i;yend = height-j-1;
					hadEnd = true;
					System.out.println(xend+"   "+yend);
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		k = (double)(ystart-yend)/(xstart-xend);
		return k;
	}

	public double calcRDiff(String filepath){		//����Բ�ĵ���Ե��뾶����̰뾶֮�����ΪͼƬ��ַ
		BufferedImage bi = null;
		int []rgb = new int[3];
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		double xcenter = 0,ycenter = 0;
		boolean hadFirst = false,hadEnd = false;
		ExecutorService executor = Executors.newCachedThreadPool();		//ִ������Ϊ�����й��߳�
		File file = new File(filepath);
		try{
			bi = ImageIO.read(file);
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		int width = bi.getWidth();
		int height = bi.getHeight();							
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		for(int j = miny;j<height;j++){		//����ɨ�裬Ѱ����������꣬ȷ��Բ���ڵ������귶Χ
			for(int i = minx;i<width-1;i++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i+1,j);
				if(pixel!=pixel_2&&!hadFirst){
					ystart = j;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(i,height-j-1);
				int pixel_4 = bi.getRGB(i+1,height-j-1);
				if(pixel_3!=pixel_4&&!hadEnd){
					yend = height-j-1;		//������ɨ�裬Ѱ���յ��������
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		hadFirst = false;hadEnd = false;
		for(int i = minx;i<width;i++){
			for(int j = miny;j<height-1;j++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i,j+1);
				if(pixel!=pixel_2&&!hadFirst){	//Բ����������
					xstart = i;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(width-i-1,j);
				int pixel_4 = bi.getRGB(width-i-1,j+1);
				if(pixel_3!=pixel_4&&!hadEnd){
					xend = width-i-1;		//������ɨ�裬Ѱ���յ�������
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		System.out.println(xstart+","+ystart+"  "+xend+","+yend);
		xcenter = (double)(xstart+xend)/2;			//�����Բ��λ��
		ycenter = (double)(ystart+yend)/2;
		System.out.println(xcenter+","+ycenter);
		try{
			rarray.add(executor.submit(new CalR(bi,CalR.L,xcenter,ycenter)).get());	//���˸��̼߳���Բ�ĵ���Ե�ľ��룬����������ľ������˳���
			rarray.add(executor.submit(new CalR(bi,CalR.LU,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.U,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.RU,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.R,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.RD,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.D,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.LD,xcenter,ycenter)).get());
			for(double i :rarray)	//��ӡ�鿴�뾶
				System.out.println(i);
			System.out.println("MaxValue:  "+getMaxValue(rarray));	//��ӡ�鿴���ֵ����Сֵ
			System.out.println("MinValue:  "+getMinValue(rarray));
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		executor.shutdown();
		
		return (getMaxValue(rarray)-getMinValue(rarray));	//�������뾶����С�뾶�Ĳ�ֵ
	}

	public double calcRDiff(BufferedImage bi){		//����Բ�ĵ���Ե��뾶����̰뾶֮�����Ϊ�ѷ��뻺���ͼƬ
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		double xcenter = 0,ycenter = 0;
		boolean hadFirst = false,hadEnd = false;
		ExecutorService executor = Executors.newCachedThreadPool();
		int width = bi.getWidth();
		int height = bi.getHeight();							
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		for(int j = miny;j<height;j++){		//����ɨ�裬Ѱ����������꣬ȷ��Բ���ڵ������귶Χ
			for(int i = minx;i<width-1;i++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i+1,j);
				if(pixel!=pixel_2&&!hadFirst){
					ystart = j;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(i,height-j-1);
				int pixel_4 = bi.getRGB(i+1,height-j-1);
				if(pixel_3!=pixel_4&&!hadEnd){
					yend = height-j-1;		//������ɨ�裬Ѱ���յ��������
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		hadFirst = false;hadEnd = false;
		for(int i = minx;i<width;i++){
			for(int j = miny;j<height-1;j++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i,j+1);
				if(pixel!=pixel_2&&!hadFirst){
					xstart = i;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(width-i-1,j);
				int pixel_4 = bi.getRGB(width-i-1,j+1);
				if(pixel_3!=pixel_4&&!hadEnd){
					xend = width-i-1;		//������ɨ�裬Ѱ���յ��������
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		System.out.println(xstart+","+ystart+"  "+xend+","+yend);
		xcenter = (double)(xstart+xend)/2;
		ycenter = (double)(ystart+yend)/2;
		System.out.println(xcenter+","+ycenter);
		try{
			rarray.add(executor.submit(new CalR(bi,CalR.L,xcenter,ycenter)).get());		//����������İ뾶����˳���
			rarray.add(executor.submit(new CalR(bi,CalR.LU,xcenter,ycenter)).get());	
			rarray.add(executor.submit(new CalR(bi,CalR.U,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.RU,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.R,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.RD,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.D,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.LD,xcenter,ycenter)).get());
			for(double i :rarray)
				System.out.println(i);
			System.out.println("MaxValue:  "+getMaxValue(rarray));
			System.out.println("MinValue:  "+getMinValue(rarray));
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		executor.shutdown();
		
		return (getMaxValue(rarray)-getMinValue(rarray));
	}

	public PointLoc calcRCenter(BufferedImage bi){			//����Բ������
		int xstart = 0,ystart = 0,xend = 0,yend = 0,xcenter = 0,ycenter = 0;;
		boolean hadFirst = false,hadEnd = false;
		int width = bi.getWidth();
		int height = bi.getHeight();							
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		for(int j = miny;j<height;j++){		//����ɨ�裬Ѱ����������꣬ȷ��Բ���ڵ������귶Χ
			for(int i = minx;i<width-1;i++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i+1,j);
				if(pixel!=pixel_2&&!hadFirst){
					ystart = j;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(i,height-j-1);
				int pixel_4 = bi.getRGB(i+1,height-j-1);
				if(pixel_3!=pixel_4&&!hadEnd){
					yend = height-j-1;		//������ɨ�裬Ѱ���յ��������
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		hadFirst = false;hadEnd = false;
		for(int i = minx;i<width;i++){
			for(int j = miny;j<height-1;j++){
				int pixel = bi.getRGB(i,j);
				int pixel_2 = bi.getRGB(i,j+1);
				if(pixel!=pixel_2&&!hadFirst){
					xstart = i;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(width-i-1,j);
				int pixel_4 = bi.getRGB(width-i-1,j+1);
				if(pixel_3!=pixel_4&&!hadEnd){
					xend = width-i-1;		//������ɨ�裬Ѱ���յ��������
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		xcenter = (xstart+xend)/2;
		ycenter = (ystart+yend)/2;
		
		return new PointLoc(xcenter,ycenter);

	}
	
	public double getMaxValue(ArrayList<Double> array){		//�����ȡ˳���������ֵ
		double value = array.get(0);
		for(int i = 0;i<array.size();i++){
			if(value<array.get(i))
				value = array.get(i);
		}
		return value;
	}

	public double getMinValue(ArrayList<Double> array){		//�����ȡ˳�������С��ֵ
		double value = array.get(0);
		for(int i = 0;i<array.size();i++){
			if(value>array.get(i))
				value = array.get(i);
		}
		return value;
	}

	public int getSimilarestIndex(ArrayList<PointLoc> array,PointLoc p){
		int index = 0,x = p.getX(),y = p.getY();
		
		for(int i = 0;i<array.size();i++){
			PointLoc p1 = array.get(i);
			int x1 = p1.getX();
			int y1 = p1.getY();
			if(Math.abs(x-x1)<=CalcData.MAX_JITTER_THRESHOLD){
				index = i;
				break;
			}
		}
		return index;
	}

	public static void main(String[] args){		//������
		CalcData cd = new CalcData();
		//System.out.println(cd.calcSlope("G:/java/��Ŀ/27.��Ļ�������/src/1.png"));
		cd. calcRDiff("src/circle.png");
	}

	/*
	*������߳����ڼ���Բ�ĵ���Ե�ľ���
	*һ���߳�ֻ�ɼ���һ������ľ���
	*/
	class CalR implements Callable<Double>{	//����Բ��������İ뾶
		BufferedImage bi;
		final static int L = 1,LU = 2,U = 3,RU = 4, R = 5,RD = 6, D = 7, LD = 8; //L��R�ң�U�ϣ�D�£�����ָ����ȷ������
		int mode;	//��������
		double xcenter,ycenter;
		CalR(BufferedImage input,int direction,double inputx,double inputy){	//����ΪͼƬ���������򣬲������
			bi = input;
			mode = direction;
			xcenter = inputx;
			ycenter = inputy;
		}

		@Override
		public Double call(){
			int xstep = 1,ystep = 1;  //X��Y�Ĳ�����Ĭ�������½ǲ���
			int xmove = 0,ymove = 0;	//x��y��������λ��
			double radius = 0;  //�뾶
			switch(mode){			
				case L:
					xstep = -1;
					ystep = 0;
					break;
				case LU:
					xstep = -1;
					ystep = -1;
					break;
				case U:
					xstep = 0;
					ystep = -1;
					break;
				case RU:
					xstep = 1;
					ystep = -1;
					break;
				case R:
					xstep = 1;
					ystep = 0;
					break;
				case RD:
					xstep = 1;
					ystep = 1;
					break;
				case D:
					xstep = 0;
					ystep = 1;
					break;
				case LD:
					xstep = -1;
					ystep = 1;
					break;
				default:
					break;
			}
			int width = bi.getWidth();
			int height = bi.getHeight();							
			int minx = bi.getMinX();
			int miny = bi.getMinY();
			int curx = (int)xcenter;			//curx��cury��ʾ��ǰ������λ��,������㲻�ı�
			int cury = (int)ycenter;
			while(curx>=minx&&curx<width&&cury>=miny&&cury<height){		//������������Բ��
				int pixel = bi.getRGB(curx,cury);	//��¼������ʼǰ��RGBֵ
				curx+=xstep;		//��ʼ����
				cury+=ystep;
				int pixel_2 = bi.getRGB(curx,cury); //��¼�µ��������RGBֵ
				if(pixel!=pixel_2)
					break;			
			}
			curx-=xstep;	//λ�ò�����ѭ��������λ����Բ�ı�Ե��
			curx-=ystep;	//���������Խ������Ӱ��
			//System.out.println(xmove+"   "+ymove);
			radius = Math.sqrt((double)((curx-xcenter)*(curx-xcenter)+(cury-ycenter)*(cury-ycenter)));	//������㹫ʽ
			//System.out.println("���� "+((double)curx-xcenter));
			return radius;
		}
	}
}