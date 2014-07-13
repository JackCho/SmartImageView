package com.jit.lib;

import com.jit.lib.SmartImageTask.OnCompleteListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView mLoadProgress;
	private SmartImageView mImageView;
	
	private String url = "http://images.cnitblog.com/blog/430074/201302/01220037-4e6a57c1199748fea9f8391e7e0548d7.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLoadProgress = (TextView) findViewById(R.id.load_progress);
		mLoadProgress.setText("图片加载过程中...");
		
		mImageView = (SmartImageView) findViewById(R.id.smart_iv);
		//mImageView.setImageUrl(url);
		mImageView.setImageUrl(url, new OnCompleteListener() {

			@Override
			public void onSuccess(Bitmap bitmap) {
				mLoadProgress.setText("图片加载成功");
			}

			@Override
			public void onFail() {
				mLoadProgress.setText("图片加载失败");
			}
		});
	}

}
