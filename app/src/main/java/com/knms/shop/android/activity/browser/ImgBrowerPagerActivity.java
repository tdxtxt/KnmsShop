package com.knms.shop.android.activity.browser;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bm.library.PhotoView;
import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.BaseActivity;
import com.knms.shop.android.app.KnmsShopApp;
import com.knms.shop.android.bean.body.media.Pic;
import com.knms.shop.android.helper.DialogHelper;
import com.knms.shop.android.helper.ImageLoadHelper;
import com.knms.shop.android.helper.SDCardHelper;
import com.knms.shop.android.view.HackyViewPager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类说明：
 * 图片浏览器，采用Intent传参(data：List<String>-图片url地址集合；pics:List<Pic>上传成功的图片对象 ;position：int-显示第几张图片;
 *  showDeleteBtn:是否显示删除按钮,默认为false不显示)
 *
 * @author 作者:tdx
 * @date 时间:2016年9月6日 下午4:06:51
 */
public class ImgBrowerPagerActivity extends BaseActivity {
    List<String> list;
    List<Pic> pics;
    int currentPage = 0;
    private boolean showDeleteBtn = false;//true显示删除按钮,false不显示删除按钮

    LinearLayout ll_content;
    private ViewPager mViewPager;
    private TextView tv_center;

    @Override
    protected void getParmas(Intent intent) {
        list = intent.getStringArrayListExtra("data");
        if (list == null) {
            list = new ArrayList<String>();

            pics = (List<Pic>) intent.getSerializableExtra("pics");
            if (pics != null && pics.size() > 0) {
                for (Pic pic : pics) {
                    list.add(pic.url);
                }
            }
        }
        currentPage = intent.getIntExtra("position", 0);
        showDeleteBtn = intent.getBooleanExtra("showDeleteBtn", false);
        if (list == null) list = new ArrayList<>();
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_img_brower;
    }

    @Override
    protected void initView() {
        ll_content = findView(R.id.ll_img_brower_content);
        tv_center = findView(R.id.tv_title_center);
        if (showDeleteBtn) {
            findView(R.id.iv_icon_right).setVisibility(View.VISIBLE);
        } else {
            findView(R.id.iv_icon_right).setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        tv_center.setText(currentPage + 1 + "/" + list.size());
        mViewPager = new HackyViewPager(this);
        ll_content.addView(mViewPager);
        mViewPager.setAdapter(new ImgBrowerPagerAdapter(list));
        mViewPager.setCurrentItem(currentPage);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
                currentPage = position;
                tv_center.setText(currentPage + 1 + "/" + list.size());
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
//        findView(R.id.iv_icon_right).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentPage >= list.size()) {
//                    finshActivity();
//                    return;
//                }
//                pics.get(currentPage).order = "";
//                pics.get(currentPage).isSelect = false;
//                //发送通知
//                RxBus.get().post(BusAction.ACTION_DELETE_PIC, pics.get(currentPage));
//                pics.remove(currentPage);
//                list.remove(currentPage);
//                mViewPager.setAdapter(new ImgBrowerPagerAdapter(list));
//                currentPage = currentPage - 1;
//                mViewPager.setCurrentItem(currentPage);
//                tv_center.setText(currentPage + "/" + list.size());
//                if (list.size() == 0) finshActivity();
//            }
//        });
    }

    @Override
    protected String umTitle() {
        return "浏览大图";
    }

    public class ImgBrowerPagerAdapter extends PagerAdapter {
        List<String> urls;

        public ImgBrowerPagerAdapter(List<String> urls) {
            this.urls = urls;
            if (this.urls == null) this.urls = new ArrayList<String>();
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setBackgroundColor(Color.rgb(0, 0, 0));
            photoView.enable();
            String url = urls.get(position);
            if (TextUtils.isEmpty(url)) url = "";
            ImageLoadHelper.getInstance().displayImageOrigin(url, photoView);
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog(drawableToBitmap(photoView.getDrawable()));
                    return false;
                }
            });
            //单点返回
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KnmsShopApp.getInstance().finishActivity(ImgBrowerPagerActivity.this);
                    ImgBrowerPagerActivity.this.overridePendingTransition(0, 0);
                }
            });
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private void showDialog(final Bitmap bitmap) {
            final DialogHelper dialog = new DialogHelper();
            dialog.showBottomDialog(ImgBrowerPagerActivity.this, R.layout.dialog_sava_img, new DialogHelper.OnEventListener<Dialog>() {
                @Override
                public void eventListener(View parentView, final Dialog window) {
                    TextView savaImg = (TextView) parentView.findViewById(R.id.sava_img);
                    TextView lookImg = (TextView) parentView.findViewById(R.id.look_img);
                    lookImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            window.dismiss();
                        }
                    });
                    savaImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveImageToGallery(ImgBrowerPagerActivity.this, bitmap);
                            window.dismiss();
                        }
                    });

                }
            });
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(SDCardHelper.getCacheImgDir());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ?
                        Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
