package com.iplatform.testtinker.tinker.util;

import com.iplatform.testtinker.tinker.crash.UncaughtExceptionHandler;
import com.iplatform.testtinker.tinker.reporter.LoadReporter;
import com.iplatform.testtinker.tinker.reporter.PatchListener;
import com.iplatform.testtinker.tinker.reporter.PatchReporter;
import com.iplatform.testtinker.tinker.service.ResultService;
import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.UpgradePatchRetry;

public class TinkerManager {
    private static final String TAG = "TinkerManager";

    private static ApplicationLike          sApplicationLike;
    private static UncaughtExceptionHandler sUncaughtExceptionHandler;
    private static boolean                  isInstalled = false;

    public static ApplicationLike getTinkerApplicationLike() {
        return sApplicationLike;
    }

    public static void setTinkerApplicationLike(ApplicationLike applicationLike) {
        sApplicationLike = applicationLike;
    }

    public static void initFastCrashProtect() {
        if (sUncaughtExceptionHandler == null) {
            sUncaughtExceptionHandler = new UncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(sUncaughtExceptionHandler);
        }
    }

    public static void setUpgradeRetryEnable(boolean enable) {
        UpgradePatchRetry.getInstance(sApplicationLike.getApplication()).setRetryEnable(enable);
    }


    /**
     * all use default class, simply Tinker install method
     */
    public static void sampleInstallTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        TinkerInstaller.install(appLike);
        isInstalled = true;

    }

    /**
     * you can specify all class you want.
     * sometimes, you can only install tinker in some process you want!
     *
     * @param appLike
     */
    public static void installTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        //or you can just use DefaultLoadReporter
        com.tencent.tinker.lib.reporter.LoadReporter loadReporter = new LoadReporter(appLike.getApplication());
        //or you can just use DefaultPatchReporter
        com.tencent.tinker.lib.reporter.PatchReporter patchReporter = new PatchReporter(appLike.getApplication());
        //or you can just use DefaultPatchListener
        com.tencent.tinker.lib.listener.PatchListener patchListener = new PatchListener(appLike.getApplication());
        //you can set your own upgrade patch if you need
        AbstractPatch upgradePatchProcessor = new UpgradePatch();

        TinkerInstaller.install(appLike,
                loadReporter, patchReporter, patchListener,
                ResultService.class, upgradePatchProcessor);

        isInstalled = true;
    }
}
