package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.record.EventRecord;
import nil.nadph.qnotified.record.FriendRecord;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.util.Iterator;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class TroubleshootActivity extends IphoneTitleBarActivityCompat {
    @Override
    public boolean doOnCreate(Bundle savedInstanceState) {
        super.doOnCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;
        ll.addView(subtitle(this, "若模块更新后仍有问题或bug请点击[清除缓存]可尝试修复问题"));
        ll.addView(newListItemButton(this, "清除缓存", "清除模块缓存并重新计算适配数据", null, clickToCleanCache()));
        ll.addView(subtitle(this, "清除与重置(不可逆)"));
        ll.addView(newListItemButton(this, "重置模块设置", "不影响历史好友信息", null, clickToReset()));
        ll.addView(newListItemButton(this, "清除[已恢复]的历史记录", "删除当前帐号下所有状态为[已恢复]的历史好友记录", null, clickToWipeDeletedFriends()));
        ll.addView(newListItemButton(this, "清除所有的历史记录", "删除当前帐号下所有的历史好友记录", null, clickToWipeAllFriends()));

        ll.addView(subtitle(this, ""));
        ll.addView(subtitle(this, "以下内容基本上都没用，它们为了修复故障才留在这里。"));

        for (int i = 1; i <= DexKit.DEOBF_NUM; i++) {
            try {
                String tag = DexKit.a(i);
                String orig = DexKit.c(i);
                if (orig == null) continue;
                orig = orig.replace("/", ".");
                String shortName = Utils.getShort$Name(orig);
                Class ccurr = DexKit.tryLoadOrNull(i);
                String currName = "null";
                if (ccurr != null) {
                    currName = ccurr.getName();
                }
                ll.addView(subtitle(this, "  [" + i + "]" + shortName + "\n" + orig + "\n->" + currName));
            } catch (Throwable e) {
                ll.addView(subtitle(this, "  [" + i + "]" + e.toString()));
            }
        }
        ll.addView(subtitle(this, "SystemClassLoader\n" + ClassLoader.getSystemClassLoader() + "\nContext.getClassLoader()\n" + this.getClassLoader() + "\nThread.getContextClassLoader()\n" + Thread.currentThread().getContextClassLoader()));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setTitle("故障排除");
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }


    public View.OnClickListener clickToWipeDeletedFriends() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ExfriendManager exm = ExfriendManager.getCurrent();
                            Iterator it = exm.getEvents().entrySet().iterator();
                            while (it.hasNext()) {
                                EventRecord ev = (EventRecord) ((Map.Entry) it.next()).getValue();
                                if (exm.getPersons().get(ev.operand).friendStatus == FriendRecord.STATUS_FRIEND_MUTUAL)
                                    it.remove();
                            }
                            exm.saveConfigure();
                            showToast(TroubleshootActivity.this, TOAST_TYPE_SUCCESS, "操作成功", Toast.LENGTH_SHORT);
                        } catch (Throwable e) {
                        }
                    }
                });
                dialog.setNegativeButton("取消", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("此操作将删除当前帐号(" + getLongAccountUin() + ")下的 已恢复 的历史好友记录(记录可单独删除).如果因bug大量好友被标记为已删除,请先刷新好友列表,然后再点击此按钮.\n此操作不可恢复");
                dialog.setTitle("确认操作");
                dialog.show();
            }
        };
    }

    public View.OnClickListener clickToWipeAllFriends() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ExfriendManager exm = ExfriendManager.getCurrent();
                            exm.getConfig().getFile().delete();
                            exm.getConfig().reinit();
                            exm.reinit();
                            showToast(TroubleshootActivity.this, TOAST_TYPE_SUCCESS, "操作成功", Toast.LENGTH_SHORT);
                        } catch (Throwable e) {
                        }
                    }
                });
                dialog.setNegativeButton("取消", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("此操作将删除当前帐号(" + getLongAccountUin() + ")下的 全部 的历史好友记录,通常您不需要进行此操作.如果您的历史好友列表中因bug出现大量好友,请在联系人列表下拉刷新后点击 删除标记为已恢复的好友 .\n此操作不可恢复");
                dialog.setTitle("确认操作");
                dialog.show();
            }
        };
    }

    public View.OnClickListener clickToCleanCache() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ConfigManager cfg = ConfigManager.getCache();
                            cfg.getAllConfig().clear();
                            cfg.getFile().delete();
                            System.exit(0);
                        } catch (Throwable e) {
                            log(e);
                        }
                    }
                });
                dialog.setNegativeButton("取消", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("确认清除缓存,并重新计算适配数据?\n点击确认后请等待3秒后手动重启QQ.");
                dialog.setTitle("确认操作");
                dialog.show();
            }
        };
    }

    public View.OnClickListener clickToReset() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ConfigManager cfg = ConfigManager.getDefaultConfig();
                            cfg.getAllConfig().clear();
                            cfg.getFile().delete();
                            System.exit(0);
                        } catch (Throwable e) {
                            log(e);
                        }
                    }
                });
                dialog.setNegativeButton("取消", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("此操作将删除该模块的所有配置信息,包括屏蔽通知的群列表,但不包括历史好友列表.点击确认后请等待3秒后手动重启QQ.\n此操作不可恢复");
                dialog.setTitle("确认操作");
                dialog.show();
            }
        };
    }

}
