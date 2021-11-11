import java.awt.image.*;
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.util.concurrent.*;

/*
*本类的任务是计算，其中包含了可容许的最大误差和一些算法 
*计算任务应当全权交由本类完成
*/
public class CalcData {
	public static final double MAX_SLOPE_THRESHOLD = 0.05,Max_RDIFFERENCE_THRESHOLD = 3;	//最大斜率差阈值,最大半径差像素阈值
	ArrayList<Double> rarray = new ArrayList<Double>();		//保存圆的八个方向半径
	
	/*
	*下面方法用于计算斜率，读取一张二值化的图并双向查找直线的起点和终点，
	*然后用起点和终点计算斜率，这里输入的二值化图可以不是黑白，只要是双色图片（如红白，红蓝）都能计算出对应的起点和终点
	*/
	public double calcSlope(String filepath){		//计算斜率，参数是图片的路径
		double k = 0.0;
		BufferedImage bi = null;
		int []rgb = new int[3];
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		boolean hadFirst = false,hadEnd = false;		//，记录是否找到第一个点和最后一个点
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
				int pixel = bi.getRGB(i,j);			//正向读取图片RGB
				int pixel_2 = bi.getRGB(i+1,j);
				if(pixel!=pixel_2&&!hadFirst){		//如果RGB发生改变，且尚且还没找到第一个点，那么RGB发生改变的点作为起点
					xstart = i;ystart = j;
					hadFirst = true;
					System.out.println(xstart+"  "+ystart);
				}
				int pixel_3 = bi.getRGB(i,height-j-1);	//反向读取图片RGB
				int pixel_4 = bi.getRGB(i+1,height-j-1);
				if(pixel_3!=pixel_4&&!hadEnd){		//如果RGB发生改变，且尚且还没找到终点，那么RGB发生改变的点作为终点
					xend = i;yend = height-j-1;
					hadEnd = true;
					System.out.println(xend+"   "+yend);	
				}
				if(hadFirst&&hadEnd)		//若起点和终点已经全部找到，剩下的RGB即可不用计算，直接跳出循环
					break;
			}
		}
		k = (double)(ystart-yend)/(xstart-xend);	//计算斜率
		return k;
	}
	
	/*
	*此方法是上面方法的不同参数版本，原理相同
	*传入的是一张已放入缓存的图片，
	*较上面的方法的优点是，可以对图片进行一定处理后再计算斜率
	*/
	public double calcSlope(BufferedImage bi){	//计算斜率，参数是已放入缓存的图片
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

	public double calcRDiff(String filepath){		//计算圆心到边缘最长半径和最短半径之差，参数为图片地址
		BufferedImage bi = null;
		int []rgb = new int[3];
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		double xcenter = 0,ycenter = 0;
		boolean hadFirst = false,hadEnd = false;
		ExecutorService executor = Executors.newCachedThreadPool();		//执行器，为我们托管线程
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
		for(int j = miny;j<height;j++){		//按行扫描，寻找起点纵坐标，确定圆所在的纵坐标范围
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
					yend = height-j-1;		//逆向按行扫描，寻找终点点纵坐标
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
				if(pixel!=pixel_2&&!hadFirst){	//圆的起点横坐标
					xstart = i;
					hadFirst = true;
				}
				int pixel_3 = bi.getRGB(width-i-1,j);
				int pixel_4 = bi.getRGB(width-i-1,j+1);
				if(pixel_3!=pixel_4&&!hadEnd){
					xend = width-i-1;		//逆向按行扫描，寻找终点点横坐标
					hadEnd = true;
				}
				if(hadFirst&&hadEnd)
					break;
			}
		}
		System.out.println(xstart+","+ystart+"  "+xend+","+yend);
		xcenter = (double)(xstart+xend)/2;			//计算出圆心位置
		ycenter = (double)(ystart+yend)/2;
		System.out.println(xcenter+","+ycenter);
		try{
			rarray.add(executor.submit(new CalR(bi,CalR.L,xcenter,ycenter)).get());	//开八个线程计算圆心到边缘的距离，并将计算出的距离放入顺序表
			rarray.add(executor.submit(new CalR(bi,CalR.LU,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.U,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.RU,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.R,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.RD,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.D,xcenter,ycenter)).get());
			rarray.add(executor.submit(new CalR(bi,CalR.LD,xcenter,ycenter)).get());
			for(double i :rarray)	//打印查看半径
				System.out.println(i);
			System.out.println("MaxValue:  "+getMaxValue(rarray));	//打印查看最大值和最小值
			System.out.println("MinValue:  "+getMinValue(rarray));
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		executor.shutdown();
		
		return (getMaxValue(rarray)-getMinValue(rarray));	//返回最大半径和最小半径的差值
	}

	public double calcRDiff(BufferedImage bi){		//计算圆心到边缘最长半径和最短半径之差，参数为已放入缓存的图片
		int xstart = 0,ystart = 0,xend = 0,yend = 0;
		double xcenter = 0,ycenter = 0;
		boolean hadFirst = false,hadEnd = false;
		ExecutorService executor = Executors.newCachedThreadPool();
		int width = bi.getWidth();
		int height = bi.getHeight();							
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		for(int j = miny;j<height;j++){		//按行扫描，寻找起点纵坐标，确定圆所在的纵坐标范围
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
					yend = height-j-1;		//逆向按行扫描，寻找终点点纵坐标
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
					xend = width-i-1;		//逆向按行扫描，寻找终点点纵坐标
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
			rarray.add(executor.submit(new CalR(bi,CalR.L,xcenter,ycenter)).get());		//将各个方向的半径放入顺序表
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
	
	public double getMaxValue(ArrayList<Double> array){		//计算获取顺序表中最大的值
		double value = array.get(0);
		for(int i = 0;i<array.size();i++){
			if(value<array.get(i))
				value = array.get(i);
		}
		return value;
	}

	public double getMinValue(ArrayList<Double> array){		//计算获取顺序表中最小的值
		double value = array.get(0);
		for(int i = 0;i<array.size();i++){
			if(value>array.get(i))
				value = array.get(i);
		}
		return value;
	}

	public static void main(String[] args){		//测试用
		CalcData cd = new CalcData();
		//System.out.println(cd.calcSlope("G:/java/项目/27.屏幕质量检测/src/1.png"));
		cd. calcRDiff("src/circle.png");
	}
	
	/*
	*下面的线程用于计算圆心到边缘的距离
	*一个线程只可计算一个方向的距离
	*/
	class CalR implements Callable<Double>{	//计算圆各个方向的半径
		BufferedImage bi;
		final static int L = 1,LU = 2,U = 3,RU = 4, R = 5,RD = 6, D = 7, LD = 8; //L左，R右，U上，D下，根据指向方向确定步进
		int mode;	//步进方向
		double xcenter,ycenter;
		CalR(BufferedImage input,int direction,double inputx,double inputy){	//参数为图片，步进方向，步进起点
			bi = input;
			mode = direction;
			xcenter = inputx;
			ycenter = inputy;
		}

		@Override
		public Double call(){
			int xstep = 1,ystep = 1;  //X和Y的步进，默认向右下角步进
			int xmove = 0,ymove = 0;	//x和y所产生的位移
			double radius = 0;  //半径
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
			int curx = (int)xcenter;			//curx和cury表示当前步进的位置,步进起点不改变
			int cury = (int)ycenter;
			while(curx>=minx&&curx<width&&cury>=miny&&cury<height){		//步进起点控制在圆中
				int pixel = bi.getRGB(curx,cury);	//记录步进开始前的RGB值
				curx+=xstep;		//开始步进
				cury+=ystep;
				int pixel_2 = bi.getRGB(curx,cury); //记录新到达坐标的RGB值
				if(pixel!=pixel_2)
					break;			
			}
			curx-=xstep;	//位置补正，循环跳出的位置是圆的边缘，
			curx-=ystep;	//不作补正对结果并无影响
			//System.out.println(xmove+"   "+ymove);
			radius = Math.sqrt((double)((curx-xcenter)*(curx-xcenter)+(cury-ycenter)*(cury-ycenter)));	//距离计算公式
			//System.out.println("测试 "+((double)curx-xcenter));
			return radius;
		}
	}
}
