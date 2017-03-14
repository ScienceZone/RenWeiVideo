/*
package com.ipmph.v.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ipmph.v.application.AppApplication;


 * com.qihoo.yunpan.phone.widget.TextWithDrawable
 
public class TextWithDrawable extends View {

    private String mTxt;
    private Drawable mNormalDrawable, mSelectedDrawable, mPressedDrawable, mStatus, mUnableDrawable;
    private TextPaint txtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    public static final int DIRECT_LEFT = 1, DIRECT_TOP = 2, DIRECT_RIGHT = 3, DIRECT_BOTTOM = 4;
    private static final String Tag = "TextWithDrawable";
    private int direct = DIRECT_TOP;
    private int padding = 0;
    public int mNormalColor = Color.BLACK;
    private int mPressedColor = mNormalColor, mSelectColor = mNormalColor, mUnEnableColor = Color.GRAY;

    public TextWithDrawable(Context context) {
        this(context, null);
    }

    public TextWithDrawable(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextWithDrawable);
        mNormalColor = a.getColor(R.styleable.TextWithDrawable_txt_color, Color.BLACK);
        mSelectColor = a.getColor(R.styleable.TextWithDrawable_txt_selected_color, mNormalColor);
        mPressedColor = a.getColor(R.styleable.TextWithDrawable_txt_pressed_color, mNormalColor);
        mUnEnableColor = a.getColor(R.styleable.TextWithDrawable_txt_unenabled_color, mNormalColor);
        float textSize = a.getDimension(R.styleable.TextWithDrawable_txt_size, 24);
        txtPaint.setTextSize(textSize);
        txtPaint.setTextAlign(Align.CENTER);
        mTxt = a.getString(R.styleable.TextWithDrawable_txt);
        mNormalDrawable = a.getDrawable(R.styleable.TextWithDrawable_drawable);
        mSelectedDrawable = a.getDrawable(R.styleable.TextWithDrawable_selected_drawable);
        mPressedDrawable = a.getDrawable(R.styleable.TextWithDrawable_pressed_drawable);
        mUnableDrawable = a.getDrawable(R.styleable.TextWithDrawable_unenable_drawable);
        //mCurrDrawable = mNormalDrawable;
        direct = a.getInteger(R.styleable.TextWithDrawable_direct, DIRECT_TOP);
        padding = (int) a.getDimension(R.styleable.TextWithDrawable_drawable_padding, 0);
        a.recycle();
    }

    public void setText(int res) {
        setText(getResources().getString(res));
    }

    public void setText(String txt) {
        mTxt = txt;
        invalidate();
    }

    public String getText() {
        return mTxt;
    }

    public void setTextSize(int size) {
        txtPaint.setTextSize(size);
    }

    public void setTextColor(int color) {
        mNormalColor = color;
    }

    public void setTextPressedColor(int color) {
        mPressedColor = color;
    }

    public void setTextSelectColor(int color) {
        mSelectColor = color;
    }

    public void setDrawableDirect(int direct) {
        this.direct = direct;
    }

    public void setDrawable(int drawable) {
        setDrawable(getResources().getDrawable(drawable));
    }

    public void setPressedDrawable(int drawable) {
        setPressedDrawable(getResources().getDrawable(drawable));
    }

    public void setPressedDrawable(Drawable drawable) {
        mPressedDrawable = drawable;
    }

    public void setDrawable(Drawable drawable) {
        mNormalDrawable = drawable;
        //mCurrDrawable = drawable;
        requestLayout();
        invalidate();
    }

    *//**
     * Apply an arbitrary colorfilter to the image.
     *
     * @param cf the colorfilter to apply (may be null)
     *//*
    public void setColorFilter(int color) {
        ColorFilter cf = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        if (cf != null) {
            mNormalDrawable = mNormalDrawable.mutate();
            mNormalDrawable.setColorFilter(cf);
            invalidate();
        }
    }

    public void setSelectDrawable(Drawable drawable) {
        mSelectedDrawable = drawable;
        invalidate();
    }

    public void setDrawablePadding(int padding) {
        this.padding = padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Drawable mCurrDrawable = getCurrDrawable();
        int bW = mCurrDrawable != null ? mCurrDrawable.getIntrinsicWidth() : 0;
        int bH = mCurrDrawable != null ? mCurrDrawable.getIntrinsicHeight() : 0;
        FontMetrics fm = txtPaint.getFontMetrics();
        int txtH = (int) (Math.ceil(fm.descent - fm.ascent));
        if (TextUtils.isEmpty(mTxt)) {
            txtH = 0;
        }
        int vOffset = (int) (3 * AppApplication.density);
        int dLeft = 0, dTop = 0, txtLeft = 0, txtTop = 0;
        if (direct == DIRECT_LEFT) {
            int txtW = (int) txtPaint.measureText(mTxt);
            dLeft = (width - txtW - bW - padding) / 2;
            dTop = (height - bH) / 2;
            txtLeft = dLeft + bW + padding + txtW / 2;
            //            txtTop = height / 2 + vOffset;
            int temHeight = (int) Math.abs(txtPaint.ascent() + txtPaint.descent());
            txtTop = height / 2 + temHeight / 2;
        } else if (direct == DIRECT_TOP) {
            dLeft = (width - bW) / 2;
            dTop = (height - bH - txtH - padding) / 2;
            txtLeft = width / 2;
            txtTop = txtH + dTop + bH + padding - vOffset;
        } else if (direct == DIRECT_RIGHT) {
            int txtW = (int) txtPaint.measureText(mTxt);
            int paddingLeft = (width - txtW - bW - padding) / 2;
            dLeft = paddingLeft + txtW + padding;
            dTop = (height - bH) / 2;
            txtLeft = paddingLeft + txtW / 2;
            txtTop = height / 2 + vOffset;
        } else if (direct == DIRECT_BOTTOM) {
            int paddingTop = (height - bH - txtH - padding) / 2;
            dLeft = (width - bW) / 2;
            dTop = paddingTop + txtH + padding;
            txtLeft = width / 2;
            txtTop = paddingTop + txtH - vOffset;
        }
        if (!TextUtils.isEmpty(mTxt)) {
            if (isSelected()) {
                txtPaint.setColor(mSelectColor);
            } else if (isPressed()) {
                txtPaint.setColor(mPressedColor);
            } else if (!isEnabled()) {
                txtPaint.setColor(mUnEnableColor);
            } else {
                txtPaint.setColor(mNormalColor);
            }
            canvas.drawText(mTxt, txtLeft, txtTop, txtPaint);
        }
        if (mCurrDrawable != null) {
            int dRight = dLeft + bW;
            mCurrDrawable.setBounds(dLeft, dTop, dRight, dTop + bH);
            mCurrDrawable.draw(canvas);
            if (mStatus != null) {
                int statusW = mStatus.getIntrinsicWidth();
                int statusH = mStatus.getIntrinsicHeight();
                mStatus.setBounds(dRight - statusW, dTop, dRight, dTop + statusH);
                mStatus.draw(canvas);
            }
        }
    }

    private Drawable getCurrDrawable() {
        Drawable drawable = null;
        if (isSelected() && mSelectedDrawable != null) {
            drawable = mSelectedDrawable;
            Log.d(Tag, "mSelectedDrawable");
        }

        if (isPressed() && mPressedDrawable != null) {
            drawable = mPressedDrawable;
            Log.d(Tag, "mPressedDrawable");
        }

        if (!isEnabled() && mUnableDrawable != null) {
            drawable = mUnableDrawable;
            Log.d(Tag, "mUnableDrawable");
        }

        if (drawable == null) {
            drawable = mNormalDrawable;
            Log.d(Tag, "mNormalDrawable");
        }
        return drawable;
    }

     @Override
     public void setSelected(boolean selected) {
         super.setSelected(selected);
         if (selected && mSelectedDrawable != null) {
             mCurrDrawable = mSelectedDrawable;
         } else {
             mCurrDrawable = mNormalDrawable;
         }
     }

     @Override
     public void setPressed(boolean pressed) {
         super.setPressed(pressed);
         if (pressed && mPressedDrawable != null) {
             mCurrDrawable = mPressedDrawable;
         } else if (mSelectedDrawable == null || isSelected() == false) {
             mCurrDrawable = mNormalDrawable;
         } else if (isSelected() == false && pressed == false) {
             mCurrDrawable = mNormalDrawable;
         }
     }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public void setStatus(int res) {
        if (res <= 0) {
            mStatus = null;
        } else {
            mStatus = getResources().getDrawable(res);
        }
        invalidate();
    }

    private int getStringHeight(String str) {
        Paint pFont = new Paint();
        Rect rect = new Rect();
        pFont.getTextBounds(str, 0, 1, rect);
        return rect.width();
    }
}
*/