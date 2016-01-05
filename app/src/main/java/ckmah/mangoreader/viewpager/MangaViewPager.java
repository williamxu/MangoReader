package ckmah.mangoreader.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import ckmah.mangoreader.activity.MangaViewerActivity;

public class MangaViewPager extends ViewPager {

    public MangaViewerActivity activity;
    private boolean leftToRight;

    public MangaViewPager(Context context) {
        super(context);
        init(context);
    }

    public MangaViewPager(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    private void init(Context context) {
        ((AppCompatActivity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                // | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                // | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    // Callback, for viewpager to advance either the page or the chapter
    // TODO trigger this on swipe in addition to touch
    public void nextPage() {
        if (leftToRight) {
            if (getCurrentItem() < getAdapter().getCount() - 1) {
                // go to next page
                setCurrentItem(getCurrentItem() + 1);
            } else {
                // next chapter
                activity.nextChapter();
            }
        } else {
            if (getCurrentItem() > 0) {
                // go to next page
                setCurrentItem(getCurrentItem() - 1);
            } else {
                // next chapter
                activity.nextChapter();
            }
        }
    }

    public void previousPage() {
        if (leftToRight) {
            if (getCurrentItem() > 0) {
                // previous page
                setCurrentItem(getCurrentItem() - 1);
            } else {
                // previous chapter
                activity.prevChapter();
            }

        } else {
            if (getCurrentItem() < getAdapter().getCount() - 1) {
                // previous page
                setCurrentItem(getCurrentItem() + 1);
            } else {
                // previous chapter
                activity.prevChapter();
            }
        }
    }

    public void setLeftToRight(boolean leftToRight) {
        this.leftToRight = leftToRight;
    }
}
