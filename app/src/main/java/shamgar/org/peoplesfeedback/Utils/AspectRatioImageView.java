package shamgar.org.peoplesfeedback.Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends android.support.v7.widget.AppCompatImageView {

    public AspectRatioImageView(Context context) {
        super(context);
        this.setScaleType(ScaleType.FIT_XY);

    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScaleType(ScaleType.FIT_XY);

    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ScaleType.FIT_XY);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Drawable d = getDrawable();

        if (d != null && d.getIntrinsicWidth() > 0)
        {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (width <= 0)
                width = getLayoutParams().width;

            int height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        }
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}