/**
*����λ�ã�ʹ��Ĭ�Ϲ��������ɵ�������0,0
*/
public class PointLoc{
	private int x;
	private int y;

	PointLoc(){
		x = 0;
		y = 0;
	}

	PointLoc(int x,int y){
		this.x = x;
		this.y = y;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}
	
	@Override
	public String toString(){
		return x+","+y;
	}
}
