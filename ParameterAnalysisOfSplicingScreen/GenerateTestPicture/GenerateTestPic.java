import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import javax.imageio.ImageIO;
class GenerateTestPic 
{
	//���������Ҷ�ͼ
	void batchGrayPic(int width,int height,String path)
	{
		for(int i = 0;i<256;i++)
		{
			BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			bi = paintTraverse(i,i,i,bi);
			
			String pname = "gray_"+i+"jpg";
			writePic(bi,path,pname);
		}
	}


    
	//����RGB���л���ͼƬ
	BufferedImage paintTraverse(int r,int g,int b,BufferedImage bi)
	{
		int width = bi.getWidth(),height = bi.getHeight();
		Color rgb = new Color(r,g,b);
		for(int i = 0;i<width;i++)
		{
			for(int j = 0;j<height;j++)
			{
				bi.setRGB(i,j,rgb.getRGB());
			}
		}


		return bi;
	}



    //�������·���������ǩ����滭�õ�ͼƬ
	void writePic(BufferedImage bi,String path,String pname)
	{
		try
		{
			String obj = path+pname;
			ImageIO.write(bi,"JPG",new File(obj));//�����ֵ�����ͼƬ
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		
	}




	//�����ض�RGBֵͼƬ
	void generateSpecificRGB(int r,int g,int b,int width,int height,String path)
	{
		BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		bi = paintTraverse(r,g,b,bi);

		String pname = "rgb_"+r+String.valueOf(g)+b+".jpg";
		writePic(bi,path,pname);
	}



	
	//�����ض�ɫ����ɫ����ͼƬ
	void diffTestPic(int num,int r,int g,int b,int width,int height,String path)//�����ֱ�Ϊ������Ļ��ţ�����������ĻRGB
	{
		BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		bi = paintTraverse(r,g,b,bi);

		
		//���ò�������
		int diffw = (int)(width/2),diffh = (int)(height/2);
		int diffr = r-20,diffg = g-20,diffb = b-20;
		Color diffcolor = new Color(diffr,diffg,diffb);
		//BufferedImage bi = new BufferedImage(diffw,diffh,BufferedImage.TYPE_INT_RGB);
		if(num==1)
		{
			for(int i = 0;i<diffw;i++)
			{
				for(int j = 0;j<diffh;j++)
				{
					bi.setRGB(i,j,diffcolor.getRGB());
				}
			}
		}
		else if(num==2)
		{
			for(int i = diffw;i<width;i++)
			{
				for(int j = 0;j<diffh;j++)
				{
					bi.setRGB(i,j,diffcolor.getRGB());
				}
			}
		}
		else if(num==3)
		{
			for(int i = 0;i<diffw;i++)
			{
				for(int j = diffh;j<height;j++)
				{
					bi.setRGB(i,j,diffcolor.getRGB());
				}
			}
		}
		else if(num==4)
		{
			for(int i = diffw;i<width;i++)
			{
				for(int j = diffh;j<height;j++)
				{
					bi.setRGB(i,j,diffcolor.getRGB());
				}
			}
		}


		String pname = "diffpic_rgb_"+r+String.valueOf(g)+b+".jpg";//����Ӧ�ü�������ʱ�����֤Ψһ�ԣ�������ǰ�ʼǲ�֪�������ˣ�������ʱ��һ��
		writePic(bi,path,pname);
	}




	//���������ض�ɫ����ɫ����ͼƬ
	void batDiffPic(int num,int width,int height,String path)
	{
		for(int i = 2;i<26;i++)
		{
			diffTestPic(num,i*10,i*10,i*10,width,height,path);
		}
	}
}