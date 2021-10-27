import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;


import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class mycam {
	public static void main(String[] args) throws InterruptedException {		 
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		WebcamPanel panel = new WebcamPanel(webcam);
		JButton btn = new JButton("拍照");
		
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
					Calendar calender = Calendar.getInstance();
					Date date = calender.getTime();
					BufferedImage bimage = webcam.getImage();
					ImageIO.write(bimage,"PNG",new File(sdf.format(date)+".png"));
					System.out.println("拍照完成!");
				}catch(IOException error){
					System.out.println("无法创建文件!");
					error.printStackTrace(System.out);
				}

			}
		});
		//panel.setFPSDisplayed(true);
		//panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);
		JFrame jf = new JFrame("My Cam"); 
		jf.add(panel,BorderLayout.NORTH);
		jf.add(btn,BorderLayout.SOUTH);
		jf.setResizable(false);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
 
	}
}
