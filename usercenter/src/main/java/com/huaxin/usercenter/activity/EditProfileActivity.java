package com.huaxin.usercenter.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.librarybase.base.BaseActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.entity.ProvinceInfo;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.intercepter.WeChatPresenter;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.entity.UserBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.EDIT_PROFILE)
public class EditProfileActivity extends BaseActivity {


    @BindView(R2.id.iv_choose_avatar)
    ImageView ivChooseAvatar;
    @BindView(R2.id.et_nickname)
    EditText etNickname;
    @BindView(R2.id.tv_phone)
    TextView tvPhone;
    @BindView(R2.id.tv_address)
    TextView tvAddress;
    @BindView(R2.id.et_personal_signature)
    EditText etPersonalSignature;
    @BindView(R2.id.btn_save)
    Button btnSave;
    @BindView(R2.id.rb_male)
    RadioButton rbMale;
    @BindView(R2.id.rb_female)
    RadioButton rbFemale;
    @BindView(R2.id.rg_sex)
    RadioGroup rgSex;

    private int sexValue = 0;
    private List<ProvinceInfo> options1Items = new ArrayList<>();
    private List<List<String>> options2Items = new ArrayList<>();
    private List<List<List<String>>> options3Items = new ArrayList<>();
    private boolean mHasLoaded;
    private String chooseCity;
    private String avatarPath;

    private boolean isDataUpdate;
    private boolean isImageUpdate;


    private ArrayList<ImageItem> picList = new ArrayList<>();

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).keyboardEnable(true).init();
    }


    @Override
    protected void loadData() {
        addData(AppUtils.getProvinceInfos());
        getData();
    }


    private void addData(List<ProvinceInfo> provinceInfos) {
        /**
         * 添加省份数据
         */
        options1Items = provinceInfos;

        //遍历省份（第一级）
        for (ProvinceInfo provinceInfo : provinceInfos) {
            //该省的城市列表（第二级）
            List<String> cityList = new ArrayList<>();
            //该省的所有地区列表（第三级）
            List<List<String>> areaList = new ArrayList<>();

            for (ProvinceInfo.City city : provinceInfo.getCityList()) {
                //添加城市
                String cityName = city.getName();
                cityList.add(cityName);
                //该城市的所有地区列表
                List<String> cityAreaList = new ArrayList<>();
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (city.getArea() == null || city.getArea().size() == 0) {
                    cityAreaList.add("");
                } else {
                    cityAreaList.addAll(city.getArea());
                }
                //添加该省所有地区数据
                areaList.add(cityAreaList);
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(areaList);
        }

        mHasLoaded = true;
    }


    private void getData() {
        HashMap params = new HashMap<>();
        params.put("uid", AppUtils.getUid());
        params.put("mobile_code", AppUtils.getDeviceId(this));
        HttpUtils.postData(UrlConstants.PROFILE, params, new JsonCallback<BaseEntity<UserBean>>() {
            @Override
            protected void onSuccess(BaseEntity<UserBean> data) {
                setUser(data.getData());
            }
        });
    }

    private void setUser(UserBean data) {
        ImageUtils.displayImage(this, data.getThumb(), R.drawable.ic_choose_avater, ivChooseAvatar);
        etNickname.setText(data.getNickname());
        if (data.getSex() == 1) {
            rbMale.setChecked(true);
        } else if (data.getSex() == 2) {
            rbFemale.setChecked(true);
        }
        tvPhone.setText(data.getPhone());
        if (!StringUtils.isEmpty(data.getAddress())) {
            tvAddress.setText(data.getAddress());
        }
        etPersonalSignature.setHint(data.getIntro());
    }


    @Override
    protected int getContentViewId() {
        return R.layout.act_edit_profile;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("编辑资料");
        rgSex.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_female) {
                sexValue = 2;
            } else if (checkedId == R.id.rb_male) {
                sexValue = 1;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick({R2.id.iv_choose_avatar, R2.id.tv_address, R2.id.btn_save})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_choose_avatar) {
            showBottomSheetDialog();
        } else if (id == R.id.tv_address) {
            showPickerView(false);
        } else if (id == R.id.btn_save) {
            doUpdateProfile();

        }
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
                ToastUtils.showLong("压缩成功,正在上传，请稍等");
                uploadImages(outfile);
            }
        });
    }


    private void uploadImages(String[] outfile) {
        File file = new File(outfile[0]);
        OkGo.<String>post(UrlConstants.UPDATE_AVATAR)
                .tag(this)
                .params("urlFile", file)
                .params("uid", AppUtils.getUid())
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

    private void doUpdateProfile() {
        String nickName = etNickname.getText().toString();
        if (StringUtils.isEmpty(nickName)) {
            ToastUtils.showShort("昵称不能为空");
            return;
        }
        HashMap params = new HashMap<>();
        params.put("uid", AppUtils.getUid());
        params.put("cname", nickName);
        params.put("sex", sexValue);
        if (!StringUtils.isEmpty(chooseCity)) {
            params.put("address", chooseCity);
        }
        String signature = etPersonalSignature.getText().toString();
        if (!StringUtils.isEmpty(signature)) {
            params.put("intro", signature);
        }
        HttpUtils.postData(UrlConstants.UPDATE_PROFILE, params, new JsonCallback<BaseEntity>() {
            @Override
            protected void onSuccess(BaseEntity data) {
                if (!picList.isEmpty()) {
//                updateAvatar();
                    compressImages();
                }else {
                    finish();
                }
            }

        });
    }

    private void showPickerView(boolean isDialog) {// 弹出选择器
        if (!mHasLoaded) {
            ToastUtils.showShort("数据加载中...");
            return;
        }

        int[] defaultSelectOptions = getDefaultCity();

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (v, options1, options2, options3) -> {
            //返回的分别是三个级别的选中位置
            String tx = options1Items.get(options1).getPickerViewText() + "-" +
                    options2Items.get(options1).get(options2) + "-" +
                    options3Items.get(options1).get(options2).get(options3);
            chooseCity = options2Items.get(options1).get(options2);
            tvAddress.setText(tx);
            return false;
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                //切换选项时，还原到第一项
                .isRestoreItem(true)
                //设置选中项文字颜色
                .setTextColorCenter(Color.BLACK)
                .setContentTextSize(20)
                .isDialog(isDialog)
                .setSelectOptions(defaultSelectOptions[0], defaultSelectOptions[1], defaultSelectOptions[2])
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    /**
     * @return 获取默认城市的索引
     */
    private int[] getDefaultCity() {
        int[] res = new int[3];
        ProvinceInfo provinceInfo;
        List<ProvinceInfo.City> cities;
        ProvinceInfo.City city;
        List<String> ares;
        for (int i = 0; i < options1Items.size(); i++) {
            provinceInfo = options1Items.get(i);
            if ("江苏省".equals(provinceInfo.getName())) {
                res[0] = i;
                cities = provinceInfo.getCityList();
                for (int j = 0; j < cities.size(); j++) {
                    city = cities.get(j);
                    if ("南京市".equals(city.getName())) {
                        res[1] = j;
                        ares = city.getArea();
                        for (int k = 0; k < ares.size(); k++) {
                            if ("雨花台区".equals(ares.get(k))) {
                                res[2] = k;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return res;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        options1Items.clear();
        options2Items.clear();
        options3Items.clear();
        mHasLoaded = false;
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.dialog_camera_layout, null);
        bottomSheet.setContentView(view);
        bottomSheet.findViewById(R.id.tv_cancel).setOnClickListener(v -> bottomSheet.cancel());
        bottomSheet.findViewById(R.id.tv_pick_from_album).setOnClickListener(v -> {
            bottomSheet.cancel();
            pick(1);
        });
        bottomSheet.findViewById(R.id.tv_take_photo).setOnClickListener(v -> {
            bottomSheet.cancel();
            takePhoto();
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

    private void takePhoto() {
        ImagePicker.takePhoto(this, (OnImagePickCompleteListener) items -> {
            picList.clear();
            picList.addAll(items);
            Glide.with(this).load(picList.get(0).path).into(ivChooseAvatar);

        });
    }

    private Set<MimeType> getMimeTypes() {
        Set<MimeType> mimeTypes = new HashSet<>();
        mimeTypes.add(MimeType.JPEG);
        mimeTypes.add(MimeType.PNG);
        return mimeTypes;
    }

    private void pick(int count) {
        final IPickerPresenter presenter = new WeChatPresenter();
        ImagePicker.withMulti(presenter)//指定presenter
                .setMaxCount(count)//设置选择的最大数
                .setColumnCount(4)//设置列数
                .setOriginal(true) //显示原图
                .mimeTypes(getMimeTypes())//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSelectMode(SelectMode.MODE_SINGLE)
                .setPreviewVideo(true)  //大图预览时，过滤掉视频预览
                .showCamera(true)//显示拍照
                .setPreview(true)//是否开启预览
                .setVideoSinglePick(true)//设置视频单选
                .setSinglePickWithAutoComplete(false)
                .setSinglePickImageOrVideoType(true)//设置图片和视频单一类型选择
                .setMaxVideoDuration(60000L)//设置视频可选取的最大时长
                .setMinVideoDuration(5000L)
                .setLastImageList(null)//设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
//                .setShieldList(picList)//设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .pick(this, (OnImagePickCompleteListener) items -> {
                    //图片选择回调，主线程
//                        if (mRbSave.isChecked()) {
//                            picList.clear();
//                        }
                    picList.clear();
                    picList.addAll(items);
                    Glide.with(this).load(picList.get(0).path).into(ivChooseAvatar);

                });
    }
}
