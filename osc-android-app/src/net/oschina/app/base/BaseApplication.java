package net.oschina.app.base;

import net.oschina.app.R;
import net.oschina.app.util.StringUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class BaseApplication extends Application {
	private static String PREF_NAME = "creativelocker.pref";
	private static String LAST_REFRESH_TIME = "last_refresh_time.pref";
	static Context _context;
	static Resources _resource;
//	lastToast表示上一次Toast展示的信息字符串。BaseApplication的生命周期是全局，
//	所以此类属性lastToast的生命周期也是全局
	private static String lastToast = "";  //记录上一次土司内容
//	lastToastTime表示上一个Toast的结束时间。BaseApplication的生命周期是全局，
//	所以此类属性lastToast的生命周期也是全局
	private static long lastToastTime;

	private static boolean sIsAtLeastGB;

	// 咱们将判断执行此开源中国app的系统的版本号的代码 放置在 类代码块中，就是说
	// 只要系统加载了BaseApplication,那么就会执行 此 版本判断语句。可以说，此判断语句
	// 在此应用的最开始阶段 就被执行了，对吗？！
	static {
		// 对Build.VERSION.... 的解释，见附件《Android Build.VERSION.SDK_INT兼容版本》
		// 此项目中是使用SharedPreferred来读写一些数据。关于SharedPreferred见
		// 《SharedPreferences说明》。此处是判断执行app的系统的版本号
		// （Build.VERSION.SDK_INT ）是否大于等于 Build.VERSION_CODES.GINGERBREAD
		// （android2.3， version = 9）
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			// 此处 布尔类型的变量sIsAteastGB 是起到一个标识的作用，sIsAteastGB是一个类属性，当
			// 系统加载此类BaseApplication的时候呢 会对此变量付初始值 false。如果系统的版本号
			// 大于等于 2.3，则变量sIsAtLeastGB 赋值为 true。那么 如果sIsAtleastGB的值为true，那么
			// 此类中下面的 类方法
			/*
			 * public static void apply(SharedPreferences.Editor editor) { if
			 * (sIsAtLeastGB) { editor.apply(); } else { editor.commit(); } }
			 */
			// 此时 类方法中的editor 的提交数据的方法则会选择 apply()，反之选择commit()。咱们知
			// 道 apply()是android2.3（包括2.3）之后 ，才提供的新api，若在2.3之前执行次方法 程序
			// 会报错。所以这里要进行一个版本适配的判断。
			sIsAtLeastGB = true;
			
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		必须调用父类Application的onCreate方法，即必须在MyApplication中的onCreate方法中添加
//		这一句代码“super.onCreate()”调用Application中的onCreate方法
		
		// 点击
		// 此类是BaseApplication,是继承Application的，点击进入类Application源码 看到Application
		// 是继承ContextWrapper 的，而ContextWrapper中有一个方法 getApplicationContext()，此
		// 方法的返回值类型为 上下文Context对象。简单的说，上下文Context分为Application-Context
		// 和Activity-Context 两种，这里是通过 getApplicationContext()
		// 获得Application-Context对象。
		// 此对象 可以强制类型转换为Application的子类，如 下面的方法context()。但是不可以强制转换
		// 为某Activity的子类。
		_context = getApplicationContext();// 详情可见《Context、Application、Activity》、
		// 《Application说明》

		// 调用上下文Context对象的getResources方法，得到 Resources对象
		_resource = _context.getResources();

	}

	// 类中的代码可能运行于多线程环境下，如果要考虑同步的问题，则可以－synchronized，
	// 大大简化了Java中多线程同步的使用。
	// 下面这是 定义了 一个 使用同步锁来修饰一个方法 content()
	public static synchronized BaseApplication context() {
		// 返回了一个 (BaseApplication) _context ，可能有的同学会迷惑。
		// 此方法的返回值类型是BaseApplication，而方法中的返回值为
		// (BaseApplication) _context，这是 社么意思呢？
		// 咱们知道，方法中的返回值的类型 要和 定义方法时的返回值类型
		// 相同。那BaseApplication的类型和(BaseApplication) _context 是相同的吗？
		// BaseApplication 直接继承于 Application，Application 直接继承于 ContextWrapper
		// ，ContextWrapper 直接继承与 Context。所以 Context是BaseApplication
		// 的父类，所以 (BaseApplication) _context 是将父类对象强制类型转化为 子类对象，
		// 所以 BaseApplication的类型和(BaseApplication) _context 是相同的。
		return (BaseApplication) _context;
	}

	// 定义一个获取 类Resources 对象的 类方法
	public static Resources resources() {
		return _resource;
	}

	/**
	 * 放入已读文章列表中
	 * 
	 * @param id
	 */
	public static void putReadedPostList(String prefFileName, String key,
			String value) {
//		SharedPreferences 见《SharedPreferences说明》
		// Map<String,?> getAll() Retrieve all values from the preferences  
		SharedPreferences preferences = getPreferences(prefFileName);
		int size = preferences.getAll().size();
		Editor editor = preferences.edit();//Editor为定义在SharedPreferences内的一个接口
//		当对象preferences的数量>=100的时候，将通过方法editor.clear();
//		清空SharedPreferences里所有数据
		if (size >= 100) {
			editor.clear();
		}
		editor.putString(key, value);
		apply(editor);
	}

	/**
	 * 读取是否是已读的文章列表
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isOnReadedPostList(String prefFileName, String key) {
		return getPreferences(prefFileName).contains(key);
	}

	/***
	 * 记录列表上次刷新时间
	 * 
	 * @author 火蚁 2015-2-9 下午2:21:37
	 * 
	 * @return void
	 * @param key
	 * @param value
	 */
	public static void putToLastRefreshTime(String key, String value) {
		SharedPreferences preferences = getPreferences(LAST_REFRESH_TIME);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		apply(editor);
	}

	/***
	 * 获取列表的上次刷新时间
	 * 
	 * @author 火蚁 2015-2-9 下午2:22:04
	 * 
	 * @return String
	 * @param key
	 * @return
	 */
	public static String getLastRefreshTime(String key) {
		return getPreferences(LAST_REFRESH_TIME).getString(key,
				StringUtils.getCurTimeStr());
	}

//	TargetApi、版本适配 见文件夹《Android版本兼容杂谈》
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void apply(SharedPreferences.Editor editor) {

		if (sIsAtLeastGB) {
			editor.apply();
		} else {
			editor.commit();
		}
		System.out.println("static apply");
	}

	public static void set(String key, boolean value) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		apply(editor);
	}

	public static void set(String key, String value) {
		Editor editor = getPreferences().edit();
		editor.putString(key, value);
		apply(editor);
	}

	public static boolean get(String key, boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}

	public static String get(String key, String defValue) {
		return getPreferences().getString(key, defValue);
	}

	public static int get(String key, int defValue) {
		return getPreferences().getInt(key, defValue);
	}

	public static long get(String key, long defValue) {
		return getPreferences().getLong(key, defValue);
	}

	public static float get(String key, float defValue) {
		return getPreferences().getFloat(key, defValue);
	}

//	TargetApi、版本适配 见文件夹《Android版本兼容杂谈》
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences() {

		SharedPreferences pre = context().getSharedPreferences(PREF_NAME,
				Context.MODE_MULTI_PROCESS);

		// 方法getSharedPreferences 各位不陌生，一般都是用此方法类获取SharedPreferences的对象。
		// 点击进入SharedPreferences的源码，知道他是一个接口。在使用SharedPreferences存储数据的
		// 时候啊，都是使用方法getSharedPreferences来获取SharedPreferences的对象。
		// 此方法是在类Context中定义的一个抽象方法，然后又在Context的子类ContextWrapper中重写了
		// 此方法，此时方法的返回值是 SharedPreferences对象。
		// 方法context()的返回值类型为BaseApplication，BaseApplication是ContextWrapper的子类，
		// 所以 BaseApplication的对象可以调用 方法getSharedPreferences，返回一个
		// SharedPreferences对象
		return pre;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences(String prefName) {
		return context().getSharedPreferences(prefName,
				Context.MODE_MULTI_PROCESS);
	}

//	获得之前保存的显示的宽度和高端，返回结果为 int类型的数组
	public static int[] getDisplaySize() {
		return new int[] { getPreferences().getInt("screen_width", 480),
				getPreferences().getInt("screen_height", 854) };
	}

//	保存屏幕的 宽、高、分辨率
//	Andorid.util 包下的DisplayMetrics 类提供了一种关于显示的通用信息，如显示大小，分辨率和字体。
	public static void saveDisplaySize(Activity activity) {
//		new一个对象 displaymetrics
		DisplayMetrics displaymetrics = new DisplayMetrics();
//		将当前窗口的一些信息与对象displaymetrics关联
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
//		使用SharedPreferences.Editor分别存储 屏幕宽度、高端、分辨率
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt("screen_width", displaymetrics.widthPixels);//屏幕 宽度
		editor.putInt("screen_height", displaymetrics.heightPixels);// 屏幕 高端
		editor.putFloat("density", displaymetrics.density);// 分辨率
		editor.commit();
	}

	public static String string(int id) {
//		这里是先调用上下文的getResource 得到Resource对象，再调用
//		Resource对象的getString，也可以直接 调用上下文的getString
//		其实，
//		直接 调用上下文的getString的源代码就是通过
//		先调用上下文的getResource 得到Resource对象，再调用
//		Resource对象的getString 来实现的。
		return _resource.getString(id);
	}

	public static String string(int id, Object... args) {
		return _resource.getString(id, args);
	}

	public static void showToast(int message) {
		showToast(message, Toast.LENGTH_LONG, 0);
	}

	public static void showToast(String message) {
		showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
	}

	public static void showToast(int message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon);
	}

	public static void showToast(String message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon, Gravity.BOTTOM);
	}

	public static void showToastShort(int message) {
		showToast(message, Toast.LENGTH_SHORT, 0);
	}

	public static void showToastShort(String message) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
	}

	public static void showToastShort(int message, Object... args) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM, args);
	}

	public static void showToast(int message, int duration, int icon) {
		showToast(message, duration, icon, Gravity.BOTTOM);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity) {
		showToast(context().getString(message), duration, icon, gravity);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity, Object... args) {
		showToast(context().getString(message, args), duration, icon, gravity);
	}

//	自定义Toast
	public static void showToast(String message, int duration, int icon,
			int gravity) {
//		判断 要显示的Toast的字符串信息是否为null且不为""。
//		String提供的方法equalsIgnoreCase，与String提供的equals类似，用于比较两个
//		字符串是否相等，不过 equalsIgnoreCase是忽略字符串的大小写因素
		if (message != null && !message.equalsIgnoreCase("")) {
			long time = System.currentTimeMillis();//获取当前的UTC times
//	1、将要显示的Toast信息与生命周期也是全局类属性lastToast（lastToast为上一次
//			Toast展示的信息） 比较是否相等。
//	2、现在时间减去上一个Toast的结束时间是否大于两秒，意思为 两次Toast间隔是否
//			大于两秒。Math.abs 的作用是对参数去绝对值。
			if (!message.equalsIgnoreCase(lastToast)
					|| Math.abs(time - lastToastTime) > 2000) {
				View view = LayoutInflater.from(context()).inflate(
						R.layout.view_toast, null);
				((TextView) view.findViewById(R.id.title_tv)).setText(message);
				if (icon != 0) {
					((ImageView) view.findViewById(R.id.icon_iv))
							.setImageResource(icon);
					((ImageView) view.findViewById(R.id.icon_iv))
							.setVisibility(View.VISIBLE);
				}
//				通过 new + 构造器 来创建对象。此对象与 Application-Context对象（context()返回结果
//				即为Application-Context）相关联。
//				注，将Toast对象和Application-Context相关联，我个人觉的不好，因为此Toast对象的生命
//				周期为全局，也就是说，会一直占用内存。若在项目中调用此方法是为它传递一个
//				Activity-Context对象 或其他的，是不是会好一些呢？？？？
				Toast toast = new Toast(context());
				toast.setView(view);
				if (gravity == Gravity.CENTER) {
					toast.setGravity(gravity, 0, 0);
				} else {
					toast.setGravity(gravity, 0, 35);
				}
//设置Toast的展示时间
				toast.setDuration(duration);
//				调用show方法，开始展示
				toast.show();
				lastToast = message;
				lastToastTime = System.currentTimeMillis();
			}
		}
	}
}
