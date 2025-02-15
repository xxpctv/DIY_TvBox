package com.github.tvbox.osc.base;

import androidx.multidex.MultiDexApplication;

import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.github.tvbox.osc.data.AppDataManager;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.EpgUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LocaleHelper;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.js.JSEngine;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import com.github.tvbox.osc.util.LOG;
import com.p2p.P2PClass;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

/**
 * @author pj567
 * @date :2020/12/17
 * @description:
 */
public class App extends MultiDexApplication {
    private static App instance;
private static P2PClass p;
    public static String burl;
    @Override
    public void onCreate() {
        }
         public static P2PClass getp2p() {
        try {
            if (p == null) {
                p = new P2PClass(instance.getExternalCacheDir().getAbsolutePath());
            }
            return p;
        } catch (Exception e) {
            LOG.e(e.toString());
            return null;
        }
    }
        super.onCreate();
        instance = this;
        initParams();
        // takagen99 : Initialize Locale
        initLocale();
        // OKGo
        OkGoHelper.init();
        // Get EPG Info
        EpgUtil.init();
        // 初始化Web服务器
        ControlManager.init(this);
        //初始化数据库
        AppDataManager.init();
        LoadSir.beginBuilder()
                。addCallback(new EmptyCallback())
                。addCallback(new LoadingCallback())
                。commit();
        AutoSizeConfig.getInstance().setCustomFragment(true).getUnitsManager()
                。setSupportDP(false)
                。setSupportSP(false)
                。setSupportSubunits(Subunits.MM);
        PlayerHelper.init();

        // Delete Cache
        File dir = getCacheDir();
        FileUtils.recursiveDelete(dir);
        dir = getExternalCacheDir();
        FileUtils.recursiveDelete(dir);

        // Add JS support
        JSEngine.getInstance().create();
    }

    private void initParams() {
        // Hawk
        Hawk.init(this).build();
        Hawk.put(HawkConfig.DEBUG_OPEN, false);

        putDefault(HawkConfig.HOME_REC, 0);       // Home Rec 0=豆瓣, 1=推荐, 2=历史
        putDefault(HawkConfig.PLAY_TYPE, 1);      // Player   0=系统, 1=IJK, 2=Exo
        putDefault(HawkConfig.IJK_CODEC, "硬解码");// IJK Render 软解码, 硬解码
        putDefault(HawkConfig.HOME_SEARCH_POSITION, false);// 上方/下方
        putDefault(HawkConfig.HOME_MENU_POSITION, false);// 上方/下方
        putDefault(HawkConfig.HOME_SHOW_SOURCE, true);// 开启/关闭
        putDefault(HawkConfig.HOME_REC_STYLE, true);// 开启/关闭
        putDefault(HawkConfig.HOME_NUM, 0);    // 0=30 1=50 2=70


    }

    private void initLocale() {
        if (Hawk.get(HawkConfig.HOME_LOCALE, 0) == 0) {
            LocaleHelper.setLocale(App.this, "zh");
        } else {
            LocaleHelper.setLocale(App.this, "");
        }
    }

    public static App getInstance() {
        return instance;
    }

    private void putDefault(String key, Object value) {
        if (!Hawk.contains(key)) {
            Hawk.put(key, value);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JSEngine.getInstance().destroy();
    }

}
