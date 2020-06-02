package com.huaxin.video.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ArrayUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.donkingliang.labels.LabelsView;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.video.R;
import com.huaxin.video.entity.VideoSearchEntity;

import java.util.List;

public class VideoSearchAdapter extends BaseQuickAdapter<VideoSearchEntity, BaseViewHolder> {

    private final SpannableStringBuilder mBuilder;

    public VideoSearchAdapter(@Nullable List<VideoSearchEntity> data) {
        super(R.layout.item_details_like, data);
        mBuilder = new SpannableStringBuilder();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, @Nullable VideoSearchEntity VideoSearchEntity) {
        ImageView imageView = baseViewHolder.getView(R.id.iv_avatar);
//        Glide.with(getContext()).load( VideoSearchEntity.getIntroduction_image()).placeholder(R.mipmap.ic_detail_like_ph).centerCrop().into(imageView);
        ImageUtils.displayImage(getContext(),VideoSearchEntity.getThumb(),R.mipmap.ic_detail_like_ph,imageView);
        String numFormat = getContext().getResources().getString(R.string.hint_play_num);
        baseViewHolder.setText(R.id.tv_num, getNumFormatString(mBuilder, VideoSearchEntity.getPlay_count(), numFormat));
        baseViewHolder.setText(R.id.tv_title, VideoSearchEntity.getTitle());
        if (!TextUtils.isEmpty(VideoSearchEntity.getTags())) {
            LabelsView labelsView = baseViewHolder.getView(R.id.labels);
            labelsView.setLabels(ArrayUtils.asArrayList(VideoSearchEntity.getTags().split("\\|")));
        }
        baseViewHolder.itemView.setOnClickListener(v -> ARouter.getInstance().build(ARConstants.PATH_VIDEO_PLAY).withInt("id", VideoSearchEntity.getId()).navigation());

    }

    private SpannableStringBuilder getNumFormatString(SpannableStringBuilder builder, int count, String formatString) {
        builder.clear();
        String countFormat = String.valueOf(count);
        SpannableString spannedString = new SpannableString(countFormat);
        ForegroundColorSpan span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.text_color));
        spannedString.setSpan(span, 0, countFormat.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(spannedString);
        builder.append(" ");
        spannedString = new SpannableString(formatString);
        span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.colorTitle));
        spannedString.setSpan(span, 0, formatString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(spannedString);
        return builder;
    }
}
