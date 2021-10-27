/*
*�������ڱ���ĳһ������������Ϣ(����R����G����B������ȡ����)
*/
import java.util.ArrayList;
class InfoOfIndex
{
	ArrayList<Integer> rSet = null;
	ArrayList<Integer> gSet = null;
	ArrayList<Integer> bSet = null;
	int rSize = 0;
	int gSize = 0;
	int bSize = 0;
	int rSetAccumulation = 0;
	int gSetAccumulation = 0;
	int bSetAccumulation = 0;
		
			
	InfoOfIndex(ArrayList<Integer> rSet,ArrayList<Integer> gSet,ArrayList<Integer> bSet)
	{
		this.rSet = rSet;
		this.gSet = gSet;
		this.bSet = bSet;
		this.rSize = rSet.size();
		this.gSize = gSet.size();
		this.bSize = bSet.size();

		rSetAccumulation = accumulateSet(rSet);
		gSetAccumulation = accumulateSet(gSet);
		bSetAccumulation = accumulateSet(bSet);
	}


	//ͳ�����ݼ��������ݺ�
	int accumulateSet(ArrayList<Integer> dataSet)
	{
		int size = dataSet.size();
		int numall = 0;//���ȹ���,���ٿ�������842�������ֵ��
		for(int i = 0;i<size;i++)
		{
			int num = dataSet.get(i);
			numall = numall + num;
		}

		return numall;
	}
}