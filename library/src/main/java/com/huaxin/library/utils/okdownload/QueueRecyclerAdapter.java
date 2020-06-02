/*
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaxin.library.utils.okdownload;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.huaxin.library.R;
import com.huaxin.library.base.BaseApplication;
import com.huaxin.library.db.Download;
import com.huaxin.library.db.MyDatabase;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.ImageUtils;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.orhanobut.logger.Logger;

import java.util.List;

public class QueueRecyclerAdapter
        extends RecyclerView.Adapter<QueueRecyclerAdapter.QueueViewHolder> {

    private final List<Download> mData;
    private final Context mContext;
    private final DownloadManager downloadManager;
    public OnRecycleItemClickListener onRecycleItemListener;
    private boolean edited;

    private static final int NORMAL_VIEW = 0;


    public QueueRecyclerAdapter(Context context, List<Download> tabListEntities, DownloadManager downloadController) {
        this.mData = tabListEntities;
        this.mContext = context;
        this.downloadManager = downloadController;

    }

    public void refreshData(List<Download> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Download> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
        notifyDataSetChanged();
    }



    @Override
    public QueueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QueueViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false), viewType);

    }

    @Override
    public void onBindViewHolder(final QueueViewHolder holder, final int position) {
        final Download data = mData.get(position);

        holder.tv_title.setText(data.getTitle());
        ImageUtils.displayImage(mContext, data.getThumb(), R.drawable.ic_placeholder, holder.iv_avatar);
        ImageView choose = holder.iv_choose;
        if (edited) {
            choose.setVisibility(View.VISIBLE);
            if (data.isChecked()) {
                Glide.with(mContext).load(R.drawable.ic_cb_checked).into(choose);
            } else {
                Glide.with(mContext).load(R.drawable.ic_cb_unchecked).into(choose);
            }
            choose.setOnClickListener(v -> {
                data.setChecked(!data.isChecked());
                notifyItemChanged(position);
            });
        } else {
            choose.setVisibility(View.GONE);
        }
        final String url = data.getUrl();

        //初始化进入页面的下载进度缓存信息
        DownloadTask task = downloadManager.getTask(url);
        if (task != null) {
            BreakpointInfo info = StatusUtil.getCurrentInfo(task);
            if (info != null) {
                long totalOffset = info.getTotalOffset();
                long totalLength = info.getTotalLength();
                String curSize = totalOffset / 1024 / 1024 + "MB/" + totalLength / 1024 / 1024 + "MB";
                holder.tv_downloadSize.setText(curSize);
            }
        }


        //每次滑动刷新列表重新绑定holder
        downloadManager.bind(holder, url);
        holder.tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String started = downloadManager.getStatus(url);
                switch (started) {
                    case DownloadManager.NONE:
                        Logger.d("url: " + url);
                        downloadManager.addTask(url);
                        downloadManager.start(url);
                        downloadManager.bind(holder, url);
//                        TaskCacheUtils.saveTaskCache(data);
                        Download item = new Download();
                        item.setUrl(url);
                        item.setIntro(data.getIntro());
                        item.setThumb(data.getThumb());
                        item.setTitle(data.getTitle());
                        MyDatabase.getInstance(BaseApplication.instance()).downloadDao().insert(item);
                        break;
                    case DownloadManager.START_TASK:
                    case DownloadManager.PROGRESS:
                    case DownloadManager.CONNECTED:
                    case DownloadManager.PENDING:
                        downloadManager.stop(url);
                        break;
                    case DownloadManager.PAUSE:
                    case DownloadManager.IDLE:
                    case DownloadManager.UNKNOWN:
                        downloadManager.start(url);
                        break;
                    case DownloadManager.COMPLETED:
                        ARouter.getInstance().build(ARConstants.PATH_VIDEO_PLAY).withInt("id", (int) data.getId()).navigation();
                        break;
                    case DownloadManager.RETRY:
                    case DownloadManager.ERROR:
                        downloadManager.start(url);
                        break;
                }

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("itemView  onClick");
                Log.e("chia", task.getFilename() + "file" + task.getFile());
                if (onRecycleItemListener != null) {
                    onRecycleItemListener.onItemClick(position, edited);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return NORMAL_VIEW;
    }

    public static class QueueViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_avatar;
        public TextView tv_title;
        public TextView statusTv;

        public TextView tv_filesize;
        public TextView tv_download;
        public ImageView iv_choose;
        public TextView tv_downloadSize;
        public ProgressBar progressBar;

        public View ll_downloadView;

        public QueueViewHolder(View itemView, int viewType) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_download = itemView.findViewById(R.id.tv_download);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            iv_choose = itemView.findViewById(R.id.iv_choose);
            tv_downloadSize = itemView.findViewById(R.id.tv_downloadSize);
            statusTv = itemView.findViewById(R.id.tv_state);
            progressBar = itemView.findViewById(R.id.progressBar);
            ll_downloadView = itemView.findViewById(R.id.ll_downloadView);
        }
    }


    private String formatDowncount(int count) {
        if (count >= 1000 && count < 10000) {
            return count / 1000 + "千";
        } else if (count >= 10000 && count < 100000000) {
            return count / 10000 + "万";
        } else {
            return count / 100000000 + "亿";
        }
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener l) {
        this.onRecycleItemListener = l;
    }

    public interface OnRecycleItemClickListener {
        void onItemClick(int position, boolean isEditMode);

    }



}