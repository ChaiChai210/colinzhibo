package com.huaxin.library.db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class WatchRecordRepository {

    private WatchDao WatchDao;

    private List<WatchRecord> allWatchRecord;
    private List<WatchRecord> someWatchRecord;

    public WatchRecordRepository(Context context) {
        //UserRoomDatabase db = UserRoomDatabase.getInstance(application);
        //mUserDao = db.userDao();
        //allUser = mUserDao.getUserList();
        //使用ViweModel可以直接使用上面注释了的
        new InitThread(context).start();
    }

    public List<WatchRecord> getAllWatchRecord() {
        return allWatchRecord;
    }
    public List<WatchRecord> getSomeWatchRecord() {
        return someWatchRecord;
    }

    public void deleteAll(List<WatchRecord> watchRecords) {
        new DeleteAsyncTask(WatchDao).execute((WatchRecord[]) watchRecords.toArray());
    }

//    public void update(WatchRecord watchRecord){
//       new UpdateAsyncTask(WatchDao).execute(watchRecord);
//    }

    public void insert(WatchRecord watchRecord) {
        new InsertAsyncTask(WatchDao).execute(watchRecord);
    }

    public void getWatchList(Context context,long min, long max) {
       new Thread(() -> {
           MyDatabase db = MyDatabase.getInstance(context);
           WatchDao = db.watchDao();
           someWatchRecord =  WatchDao.getWatchList(min,max);
       }).start();
    }

    private class InitThread extends Thread {
        Context context;

        InitThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            MyDatabase db = MyDatabase.getInstance(context);
            WatchDao = db.watchDao();
            allWatchRecord = WatchDao.getWatchList();
        }
    }

    //插入
    private static class InsertAsyncTask extends AsyncTask<WatchRecord, Void, Void> {

        private WatchDao mAsyncTaskDao;

        InsertAsyncTask(WatchDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final WatchRecord... params) {
            int size = mAsyncTaskDao.getWatchList().size();
            if (size > 100) {
                WatchRecord watchRecord = mAsyncTaskDao.getWatchList().get(size - 1);
                mAsyncTaskDao.delete(watchRecord);
            }
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    //删除
    private static class DeleteAsyncTask extends AsyncTask<WatchRecord, Void, Void> {

        private WatchDao mAsyncTaskDao;

        DeleteAsyncTask(WatchDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(WatchRecord... data) {
            mAsyncTaskDao.deleteAll(data);
            return null;
        }

    }
//    private static class SearchAsyncTask extends AsyncTask<Void, Void, Void> {
//
//        private WatchDao mAsyncTaskDao;
//        private long min;
//        private long max;
//
//        SearchAsyncTask(WatchDao dao, long min, long max) {
//            mAsyncTaskDao = dao;
//            this.min = min;
//            this.max = max;
//        }
//
//        @Override
//        protected Void doInBackground(Void... data) {
//            someWatchRecord =  mAsyncTaskDao.getWatchList(min,max);
//           return null;
//        }
//
//    }
}