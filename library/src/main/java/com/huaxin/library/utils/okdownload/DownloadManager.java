package com.huaxin.library.utils.okdownload;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.huaxin.library.base.BaseApplication;
import com.huaxin.library.db.Download;
import com.huaxin.library.db.DownloadDao;
import com.huaxin.library.db.MyDatabase;
import com.liulishuo.okdownload.DownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadManager {


    public final static String NONE = "none";
    public final static String START_TASK = "START_TASK";
    public final static String RETRY = "RETRY";
    public final static String COMPLETED = "COMPLETED";
    public final static String CONNECTED = "CONNECTED";
    public final static String PROGRESS = "PROGRESS";
    public final static String PAUSE = "PAUSE";
    public final static String ERROR = "ERROR";
    public final static String PENDING = "PENDING";
    public final static String IDLE = "IDLE";
    public final static String UNKNOWN = "UNKNOWN";

    private final QueueListener listener = new QueueListener();

    static File cacheFile = new File(getParentFile(BaseApplication.instance()) + "/DownloadVideo");

    static DownloadManager downloadManager = new DownloadManager();

    HashMap<String, DownloadTask> taskHashMap = new HashMap<>();
    private DownloadDao downloadDao;

    public static DownloadManager getInstance() {
        return downloadManager;
    }

    private DownloadManager() {
        refreshCache();
    }

    public void refreshCache() {
        taskHashMap.clear();
        downloadDao = MyDatabase.getInstance(BaseApplication.instance()).downloadDao();
        List<Download> downloadList = downloadDao.getDownloadList();
        for (Download data : downloadList) {
            String url = data.getUrl();
            DownloadTask task = buildTask(url);
            taskHashMap.put(url, task);
        }
    }

    public List<Download> getFinishedDownload() {
        List<Download> temp = new ArrayList<>();
        List<Download> downloadList = downloadDao.getDownloadList();
        for (Download data : downloadList) {
            String url = data.getUrl();
            if (getStatus(url).equals(DownloadManager.COMPLETED)) {
                temp.add(data);
            }
        }
        return temp;
    }

    public List<Download> getUnFinishedDownload() {
        List<Download> temp = new ArrayList<>();
        List<Download> downloadList = downloadDao.getDownloadList();
        for (Download data : downloadList) {
            String url = data.getUrl();
            if (!getStatus(url).equals(DownloadManager.COMPLETED)) {
                temp.add(data);
            }
        }
        return temp;
    }

    public DownloadTask buildTask(String url) {
        // https://video.imgwd.com/storage/video_1/2020-03-11/62a4cb18a614b4273834303733291ffb/demo.m3u8
        String temp = url.replace("/demo.mp4", "");
        String fileName = temp.substring(temp.lastIndexOf("/"));
        return new DownloadTask.Builder(url, cacheFile)
                .setFilename(fileName)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(16)
                .setConnectionCount(1)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
    }

    public void addTask(String url) {
        DownloadTask task = buildTask(url);
        taskHashMap.put(url, task);
    }


    public DownloadTask getTask(String url) {
        return taskHashMap.get(url);
    }

    public void start(String url) {
        Log.e("chia", url);
        DownloadTask task = getTask(url);
        if (task != null) {
            task.enqueue(listener);
        }
    }

    public void stop(String url) {
        DownloadTask task = getTask(url);
        if (task != null) {
            task.cancel();
        }
    }

    public String getStatus(String url) {
        DownloadTask task = getTask(url);
        if (task != null) {
            return TagUtil.getStatus(task);
        } else {
            return NONE;
        }
    }


    public void bind(final QueueRecyclerAdapter.QueueViewHolder holder, String url) {
        final DownloadTask task = getTask(url);
        if (task != null) {
            listener.bind(task, holder);
            listener.resetInfo(task, holder);
        }
    }

    public static File getParentFile(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static File getCacheFile() {
        return cacheFile;
    }

    public QueueListener getListener() {
        return listener;
    }

    //删除文件和任务信息
    public void deleteFiles(List<Download> downloads) {
        for (Download item : downloads) {
            String temp = item.getUrl().replace("/demo.mp4", "");
            String fileName = temp.substring(temp.lastIndexOf("/"));
            FileUtils.delete(new File(cacheFile + fileName));
            DownloadTask task = getTask(item.getUrl());
            String status = TagUtil.getStatus(task);
            if (status.equals(DownloadManager.PROGRESS)) {
                task.cancel();
            }
            TagUtil.clearProceedTask(getTask(item.getUrl()));
            taskHashMap.remove(item.getUrl());

        }
    }

}
