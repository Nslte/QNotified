package nil.nadph.qnotified.hook;

import android.app.Application;
import android.graphics.Bitmap;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Initiator._TroopPicEffectsController;
import static nil.nadph.qnotified.util.Utils.*;

public class ShowPicGagHook extends BaseDelayableHook {

    private static final ShowPicGagHook self = new ShowPicGagHook();
    private boolean inited = false;

    private ShowPicGagHook() {
    }

    public static ShowPicGagHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method showPicEffect = null;
            for (Method m : _TroopPicEffectsController().getDeclaredMethods()) {
                Class[] argt = m.getParameterTypes();
                if (argt.length > 2 && argt[1].equals(Bitmap.class)) {
                    showPicEffect = m;
                    break;
                }
            }
            XposedBridge.hookMethod(showPicEffect, new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    param.setResult(null);
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int[] getPreconditions() {
        return new int[0];
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_gag_show_pic);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}