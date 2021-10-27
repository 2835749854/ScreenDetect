/**
*�ṩɫ����������
*
*/

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Set;
import java.io.IOException;
class ChromatismAnalysisServer
{
	//��ȡָ��·��ͼƬ�����ݼ�
	LinkedHashMap<String,InfoOfIndex> getDataset(String picPath)
	{
		//��ʼ������ģ��
		BufferedImage buffimg = null;
		try
		{
			buffimg = ImageIO.read(new File(picPath));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		int height = buffimg.getHeight();int width = buffimg.getWidth();
		LinkedHashMap<String,InfoOfIndex> dataOfPic = new LinkedHashMap<String,InfoOfIndex>();
		
		//ͼƬ���1
		InfoOfIndex first = processPiece(0,0,(int)width/2,(int)height/2,buffimg);
		//ͼƬ���2
		InfoOfIndex second = processPiece((int)width/2,0,width,(int)height/2,buffimg);
		//ͼƬ���3
		InfoOfIndex third = processPiece(0,(int)height/2,(int)width/2,height,buffimg);
		//ͼƬ���4
		InfoOfIndex fourth = processPiece((int)width/2,(int)height/2,width,height,buffimg);
		//ͼƬ���0
		ArrayList<Integer> rSetAll = new ArrayList<Integer>();
		rSetAll.addAll(first.rSet);
		rSetAll.addAll(second.rSet);
		rSetAll.addAll(third.rSet);
		rSetAll.addAll(fourth.rSet);
		ArrayList<Integer> gSetAll = new ArrayList<Integer>();
		gSetAll.addAll(first.gSet);
		gSetAll.addAll(second.gSet);
		gSetAll.addAll(third.gSet);
		gSetAll.addAll(fourth.gSet);
		ArrayList<Integer> bSetAll = new ArrayList<Integer>();
		bSetAll.addAll(first.bSet);
		bSetAll.addAll(second.bSet);
		bSetAll.addAll(third.bSet);
		bSetAll.addAll(fourth.bSet);
		InfoOfIndex zero = new InfoOfIndex(rSetAll,gSetAll,bSetAll);


		dataOfPic.put("first",first);
		dataOfPic.put("second",second);
		dataOfPic.put("third",third);
		dataOfPic.put("fourth",fourth);
		dataOfPic.put("zero",zero);

		return dataOfPic;
	}




	//�������ݼ������ط������
	String analysis(LinkedHashMap<String,InfoOfIndex> allIndexInfo)
	{
		InfoOfIndex infoAll = allIndexInfo.remove("zero");
		int rSetAccumulationAll = infoAll.rSetAccumulation;
		int gSetAccumulationAll = infoAll.gSetAccumulation;
		int bSetAccumulationAll = infoAll.bSetAccumulation;
		int rSetAllNum = infoAll.rSize;
		int gSetAllNum = infoAll.gSize;
		int bSetAllNum = infoAll.bSize;
		double averRAll = (double)rSetAccumulationAll/rSetAllNum;
		double averGAll = (double)gSetAccumulationAll/gSetAllNum;
		double averBAll = (double)bSetAccumulationAll/bSetAllNum;
		
		Set<String> s = allIndexInfo.keySet();//��ó�zero�����м���set����
		String result = "";
        for(String key : s)
        {
            //���ݵõ��ļ�����ȡ��Ӧ��ֵ
            InfoOfIndex dataOfIndex = allIndexInfo.get(key);
            System.out.println("��ȡ---"+key);

			int rNum =  dataOfIndex.rSet.size();
			int gNum =  dataOfIndex.gSet.size();
			int bNum =  dataOfIndex.bSet.size();
			double averR = (double)dataOfIndex.rSetAccumulation/rNum;
			double averG = (double)dataOfIndex.gSetAccumulation/gNum;
			double averB = (double)dataOfIndex.bSetAccumulation/bNum;


			double ratioR = averR/averRAll;
			double ratioG = averG/averGAll;
			double ratioB = averB/averBAll;
			System.out.println(key+"����ĻR��G��B��ֵ�ȷֱ�Ϊ"+ratioR+" "+ratioG+" "+ratioB);

			
			if((ratioR<0.88)||(ratioR>1.12))
			{
				System.out.println(key+"����ĻRֵ�쳣");
				result = result + key + "����ĻRֵ�쳣\n";
			}
			if((ratioG<0.88)||(ratioG>1.12))
			{
				System.out.println(key+"����ĻGֵ�쳣");
				result = result + key + "����ĻGֵ�쳣\n";
			}
			if((ratioB<0.88)||(ratioB>1.12))
			{
				System.out.println(key+"����ĻBֵ�쳣");
				result = result + key + "����ĻBֵ�쳣\n";
			}
        }

		return result;
	}





	//����ͼ�����������(��������)��λ������ÿ�
	InfoOfIndex processPiece(int x1,int y1,int x2,int y2,BufferedImage buffimg)
	{
		ArrayList<Integer> rSet = new ArrayList<Integer>();
		ArrayList<Integer> gSet = new ArrayList<Integer>();
		ArrayList<Integer> bSet = new ArrayList<Integer>();
		int rSetAccumulation = 0;
		int gSetAccumulation = 0;
		int bSetAccumulation = 0;
		
		for(int j = y1;j<y2;j=j+10)
		{
			for(int i = x1;i<x2;i=i+10)
			{
				int pixel = buffimg.getRGB(i,j);
				int r = (pixel&(0x00FF0000))>>16;
				int g = (pixel&(0x0000FF00))>>8;
				int b = (pixel&(0x000000FF));

				rSet.add(r);
				gSet.add(g);
				bSet.add(b);
			}
		}

		InfoOfIndex dataOfPoint = new InfoOfIndex(rSet,gSet,bSet);

		return dataOfPoint;
	}
}
