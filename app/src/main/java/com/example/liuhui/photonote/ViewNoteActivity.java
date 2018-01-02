package com.example.liuhui.photonote;

import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ViewNoteActivity extends AppCompatActivity {

    private static final String TAG = "ViewNoteActivity";
    private ArrayList<String> paths;

    private int currentIndex = 0;
    private ArrayList<Note> notes = new ArrayList<>();

    static {
        Mark.deleteAll(Mark.class);
        Mark mark = new Mark(0.5, 0.5, 1, "one：测试mark文字");
        mark.save();
        Mark mark1 = new Mark(0.3, 0.7, 1, "two：测试mark文字");
        mark1.save();
    }

    private static ArrayList<View> containers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        paths = getIntent().getStringArrayListExtra("paths");
        currentIndex= getIntent().getIntExtra("currentIndex", 0);
        notes = getIntent().getParcelableArrayListExtra("notes");
        Toast.makeText(ViewNoteActivity.this,
                currentIndex+ "/"+ notes.size(), Toast.LENGTH_SHORT).show();

        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < paths.size(); i++) {
            Log.d(TAG, "onCreate: paths:" + paths.get(i));
            View container = inflater.inflate(R.layout.photo_view_container, null);

            final PhotoView photoView = container.findViewById(R.id.photo_view);
            photoView.setImageBitmap(BitmapFactory.decodeFile(paths.get(i)));
            photoView.setPos(i);

            if (i == 0) {
                FrameLayout mark_layer = container.findViewById(R.id.mark_layer);
//                ImageView mark = new ImageView(mark_layer.getContext());
//                mark.setImageResource(R.drawable.ic_spot);
//                mark.setX(200);
//                mark.setY(200);
                final TextView mark = new TextView(mark_layer.getContext());
                mark.setText("test");
                mark.setTextSize(50);
                mark.setTextColor(Color.rgb(200, 200, 200));
                mark.setX(200);
                mark.setY(200);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mark.setLayoutParams(layoutParams);
                mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ViewNoteActivity.this, "click mark!", Toast.LENGTH_SHORT).show();
                    }
                });
                mark_layer.addView(mark);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            RectF rectF = photoView.getDisplayRect();
                            float x = rectF.centerX();
                            float y = rectF.centerY();
                            float w = rectF.width();
                            float h = rectF.height();
                            final float ox = x - w/2;
                            final float oy = y - h/2;
                            mark.post(new Runnable() {
                                @Override
                                public void run() {
                                    mark.setX(ox);
                                    mark.setY(oy);
                                }
                            });
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
            containers.add(container);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return paths.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
//                PhotoView photoView = new PhotoView(container.getContext());
//                photoView.setPos(position);
//                photoView.setImageBitmap(BitmapFactory.decodeFile(paths.get(position)));
//                container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                container.addView(containers.get(position),
                        ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
//                return photoView;
                return containers.get(position);
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
}
