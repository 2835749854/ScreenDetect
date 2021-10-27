/**
*���������
*
*
**/
import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JDialog;
import javax.imageio.ImageIO;
public class MainGUI
{
	final void init(JFrame f,String title,int width,int height)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			//ImagePanel imgp = null;//ͼ�����(�ɲ˵���ʼ�����ӹ�)
			JPanel showP = null;
			JScrollPane scrollPane = null;//����ı���Ĺ������(�ɲ˵���ʼ�����ӹ�)
			CoordinatePanel cp = null;//�������(�ɲ˵���ʼ�����ӹ�)
			public void run()
			{
				CustomMenuBar cmb = new CustomMenuBar();
				
				//imgp = cmb.imgp;
				showP = cmb.showP;
				cp = cmb.cp;
				scrollPane = cmb.scrollPane;
				
				f.setJMenuBar(cmb);
				//f.getContentPane().add(imgp);
				f.getContentPane().add(showP);
				f.getContentPane().add(scrollPane);
				f.getContentPane().add(cp);

				f.getContentPane().setLayout(null);

				f.setTitle(title);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setLocation(250,100);
				f.setSize(width,height);
				f.setVisible(true);
			}
		});
	}




	public static void main(String[] args)
	{
		new MainGUI().init(new JFrame(),"����ɫ��������",950,620);
	}
}