package com.example.administrator.yymusic;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import com.example.administrator.yymusic.common.MusicConst;
import com.example.administrator.yymusic.sys.MusicPlayer;
import com.example.administrator.yymusic.sys.MusicSys;
import com.example.administrator.yymusic.tool.FileOperationTask;
import com.example.administrator.yymusic.utils.ShareUtils;

import java.io.File;


/**
 * Created by Administrator on 2016/5/22.
 *
 * @author yysleep
 */
public class MusicApplication extends Application {

    public static int length;

    private final String TAG = "MusicApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        initStorage();

        length = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 2;

        // MusicSys.getInstance().initMusicList(getApplicationContext());

        // 注册观察者
        getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, new MusicObserver(null));

        ShareUtils.getInstance().init(getApplicationContext());
    }


    private class MusicObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MusicObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (FileOperationTask.isOurSelfDelete) {
                Log.i(TAG, "第一次数据库发生了变化 为 yymusic 自身删除 ...");
                FileOperationTask.isOurSelfDelete = false;
                return;
            }
            if (FileOperationTask.autoSync) {
                Log.i(TAG, "第二次数据库发生了变化 为 yymusic 自身删除 ...");
                FileOperationTask.autoSync = false;
                return;
            }
            if (!ShareUtils.getInstance().getReadPermission()) {
                Log.i(TAG, "数据库发生了变化 但是没有读取权限");
                return;
            }
            Log.i(TAG, "数据库发生了变化 正在刷新本地数据 ...");
            MusicSys.getInstance().initMusicList(getApplicationContext());
            MusicPlayer.getInstance().update();
            getApplicationContext().sendBroadcast(new Intent(MusicConst.ACTION_UPDATE_ALL_MUSIC_LIST));
        }
    }

    private boolean initStorage() {

        // 图片存储路径
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yymusic/album/";
        File fileImage = new File(imagePath);
        if (!fileImage.exists()) {
            if (!fileImage.mkdirs()) {
                return false;
            }
        }

        return true;
    }

}
