package com.nimmble.rgpro.activity;

//import com.daasuu.mp4compose.filter.GlOverlayFilter;
/**

public class GlWatermarkFilter extends GlOverlayFilter {

    private Bitmap bitmap;
    private Position position = Position.LEFT_BOTTOM;


     static  class AnonymousClass1 {
        static final int[] $SwitchMap$com$daasuu$mp4compose$filter$GlWatermarkFilter$Position = new int[Position.values().length];


        static {
            $SwitchMap$com$daasuu$mp4compose$filter$GlWatermarkFilter$Position[Position.LEFT_TOP.ordinal()] = 1;
            $SwitchMap$com$daasuu$mp4compose$filter$GlWatermarkFilter$Position[Position.LEFT_BOTTOM.ordinal()] = 2;
            $SwitchMap$com$daasuu$mp4compose$filter$GlWatermarkFilter$Position[Position.RIGHT_TOP.ordinal()] = 3;
            try {
                $SwitchMap$com$daasuu$mp4compose$filter$GlWatermarkFilter$Position[Position.RIGHT_BOTTOM.ordinal()] = 4;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    public enum Position {
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }

    public GlWatermarkFilter(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public GlWatermarkFilter(Bitmap bitmap2, Position position2) {
        this.bitmap = bitmap2;
        this.position = position2;
    }


    public void drawCanvas(Canvas canvas) {
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 != null && !bitmap2.isRecycled()) {
            int i = AnonymousClass1.$SwitchMap$com$daasuu$mp4compose$filter$GlWatermarkFilter$Position[this.position.ordinal()];
            if (i == 1) {
                canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, null);
            } else if (i == 2) {
                canvas.drawBitmap(this.bitmap, 0.0f, (float) (canvas.getHeight() - this.bitmap.getHeight()), null);
            } else if (i == 3) {
                canvas.drawBitmap(this.bitmap, (float) (canvas.getWidth() - this.bitmap.getWidth()), 0.0f, null);
            } else if (i == 4) {
                canvas.drawBitmap(this.bitmap, (float) (canvas.getWidth() - this.bitmap.getWidth()), (float) (canvas.getHeight() - this.bitmap.getHeight()), null);
            }
        }
    }

    public void release() {
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 != null && !bitmap2.isRecycled()) {
            this.bitmap.recycle();
        }
    }

}
**/