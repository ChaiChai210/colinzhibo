package com.huaxin.library.utils.okdownload;

import com.huaxin.library.base.BaseApplication;
import com.huaxin.library.entity.DownloadVideoItem;

import java.io.File;
import java.util.ArrayList;

public class TaskCacheUtils {

    public static void saveTaskCache(DownloadVideoItem data){
        ArrayList<DownloadVideoItem> list= getSaveTaskCache();
        list.add(data);
        SharePreferenceManager.putObject(BaseApplication.instance(),"app_datas",list);
    }

    public static ArrayList<DownloadVideoItem> getSaveTaskCache(){
        ArrayList<DownloadVideoItem> list= (ArrayList<DownloadVideoItem>) SharePreferenceManager.getObject(BaseApplication.instance(),"app_datas");
        if(list==null){
            list=new ArrayList<>();
        }
        return  list;
    }

    public static String deleteTaskCache(int deletePosition){
        ArrayList<DownloadVideoItem> list= getSaveTaskCache();
        if(list.size()>0){
//            L.d("deletePosition："+deletePosition);
            String url=list.get(deletePosition).getUrl();
            String apkName=url.substring(url.lastIndexOf("/"),url.length());
            File file=new File(DownloadManager.getCacheFile(),apkName);
            if(file.exists()){
//                L.d("删除的文件："+apkName);
                file.delete();
            }
            boolean is=list.remove(list.get(deletePosition));
//            L.d("是否删除成功："+is);
            SharePreferenceManager.putObject(BaseApplication.instance(),"app_datas",list);
            return url;
        }

        return "";
    }

}
