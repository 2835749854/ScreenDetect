/**
*软件主界面
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
			//ImagePanel imgp = null;//图像面板(由菜单初始化并接管)
			JPanel showP = null;
			JScrollPane scrollPane = null;//存放文本域的滚动面板(由菜单初始化并接管)
			CoordinatePanel cp = null;//坐标面板(由菜单初始化并接管)
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
		new MainGUI().init(new JFrame(),"大屏色差分析软件",950,620);
	}
}