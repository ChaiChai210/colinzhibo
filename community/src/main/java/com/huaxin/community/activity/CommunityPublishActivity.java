package com.huaxin.community.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.librarybase.base.BaseActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.community.R;
import com.huaxin.community.R2;
import com.huaxin.library.entity.LabelClassify;
import com.huaxin.library.entity.LabelDetail;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.intercepter.WeChatPresenter;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.UrlConstants;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tencent.mmkv.MMKV;
import com.vincent.videocompressor.VideoCompress;
import com.xuexiang.xui.widget.progress.CircleProgressView;
import com.xuexiang.xui.widget.tabbar.TabControlView;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_COMMUNTIY_PUBLISH)
public class CommunityPublishActivity extends BaseActivity {
    @BindView(R2.id.tab_option)
    TabControlView tabOption;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.tv_label)
    TextView tvLabel;
    @BindView(R2.id.layout_label)
    LinearLayout layoutLabel;
    @BindView(R2.id.gridLayout)
    GridLayout gridLayout;
    @BindView(R2.id.et_input)
    EditText idEtInput;
    @BindView(R2.id.btn_publish)
    Button btnPublish;
    @BindView(R2.id.hpv_compress)
    CircleProgressView hpvCompress;
    private static final String TYPE_VIDEO = "video";
    private static final String TYPE_IAMGE = "image";
    private static final String TYPE_TEXT = "text";
    private String type = TYPE_VIDEO;
    private ArrayList<ImageItem> picList = new ArrayList<>();

    private int maxCount = 1;
    private LabelDetail mChooseCircle;

    private int circleId;
    private String inputTitle;

    private String videoUrl;
    private String imageUrl;

    @Override
    protected void loadData() {
        picList.clear();
        refreshGridLayout();
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("发布帖子");
        MMKV kv = MMKV.defaultMMKV();
        videoUrl = kv.decodeString(Constant.SP_SQ_VIDEO) + "/api/Article/Create";
        imageUrl = kv.decodeString(Constant.SP_SQ_IMG) + "/api/Article/Create";
        tabOption.setOnTabSelectionChangedListener((title, value) -> {
            if (title.equals("视频")) {
                type = TYPE_VIDEO;
                maxCount = 1;
                ImmersionBar.with(this).keyboardEnable(true).init();
            } else if (title.equals("图片")) {
                type = TYPE_IAMGE;
                maxCount = 9;
                ImmersionBar.with(this).keyboardEnable(true).init();
            } else if (title.equals("短文")) {
                type = TYPE_TEXT;
                ImmersionBar.with(this).keyboardEnable(false).init();
            }
            setLayout();
        });

        tvLabel.setOnClickListener(v -> {
            ARouter.getInstance().build(ARConstants.PATH_CHOOSE_CIRCLE).navigation(this, ARConstants.REQUEST_IMAGE_CHOOSE_CIRCLE);
        });
        btnPublish.setOnClickListener(v -> doPublish());
    }

    private void doPublish() {

        if (mChooseCircle == null) {
            ToastUtils.showShort("您还没选择圈子");
            return;
        }
        inputTitle = etTitle.getText().toString().trim();
        if (inputTitle.length() < 4 || inputTitle.length() > 20) {
            ToastUtils.showShort("标题字数限制在4到20个字之间");
            return;
        }
        if (type.equals(TYPE_VIDEO)) {
            if (picList == null || picList.isEmpty()) {
                ToastUtils.showShort("您还没选择视频或图片");
                return;
            }
            compressVideo();
        } else if (type.equals(TYPE_IAMGE)) {
            if (picList == null || picList.isEmpty()) {
                ToastUtils.showShort("您还没选择视频或图片");
                return;
            }
            compressImages();
        } else if (type.equals(TYPE_TEXT)) {
            String input = idEtInput.getText().toString().trim();
            if(TextUtils.isEmpty(input)){
                ToastUtils.showShort("发布内容不能为空");
                return;
            }
            publishText();
        }

    }

    private void publishText() {
        OkGo.<BaseEntity>post(UrlConstants.PUBLISH_TEXT)
                .tag(this)
                .params("uid", AppUtils.getUid())
                .params("cate_id", circleId)
                .params("type", 1)
                .params("title", inputTitle)
                .params("urlFile",idEtInput.getText().toString().trim())
                .execute(new JsonCallback<BaseEntity>() {
                    @Override
                    public void onSuccess(BaseEntity data) {
                        if (data.getCode() == 0) {
                            ToastUtils.showShort("发布成功");
                            finish();
                        }
                    }
                });
    }

    private void compressImages() {
        Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
        compressOptions.config = Bitmap.Config.ARGB_8888;
        int size = picList.size();
        File[] sourceFiles = new File[size];
        for (int i = 0; i < size; i++) {
            sourceFiles[i] = new File(picList.get(i).getPath());
        }
        Tiny.getInstance().source(sourceFiles).batchAsFile().withOptions(compressOptions).batchCompress(new FileBatchCallback() {
            @Override
            public void callback(boolean isSuccess, String[] outfile, Throwable t) {
                if (!isSuccess) {
                    ToastUtils.showShort("压缩失败");
//                    mCompressTv.setText("batch compress file failed!");
                    return;
                }
                ToastUtils.showShort("压缩成功,正在上传，请稍等");
                uploadImages(outfile);
            }
        });
    }

    private void uploadImages(String[] outfile) {
        int size = outfile.length;
        HttpParams params = new HttpParams();
        for (int i = 0; i < size; i++) {
            params.put("urlFile" + (i + 1), new File(outfile[i]));
        }


        if (Constant.TEST) {
            imageUrl = UrlConstants.TESTAPI + "Article/Create";
        }
        OkGo.<String>post(imageUrl)
                .tag(this)
                .params(params)
                .params("uid", AppUtils.getUid())
                .params("cate_id", circleId)
                .params("type", 2)
                .params("title", inputTitle)
                .isMultipart(true)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ToastUtils.showShort("发布成功");
                        finish();
                    }

//
//                    @Override
//                    public void uploadProgress(Progress progress) {
//                        super.uploadProgress(progress);
//                        hpvCompress.setVisibility(View.VISIBLE);
//                        hpvCompress.setProgress(progress.currentSize/progress.totalSize);
//                    }
                });
    }

    //压缩视频
    private void compressVideo() {
        String fileName = "VIDEO_" + System.currentTimeMillis();
        String destPath = PBitmapUtils.getDCIMDirectory().getAbsolutePath() +
                File.separator + fileName + ".mp4";
//        uploadVideo(destPath);
        VideoCompress.compressVideoLow(picList.get(0).getPath(), destPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                hpvCompress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess() {
                ToastUtils.showShort("压缩成功,正在上传，请稍等");
                btnPublish.setEnabled(false);
                hpvCompress.setProgress(100);
                uploadVideo(destPath);
            }

            @Override
            public void onFail() {
                ToastUtils.showShort("压缩失败");
                hpvCompress.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(float percent) {
//                int progress = (int) (percent * 100);
                hpvCompress.setProgress(percent);
//                tv_progress.setText(String.valueOf(percent) + "%");
            }
        });
    }

    //    上传视频
    private void uploadVideo(String destPath) {
        if (Constant.TEST) {
            videoUrl = UrlConstants.TESTAPI + "Article/Create";
        }
        File file = new File(destPath);
        OkGo.<BaseEntity>post(videoUrl)
                .tag(this)
                .params("urlFile", file)
                .params("uid", AppUtils.getUid())
                .params("cate_id", circleId)
                .params("type", 3)
                .params("title", inputTitle)
                .isMultipart(true)
                .execute(new JsonCallback<BaseEntity>() {
                    @Override
                    public void onStart(Request<BaseEntity, ? extends Request> request) {
                        super.onStart(request);
//                        mLoadingDialog.show();
                    }

                    @Override
                    public void onSuccess(BaseEntity data) {
                        if (data.getCode() == 0) {
//                            mLoadingDialog.dismiss();
                            ToastUtils.showShort("发布成功");
                            finish();
                        }
                    }
                });
    }

    private void setLayout() {
        if (type.equals("text")) {
            idEtInput.setVisibility(View.VISIBLE);
        } else {
            idEtInput.setVisibility(View.GONE);
        }
//
        if (type.equals(TYPE_VIDEO) || type.equals(TYPE_IAMGE)) {
            gridLayout.setVisibility(View.VISIBLE);
        } else {
            gridLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private Set<MimeType> getMimeTypes(String type) {
        Set<MimeType> mimeTypes = new HashSet<>();
        if (type.equals(TYPE_VIDEO)) {
            mimeTypes.add(MimeType.MPEG);
            mimeTypes.add(MimeType.MP4);
            mimeTypes.add(MimeType.AVI);
        } else if (type.equals(TYPE_IAMGE)) {
            mimeTypes.add(MimeType.JPEG);
            mimeTypes.add(MimeType.PNG);
        }
        return mimeTypes;
    }


    /**
     * 刷新图片显示
     */
    private void refreshGridLayout() {
        gridLayout.setVisibility(View.VISIBLE);
        gridLayout.removeAllViews();
        int num = picList.size();
        final int picSize = (ScreenUtils.getScreenWidth() - AppUtils.dp(this, 20)) / 3;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(picSize, picSize);
        if (num >= maxCount) {
            gridLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_pic_select, null);
                view.setLayoutParams(params);
//                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                gridLayout.addView(view);
            }
        } else {
            gridLayout.setVisibility(View.VISIBLE);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_image));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(dp(5), dp(5), dp(5), dp(5));
            imageView.setOnClickListener(v -> {
//                    startPick();
                showBottomSheetDialog();
            });
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_pic_select, null);
                view.setLayoutParams(params);
//                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                gridLayout.addView(view);
            }
            gridLayout.addView(imageView);
        }
    }

    public void setPicItemClick(RelativeLayout layout, final int pos) {
        RoundedImageView iv_pic = (RoundedImageView) layout.getChildAt(0);
        ImageView iv_close = (ImageView) layout.getChildAt(1);
        Glide.with(this).load(picList.get(pos).path).into(iv_pic);
        iv_close.setOnClickListener(v -> {
            picList.remove(pos);
            refreshGridLayout();
        });
        iv_pic.setOnClickListener(v -> preview(pos));
    }

    private void preview(int pos) {
        IPickerPresenter presenter = new WeChatPresenter();
        ImagePicker.preview(this, presenter, picList, pos, null);
    }

    private void pick(int count, String type) {
        final IPickerPresenter presenter = new WeChatPresenter();
        ImagePicker.withMulti(presenter)//指定presenter
                .setMaxCount(count)//设置选择的最大数
                .setColumnCount(4)//设置列数
                .setOriginal(true) //显示原图
                .mimeTypes(getMimeTypes(type))//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSelectMode(getSelectMode())
                .setPreviewVideo(true)  //大图预览时，过滤掉视频预览
                .showCamera(false)//显示拍照
                .setPreview(false)//是否开启预览
                .setVideoSinglePick(true)//设置视频单选
                .setSinglePickWithAutoComplete(false)
                .setSinglePickImageOrVideoType(true)//设置图片和视频单一类型选择
                .setMaxVideoDuration(60000L)//设置视频可选取的最大时长
                .setMinVideoDuration(5000L)
                .setLastImageList(null)//设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
                .setShieldList(picList)//设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .pick(this, (OnImagePickCompleteListener) items -> {
                    //图片选择回调，主线程
//                        if (mRbSave.isChecked()) {
//                            picList.clear();
//                        }
                    picList.addAll(items);
                    refreshGridLayout();
                });
    }


    private int getSelectMode() {

//        暂时不做裁剪
//        return SelectMode.MODE_CROP;
        return SelectMode.MODE_MULTI;
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.dialog_camera_layout, null);
        bottomSheet.setContentView(view);
        bottomSheet.findViewById(R.id.tv_cancel).setOnClickListener(v -> bottomSheet.cancel());
        bottomSheet.findViewById(R.id.tv_pick_from_album).setOnClickListener(v -> {
            bottomSheet.cancel();
            pick(maxCount - picList.size(), type);
        });
        bottomSheet.findViewById(R.id.tv_take_photo).setOnClickListener(v -> {
            bottomSheet.cancel();
            if (type.equals(TYPE_IAMGE)) {
                takePhoto();
            } else if (type.equals(TYPE_VIDEO)) {
                takeVideo();
            }
        });

        try {
            // hack bg color of the BottomSheetDialog
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.setBackgroundResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bottomSheet.show();
    }

    private void takeVideo() {
        ImagePicker.takeVideo(this, (OnImagePickCompleteListener) items -> {
            int seconds = (int) (items.get(0).duration / 1000);
            if (seconds < 5) {
                ToastUtils.showLong("时间不能少于5秒");
            } else {
                picList.addAll(items);
                refreshGridLayout();
            }
        });
    }

    private void takePhoto() {
        ImagePicker.takePhoto(this, (OnImagePickCompleteListener) items -> {
            picList.addAll(items);
            refreshGridLayout();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                mChooseCircle = data.getParcelableExtra(Constant.EXTRA_CIRCLR_ITEM);
                tvLabel.setText(mChooseCircle.getName());
                circleId = mChooseCircle.getId();
            }
        }
    }


}
