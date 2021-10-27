/**
*
*相机子菜单操作类
*
*/
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;



import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
class CameraMenuOperation
{
	/**
	*相机(面板)
	*/
		//生成一个相机(只框架，无默认面板大小)
		WebcamPanel initWebcamPanel()
		{

			final Dimension size = WebcamResolution.VGA.getSize();
			//final Dimension size = new Dimension(500,400);

			final Webcam webcam = Webcam.getDefault();
			webcam.setViewSize(size);

			final WebcamPanel panel = new WebcamPanel(webcam, true);
			panel.setFPSDisplayed(true);
			panel.setDisplayDebugInfo(true);
			panel.setImageSizeDisplayed(true);
			panel.setMirrored(true);


			panel.setLayout(null);

			return panel;
		}




	//打开相机(返回相机面板)
	WebcamPanel startACamera()
	{
		WebcamPanel cameraPane = initWebcamPanel();
		

		return cameraPane;
	}



	//上传（分析）指定路径的图片
	void uploadPic(String picPath)
	{
		ChromatismAnalysisServer cas = new ChromatismAnalysisServer();
		LinkedHashMap<String,InfoOfIndex> allIndexInfo = cas.getDataset(picPath);
		String result = cas.analysis(allIndexInfo);
	}

}
