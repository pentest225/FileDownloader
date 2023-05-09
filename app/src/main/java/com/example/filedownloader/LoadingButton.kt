package com.example.filedownloader

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log

import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates
import kotlin.random.Random


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingProgress :Float = 0F
    private var animationRepeatCount = 0
    private val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private val primaryColorDark = ContextCompat.getColor(context,R.color.colorPrimaryDark)
    private val secondaryColor = ContextCompat.getColor(context,R.color.colorAccent)
    private var valueAnimator = ValueAnimator()
    private val randomProgress = (3..7).random() / 10f
    private var buttonStateLabel : String = ButtonState.Loading.toString()
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        if(old == ButtonState.Completed && new == ButtonState.Loading){
            //START ANIMATION
            startAnimation()
        }
        if(old == ButtonState.Loading && new == ButtonState.LoadingEnd){
            //END ANIMATION
            stopAnimation()
        }
    }
    private fun startAnimation(){
        valueAnimator = ValueAnimator.ofFloat(0f,randomProgress).apply {
            duration = 4000
            repeatCount = animationRepeatCount
            repeatMode = ValueAnimator.RESTART
        }
        valueAnimator.addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            loadingProgress = value
            invalidate()
        }
        valueAnimator.start()
    }
    private fun stopAnimation(){
        valueAnimator = ValueAnimator.ofFloat(randomProgress,1.0f).apply {
            duration = 1000
            repeatCount = animationRepeatCount
            repeatMode = ValueAnimator.RESTART
        }
        valueAnimator.addUpdateListener { animator ->
            val value = animator.animatedValue as Float
            Log.e("LOADING_BUTTON", "stopAnimation: $value")
            if(value == 1f){
                buttonState = ButtonState.Completed
                invalidate()
            }
            loadingProgress = value
            invalidate()
        }
        valueAnimator.start()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
        color = primaryColor
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
          buttonStateLabel =  getString(R.styleable.LoadingButton_state) ?: "completed"
          loadingProgress = getFloat(R.styleable.LoadingButton_progress,-0f)
        }
        buttonState = ButtonState.getByLabel(buttonStateLabel)
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w
        heightSize = h
    }
    private fun drawProgressArc(canvas: Canvas,text: String){
            //Draw Arc
            //Draw Rectangle After Text
            val textWidth = paint.measureText(text)
            val rectStartX = widthSize/2f + textWidth /2f +10
            val rectStartY = heightSize * 0.3
            val rectEndX = rectStartX + 80
            val rectEndY = rectStartY + 80
            val rectF = RectF(rectStartX,rectStartY.toFloat(),rectEndX,rectEndY.toFloat())
            paint.color = secondaryColor
            val arcProgress = loadingProgress *100 * 350 /100
            canvas.drawArc(rectF,0f,arcProgress,true,paint)
    }


    private fun drawCustomButton(canvas: Canvas){
        //1 BACKGROUND RECTANGLE
        paint.color = primaryColor
        val buttonRectF = RectF(0f,0f,widthSize.toFloat(),heightSize.toFloat())
        canvas.drawRect(buttonRectF,paint)

        if(buttonState == ButtonState.Loading || buttonState == ButtonState.LoadingEnd){
            paint.color = primaryColor
            val buttonRectF = RectF(0f,0f,widthSize.toFloat(),heightSize.toFloat())
            canvas.drawRect(buttonRectF,paint)
            paint.color = primaryColorDark
            val progressRect = loadingProgress*100 * widthSize /100
            val loadingRectF = RectF(0f,0f,progressRect,heightSize.toFloat())
            canvas.drawRect(loadingRectF,paint)
            //Draw Arc
            //Draw Rectangle After Text
            val textWidth = paint.measureText(getTextContent())
            val rectStartX = widthSize/2f + textWidth /2f +10
            val rectStartY = heightSize * 0.3
            val rectEndX = rectStartX + 80
            val rectEndY = rectStartY + 80
            val rectF = RectF(rectStartX,rectStartY.toFloat(),rectEndX,rectEndY.toFloat())
            paint.color = secondaryColor
            val arcProgress = loadingProgress *100 * 350 /100
            canvas.drawArc(rectF,0f,arcProgress,true,paint)
        }

        // TEXT
        paint.color = Color.WHITE
        val textSize = resources.getDimension(R.dimen.default_text_size)
        paint.textSize = textSize
        paint.textAlign = Paint.Align.CENTER

        val baseLineY = heightSize/2f + 10f
        //Draw Text
        paint.color = Color.WHITE
        canvas.drawText(getTextContent(),widthSize/2f,baseLineY,paint)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { canvas ->
            drawCustomButton(canvas)
        }
    }
    private fun drawArc(canvas: Canvas){
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
    }
    private fun getTextContent():String {

        return when(buttonState){
            ButtonState.Clicked -> context.getString(R.string.button_label)
            ButtonState.Completed -> context.getString(R.string.button_label)
            ButtonState.Loading -> context.getString(R.string.button_loading)
            else -> context.getString(R.string.button_loading)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}