package net.oschina.app;

import java.io.File;

import net.oschina.app.ui.MainActivity;
import net.oschina.app.util.TDevice;

import org.kymjs.kjframe.http.KJAsyncTask;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 应用启动界面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月22日 上午11:51:56
 * 
 */

/*程序的启动页面*/

public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 防止第三方跳转时出现双实例
//        现在呢，咱们来解决一个问题，我们解决这个问题的考虑范围是 
//        按 home键 将程序 转入 后台 或者说 在栈中，这个程序 没有 被kill掉。
//        若因系统内存不足等原因，系统将此 在后台运行的 程序 kill掉了，不在
//        咱们讨论的范围内哦！
//     咱们在操作一个应用程序A的时候，咱们经常习惯 按home键 将程序A转入
//        后台运行并进入launcher界面来打开其他应用程序。过了一会儿，您啊 
//        又从launcher界面 点击程序A 图标进入程序，此时  咱们很多时候 是
//        想让 程序A 直接进入到上一次 咱们按 home键的那个 界面，对吧？！
//        但是呢，一般 当你从launcher界面 点击程序A 图标 ，都会先启动 程序
//        的启动页面，也就是说 从程序的启动界面开始进入程序A。
//        所以，咱们为了 不启动 启动页面AppStart  就在类
//        AppStart的onCreate的方法的最前端中，做一个判断 如果在程序的 栈 
//        中，有程序A的其他页面存在，那么就直接 finish掉 启动页面AppStart,
//        此时 程序A会直接进入 上次 栈 中已经存储的页面（就是你按 home 键
//        的那个 页面）。例如，手机QQ  微信 微博等的应用app 也都是这样
//        设计的哦。
//        好，咱们是怎么判断的呢？ 咱们就判断 栈 中，是否有MainActivity这个
//        类的实例，若有实例 就finish掉启动页面。
//        那问题来了，如何判断 栈 中，是否存在MainActivity的实例 呢？？
//        有的朋友 可能首先想到 使用instanceof。但是，由于instanceof运算符的
//        特殊性，当前面 对象时后面类的实例或者其子类的实例都将返回true,所以
//        使用instanceof进行判断是有问题的。这里呢，咱们就要使用到反射的知识
//        了。以类Person   和  对象obj  为例,判断obj是否为类Person的实例
//        if(obj != null && obj.getClass().equals(Person.class)){
//   			return true;	     
//    		}
//        第一个问题：
//        那么，对象.getClass和类.class 是什么呢，这是反射的知识
//        Class类是为了保存JAVA虚拟机运行时(RTTI)对所有对象进行类型识别的信息而设立的.  
//        当然Class也是继承自Object类的,每个类都有Class对象,想得到一个类的Class对象共有
//        三种方法.
//        1:调用getClass()   
//        Employee emp;  
//        Class   cls=emp.getClass();  返回来的对象保存着对象的类信息
//        2:静态方法forName(String   )   
//        String   className="Employee";  
//        Class   cls=Class.forName(className);  
//        3:class成员变量法 
//        Class   cls=Employee.class;   
//        第二个问题：obj.getClass().equals(Person.class) 应该用equals还是
//        == 进行比较呢？
//        Java程序中测试两个变量是否相等有两种方式，一种是 == ，另一种
//        是 equals。当使用 == 来判断两个变量是否相等时，如果两个变量
//        是基本类型的变量，且都是数值型（不一定要去数据类型严格形同），
//        则只要两个变量的值相等，使用==判断就会返回true。
//        但对于两个引用类型的变量，必须他们指向同一个对象时，== 判断
//        才会van会true。
//        equals方法是Object类提供的一个实例方法，因此 所有引用变量
//        都可以调用该方法来判断是否与其他引用变量相等。
//        但是，咱们进入 Object的equals的源代码 发现是这样的
//          public boolean equals(Object o) {
//        			return this == o;
//    		}
//        所以，equals没有被重写的情况下，equals或== 对两个对象 进行
//        比较的 作用是一样的。同样要求两个引用变量指向同一个对象 才会
//        返回true。因此，这个Object类提供的equals方法没有太大的实际
//        意义。一般情况下，咱们通过重写equals方法，来自定义相等的标准。
//        例如，咱们自定义了一个类，在类里面重写了equals方法，这个
//        重写的equals就是这个类的对象们之间进行比较是否相等的标准。
//        再例如，咱们在进行 字符串 比较的时候呢，只要求他们引用字符
//        串对象里包含的字符序列相同即认为相等。原因是 String已经重写
//        了Object的equals() 方法，String的equals()方法判断两个字符串相等
//        的标准是：只要两个字符串所包含的字符序列相同，通过equals()
//        比较则返回true。
//        所以，obj.getClass().equals(Person.class) 应该用equals还是
//      == 进行比较呢 的问题，只要弄明白 Class 是否重写了equals方法
//        就可以得到答案。若没有重写 equals方法，则 使用 equals 或
//        == 作用是一样的；若重写了，就依据具体情况了。
//        咱们 发现Class 并没重写 方法equals()，所以 此处 使用 equals 或
//      == 作用是一样的。
/*        public static Activity getActivity(Class<?> cls) {
            if (activityStack != null)
                for (Activity activity : activityStack) {
                    if (activity != null && activity.getClass().equals(cls)) {
                        return activity;
                    }
                }
            return null;
        }*/
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        
        //现在啊  咱们来使用AlphaAnimation ，使得启动屏透明度alpha渐变展示
//        控制对象alpha的动画。这个动画可以通过改变alpha属性，
//        达到渐进渐出的效果。
//        使用构造方法：AlphaAnimation(float fromAlpha, float toAlpha)创建
//        对象
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
//        设置动画持续时长(一秒)
        aa.setDuration(1000);
//        立即启动动画，这里呢  使用startAnimation(aa)或setAnimation(aa)，
//        这两个方法 都可以达到 启动动画的效果。它们肯定是有差别的嘛，
//        我建议 这里使用startAnimation
        view.startAnimation(aa);
//        添加事件监听  对动画的 开始时、结束时 等进行监听。当动画结束 跳转
//        到 MainActivity
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
//            	在动画结束时，会触发的方法里面 定义 启动MainActivity 的业务逻辑
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}
        });
    }
    
   /* 对于一个activity不管是 新创建 或者 从后台回到前台...等方式，系统都会调用
    这个 onResume 方法。那么， 咱们在这个方法中做一个判断 ：若此应用的这个APK
    是首次安装在手机上 且 第一次运行（注：此应用第一次安装在手机上 和 此应用apk之前安装过卸载了现在
    又重新安装在手机上 ，这里我都称为 这个APK是首次安装在手机上），那么咱们就将此
    应用程序的缓存图片路径下的所有图片都删除（表明这些图片不是 本次安装运行所产生的图片）。
    */

 /*   那么如何判断这个APK是首次安装在手机上呢？
    首先去此应用的存储路径上的一个文件中去获取一个 数据，这个数据 定义为 之前缓存的版本号。
    若无法获取此 数据，则默认返回为 -1（第一次安装后，此数据肯定为-1。运行一次之后，下面的代码将会改变
    对此数据重新赋值）。然后再获取 当前已经 安装的、在代码中表明的此应用的版本号（肯定是大于-1）。
    获取之前缓存的版本号 和 对此数据重新赋值 的功能的实现，咱们都是使用 开源工具包KJFrameForAndroid 
    的类PreferenceHelper。
    KJFrameForAndroid 又叫KJLibrary，是一个
   	Android的快速开发工具包。封装了android中的文件读写、Bitmap、Http、插件
   	模块加载.....等操作的框架，这个开发工具包呢 让咱们用最少的代码轻松实现这些功能，
 	所以咱们来使用它。KJFrameForAndroid的源代码就是封装了Android原生SDK中支持的一些类和方法。
 	详情可以查看文档：https://github.com/kymjs/KJFrameForAndroid   或者  
 	http://git.oschina.net/kymjs/KJFrameForAndroid。
 	通过查看 此开源工具包的 api文档 和 其源代码，知道：PreferenceHelper 是以android原生的小数据
 	存储类SharedPreferences 为基础，再添加一些操作逻辑，最终形成了简单明了的 读写存储 小数据的
 	工具类 PreferenceHelper。SharedPreferences 对文件存储的原理是 在内部存储路径上 创建一个 存储
 	key-value 的形式的数据的文件，并且此文件会随着 此应用程序的卸载而系统自动删除。PreferenceHelper
 	的核心是SharedPreferences，所以PreferenceHelper在数据存储形态方面拥有SharedPreferences的所有特性。
    */
    @Override
    protected void onResume() {
        super.onResume();
//        此处调用PreferenceHelper的
//        readInt(android.content.Context context, java.lang.String fileName, java.lang.String k, int defv) 方法，
//        第一个参数是 上下文对象、第二个参数是 创建的用于报错此数据的文件名、第三个参数是 key值（保存的数据
//        就是与之对应的value值）、第四个参数 是默认值（当没有此文件 或 没有此key对应的value值时，返回默认值）
        int cacheVersion = PreferenceHelper.readInt(this, "first_install",
                "first_install", -1);
//        获取项目代码中标明的版本号
        int currentVersion = TDevice.getVersionCode();
//        如果之前缓存的版本号小于现在的版本号，则表明此应用的这个APK是首次安装在手机上 且 第一次运行，所以
//        此程序图片缓存路径下的图片就不是 本次安装运行产生的，所以应该删除。并且 对此缓存数据经常重写赋值，
//        所赋的值就是 当前的版本号。使得 以后再运行此程序时 ，cacheVersion < currentVersion 不成立 避免删除
//        所缓存的图片。
        if (cacheVersion < currentVersion) {
            PreferenceHelper.write(this, "first_install", "first_install",
                    currentVersion);
            cleanImageCache();
        }
    }

    /*如何删除缓存图片路径下的所有图片呢？ 这里 咱们还是使用快捷开发工具包 KJFrameForAndroid*/
    private void cleanImageCache() {
//    	使用类 org.kymjs.kjframe.utils.FileUtils 的方法
//    	public static java.io.File getSaveFolder(java.lang.String folderName)。
//    	此方法的参数是SD卡下的 文件夹目录。
//    	此方法的返回值为 SD卡（准确的说是 外部存储，不限于SD卡）下的指定文件夹的 File对象，若文件夹不存在则创建
        final File folder = FileUtils.getSaveFolder("OSChina/imagecache");
        
//        循环遍历出 该路径下所有的 文件并删除。这是一个耗时操作，所以这里咱们需要 使用异步线程
//        来完成此任务，可以使用Android自带的AsyncTask来实现。这里呢，咱们还是使用 
//        快速开发工具包KJFrameForAndroid中类KJAsyncTask，本类修改自 The Android Open SourceProject
//    创建这个开源类的目的是：    使用高并发的线程池并发执行异步任务，用于替换Android自带的AsyncTask，
//        达到多线程执行的最大效率。
//        这个类提供了一个静态方法 public static void execute(java.lang.Runnable runnable)，直接执行一个runnable，
//        用于瞬间大量并发的场景，比如，假设用户拖动ListView时如果需要启动大量异步线程，
//        而拖动过去时间很久的用户已经看不到，允许任务丢失。
//        详细介绍http://kjframe.github.io/
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (File file : folder.listFiles()) {
                    file.delete();
                }
            }
        });
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
//    	通过构造器 Intent(Context packageContext, Class<?> cls)
// 来创建对象，这里呢 创建了一个Service组件的实例。Service与
//    	Activity的差别是 Service一直运行在后台，没有用户交互界面，并且
//    	Service与Activity一样 有自己的生命周期。
//    	咱们启动一个Service在后台帮咱们完成 一些 工作，就相当于 启动了
//    	一个子线程 来完成一些耗时操作 一样的功效。
//    	现在，咱们进入 Service类  LogUploadService 看看他的功能是什么？
        Intent uploadLog = new Intent(this, LogUploadService.class);
        startService(uploadLog);
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
