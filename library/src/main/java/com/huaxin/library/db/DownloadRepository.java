//package com.huaxin.library.db;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import java.util.List;
//
//public class DownloadRepository {
//
//    private DownloadDao DownloadDao;
//
//
//    public DownloadRepository(Context context) {
//        //UserRoomDatabase db = UserRoomDatabase.getInstance(application);
//        //mUserDao = db.userDao();
//        //allUser = mUserDao.getUserList();
//        //使用ViweModel可以直接使用上面注释了的
//        WatchRecordRoomDatabase db = WatchRecordRoomDatabase.getInstance(context);
//        DownloadDao = db.downloadDao();
//    }
//
//
//    public void deleteAll(List<Download> watchRecords) {
//        new DeleteAsyncTask(DownloadDao).execute((Download[]) watchRecords.toArray());
//    }
//
////    public void update(Download Download){
////       new UpdateAsyncTask(DownloadDao).execute(Download);
////    }
//
//    public void insert(Download Download) {
//        new InsertAsyncTask(DownloadDao).execute(Download);
//    }
//
//
//
//    //插入
//    private static class InsertAsyncTask extends AsyncTask<Download, Void, Void> {
//
//        private DownloadDao mAsyncTaskDao;
//
//        InsertAsyncTask(DownloadDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(final Download... params) {
//            int size = mAsyncTaskDao.getWatchList().size();
//            if (size > 100) {
//                Download Download = mAsyncTaskDao.getWatchList().get(size - 1);
//                mAsyncTaskDao.delete(Download);
//            }
//            mAsyncTaskDao.insert(params[0]);
//            return null;
//        }
//    }
//
//    //删除
//    private static class DeleteAsyncTask extends AsyncTask<Download, Void, Void> {
//
//        private DownloadDao mAsyncTaskDao;
//
//        DeleteAsyncTask(DownloadDao dao) {
//            mAsyncTaskDao = dao;
//        }
//
//        @Override
//        protected Void doInBackground(Download... data) {
//            mAsyncTaskDao.deleteAll(data);
//            return null;
//        }
//
//    }
//}