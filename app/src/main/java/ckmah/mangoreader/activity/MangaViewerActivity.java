package ckmah.mangoreader.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.william.mangoreader.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ckmah.mangoreader.activity.seekbar.MangaViewerSeekBarChangeListener;
import ckmah.mangoreader.activity.seekbar.ReversibleSeekBar;
import ckmah.mangoreader.activity.viewpager.MangaViewPager;
import ckmah.mangoreader.adapter.MangaImagePagerAdapter;
import ckmah.mangoreader.listener.MVPGestureListener;
import ckmah.mangoreader.model.MangaEdenImageItem;
import ckmah.mangoreader.parse.MangaEden;
import retrofit.Callback;
import retrofit.RetrofitError;

public class MangaViewerActivity extends AppCompatActivity {

    // Constants for animating toolbar positions.
    private static float LAYOUT_HEIGHT;
    private static float SEEKBAR_YPOS;
    private static float STATUS_BAR_YPOS;

    private Toolbar mToolbar;
    private Toolbar seekBarToolBar;
    private ReversibleSeekBar seekBar;

    private MangaViewPager mangaViewPager;
    private MangaImagePagerAdapter imageAdapter;

    private List<MangaEdenImageItem> images; // holds requested images for single chapter
    private String mangaTitle;
    private int chapterIndex;
    private int chapterTotalSize;
    private ArrayList<String> chapterIds;

    private boolean readLeftToRight;

    private MVPGestureListener gestureListener;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_viewer);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
        LAYOUT_HEIGHT = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        STATUS_BAR_YPOS = (float) Math.ceil(25 * getResources().getDisplayMetrics().density);
        SEEKBAR_YPOS = (float) Math.ceil(LAYOUT_HEIGHT - (30 * getResources().getDisplayMetrics().density));

        // Activity Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_manga_viewer);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.setVisibility(View.INVISIBLE);
        // ToolBar containing SeekBar
        seekBarToolBar = (Toolbar) findViewById(R.id.toolbar_seekbar);
        seekBarToolBar.setContentInsetsAbsolute(0, 0);
        seekBarToolBar.setVisibility(View.INVISIBLE);
        images = new ArrayList<>();

        mangaViewPager = (MangaViewPager) findViewById(R.id.manga_view_pager);
        mangaViewPager.activity = this;

        mangaTitle = getIntent().getExtras().getString("mangaTitle");
        chapterIndex = getIntent().getExtras().getInt("chapterIndex");
        chapterIds = getIntent().getExtras().getStringArrayList("chapterIds");
        chapterTotalSize = chapterIds.size();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        readLeftToRight = sharedPref.getBoolean(getString(R.string.PREF_KEY_READ_DIRECTION), false);
        boolean showPageNumbers = sharedPref.getBoolean(getString(R.string.PREF_KEY_PAGE_NUMBERS), false);
        Log.d("MVACTIVITY", "read left to right?" + readLeftToRight);
        gestureListener = new MVPGestureListener(this, mangaViewPager) {
            @Override
            public void hideSystemUI() {

                mToolbar.animate()
                        .y(STATUS_BAR_YPOS) // start position
                        .translationY(-1 * mToolbar.getHeight()) // animate this distance
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mToolbar.setVisibility(View.INVISIBLE);
                            }
                        });
                seekBarToolBar.animate()
                        .y(SEEKBAR_YPOS) // start position
                        .translationY(seekBarToolBar.getHeight()) // animate this distance
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                seekBarToolBar.setVisibility(View.INVISIBLE);
                            }
                        });
                getWindow().getDecorView().setSystemUiVisibility( // hide system UI
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }

            @Override
            public void showSystemUI() {
                mToolbar.setVisibility(View.VISIBLE);
                seekBarToolBar.setVisibility(View.VISIBLE);
                mToolbar.animate()
                        .translationY(mToolbar.getHeight()) // animate this distance
                        .y(STATUS_BAR_YPOS) // animation end position
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(null);
                seekBarToolBar.animate()
                        .translationY(-1 * seekBarToolBar.getHeight()) // animate this distance
                        .y(SEEKBAR_YPOS) // animation end position
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .setListener(null);
                getWindow().getDecorView().setSystemUiVisibility( // show system UI
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        };
        mangaViewPager.setLeftToRight(readLeftToRight);
        gestureListener.setLeftToRight(readLeftToRight);

        mangaViewPager.setMVPGestureListener(gestureListener);
        displayChapter();

        seekBar = (ReversibleSeekBar) findViewById(R.id.seekBar);
        seekBar.setLeftToRight(readLeftToRight);
        SeekBar.OnSeekBarChangeListener mangaViewerSeekBarChangeListener = new MangaViewerSeekBarChangeListener(mangaViewPager, seekBar);
        seekBar.setOnSeekBarChangeListener(mangaViewerSeekBarChangeListener);
        mangaViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) mangaViewerSeekBarChangeListener);
    }

    private void displayChapter() {
        // chapter title set to manga name and chapter #
        getSupportActionBar().setTitle(mangaTitle + " - Ch. " + chapterIndex);

        // TODO show progressbar or loading indicator

        // Fetch the chapter images in the background
        String chapterId = chapterIds.get(chapterIndex);
        MangaEden.getMangaEdenService(this).getMangaImages(chapterId, new Callback<MangaEden.MangaEdenChapter>() {
            @Override
            public void success(MangaEden.MangaEdenChapter chapter, retrofit.client.Response response) {
                images = chapter.images;
                if (readLeftToRight) {
                    Collections.reverse(images);
                }
                imageAdapter = new MangaImagePagerAdapter(getSupportFragmentManager(), images.size());
                mangaViewPager.setAdapter(imageAdapter);
                if (readLeftToRight) {
                    mangaViewPager.setCurrentItem(0, false);
                } else {
                    mangaViewPager.setCurrentItem(images.size() - 1, false);
                }

                // Show the very first page
                mangaViewPager.setCurrentItem(images.size() - 1);
                seekBar.setMax(imageAdapter.getCount() - 1);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("ERROR", error.getMessage());
            }
        });

    }

    /**
     * Navigates to next available chapter.
     *
     * @return Returns chapter number. Returns -1 if no next chapter.
     */
    public int nextChapter() {
        if (chapterIndex == chapterTotalSize - 1) {
            // handle last chapter
            return -1;
        } else {
            Log.d("MVACTIVITY", "NEXT CHAPTER");
            chapterIndex++;
            displayChapter();
            return chapterIndex;
        }
    }

    /**
     * Navigates to previous chapter if available.
     *
     * @return Returns chapter number. Returns -1 if no previous chapter.
     */
    public int prevChapter() {
        if (chapterIndex == 0) {
            // handle first chapter
            return -1;
        } else {
            Log.d("MVACTIVITY", "PREVIOUS CHAPTER");
            chapterIndex--;
            displayChapter();
            return chapterIndex;
        }
    }

    public void reverseReadingDirection() {
        mangaViewPager.setLeftToRight(readLeftToRight);
        seekBar.setLeftToRight(readLeftToRight);
        gestureListener.setLeftToRight(readLeftToRight);
        Collections.reverse(images);
        mangaViewPager.setCurrentItem(images.size() - 1 - mangaViewPager.getCurrentItem(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manga_viewer, menu);
        MenuItem toggleLeftRight = menu.findItem(R.id.toggle_left_right);
        toggleLeftRight.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                readLeftToRight = !readLeftToRight;
                Log.d("MVActivity", String.valueOf(readLeftToRight));
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.PREF_KEY_READ_DIRECTION), readLeftToRight);
                editor.apply();
                reverseReadingDirection();
                String toast = "";
                if (readLeftToRight) {
                    toast = "Reading left to right";
                } else {
                    toast = "Reading right to left";
                }
                Toast.makeText(MangaViewerActivity.this, toast, Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        return true;
    }

    public List<MangaEdenImageItem> getImages() {
        return images;
    }

}
