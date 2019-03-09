package com.phonepe.memorygame;

import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final long TOTAL_TIME = 60000;
    private ProgressBar mTimerBar;
    private ImageView mLastOpenView;

    private boolean[] mGridOpen;
    private boolean mCanAcceptClick;
    private long mStartTime;
    private int mCurrentLevel;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerBar = findViewById(R.id.timer_bar);
        GridView gameGrid = findViewById(R.id.game_grid);

        String currentLevelStr = PersistanceUtil.getCurrentLevel(this);
        try {
            mCurrentLevel = Integer.parseInt(currentLevelStr);
        } catch (NumberFormatException e) {
            showErrorDialog();
        }
        ((TextView) findViewById(R.id.level_text)).setText(getString(R.string.level_placeholder, currentLevelStr));
        ((TextView) findViewById(R.id.score_text)).setText(getString(R.string.score_placeholder,
                PersistanceUtil.getCurrentScore(this)));
        String gridStructure = Utils.getCurrentGridStructure(this);
        if (TextUtils.isEmpty(gridStructure)) {
            showErrorDialog();
        }

        int row = 0, column = 0;
        try {
            int separatorIndex = gridStructure.indexOf("x");
            if (separatorIndex > 0) {
                row = Integer.parseInt(gridStructure.substring(0, separatorIndex));
                column = Integer.parseInt(gridStructure.substring(separatorIndex + 1));
            } else {
                showErrorDialog();
            }
        } catch (NumberFormatException e) {
            showErrorDialog();
        }

        gameGrid.setNumColumns(column);
        gameGrid.setColumnWidth(GridView.AUTO_FIT);
        gameGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        int pictureCount = row * column / 2;
        int[] pictureGrid = Utils.createPictureGrid(row, column, pictureCount);
        mGridOpen = new boolean[row * column];
        GameGridAdapter adapter = new GameGridAdapter(this, pictureGrid, mGridClickListener);
        gameGrid.setAdapter(adapter);

        mStartTime = System.currentTimeMillis();
        initiateTimer();
        countDownTimer.start();
        mCanAcceptClick = true;
    }

    private void showErrorDialog() {
        showAlertDialog(R.string.error_title, R.string.error_message, R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PersistanceUtil.clearPreference(MainActivity.this);
                recreate();
            }
        });
    }

    private void showAlertDialog(@StringRes int titleRes, @StringRes int messageRes,
                                 @SuppressWarnings("SameParameterValue") @StringRes int buttonText,
                                 DialogInterface.OnClickListener onClickListener) {
        if (isFinishing() || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed()) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(buttonText, onClickListener).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private View.OnClickListener mGridClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mCanAcceptClick) {
                return;
            }
            int drawableRes = (int) view.getTag(R.id.tag_picture_res);
            if (drawableRes != -1) {
                ImageView imageView = (ImageView) view;
                imageView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
                imageView.setImageResource(drawableRes);
                if (mLastOpenView == null) {
                    mLastOpenView = imageView;
                } else {
                    int lastDrawableRes = (int) mLastOpenView.getTag(R.id.tag_picture_res);
                    if (lastDrawableRes != drawableRes) {
                        mCanAcceptClick = false;
                        resetImages(imageView, mLastOpenView);
                    } else {
                        mGridOpen[(int) view.getTag(R.id.tag_view_index)] = true;
                        mGridOpen[(int) mLastOpenView.getTag(R.id.tag_view_index)] = true;
                        checkGameStatus();
                    }
                    mLastOpenView = null;
                }
            }
        }
    };

    private void resetImages(final ImageView view1, final ImageView view2) {
        mTimerBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                view1.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                view1.setImageDrawable(null);
                view2.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                view2.setImageDrawable(null);
                mCanAcceptClick = true;
            }
        }, 300);
    }

    private void checkGameStatus() {
        boolean hasGameEnded = true;
        for (Boolean open : mGridOpen) {
            if (!open) {
                hasGameEnded = false;
                break;
            }
        }

        if (hasGameEnded) {
            countDownTimer.cancel();
            showAlertDialog(R.string.success, R.string.hurray_message, R.string.ok,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    recreate();
                }
            });
            PersistanceUtil.addScore(this, (TOTAL_TIME - System.currentTimeMillis() + mStartTime) / 1000);
            PersistanceUtil.setCurrentLevel(this, String.valueOf(mCurrentLevel + 1));
        }
    }

    private void initiateTimer() {
        countDownTimer = new CountDownTimer(TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerBar.setProgress(100 * (int) millisUntilFinished / (int) TOTAL_TIME);
            }

            @Override
            public void onFinish() {
                showAlertDialog(R.string.failure, R.string.failure_message, R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recreate();
                    }
                });
            }
        };
    }
}
