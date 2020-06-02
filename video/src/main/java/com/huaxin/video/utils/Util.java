package com.huaxin.video.utils;

import android.graphics.Rect;
import android.view.View;

public class Util {


//    public static List<VideoSectionEntity> getVideoSection(List<VideoListEntity> entityList) {
//        List<VideoSectionEntity> list = new ArrayList<>();
//        for (int i = 0; i < entityList.size(); i++) {
//            list.add(new VideoSectionEntity(true, entityList.get(i)));
//            for (int j = 0; j < entityList.get(i).getData().size(); j++) {
//                list.add(new VideoSectionEntity(false, entityList.get(i).getData().get(j)));
//            }
//        }
//        return list;
//    }

    public static float getViewVisiblePercent(View view) {
        if (view == null) {
            return 0f;
        }
        float height = view.getHeight();
        Rect rect = new Rect();
        if (!view.getLocalVisibleRect(rect)) {
            return 0f;
        }
        float visibleHeight = rect.bottom - rect.top;
        return visibleHeight / height;
    }


}
