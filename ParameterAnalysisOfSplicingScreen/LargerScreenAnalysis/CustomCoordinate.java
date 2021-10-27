/**
*�Զ�������ϵ�����պ������겢����
*
*
**/
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.*;
class  CustomCoordinate extends JPanel
{
	String abscissa ="X��",ordinate = "y��";
	int height = 100,width = 100;
	ArrayList<Integer> data = null;



	CustomCoordinate()
	{
		setSize(150,150);
	}
	
	
	
	
	//���ú�������
	void setCoordinate(String abscissa,String ordinate)
	{
		this.abscissa = abscissa;
		this.ordinate = ordinate;
	}

	//���������᳤
	void setAxialLength(int width,int height)
	{
		this.width = width;
		this.height = height;
	}



	public void paint(Graphics g)
	{
		g.clearRect(0,0,250,200);//����ʹ�ñ���ɫ�����������ʾ�쳣(��ʱ֪ʶȱ����֪������ԭ��)
		//�滭������
		g.drawLine(0,(int)(0.9*height),(int)(0.9*width),(int)(0.9*height));
		g.drawString(abscissa,(int)width,(int)height);
		g.drawLine((int)(0.1*width),(int)(0.1*height),(int)(0.1*width),height);
		g.drawString(ordinate,(int)(0.2*width),(int)(0.2*height));

		//�滭����
		judgeData(g,data);
	}



	void judgeData(Graphics g,ArrayList<Integer> data)
	{
		if(data==null)
		{
			//System.out.println("�����ʼ�����");
		}
		else
		{
			paintDate(g,data);
			System.out.println("�������ݡ�������");
		}
	}



	void paintDate(Graphics g,ArrayList<Integer> data)
	{
		g.setColor(Color.red);
		System.out.println("paintDate�����ã����ڻ滭������");
		for(int i = 0;i<90;i++)
		{
			int j = (int)(data.get(i)/3);
			g.fillOval(i+10,j,2,2);
		}
	}
}