/**
*坐标面板，用于存放所有单个坐标轴面板
*
*
*
*/
import javax.swing.*;
import java.util.HashMap;
class CoordinatePanel extends JPanel
{
	CustomCoordinate cc11,cc12,cc13,cc21,cc22,cc23,cc31,cc32,cc33,cc41,cc42,cc43;
	HashMap<String,CustomCoordinate> allCoordinate = new HashMap<String,CustomCoordinate>();
	CoordinatePanel()
	{
		cc11 = new CustomCoordinate();cc11.setLocation(0,0);cc11.setCoordinate("距离","r");
		cc12 = new CustomCoordinate();cc12.setLocation(151,0);cc12.setCoordinate("距离","g");
		cc13 = new CustomCoordinate();cc13.setLocation(301,0);cc13.setCoordinate("距离","b");
		cc21 = new CustomCoordinate();cc21.setLocation(0,151);cc21.setCoordinate("距离","r");
		cc22 = new CustomCoordinate();cc22.setLocation(151,151);cc22.setCoordinate("距离","g");
		cc23 = new CustomCoordinate();cc23.setLocation(301,151);cc23.setCoordinate("距离","b");
		cc31 = new CustomCoordinate();cc31.setLocation(0,301);cc31.setCoordinate("距离","r");
		cc32 = new CustomCoordinate();cc32.setLocation(151,301);cc32.setCoordinate("距离","g");
		cc33 = new CustomCoordinate();cc33.setLocation(301,301);cc33.setCoordinate("距离","b");
		cc41 = new CustomCoordinate();cc41.setLocation(0,451);cc41.setCoordinate("距离","r");
		cc42 = new CustomCoordinate();cc42.setLocation(151,451);cc42.setCoordinate("距离","g");
		cc43 = new CustomCoordinate();cc43.setLocation(301,451);cc43.setCoordinate("距离","b");
		

		add(cc11);add(cc12);add(cc13);
		add(cc21);add(cc22);add(cc23);
		add(cc31);add(cc32);add(cc33);
		add(cc41);add(cc42);add(cc43);
		allCoordinate.put("firstR",cc11);allCoordinate.put("firstG",cc12);allCoordinate.put("firstB",cc13);
        allCoordinate.put("secondR",cc21);allCoordinate.put("secondG",cc22);allCoordinate.put("secondB",cc23);
		allCoordinate.put("thirdR",cc31);allCoordinate.put("thirdG",cc32);allCoordinate.put("thirdB",cc33);
		allCoordinate.put("fourthR",cc41);allCoordinate.put("fourthG",cc42);allCoordinate.put("fourthB",cc43);
		

		System.out.println("坐标面板坐标完成坐标添加");
		setBounds(501,0,450,600);
		setLayout(null);
	}
}
