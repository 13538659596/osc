package net.oschina.app.ui;

import net.oschina.app.AppManager;
import net.oschina.app.R;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;


/***
 * 双击退出
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2015年1月5日 下午7:07:44
 *
 */
public class DoubleClickExitHelper {

	private final Activity mActivity;
	
	private boolean isOnKeyBacking;// 实例属性 默认值为false
	private Handler mHandler;
	private Toast mBackToast;
	
	public DoubleClickExitHelper(Activity activity) {
		mActivity = activity;
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * Activity onKeyDown事件
	 * */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
/*如果点击的不是back键，则此方法结束 并返回值false*/
		if(keyCode != KeyEvent.KEYCODE_BACK) {
			return false;
		}
	/*	boolean类型的值isOnKeyBadking,起到标识的作用，让用户必须连续两次点击back键且这两次点击时间间隔
	 * 在两秒以内。
	 * isOnKeyBacking默认为false，当用户点击了一下back键  则isOnKeyBacking重新赋值为true,并展示Toast，且
	 * mHandler.postDelayed(onBackTimeRunnable, 2000); ，让线程onBackTimeRunnable两秒之后启动，此线程
	 * 将执行的任务就是对isOnKeyBacking重新赋值为false。所以 用户必须在两秒以内再次点击back键 才能进入
	 * isOnKeyBacking为true的代码块 去执行AppManager.getAppManager().AppExit(mActivity); 退出程序。
	 * 
	 * */
		if(isOnKeyBacking) {
//			从message queue中remove线程onBackTimeRunnable
			mHandler.removeCallbacks(onBackTimeRunnable);
			if(mBackToast != null){
				mBackToast.cancel();
			}
			// 退出
			AppManager.getAppManager().AppExit(mActivity);
			return true;
		} else {
			isOnKeyBacking = true;
			if(mBackToast == null) {
				mBackToast = Toast.makeText(mActivity, R.string.tip_double_click_exit, 2000);
			}
			mBackToast.show();
//			添加线程onBackTimeRunnable线程到message queue中,且此线程两秒后执行
			mHandler.postDelayed(onBackTimeRunnable, 2000);
			return true;
		}
	}
	
	private Runnable onBackTimeRunnable = new Runnable() {
		
		@Override
		public void run() {
			isOnKeyBacking = false;
			if(mBackToast != null){
				mBackToast.cancel();
			}
		}
	};
}
