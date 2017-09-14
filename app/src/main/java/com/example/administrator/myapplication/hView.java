package com.example.administrator.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/6/14.
 */

 public class hView extends ViewGroup implements View.OnTouchListener {

    private float splitRadio;//分割条在布局中的比例，在0-1之间
    private  int minSplitTop;  //顶部最小高度
    private int minSplitBottom; //底部最小高度

    private View topView ,midView,bottmView;//3个View;
    private int mSplitHeight;//记录滑动 的高度
    private int mTouchSplit;//滑动计数器，只有滑动距离大于这个的时候才能开始互动
    private int mLocationY;//指示器的坐标


    public hView(Context context) {
        super(context);
        init(context,null);
    }

    public hView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }



    private void init(Context context ,AttributeSet attrs){

     TypedArray array =  context.obtainStyledAttributes(attrs,R.styleable.hView);
     splitRadio = array.getFloat(R.styleable.hView_splitRadio,0.5f);
     minSplitTop = array.getDimensionPixelSize(R.styleable.hView_minSplitTop,0);
     minSplitBottom = array.getDimensionPixelSize(R.styleable.hView_minSplitBottom,0);
     array.recycle();
     mTouchSplit  = ViewConfiguration.get(context).getScaledPagingTouchSlop();

 }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }

    private void setupView() {
        topView = getChildAt(0);
        midView = getChildAt(1);
        bottmView = getChildAt(2);

        View handlerView = midView.findViewById(R.id.handView);
        if(handlerView== null){
            handlerView = midView;
        }
        handlerView.setOnTouchListener(this);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getChildCount()!=3)
        {
            throw new RuntimeException("需要3个");
        }
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(mSplitHeight ==0){
            mSplitHeight= (int) (height*splitRadio);
        }

        measureChild(topView,widthMeasureSpec,MeasureSpec.makeMeasureSpec(mSplitHeight,MeasureSpec.EXACTLY));
        measureChild(midView,widthMeasureSpec,heightMeasureSpec);
        measureChild(bottmView,widthMeasureSpec,MeasureSpec.makeMeasureSpec(getMeasuredHeight()-mTouchSplit,MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        l = t = 0;
        topView.layout(l,t,l+topView.getMeasuredWidth(),t+topView.getMeasuredHeight());
        midView.layout(l,mSplitHeight-midView.getMeasuredHeight(),l+midView.getMeasuredWidth(),t+mSplitHeight);
        bottmView.layout(l,t+mSplitHeight,l+bottmView.getMeasuredWidth(),t+mSplitHeight+bottmView.getMeasuredHeight());


    }


    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLocationY = (int) event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                int delta = (int) (event.getY() - mLocationY);
                if (Math.abs(delta) > mTouchSplit) {
                    if (delta > 0) {
                        mSplitHeight += delta - mTouchSplit;
                    } else {
                        mSplitHeight += delta + mTouchSplit;
                    }
                        if(mSplitHeight < minSplitTop || mSplitHeight <midView.getHeight())
                        {
                            mSplitHeight = Math.max(minSplitTop,midView.getHeight());}
                    else if(mSplitHeight > getHeight()||mSplitHeight > getHeight()-minSplitBottom){
                        mSplitHeight = Math.min(getHeight(),getHeight()-minSplitBottom);
                    }
                    requestLayout();
                }
                return true;
        }




        return false;
    }
}
