package com.puzzle;

import android.app.Activity;
import android.os.Bundle;

public class PuzzleBobbleActivity extends Activity {
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(new MyGameSurfaceView(this));
        
        /* ��ȫû��
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        int nowWidth = dm.widthPixels;//��ÿؼ���ԭʼ���
        int nowheight = dm.heightPixels;//��ÿؼ���ԭʼ�߶�
        int density = (int) dm.density;//�������������ܶ�
        //�����ֻ����ܶ�����Ϊÿһ���ֻ����ڲ��죬
        screenW = (int) (nowWidth * density);//��õ�ǰ�ֻ��Ŀ��
        screenH = (int) (nowheight * density);//��õ�ǰ�ֻ��ĸ߶�
        Log.i("PuzzleBobbleActivity", "screenW = " + screenW + ", screenH = " + screenH);
        */
    }
    
    public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
	}
    
}