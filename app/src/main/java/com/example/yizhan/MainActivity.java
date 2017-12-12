package com.example.yizhan;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("插值器实现弹性动画");

        CheckBox cb0 = (CheckBox) findViewById(R.id.cb_0);
        CheckBox cb1 = (CheckBox) findViewById(R.id.cb_1);
        CheckBox cb2 = (CheckBox) findViewById(R.id.cb_2);
        CheckBox cb3 = (CheckBox) findViewById(R.id.cb_3);


        cb0.setOnCheckedChangeListener(this);
        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);

        imageview = (ImageView) findViewById(R.id.imageView2);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ObjectAnimator objectAnimator0 = null;
        ObjectAnimator objectAnimator1 = null;
        AnimatorSet animatorSet = new AnimatorSet();

        switch (buttonView.getId()) {
            case R.id.cb_0://缩放
                if (isChecked) {
                    objectAnimator0 = ObjectAnimator.ofFloat(imageview, "scaleX", 1.0f, 2.0f);
                    objectAnimator1 = ObjectAnimator.ofFloat(imageview, "scaleY", 1.0f, 2.0f);
                } else {
                    objectAnimator0 = ObjectAnimator.ofFloat(imageview, "scaleX", 2.0f, 1.0f);
                    objectAnimator1 = ObjectAnimator.ofFloat(imageview, "scaleY", 2.0f, 1.0f);
                }

                animatorSet.playTogether(objectAnimator0, objectAnimator1);
                break;
            case R.id.cb_1://平移

                float top = imageview.getTranslationY();
                Log.i("public", "top == " + top);
                if (isChecked) {
                    objectAnimator0 = ObjectAnimator.ofFloat(imageview, "translationY", top, top + 500);
                } else {
                    objectAnimator0 = ObjectAnimator.ofFloat(imageview, "translationY", top, top - 500);
                }
                animatorSet.playTogether(objectAnimator0);
                break;
            case R.id.cb_2://旋转
                objectAnimator0 = ObjectAnimator.ofFloat(imageview, "rotation", 0, 360);
                animatorSet.playTogether(objectAnimator0);
                break;
            case R.id.cb_3://淡入淡出
                if (isChecked) {
                    objectAnimator0 = ObjectAnimator.ofFloat(imageview, "alpha", 1.0f, 0f);
                } else {
                    objectAnimator0 = ObjectAnimator.ofFloat(imageview, "alpha", 0f, 1.0f);
                }
                animatorSet.playTogether(objectAnimator0);
                break;
        }

        animatorSet.setDuration(2500);
        animatorSet.setInterpolator(new SpringInterpolator(0.3f));
        animatorSet.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_next:
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }
}
