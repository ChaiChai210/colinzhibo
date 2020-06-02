package com.huaxin.library.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;
import java.util.List;


public class CommunityImageAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    private List<Object> list;

    public CommunityImageAdapter(@Nullable List<Object> data) {
        super(R.layout.view_community_recommend_image, data);
        this.list = data;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, @NonNull Object data) {
        ImageView imageView = helper.getView(R.id.iv_image);
//        Glide.with(getContext()).load(UrlConstants.BASE_IMAGE + data).into(imageView);


        //1. 加载图片, 由于ImageView是centerCrop，必须指定Target.SIZE_ORIGINAL，禁止Glide裁剪图片；
        // 这样我就能拿到原始图片的Matrix，才能有完美的过渡效果
        Glide.with(imageView).load( data).apply(new RequestOptions()
                .override(Target.SIZE_ORIGINAL))
                .into(imageView);
        imageView.setOnClickListener(v -> {
//                onImageClickListener.onclick(imageView,helper.getAdapterPosition(),type);
            new XPopup.Builder(getContext()).asImageViewer(imageView, helper.getAdapterPosition(), list, (popupView, position) -> {
//                        RecyclerView rv = (RecyclerView) holder.itemView.getParent();
                popupView.updateSrcView(imageView);
            }, new ImageLoader()).show();
        });
    }

    public static class ImageLoader implements XPopupImageLoader {
        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
            Glide.with(imageView).load( url).apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(imageView);
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return Glide.with(context).downloadOnly().load(uri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
