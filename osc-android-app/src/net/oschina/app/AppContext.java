package net.oschina.app;

import static net.oschina.app.AppConfig.KEY_FRITST_START;
import static net.oschina.app.AppConfig.KEY_LOAD_IMAGE;
import static net.oschina.app.AppConfig.KEY_TWEET_DRAFT;

import java.util.Properties;
import java.util.UUID;

import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.User;
import net.oschina.app.cache.DataCleanManager;
import net.oschina.app.util.CyptoUtils;
import net.oschina.app.util.MethodsCompat;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;

import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.utils.KJLoger;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends BaseApplication {

    public static final int PAGE_SIZE = 20;// 默认分页大小

    private static AppContext instance;

    private int loginUid;

    private boolean login;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        initLogin();
        // Thread.setDefaultUncaughtExceptionHandler(AppException
        // .getAppExceptionHandler(this));
        UIHelper.sendBroadcastForNotice(this);
    }
    
// 在这个地方进行网络请求对象的创建，有两方面原因：
//    1、安卓应用程序的入口点是application的onCreate方法。在此处创建网络
//    请求对象，保证了 在此应用程序的其他活动之前来创建网络请求对象，后面
//    的项目中，就可以直接使用这个 网络请求对象来进行数据交互、
//     2、Application-Context的生命周期为整个Application，所以如果是一些需要
//    在整个应用期间都存在的资源 或者 对象，我们可以将它放进一个
//    Application-Context中,然后通过获取这个Application-Context来使用它们。
//    例如，此开源中国app中，在Application的子类中创建并初始化了
//    网络请求对象client，并且对这个对象进行一些属性设置。即，这个网络请求
//    对象会存在于整个 应用程序的周期中，在项目中的任意地方 任意时候，只要
//    调用这个网络请求对象client 的执行网络请求的方法 就可以进行网络数据请求了。对吗？！
    
    private AsyncHttpClient client;// 全局变量
    private void init() {
        // 初始化网络请求
//        AsyncHttpClient client = new AsyncHttpClient(); 原项目时这样写的。我个人认为，
//        若想网络请求对象client设置成为一个全局变量是否是 会好一些呢？ 就改成这样，如下
    	client = new AsyncHttpClient();
        
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);//此处
//        myCookieStore和全局变量application关联了，个人认为myCookieStore也成全局的了，
//        并且下面myCookieStore又和client关联了，应该此处 也会使得client成为全局的！！
        client.setCookieStore(myCookieStore);
        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));

        // Log控制器
        KJLoger.openDebutLog(true);// 设置KJFrameForAndroid工具包的打印日志
//        此例子中，咱们对LogCat的Log.d Log.e Log.i Log.v Log.w 包装成一个Log类。
//        下面这句代码就是对TLog的属性DEBUG进行赋值，来设置 Log的打印。
        TLog.DEBUG = BuildConfig.DEBUG;
        

        // Bitmap缓存地址。这里的Bitmap缓存，咱们还是使用KJFrameForAndroid工具包
        BitmapConfig.CACHEPATH = "OSChina/imagecache";
    }

    private void initLogin() {
        User user = getLoginUser();
        if (null != user && user.getId() > 0) {
            login = true;
            loginUid = user.getId();
        } else {
            this.cleanLoginInfo();
        }
    }

    /**
     * 获得当前app运行的AppContext
     * 
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     * 
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取App唯一标识
     * 
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     * 
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 保存登录信息
     * 
     * @param username
     * @param pwd
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final User user) {
        this.loginUid = user.getId();
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.getId()));
                setProperty("user.name", user.getName());
                setProperty("user.face", user.getPortrait());// 用户头像-文件名
                setProperty("user.account", user.getAccount());
                setProperty("user.pwd",
                        CyptoUtils.encode("oschinaApp", user.getPwd()));
                setProperty("user.location", user.getLocation());
                setProperty("user.followers",
                        String.valueOf(user.getFollowers()));
                setProperty("user.fans", String.valueOf(user.getFans()));
                setProperty("user.score", String.valueOf(user.getScore()));
                setProperty("user.favoritecount",
                        String.valueOf(user.getFavoritecount()));
                setProperty("user.gender", String.valueOf(user.getGender()));
                setProperty("user.isRememberMe",
                        String.valueOf(user.isRememberMe()));// 是否记住我的信息
            }
        });
    }

    /**
     * 更新用户信息
     * 
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final User user) {
        setProperties(new Properties() {
            {
                setProperty("user.name", user.getName());
                setProperty("user.face", user.getPortrait());// 用户头像-文件名
                setProperty("user.followers",
                        String.valueOf(user.getFollowers()));
                setProperty("user.fans", String.valueOf(user.getFans()));
                setProperty("user.score", String.valueOf(user.getScore()));
                setProperty("user.favoritecount",
                        String.valueOf(user.getFavoritecount()));
                setProperty("user.gender", String.valueOf(user.getGender()));
            }
        });
    }

    /**
     * 获得登录用户的信息
     * 
     * @return
     */
    public User getLoginUser() {
        User user = new User();
        user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
        user.setName(getProperty("user.name"));
        user.setPortrait(getProperty("user.face"));
        user.setAccount(getProperty("user.account"));
        user.setLocation(getProperty("user.location"));
        user.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
        user.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
        user.setScore(StringUtils.toInt(getProperty("user.score"), 0));
        user.setFavoritecount(StringUtils.toInt(
                getProperty("user.favoritecount"), 0));
        user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
        user.setGender(getProperty("user.gender"));
        return user;
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty("user.uid", "user.name", "user.face", "user.location",
                "user.followers", "user.fans", "user.score",
                "user.isRememberMe", "user.gender", "user.favoritecount");
    }

    public int getLoginUid() {
        return loginUid;
    }

    public boolean isLogin() {
        return login;
    }

    /**
     * 用户注销
     */
    public void Logout() {
        cleanLoginInfo();
        ApiHttpClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;

        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);
    }

    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        new KJBitmap().cleanCache();
    }

    public static void setLoadImage(boolean flag) {
        set(KEY_LOAD_IMAGE, flag);
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     * 
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static String getTweetDraft() {
        return getPreferences().getString(
                KEY_TWEET_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setTweetDraft(String draft) {
        set(KEY_TWEET_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static String getNoteDraft() {
        return getPreferences().getString(
                AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }
}
