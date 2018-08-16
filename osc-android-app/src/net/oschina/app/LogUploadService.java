package net.oschina.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.util.StringUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.FileUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.loopj.android.http.AsyncHttpResponseHandler;

/*开发一个Service需要两步：
1.定义一个继承Service的子类
2.在AndridManifest.xml文件中配置该Service。

*与Activity相似的是，Service中也定义了系列声明周期方法，那么其子
*类一般可以重写这些周期方法，在这些方法中定义 业务 逻辑。
*例如，在类Service中定义了这样的一些周期方法：
*abstract IBinder onBind(Intent intent)---这个方法是 抽象方法，所
以Service子类必须实现此方法；
*void onCreate---当此Service第一次被创建后将立即回调此方法
*void onDestroy---当此Service被关闭之前 将会调用此方法
*void onStartCommand(Intent intent,int flags,int startid)---此方法
*的早期版本是void onStart(Intent intent,int startid) ，每次客户端
*调用startService(intent)方法启动此Service时 都会回调该方法。
*boolean onUnbind(Intent intent):当此Service上绑定的所有客户端
*断开连接时 将会回调此方法。
*
*这些方法就是Service组件的框架。当Service子类想做某些事情的时候
*，只要在onCreate或onStartCommand方法张定义相关业务代码 就
*可以了。
*
*/

// 咱们来定义 Service子类 LogUploadService
public class LogUploadService extends Service {

//	Service必须 实现 此方法。在Service中，onBind 是抽象方法
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    在onStartCommand中定义业务功能代码，来实现 咱们
//    需要完成的事情。
//    此处，是 咱们自定义的 异常处理的一部分。
//    在这个项目中啊，咱们自定义了 一个异常处理类。具体的说呢，若
//    此项目在运行过程中，报出异常了  那么程序会将此异常信息保存至
//    手机文件夹OSChina下的OSCLog.log文件中，除此之外 还会发出通知
//    等异常操作。
//    当用户 再次打开此应用程序的时候，进入 开始界面后，会将保存在
//    文件OSCLog.log中的异常信息 上传至 服务器。
//    将本地文件中的数据 上传至 服务器，是一个耗时 操作，必须 定义在
//    Service中  或者 子线程中，对吗？！
//   好，此处onStartCommand方法体内的 代码就是 在实现：将保存在
//    文件OSCLog.log中的异常信息 上传至 服务器。
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//    	咱们来看一下，这个 业务功能 是如何实现的呢？
//    	首先，咱们需要找到这个文件，并创建这个文件的File对象，在学Java
//    	的时候，咱们举了几个这样的例子，对吧？获取一个 本地文件的 File对象
//    	这里呢，咱们是使用的KJFrameForAndroid 又叫KJLibrary，是一个
//    	Android的快速开发工具包。封装了android中的文件读写、Bitmap、Http、插件
//    	模块加载.....等操作的框架，这个开发工具包呢 让咱们用最少的代码轻松实现这些功能，
//    	所以咱们来使用它。
//KJFrameForAndroid的源代码就是封装了Android原生SDK中支持的一些类和方法。
//    	那如何来使用这个开发工具包呢?。。。。。。。。。
//    	获取本地文件夹OSChina中的OSCLog.log文件的File实例，使用FileUtils的静态方法
//    	public static java.io.File getSaveFile(java.lang.String folderPath,
//                java.lang.String fileNmae)
//    	其第一个参数是 其外部存储路径下 文件夹的目录，第二个参数是文件夹目录下的 文件名
//    	此方法的返回值：如果文件不存在则创建,如果如果无法创建文件或文件名为空则返回null
        final File log = FileUtils.getSaveFile("OSChina", "OSCLog.log");
        String data = null;
//        在使用Java的IO流的时候，会报出 Checked异常，所以啊 需要 异常处理，这里
//        咱们来使用 try...catch... 
        try {
//        	创建File实例的 输入流 ，这里 咱们是创建了一个 文件字节输入流，
            FileInputStream inputStream = new FileInputStream(log);
//            现在啊，咱们需要得到 这个这个字节流中封装的 字符串，怎么办？ 咱们来
//            创建一个方法
//            将一个字节输入流 InputStream流转换成字符串。来 创建一个 
//            String toConvertString(InputStream is) { }方法
            data = StringUtils.toConvertString(inputStream);
//            这样，咱们就获取到了 文件OSCLog.log 中的数据data ，咱们将这个数组转化
//            成了字符串
            
//            总结一下：若想获取本地文件中的数据，可以
//            1、获取此文件的File对象
//            2、将此File对象转化为IO流
//            3、从此IO流中 读取数据，转化为咱们需要的数据格式
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        判断给定字符串是否空白串。 空白串是指由空格、制表符、
//        回车符、换行符组成的字符串 
//        若输入字符串为null或空字符串，返回true
//        好，咱们来定义一个方法 isEmpty(String input)
        if (!StringUtils.isEmpty(data)) {// 如果data不为null或空字符串，则进入if条件体。

//        	这里啊，咱们需要将 异常信息 上传到服务器端，用到了网络编程的知识。
//        	咱们先来 认识一下 网络请求库android-async-http.....(见文件夹 android-async-http
//        	开源HTTP类库)............................................
//        	好，开发库android-async-http 介绍完之后呢，继续来说 咱们这个项目中的  将 异常
//        	信息 上传到服务器端 的模块。
//        	当然，你可以在这个地方 使用开发库android-async-http 来完成 网络编程 功能，但
//        	是 项目中会很多地方用到 网络编程 的功能模块，那咱们是 哪儿用到就在哪儿 加上
//        	网络请求 功能代码吗？  这样当然能够实现相应的 业务功能，但是 这样的话，你的代码
//        	将冗繁、杂乱、你将多次编写重复的代码、你将多次创建同样的对象....  这样的代码是
//        	很糟糕的，项目中的代码应该是 优雅的、良好重构的、可复用的，对吗？
//        	好，现在 咱们来设计 此项目中的 网络编程模块。
//        	根据android-async-http的编码格式：
/*        	 AsyncHttpClient client = new AsyncHttpClient();
        	 client.get 或 post("https://www.google.com", params, new AsyncHttpResponseHandler() {
        	     @Override
        	     public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        	          System.out.println(response);
        	     }
        	     @Override
        	     public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
        	 error)
        	 {
        	          error.printStackTrace(System.out);
        	     }
        	 });*/
//        	第一步是创建 AsyncHttpClient 的对象
//        	第二步是 AsyncHttpClient对象.get 或 post(url, params, AsyncHttpResponseHandler);
        	
//        	在此项目中，对于第一步呢  在这个项目一启动的时候 就创建AsyncHttpClient对象，详情
//        	见 类AppContext。
//        	对于第二步呢，对于 网络请求功能 此项目封装了一个网络请求分装类OSChinaApi，此项目中
//        	的网络请求功能可直接调用此类中定义的方法。网络请求分装类OSChinaApi的真正的功能实现
//        	是依附 类ApiHttpClient，此类是 网络请求库android-async-http 网络请求功能代码 所在地。 
        	
            OSChinaApi.uploadLog(data, new AsyncHttpResponseHandler() {
            	
//            	此网络请求成功 情况下，可以在此处做出相应的业务逻辑，并且此处得到 服务器的返回值
                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//                	成功将BUG日志上传之后呢，删除本地的BUG日志文件
                    log.delete();
//                    中止Service
                    LogUploadService.this.stopSelf();
                }

//                此网络请求不成功 情况下，可以在此处做出相应的业务逻辑
                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                        Throwable arg3) {
                    LogUploadService.this.stopSelf();
                }
            });
        } else {// 如果data为null或空字符串，也中止 Service
            LogUploadService.this.stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
