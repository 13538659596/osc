package net.oschina.app;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

/**
 * activity堆栈式管理
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月30日 下午6:22:05
 * 
 */
public class AppManager {
	
/*Stack是集合List的一个子类，继承关系为：List（父类）--Vector(子类)--Stack（子子类）。
 * 使用集合Stack来存储activity
 * */
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {}

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
//        	若集合activityStack为null，则新建一个集合对象activityStack
            activityStack = new Stack<Activity>();
        }
//        将一个Activity添加到此集合中
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
    	/*从集合activityStack中取得最后一个压入的activity对象，并将
    	此对象赋值给一个Activity的变量activity*/
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

//    若两个方法的 方法名相同，但形参声明不同，那这两个方法也是 两个不同的
//    方法
    
    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
//        	finish一个activity对象，首先从集合activityStack中remove掉，然后 finish  再 =null 
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     * 
     * 类A的某一个对象a存储在集合activityStack中，但是现在咱们只能得到 
     * 类A的类名 和 类A的一个对象b(对象b和对象a 可能相等也可能不相等)，
     * 那现在 咱们可以根据 类A 或 对象b 来finish掉这个存储在集合activityStack
     * 中的activity对象a。
     * 通过代码activity.getClass().equals(cls) 来判断这两个对象是不是同一个类的对象
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                finishActivity(activityStack.get(i));
                break;
            }
        }
        activityStack.clear();
    }

    /**
     * 获取指定的Activity
     * 
     * @author kymjs
     */
/*    public static Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }*/
    /**
     * 获取指定的Activity，或者说 得到集合activityStack中是否有指定类的对象
     * 
     * @author 修改了
     */
    public static Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity != null && activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
        }
    }
}