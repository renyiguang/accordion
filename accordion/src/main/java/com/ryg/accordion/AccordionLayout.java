package com.ryg.accordion;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.BounceInterpolator;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.ryg.accordion.presenter.AccordionPresenter;
import com.ryg.accordion.presenter.AccordionPresenterImpl;

/**
 * Created by renyiguang on 16/2/16.
 */
public class AccordionLayout extends ViewGroup {


  private int defaultItemWidth = 200;
  private int defaultShadowPercent = 20;// 0~100

  private int itemWidth;
  private float itemShadow = defaultShadowPercent;

  private int divideWidth;
  private int divideColor;


  private int mInitialX;
  private int mInitialY;

  private int mLastMotionX;
  private int mLastMotionY;
  private int mActivePointerId = INVALID_POINTER_ID;

  private int deltaX;

  private final static int INVALID_POINTER_ID = -1;

  private int firstVisibleItemIndex;
  private int lastVisibleItemIndex;
  private int isFolding;


  private OverScroller overScroller;
  private boolean mIsScrolling = false;

  /**
   * 滑动的最小距离，The scroll touch slop is used to calculate when we start scrolling
   */
  private int mScrollTouchSlop = 2;

  private VelocityTracker velocityTracker;
  private int xVelocity;


  private BaseAccordionAdapter baseAccordionAdapter;
  private AdapterDataSetObserver adapterDataSetObserver;

  private AccordionPresenter accordionPresenter;

  public AccordionLayout(Context context) {
    this(context, null, 0);
  }

  public AccordionLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AccordionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccordionLayout);


    int mDefaultItemWidth =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultItemWidth, context
            .getResources().getDisplayMetrics());


    /***
     *
     * getDimensionPixelOffset:获取属性，返回值为int型
     * getDimension:获取属性，返回值为float型
     *
     */
    itemWidth =
        typedArray
            .getDimensionPixelOffset(R.styleable.AccordionLayout_itemWidth, mDefaultItemWidth);// 返回值为int


    typedArray.recycle();

    overScroller = new OverScroller(context, new BounceInterpolator());

    accordionPresenter = new AccordionPresenterImpl(this);


  }


  public float getItemShadow() {
    return itemShadow;
  }

  /***
   *
   * 使用观察者模式;观察者模式和监听模式的区别是什么？
   * 
   * @param baseAccordionAdapter
   */
  public void setAdapter(BaseAccordionAdapter baseAccordionAdapter) {
    this.baseAccordionAdapter = baseAccordionAdapter;

    adapterDataSetObserver = new AdapterDataSetObserver();
    this.baseAccordionAdapter.registerDataSetObserver(adapterDataSetObserver);

    adapterDataSetObserver.onChanged();
  }


  class AdapterDataSetObserver extends DataSetObserver {
    @Override
    public void onChanged() {
      super.onChanged();

      removeAllViews();

      if (baseAccordionAdapter.getCount() > 0) {
        for (int i = 0; i < baseAccordionAdapter.getCount(); i++) {
          View view = baseAccordionAdapter.getView(i, null, null);
          view.setTag(R.string.tag_item_shadow, itemShadow);

          LayoutParams layoutParams = new LayoutParams(itemWidth, LayoutParams.MATCH_PARENT);
          addView(view, layoutParams);
        }

      }

      /***
       *
       * requestLayout()
       * 会从measure开始调用
       *
       *
       *
       * postInvalidate()
       * 会从draw开始调用
       *
       *
       *
       */

      postInvalidate();

    }

    @Override
    public void onInvalidated() {
      super.onInvalidated();
    }
  }

  private void initVelocityTrackerIfNotExists() {
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain();
    }
  }

  private void recycleVelocityTracker() {
    if (velocityTracker != null) {
      velocityTracker.recycle();
      velocityTracker = null;
    }
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    measureChildren(MeasureSpec.makeMeasureSpec(itemWidth, widthMode), heightMeasureSpec);

    setMeasuredDimension(widthSize, heightSize);

  }


  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    int startLeft = 0;// 子视图的起始横坐标
    int startTop = 0;// 子视图的起始纵坐标

    if (getChildCount() > 0) {

      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);

        float shadowPresent = (float) child.getTag(R.string.tag_item_shadow);

        child.layout(startLeft, startTop, startLeft + child.getMeasuredWidth(),
            startTop + child.getMeasuredHeight());
        startLeft += child.getMeasuredWidth() * (100 - shadowPresent) / 100;

      }
    }


  }



  private void scrollOnLayout(){
    if(getChildCount()>0){

      for (int i = getChildCount() - 1; i >= 0; i--) {

        View child = getChildAt(i);

        float shadowPresent = (float) child.getTag(R.string.tag_item_shadow);

        if (i >= firstVisibleItemIndex && i <= lastVisibleItemIndex) {
          if (shadowPresent > 0 && shadowPresent <= defaultShadowPercent) {

            shadowPresent -= (float) deltaX * 100 / child.getMeasuredWidth();
            child.setTag(R.string.tag_item_shadow, shadowPresent);

            break;
          }

        }

      }
    }
  }



  private void backOnLayout(){

    for (int i = 0; i < getChildCount(); i++) {

      View child = getChildAt(i);
      float shadowPresent = (float) child.getTag(R.string.tag_item_shadow);

      if (i >= firstVisibleItemIndex && i <= lastVisibleItemIndex) {
        if (shadowPresent < defaultShadowPercent) {
          shadowPresent += (float) deltaX *100 / child.getMeasuredWidth();
          child.setTag(R.string.tag_item_shadow, shadowPresent);
        }
        break;
      }

    }


  }


  /****
   *
   * 如果返回false，分两种情况：
   * 1 如果有子view消耗了touch事件，即onTouchEvent返回true，则在后续事件会一直调用onInterceptTouchEvent；
   * 2 如果没有子view消耗了touch事件，则后续不会调用onInterceptTouchEvent；因为子view和本view没有消耗
   * touch事件，则touch事件会交给父view来处理
   *
   * 如果返回true，则表示拦截touch事件，touch事件不会向子view传递
   *
   * @param ev
   * @return
   */



  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {


    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:

        mIsScrolling = !overScroller.isFinished();

        mInitialX = mLastMotionX = (int) ev.getX();
        mInitialY = mLastMotionY = (int) ev.getY();
        mActivePointerId = ev.getPointerId(0);

        firstVisibleItemIndex = accordionPresenter.getFirstVisibleItemIndex();
        lastVisibleItemIndex = accordionPresenter.getLastVisibleItemIndex();

        initVelocityTrackerIfNotExists();
        velocityTracker.addMovement(ev);

        xVelocity = 0 ;

        break;

      case MotionEvent.ACTION_MOVE:

        if (getScrollX() >= accordionPresenter.getScrollRange()) {
          mIsScrolling = false;
          break;
        }

        initVelocityTrackerIfNotExists();
        velocityTracker.addMovement(ev);

        final ViewParent parent = getParent();
        if (parent != null) {
          parent.requestDisallowInterceptTouchEvent(true);
        }

        mIsScrolling = true;
        break;

      case MotionEvent.ACTION_UP:

        mIsScrolling = false;

        break;
    }


    return mIsScrolling;
  }


  @Override
  public boolean onTouchEvent(MotionEvent event) {

    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        mIsScrolling = false;

        mActivePointerId = event.getPointerId(0);
        mInitialX = mLastMotionX = (int) event.getX(mActivePointerId);
        mInitialY = mLastMotionY = (int) event.getY(mActivePointerId);


        firstVisibleItemIndex = accordionPresenter.getFirstVisibleItemIndex();
        lastVisibleItemIndex = accordionPresenter.getLastVisibleItemIndex();


        final ViewParent parent = getParent();
        if (parent != null) {
          parent.requestDisallowInterceptTouchEvent(true);
        }

        break;

      case MotionEvent.ACTION_MOVE:

        if (mActivePointerId == INVALID_POINTER_ID) {
          break;
        }

        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        if (pointerIndex == -1) {
          break;
        }

        int x = (int) event.getX(mActivePointerId);
        int y = (int) event.getY(mActivePointerId);
        deltaX = mLastMotionX - x;


        // overScrollBy(deltaX, 0, getScrollX(), 0, accordionPresenter.getScrollRange(), 0, 0, 0,
        // true);

        mLastMotionX = x;
        mLastMotionY = y;

        mIsScrolling = true;



        scrollOnLayout();
        /**
         * 四个顶点的参数没有更改，故采用之前测量出的结果
         *
         */
        onLayout(true, getLeft(), getTop(), getRight(), getBottom());

        postInvalidate();

        Log.i("========", "===========ACTION_MOVE!");

        break;

      case MotionEvent.ACTION_UP:

        final VelocityTracker mVelocityTracker = velocityTracker;
        mVelocityTracker.computeCurrentVelocity(1000);

        xVelocity =  (int)velocityTracker.getXVelocity(mActivePointerId);
        mIsScrolling = false;

        onLayout(true, getLeft(), getTop(), getRight(), getBottom());

        break;
    }

    return true;
  }


  @Override
  protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

    if (overScroller.isFinished()) {
      super.scrollTo(scrollX, 0);
    }

  }


  @Override
  public void computeScroll() {
    super.computeScroll();

    if (overScroller.computeScrollOffset()) {


      /******
       *
       * scrollTo
       *
       * 坐标问题，需要研究？
       *
       *
       *
       */

      scrollTo(overScroller.getCurrX(), 0);
      postInvalidate();

    }
  }


  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.i("====", "=============onDraw!");

  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    Log.i("====", "=============dispatchDraw!");

  }


  @Override
  protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

    Log.i("====", "=============drawChild!");

    return super.drawChild(canvas, child, drawingTime);
  }
}
