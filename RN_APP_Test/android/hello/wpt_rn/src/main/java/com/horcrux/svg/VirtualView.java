package com.horcrux.svg;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.View;
import android.view.ViewParent;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.uimanager.DisplayMetricsHolder;
import com.facebook.react.uimanager.OnLayoutEvent;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static com.horcrux.svg.FontData.DEFAULT_FONT_SIZE;

@SuppressLint("ViewConstructor")
abstract public class VirtualView extends ReactViewGroup {
    final ReactContext mContext;

    VirtualView(ReactContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        mScale = DisplayMetricsHolder.getScreenDisplayMetrics().density;
    }

    /*
        N[1/Sqrt[2], 36]
        The inverse of the square root of 2.
        Provide enough digits for the 128-bit IEEE quad (36 significant digits).
    */
    private static final double M_SQRT1_2l = 0.707106781186547524400844362104849039;

    static final float MIN_OPACITY_FOR_DRAW = 0.01f;

    private static final float[] sRawMatrix = new float[]{
        1, 0, 0,
        0, 1, 0,
        0, 0, 1
    };
    float mOpacity = 1f;
    Matrix mMatrix = new Matrix();
    Matrix mTransform = new Matrix();
    Matrix mInvMatrix = new Matrix();
    Matrix mInvTransform = new Matrix();
    boolean mInvertible = true;
    boolean mTransformInvertible = true;
    private RectF mClientRect;

    int mClipRule;
    private @Nullable String mClipPath;
    @Nullable String mMask;

    private static final int CLIP_RULE_EVENODD = 0;
    static final int CLIP_RULE_NONZERO = 1;

    final float mScale;
    private boolean mResponsible;
    String mName;

    private SvgView svgView;
    private Path mCachedClipPath;
    private GroupView mTextRoot;
    private double fontSize = -1;
    private double canvasDiagonal = -1;
    private float canvasHeight = -1;
    private float canvasWidth = -1;
    private GlyphContext glyphContext;

    Path mPath;
    Path mFillPath;
    Path mStrokePath;
    RectF mBox;
    Region mRegion;
    Region mStrokeRegion;
    Region mClipRegion;
    Path mClipRegionPath;

    @Override
    public void invalidate() {
        if (this instanceof RenderableView && mPath == null) {
            return;
        }
        clearCache();
        clearParentCache();
        super.invalidate();
    }

    private void clearCache() {
        canvasDiagonal = -1;
        canvasHeight = -1;
        canvasWidth = -1;
        fontSize = -1;
        mStrokeRegion = null;
        mRegion = null;
        mPath = null;
    }

    void clearChildCache() {
        clearCache();
        for (int i = 0; i < getChildCount(); i++) {
            View node = getChildAt(i);
            if (node instanceof VirtualView) {
                ((VirtualView)node).clearChildCache();
            }
        }
    }

    private void clearParentCache() {
        VirtualView node = this;
        while (true) {
            ViewParent parent = node.getParent();
            if (!(parent instanceof VirtualView)) {
                return;
            }
            node = (VirtualView)parent;
            if (node.mPath == null) {
                return;
            }
            node.clearCache();
        }
    }

    @Nullable
    GroupView getTextRoot() {
        VirtualView node = this;
        if (mTextRoot == null) {
            while (node != null) {
                if (node instanceof GroupView && ((GroupView) node).getGlyphContext() != null) {
                    mTextRoot = (GroupView)node;
                    break;
                }

                ViewParent parent = node.getParent();

                if (!(parent instanceof VirtualView)) {
                    node = null;
                } else {
                    node = (VirtualView)parent;
                }
            }
        }

        return mTextRoot;
    }

    @Nullable
    GroupView getParentTextRoot() {
        ViewParent parent = this.getParent();
        if (!(parent instanceof VirtualView)) {
            return null;
        } else {
            return ((VirtualView) parent).getTextRoot();
        }
    }


    private double getFontSizeFromContext() {
        if (fontSize != -1) {
            return fontSize;
        }
        GroupView root = getTextRoot();
        if (root == null) {
            return DEFAULT_FONT_SIZE;
        }

        if (glyphContext == null) {
            glyphContext = root.getGlyphContext();
        }

        fontSize = glyphContext.getFontSize();

        return fontSize;
    }

    abstract void draw(Canvas canvas, Paint paint, float opacity);
    void render(Canvas canvas, Paint paint, float opacity) {
        draw(canvas, paint, opacity);
    }

    /**
     * Sets up the transform matrix on the canvas before an element is drawn.
     *
     * NB: for perf reasons this does not apply opacity, as that would mean creating a new canvas
     * layer (which allocates an offscreen bitmap) and having it composited afterwards. Instead, the
     * drawing code should apply opacity recursively.
     *
     * @param canvas the canvas to set up
     */
    int saveAndSetupCanvas(Canvas canvas) {
        int count = canvas.save();
        canvas.concat(mMatrix);
        canvas.concat(mTransform);
        return count;
    }

    /**
     * Restore the canvas after an element was drawn. This is always called in mirror with
     * {@link #saveAndSetupCanvas}.
     *
     * @param canvas the canvas to restore
     */
    void restoreCanvas(Canvas canvas, int count) {
        canvas.restoreToCount(count);
    }

    @ReactProp(name = "name")
    public void setName(String name) {
        mName = name;
        invalidate();
    }


    @ReactProp(name = "mask")
    public void setMask(String mask) {
        mMask = mask;
        invalidate();
    }

    @ReactProp(name = "clipPath")
    public void setClipPath(String clipPath) {
        mCachedClipPath = null;
        mClipPath = clipPath;
        invalidate();
    }

    @ReactProp(name = "clipRule", defaultInt = CLIP_RULE_NONZERO)
    public void setClipRule(int clipRule) {
        mClipRule = clipRule;
        invalidate();
    }

    @ReactProp(name = "opacity", defaultFloat = 1f)
    public void setOpacity(float opacity) {
        mOpacity = opacity;
        invalidate();
    }

    @ReactProp(name = "matrix")
    public void setMatrix(Dynamic matrixArray) {
        ReadableType type = matrixArray.getType();
        if (!matrixArray.isNull() && type.equals(ReadableType.Array)) {
            int matrixSize = PropHelper.toMatrixData(matrixArray.asArray(), sRawMatrix, mScale);
            if (matrixSize == 6) {
                if (mMatrix == null) {
                    mMatrix = new Matrix();
                    mInvMatrix = new Matrix();
                }
                mMatrix.setValues(sRawMatrix);
                mInvertible = mMatrix.invert(mInvMatrix);
            } else if (matrixSize != -1) {
                FLog.w(ReactConstants.TAG, "RNSVG: Transform matrices must be of size 6");
            }
        } else {
            mMatrix = null;
            mInvMatrix = null;
            mInvertible = false;
        }

        super.invalidate();
        clearParentCache();
    }

    @ReactProp(name = "responsible")
    public void setResponsible(boolean responsible) {
        mResponsible = responsible;
        invalidate();
    }

    @Nullable Path getClipPath() {
        return mCachedClipPath;
    }

    @Nullable Path getClipPath(Canvas canvas, Paint paint) {
        if (mClipPath != null) {
            ClipPathView mClipNode = (ClipPathView) getSvgView().getDefinedClipPath(mClipPath);

            if (mClipNode != null) {
                Path clipPath = mClipNode.mClipRule == CLIP_RULE_EVENODD ? mClipNode.getPath(canvas, paint) :
                        mClipNode.getPath(canvas, paint, Region.Op.UNION);
                switch (mClipNode.mClipRule) {
                    case CLIP_RULE_EVENODD:
                        clipPath.setFillType(Path.FillType.EVEN_ODD);
                        break;
                    case CLIP_RULE_NONZERO:
                        break;
                    default:
                        FLog.w(ReactConstants.TAG, "RNSVG: clipRule: " + mClipRule + " unrecognized");
                }
                mCachedClipPath = clipPath;
            } else {
                FLog.w(ReactConstants.TAG, "RNSVG: Undefined clipPath: " + mClipPath);
            }
        }

        return getClipPath();
    }

    void clip(Canvas canvas, Paint paint) {
        Path clip = getClipPath(canvas, paint);

        if (clip != null) {
            canvas.clipPath(clip);
        }
    }

    abstract int hitTest(final float[] point);

    boolean isResponsible() {
        return mResponsible;
    }

    abstract Path getPath(Canvas canvas, Paint paint);

    SvgView getSvgView() {
        if (svgView != null) {
            return svgView;
        }

        ViewParent parent = getParent();

        if (parent == null) {
            return null;
        } else if (parent instanceof SvgView) {
            svgView = (SvgView)parent;
        } else if (parent instanceof VirtualView) {
            svgView = ((VirtualView) parent).getSvgView();
        } else {
            FLog.e(ReactConstants.TAG, "RNSVG: " + getClass().getName() + " should be descendant of a SvgView.");
        }

        return svgView;
    }

    double relativeOnWidth(SVGLength length) {
        SVGLengthUnitType unit = length.unit;
        if (unit == SVGLengthUnitType.SVG_LENGTHTYPE_NUMBER){
            return length.value * mScale;
        } else if (unit == SVGLengthUnitType.SVG_LENGTHTYPE_PERCENTAGE){
            return length.value / 100 * getCanvasWidth();
        }
        return fromRelativeFast(length);
    }

    double relativeOnHeight(SVGLength length) {
        SVGLengthUnitType unit = length.unit;
        if (unit == SVGLengthUnitType.SVG_LENGTHTYPE_NUMBER){
            return length.value * mScale;
        } else if (unit == SVGLengthUnitType.SVG_LENGTHTYPE_PERCENTAGE){
            return length.value / 100 * getCanvasHeight();
        }
        return fromRelativeFast(length);
    }

    double relativeOnOther(SVGLength length) {
        SVGLengthUnitType unit = length.unit;
        if (unit == SVGLengthUnitType.SVG_LENGTHTYPE_NUMBER){
            return length.value * mScale;
        } else if (unit == SVGLengthUnitType.SVG_LENGTHTYPE_PERCENTAGE){
            return length.value / 100 * getCanvasDiagonal();
        }
        return fromRelativeFast(length);
    }

    /**
     * Converts SVGLength into px / user units
     * in the current user coordinate system
     *
     * @param length     length string
     * @return value in the current user coordinate system
     */
    double fromRelativeFast(SVGLength length) {
        double unit;
        switch (length.unit) {
            case SVG_LENGTHTYPE_EMS:
                unit = getFontSizeFromContext();
                break;
            case SVG_LENGTHTYPE_EXS:
                unit = getFontSizeFromContext() / 2;
                break;

            case SVG_LENGTHTYPE_CM:
                unit = 35.43307;
                break;
            case SVG_LENGTHTYPE_MM:
                unit = 3.543307;
                break;
            case SVG_LENGTHTYPE_IN:
                unit = 90;
                break;
            case SVG_LENGTHTYPE_PT:
                unit = 1.25;
                break;
            case SVG_LENGTHTYPE_PC:
                unit = 15;
                break;

            default:
                unit = 1;
        }
        return length.value * unit * mScale;
    }

    private float getCanvasWidth() {
        if (canvasWidth != -1) {
            return canvasWidth;
        }
        GroupView root = getTextRoot();
        if (root == null) {
            canvasWidth = getSvgView().getCanvasBounds().width();
        } else {
            canvasWidth = root.getGlyphContext().getWidth();
        }

        return canvasWidth;
    }

    private float getCanvasHeight() {
        if (canvasHeight != -1) {
            return canvasHeight;
        }
        GroupView root = getTextRoot();
        if (root == null) {
            canvasHeight = getSvgView().getCanvasBounds().height();
        } else {
            canvasHeight = root.getGlyphContext().getHeight();
        }

        return canvasHeight;
    }

    private double getCanvasDiagonal() {
        if (canvasDiagonal != -1) {
            return canvasDiagonal;
        }
        double powX = Math.pow((getCanvasWidth()), 2);
        double powY = Math.pow((getCanvasHeight()), 2);
        canvasDiagonal = Math.sqrt(powX + powY) * M_SQRT1_2l;
        return canvasDiagonal;
    }

    void saveDefinition() {
        if (mName != null) {
            getSvgView().defineTemplate(this, mName);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mClientRect != null ?
                (int) Math.ceil(mClientRect.width())
                : getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        int height = mClientRect != null ?
                (int) Math.ceil(mClientRect.height())
                : getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    /**
     * Called from layout when this view should
     * assign a size and position to each of its children.
     *
     * Derived classes with children should override
     * this method and call layout on each of
     * their children.
     * @param changed This is a new size or position for this view
     * @param pleft Left position, relative to parent
     * @param ptop Top position, relative to parent
     * @param pright Right position, relative to parent
     * @param pbottom Bottom position, relative to parent
     */
    protected void onLayout(boolean changed, int pleft, int ptop, int pright, int pbottom) {
        if (mClientRect == null) {
            return;
        }

        if (!(this instanceof GroupView)) {
            int left = (int) Math.floor(mClientRect.left);
            int top = (int) Math.floor(mClientRect.top);
            int right = (int) Math.ceil(mClientRect.right);
            int bottom = (int) Math.ceil(mClientRect.bottom);
            setLeft(left);
            setTop(top);
            setRight(right);
            setBottom(bottom);
        }
        int width = (int) Math.ceil(mClientRect.width());
        int height = (int) Math.ceil(mClientRect.height());
        setMeasuredDimension(width, height);
    }

    void setClientRect(RectF rect) {
        if (mClientRect != null && mClientRect.equals(rect)) {
            return;
        }
        mClientRect = rect;
        if (mClientRect == null) {
            return;
        }
        int left = (int) Math.floor(mClientRect.left);
        int top = (int) Math.floor(mClientRect.top);
        int right = (int) Math.ceil(mClientRect.right);
        int bottom = (int) Math.ceil(mClientRect.bottom);
        int width = (int) Math.ceil(mClientRect.width());
        int height = (int) Math.ceil(mClientRect.height());

        if (!(this instanceof GroupView)) {
            setLeft(left);
            setTop(top);
            setRight(right);
            setBottom(bottom);
        }
        setMeasuredDimension(width, height);

        EventDispatcher eventDispatcher = mContext
                .getNativeModule(UIManagerModule.class)
                .getEventDispatcher();
        eventDispatcher.dispatchEvent(OnLayoutEvent.obtain(
                this.getId(),
                left,
                top,
                width,
                height
        ));
    }

    RectF getClientRect() {
        return mClientRect;
    }

}