package com.jit.lib;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * Description：支持网络加载的ImageView 
 * 扩展功能：自定义加载中、加载失败的图片，缓存图片数据，加快图片读取速度，避免内存溢出。
 * 
 */
public class SmartImageView extends ImageView {

	//使用线程池管理网络请求
	private static final int LOADING_THREADS = 4;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(LOADING_THREADS);

	//图片下载的异步任务类
	private SmartImageTask currentTask;

	//加载中图片，布局配置
	private Drawable mLoadingDrawable;
	//加载失败图片，布局配置
	private Drawable mFailDrawable;

	public SmartImageView(Context context) {
		this(context, null);
	}

	public SmartImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmartImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, 
				R.styleable.SmartImageView, defStyle, 0);
		mLoadingDrawable = a.getDrawable(R.styleable.SmartImageView_onLoading);
		mFailDrawable = a.getDrawable(R.styleable.SmartImageView_onFail);
		a.recycle();
	}

	/**
	 * @param url
	 * 			设置加载的图片地址
	 */
	public void setImageUrl(String url) {
		setImage(new WebImage(url), null);
	}

	/**
	 * 
	 * @param url
	 * 			设置加载的图片地址
	 * @param completeListener
	 * 			图片加载完成的回调
	 */
	public void setImageUrl(String url, SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), completeListener);
	}
	
	public void setImage(final SmartImage image, final SmartImageTask.OnCompleteListener completeListener) {
		// 设置加载中图片
		if (mLoadingDrawable != null) {
			setImageDrawable(mLoadingDrawable);
		}

		// 如果此View的加载任务已经开始，取消
		if (currentTask != null) {
			currentTask.cancel();
			currentTask = null;
		}

		// 新建新的任务
		currentTask = new SmartImageTask(getContext(), image);
		currentTask.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
					@Override
					public void onComplete(Bitmap bitmap) {
						if (bitmap != null) {
							//加载成功，设置图片
							setImageBitmap(bitmap);
							//设置成功的回调
							if (completeListener != null) {
								completeListener.onSuccess(bitmap);
							}
						} else {
							// 设置失败图片
							if (mFailDrawable != null) {
								setImageDrawable(mFailDrawable);
							}
							//设置失败的回调
							if (completeListener != null) {
								completeListener.onFail();
							}
						}
					}
				});

		// 把任务加入线程池
		threadPool.execute(currentTask);
	}

	public static void cancelAllTasks() {
		threadPool.shutdownNow();
		threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
	}

}
