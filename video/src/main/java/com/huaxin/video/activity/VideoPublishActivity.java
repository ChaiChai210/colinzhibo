package com.huaxin.video.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.donkingliang.labels.LabelsView;
import com.example.librarybase.base.BaseActivity;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.huaxin.library.entity.LabelDetail;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.intercepter.WeChatPresenter;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.TimeUtils;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.video.adapter.VideoLabelsAdapter;
import com.huaxin.video.entity.VideoTypeEntity;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.tus.android.client.TusPreferencesURLStore;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

@Route(path = ARConstants.VIDEO_PUBLISH)
public class VideoPublishActivity extends BaseActivity {
    @BindView(R2.id.et_title)
    EditText etTitle;
    @BindView(R2.id.iv_upload_video)
    ImageView ivUploadVideo;
    @BindView(R2.id.iv_upload_cover)
    ImageView ivUploadCover;
    @BindView(R2.id.labels)
    LabelsView labels;
    @BindView(R2.id.rv_video_tags)
    RecyclerView rvVideoTags;
    @BindView(R2.id.rb_not_original)
    RadioButton rbNotOriginal;
    @BindView(R2.id.rb_original)
    RadioButton rbOriginal;
    @BindView(R2.id.rg_create)
    RadioGroup rgCreate;
    @BindView(R2.id.et_video_price)
    EditText etVideoPrice;
    @BindView(R2.id.tv_set_preview)
    TextView tvSetPreview;
    @BindView(R2.id.cb_protocol)
    CheckBox cbProtocol;
    @BindView(R2.id.tv_protocol)
    TextView tvProtocol;
    @BindView(R2.id.btn_submit)
    Button btnSubmit;
    private List<String> mVideoTypes = new ArrayList<>();
    //视频标签
    private ArrayList<LabelDetail> mChooseLabels = new ArrayList<>();
    private VideoLabelsAdapter chooseLabelAdapter;
    private static final String TYPE_VIDEO = "video";
    private static final String TYPE_IAMGE = "image";
    //    private String type = TYPE_VIDEO;
    private int maxCount = 1;
    private ArrayList<ImageItem> picList = new ArrayList<>();
    //获取视频时长
    private long duration;
    private String videoUrl;

    private TusClient client;
    private UploadTask uploadTask;

    @Override
    protected void loadData() {
        getVideoType();
    }

    private void getVideoType() {
        HttpUtils.getVideoType(0, new JsonCallback<BaseEntity<List<VideoTypeEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<VideoTypeEntity>> data) {
                List<VideoTypeEntity> temp = data.getData();
                if (CollectionUtils.isNotEmpty(temp)) {
                    for (VideoTypeEntity item : temp) {
                        if (item.getName().equals("全部"))
                            continue;
                        mVideoTypes.add(item.getName());
                    }
                }
                labels.setLabels(mVideoTypes);
            }

        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.video_act_publish_video;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("发布视频");
        try {
            SharedPreferences pref = getSharedPreferences("tus", 0);
            client = new TusClient();
            client.setUploadCreationURL(new URL("http://167.114.5.197:1080/files/"));
            client.enableResuming(new TusPreferencesURLStore(pref));
        } catch (Exception e) {
            showError(e);
        }
        mToolbar.inflateMenu(R.menu.video_edit);
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {

            }
            return true;
        });
        setProtocal();

        rvVideoTags.setLayoutManager(getLayoutManager());
        mChooseLabels.add(new LabelDetail("+"));
        chooseLabelAdapter = new VideoLabelsAdapter(mChooseLabels);
        chooseLabelAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.tv_tag) {
                //收藏或者取消收藏
                if (AntiShakeUtils.isInvalidClick(view1))
                    return;
                ARouter.getInstance().build(ARConstants.VIDEO_CHOOSE_LABEL)
                        .withParcelableArrayList(Constant.EXTRA_CHOSEN_LABELS, getLabels())
                        .navigation(this, ARConstants.REQUEST_CHOOSE_LABELS);
            }
        });
        rvVideoTags.setAdapter(chooseLabelAdapter);
    }
    private void showError(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internal error");
        builder.setMessage(e.getMessage());
        AlertDialog dialog = builder.create();
        dialog.show();
        e.printStackTrace();
    }
    private ArrayList<LabelDetail> getLabels() {
        ArrayList<LabelDetail> temp = new ArrayList<>();
        int size = mChooseLabels.size();
        for (int i = 0; i < size - 1; i++) {
            temp.add(mChooseLabels.get(i));
        }
        return temp;
    }

    private FlexboxLayoutManager getLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        return layoutManager;
    }

    private void setProtocal() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("我已阅读并同意《上传须知》所有内容");
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ToastUtils.showShort("跳转协议");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        builder.setSpan(span1, 7, 13, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.text_color));
        builder.setSpan(span, 7, 13, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvProtocol.setText(builder);
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean showTitleView() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.iv_upload_video, R2.id.iv_upload_cover, R2.id.tv_set_preview, R2.id.btn_submit})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_upload_video) {
            showBottomSheetDialog(TYPE_VIDEO);
        } else if (id == R.id.iv_upload_cover) {
            showBottomSheetDialog(TYPE_IAMGE);
        } else if (id == R.id.tv_set_preview) {
            doPreview();
        } else if (id == R.id.btn_submit) {
            doUpload();
        }
    }

    private void doUpload() {
        if(StringUtils.isEmpty(videoUrl))
            return;
        File file = new File(videoUrl);
        final TusUpload upload;
        try {
            upload = new TusUpload(file);
            uploadTask = new UploadTask( client, upload);
            uploadTask.execute();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private class UploadTask extends AsyncTask<Void, Long, URL> {
        private TusClient client;
        private TusUpload upload;
        private Exception exception;

        public UploadTask( TusClient client, TusUpload upload) {
            this.client = client;
            this.upload = upload;
        }



        @Override
        protected void onPostExecute(URL uploadURL) {
            Log.e("chia finish",uploadURL.toString());
        }

        @Override
        protected void onCancelled() {
//            if (exception != null) {
//                activity.showError(exception);
//            }

        }

        @Override
        protected void onProgressUpdate(Long... updates) {
            Log.e("chia", "ddd");
            long uploadedBytes = updates[0];
            long totalBytes = updates[1];
        }

        @Override
        protected URL doInBackground(Void... params) {
            try {
                TusUploader uploader = client.resumeOrCreateUpload(upload);
                long totalBytes = upload.getSize();
                long uploadedBytes = uploader.getOffset();

                // Upload file in 1MiB chunks
                uploader.setChunkSize(2 * 1024 * 1024);

                while (!isCancelled() && uploader.uploadChunk() > 0) {
                    uploadedBytes = uploader.getOffset();
                    publishProgress(uploadedBytes, totalBytes);
                }

                uploader.finish();

                return uploader.getUploadURL();

            } catch (Exception e) {
                exception = e;
                cancel(true);
            }
            return null;
        }
    }

    private void doPreview() {
        if (videoUrl == null) {
            ToastUtils.showShort("您还没选择视频");
            return;
        }
//        if (duration < 15 * 60 * 1000) {
//            ToastUtils.showShort("视频少于15分钟，不需要预览");
//        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.VIDEO_URL, videoUrl);
        bundle.putLong(Constant.VIDEO_DURATION, duration);
        ARouter.getInstance().build(ARConstants.VIDEO_PREVIEW).withBundle(Constant.VIDEO_BUNDLE, bundle).navigation();
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

    private void showBottomSheetDialog(String type) {
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

    private void pick(int count, String type) {
        final IPickerPresenter presenter = new WeChatPresenter();
        ImagePicker.withMulti(presenter)//指定presenter
                .setMaxCount(count)//设置选择的最大数
                .setColumnCount(4)//设置列数
                .setOriginal(false) //显示原图
                .mimeTypes(getMimeTypes(type))//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSelectMode(getSelectMode())
                .setPreviewVideo(true)  //大图预览时，过滤掉视频预览
                .showCamera(false)//显示拍照
                .setPreview(false)//是否开启预览
                .setVideoSinglePick(true)//设置视频单选
                .setSinglePickWithAutoComplete(false)
                .setSinglePickImageOrVideoType(true)//设置图片和视频单一类型选择
                .setMaxVideoDuration(6000000L)//设置视频可选取的最大时长,毫秒为单位
                .setMinVideoDuration(5000L)
                .setLastImageList(null)//设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
//                .setShieldList(picList)//设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .pick(this, (OnImagePickCompleteListener) items -> {
                    //图片选择回调，主线程
//                    picList.addAll(items);
                    if (type.equals(TYPE_IAMGE)) {


                        Glide.with(this).load(items.get(0).getPath()).into(ivUploadCover);
                    } else if (type.equals(TYPE_VIDEO)) {
                        videoUrl = items.get(0).getPath();
                        duration = items.get(0).duration;

                        items.get(0).getDurationFormat();
                        Glide.with(this).load(videoUrl).into(ivUploadVideo);
                    }

                });
    }

    private int getSelectMode() {

//        暂时不做裁剪
//        return SelectMode.MODE_CROP;
        return SelectMode.MODE_MULTI;
    }

    private void takeVideo() {
        ImagePicker.takeVideo(this, (OnImagePickCompleteListener) items -> {
            duration = items.get(0).duration;
            int seconds = (int) (items.get(0).duration / 1000);
            if (seconds < 5) {
                ToastUtils.showLong("时间不能少于5秒");
            } else {
//                picList.addAll(items);
                Glide.with(this).load(items.get(0).getPath()).into(ivUploadVideo);
            }
        });
    }

    private void takePhoto() {
        ImagePicker.takePhoto(this, (OnImagePickCompleteListener) items -> {
//            picList.addAll(items);
            Glide.with(this).load(items.get(0).getPath()).into(ivUploadCover);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                mChooseLabels.clear();
                mChooseLabels.addAll(data.getParcelableArrayListExtra(Constant.EXTRA_CHOOSE_LABELS));
                mChooseLabels.add(new LabelDetail("+"));
                chooseLabelAdapter.notifyDataSetChanged();
            }
        }
    }
}
