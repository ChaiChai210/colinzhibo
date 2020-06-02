package com.huaxin.main.dialog;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.azhon.appupdate.manager.DownloadManager;
import com.huaxin.library.entity.ConfigEntity;
import com.huaxin.main.R;
import com.lxj.xpopup.core.CenterPopupView;


public class UpdatePopWindow extends CenterPopupView {
    private Context mContext;

    private ConfigEntity.AndroidBean mData;
    private DownloadManager manager;
    private LinearLayout llUPdate;
    private LinearLayout forceUpdate;
    private ImageView close;
    private TextView title;
    private TextView intro;

private Button btn_force_update;
    public UpdatePopWindow(@NonNull Context context, ConfigEntity.AndroidBean android) {
        super(context);
        mContext = context;
        mData = android;
    }


    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_app_update;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        llUPdate = findViewById(R.id.ll_update);
        forceUpdate = findViewById(R.id.ll_force_update);
        btn_force_update = findViewById(R.id.btn_force_update);
        title = findViewById(R.id.tv_title);
        intro = findViewById(R.id.tv_intro);
        close = findViewById(R.id.iv_close);

        if (mData.isForce_update()) {
            llUPdate.setVisibility(GONE);
            close.setVisibility(GONE);
            forceUpdate.setVisibility(VISIBLE);

        } else {
            llUPdate.setVisibility(VISIBLE);
            forceUpdate.setVisibility(GONE);
            close.setVisibility(
                    VISIBLE
            );
        }

        findViewById(R.id.btn_update).setOnClickListener(v ->
                showPotato(mData)
        );
        btn_force_update.setOnClickListener(v ->
                showPotato(mData)
        );
        findViewById(R.id.btn_cancel_update).setOnClickListener(v ->
                dismiss()
        );
        close.setOnClickListener(v ->
                dismiss()
        );

        title.setText(String.format("新版本V%s", mData.getLatest_version()));
        intro.setText(mData.getUpdate_info());

    }

    private void showPotato(ConfigEntity.AndroidBean android) {
//        progressBar.setProgress(0);
        manager = DownloadManager.getInstance(mContext);
        manager.setApkName("花心社区.apk")
                .setApkUrl(android.getPackage_path())
                .setSmallIcon(R.mipmap.ic_comment)
                .download();
//        UpdateConfiguration configuration = new UpdateConfiguration()
//                //输出错误日志
//                .setEnableLog(true)
//                //设置自定义的下载
//                //.setHttpManager()
//                //下载完成自动跳动安装页面
//                .setJumpInstallPage(true)
//                //设置对话框背景图片 (图片规范参照demo中的示例图)
////                .setDialogImage(R.drawable.bg_update)
//                //设置按钮的颜色
//                //.setDialogButtonColor(Color.parseColor("#E743DA"))
//                //设置对话框强制更新时进度条和文字的颜色
//                //.setDialogProgressBarColor(Color.parseColor("#E743DA"))
//                //设置按钮的文字颜色
////                .setDialogButtonTextColor(Color.WHITE)
//                //设置是否显示通知栏进度
//                .setShowNotification(true)
//                //设置是否提示后台下载toast
//                .setShowBgdToast(false)
//                //设置强制更新
////                .setForcedUpgrade(mData.isForce_update());
//                .setForcedUpgrade(false);
//        //设置对话框按钮的点击监听
//        //设置下载过程的监听
//        manager = DownloadManager.getInstance(mContext);
//        manager.setApkName("花心社区.apk");
//        manager.setApkUrl(android.getPackage_path())
//                .setShowNewerToast(false)
//                .setSmallIcon(R.mipmap.ic_comment)
//                .setConfiguration(configuration)
//                .setApkVersionCode(android.getVersion_code())
//                .setApkVersionName(android.getLatest_version())
////                .setApkSize("20.4")
//                .setApkDescription(android.getUpdate_info())
////                .setApkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
//                .download();
    }


    protected void onShow() {
        super.onShow();
    }
}
