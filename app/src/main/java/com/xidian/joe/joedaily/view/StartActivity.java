package com.xidian.joe.joedaily.view;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.utils.HttpUtils;
import com.xidian.joe.joedaily.support.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/11.
 */
public class StartActivity extends BaseActivity implements Animator.AnimatorListener {
    private static final String IMAGE_FILENAME = "startImage.jpg";
    private ImageView mStartImageView;
    private  File mImageFile;
    private String mImageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);       //状态栏的隐藏
        setContentView(R.layout.start_layout);
        mStartImageView = (ImageView) findViewById(R.id.start_image_view);

        initImage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    private void initImage() {
        File imageDir = getFilesDir();
        mImageFile = new File(imageDir,IMAGE_FILENAME);
        if(mImageFile.exists()){
            mStartImageView.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
        }else{
            mStartImageView.setImageResource(R.drawable.default_img);
        }
        mStartImageView.animate().scaleX(1.5f).scaleY(1.5f).setListener(this).setDuration(1500).start();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(HttpUtils.isNetworkConnected(this)){
            HttpUtils.getJsonObject(mQueue, Constants.IMAGE, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    try {
                        mImageUrl = object.getString("img");
                        saveImage(mImageFile,mImageUrl);
                        startMainActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        startMainActivity();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(StartActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                }
            });
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveImage(final File file ,String imageUrl){

        HttpUtils.getImageObject(mQueue, imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if(file.exists()){
                    file.delete();
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }
}
