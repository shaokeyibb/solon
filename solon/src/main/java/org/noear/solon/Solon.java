package org.noear.solon;

import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.ConsumerEx;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 应用管理中心
 *
 * <pre><code>
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class Solon {
    private static long STOP_DELAY = 10 * 1000;

    private static SolonApp global;

    /**
     * 全局实例
     */
    public static SolonApp global() {
        return global;
    }

    /**
     * 应用配置
     * */
    public static SolonProps cfg(){
        return global().cfg();
    }


    /**
     * 启动应用（全局只启动一个），执行序列
     *
     * <p>
     * 1.加载配置（约定：application.properties    为应用配置文件）
     * 2.加载自发现插件（约定：/solonplugin/*.properties 为插件配置文件）
     * 3.加载注解Bean（约定：@XBean,@XController,@XInterceptor 为bean）
     * 4.执行Bean加载事件（采用：注册事件的方式进行安需通知）
     */
    public static SolonApp start(Class<?> source, String[] args) {
        return start(source, args, null);
    }

    public static SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
        //1.初始化应用，加载配置
        NvMap argx = NvMap.from(args);
        return start(source, argx, initialize);
    }

    public static SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
        if (global != null) {
            return global;
        }

        //绑定类加载器
        JarClassLoader.bindingThread();


        PrintUtil.blueln("solon.App:: Start loading");

        //1.创建应用
        global = new SolonApp(source, argx);

        //2.1.内部初始化（顺序不能乱!）
        global.init();

        //2.2.自定义初始化
        if (initialize != null) {
            try {
                initialize.accept(global);
            } catch (Throwable ex) {
                throw Utils.throwableWrap(ex);
            }
        }

        //3.运行
        global.run();


        //4.安全停止
        STOP_DELAY = Solon.cfg().getLong("solon.stop.delay", 10 *1000);

        if(global.enableSafeStop()){
            //添加关闭勾子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> Solon.stop0(false, STOP_DELAY)));
        }

        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        if (rb == null) {
            PrintUtil.blueln("solon.App:: End loading @" + global.elapsedTimes() + "ms");
        } else {
            PrintUtil.blueln("solon.App:: End loading @" + global.elapsedTimes() + "ms pid=" + rb.getName());
        }
        return global;
    }
    public static void stop() {
        stop(true, STOP_DELAY);
    }
    public static void stop(boolean exit, long delay) {
        new Thread(() -> stop0(exit, delay)).start();
    }

    public static void stop0(boolean exit, long delay) {
        if (Solon.global() == null) {
            return;
        }

        //1.预停止
        Solon.cfg().plugs().forEach(p -> p.prestop());
        System.err.println("[Security to stop] 1 completed (1.prestop 2.delay 3.stop)");

        //2.延时
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {

            }
            System.err.println("[Security to stop] 2 completed (1.prestop 2.delay 3.stop)");
        }

        //3.停目
        Solon.cfg().plugs().forEach(p -> p.stop());

        System.err.println("[Security to stop] 3 completed (1.prestop 2.delay 3.stop)");

        //4.直接退出?
        if (exit) {
            System.exit(0);
        }
    }
}
