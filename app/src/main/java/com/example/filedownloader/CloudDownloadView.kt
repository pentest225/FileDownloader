package com.example.filedownloader

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.sin


class CloudDownloadView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var viewWidth : Float = 0f
    private var viewHeight:Float = 0f
    private val paintColor = ContextCompat.getColor(context,R.color.colorCloud)
    private val primaryColorDark = ContextCompat.getColor(context,R.color.colorPrimaryDark)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 5f
        color = paintColor
    }
    private val path = Path()
    private var unitWidth = viewWidth * 0.1f
    private var unitHeight = viewHeight *0.1f
    private var circlePoint1 = CanvasPoint(unitWidth*3,unitHeight*5.1f)
    private var circlePoint2 = CanvasPoint(unitWidth*5,unitHeight*4)
    private var circlePoint3 = CanvasPoint(unitWidth*7,unitHeight*5)
    private var rectPoint = CanvasPoint(unitWidth*2,unitHeight*8)
    private var rectPoint1 = CanvasPoint(unitWidth*8,unitHeight*8)
    private var clippedRect = RectF()
    private var testCirclePoint = CanvasPoint(unitWidth*3,unitWidth*3)
    private var circle1Radius = unitWidth*1.4f
    private var circle2Radius = unitWidth*1.4f
    private var circle3Radius = unitWidth*1.2f
    private var  arrowWidth = unitWidth
    private var  arrowHeight =viewHeight + viewHeight *0.3f
    private var  arrowHookWidth = (viewHeight *0.3f)/2f
    private var downloadArrowStartPoint = CanvasPoint(viewWidth/2f-arrowWidth/2f,viewHeight*0.2f)
    private var  arrowP1 = CanvasPoint(downloadArrowStartPoint.x+arrowWidth,viewHeight*0.2f)
    private var  arrowP2 = CanvasPoint(arrowP1.x,arrowHeight*0.4f)
    private var  arrowP3 = CanvasPoint(arrowP2.x+arrowHookWidth,arrowP2.y)
    private var  arrowP4 = CanvasPoint(arrowP1.x-arrowWidth/2f,arrowHeight*0.5f)
    private var  arrowP5 = CanvasPoint(downloadArrowStartPoint.x-arrowHookWidth,arrowP3.y)
    private var  arrowP6 = CanvasPoint(downloadArrowStartPoint.x,arrowHeight*0.4f)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        unitWidth = viewWidth * 0.1f
        unitHeight = viewHeight *0.1f

        circle1Radius = unitWidth*1.4f
        circle2Radius = unitWidth*2.2f
        circle3Radius = unitWidth*1.3f
        circlePoint1 = CanvasPoint(unitWidth*3,unitHeight*5.1f)
        circlePoint2 = CanvasPoint(unitWidth*5,unitHeight*5)
        circlePoint3 = CanvasPoint(unitWidth*7,unitHeight*5.3f)
        rectPoint = CanvasPoint(unitWidth*1.5f,unitHeight*6.1f)
        rectPoint1 = CanvasPoint(viewWidth,viewHeight)
        clippedRect = RectF(rectPoint.x,rectPoint.y,rectPoint1.x,rectPoint1.y)
        clippedRect = RectF(rectPoint.x,rectPoint.y,rectPoint1.x,rectPoint1.y)

         arrowWidth = unitWidth*0.8f
         arrowHeight =viewHeight + viewHeight *0.3f
         arrowHookWidth = (viewHeight *0.3f)/2f
         downloadArrowStartPoint = CanvasPoint(viewWidth/2f-arrowWidth/2f,viewHeight*0.2f)
         arrowP1 = CanvasPoint(downloadArrowStartPoint.x+arrowWidth,viewHeight*0.2f)
         arrowP2 = CanvasPoint(arrowP1.x,arrowHeight*0.4f)
         arrowP3 = CanvasPoint(arrowP2.x+arrowHookWidth,arrowP2.y)
         arrowP4 = CanvasPoint(arrowP1.x-arrowWidth/2f,arrowHeight*0.55f)
         arrowP5 = CanvasPoint(downloadArrowStartPoint.x-arrowHookWidth,arrowP3.y)
         arrowP6 = CanvasPoint(downloadArrowStartPoint.x,arrowHeight*0.4f)

    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {canvas ->
            val x = circlePoint1.x-circle1Radius
            val y = circlePoint1.y+circle1Radius*0.97f
            canvas.clipRect(0f,0f,viewWidth,viewHeight)
            canvas.drawCircle(circlePoint1.x,circlePoint1.y,circle1Radius,paint)
            canvas.drawCircle(circlePoint2.x,circlePoint2.y,circle2Radius,paint)
            canvas.drawCircle(circlePoint3.x,circlePoint3.y,circle3Radius,paint)
            paint.color = primaryColorDark
            canvas.drawRect(x,y,viewWidth,viewHeight,paint)
//            canvas.save() // J'ai pas bien compris le rôle de cette fonction
//            canvas.restore()// Et aussi cette fonction

            paint.color = primaryColorDark
            path.moveTo(downloadArrowStartPoint.x,downloadArrowStartPoint.y)
            path.lineTo(arrowP1.x,arrowP1.y)
            path.lineTo(arrowP2.x,arrowP2.y)
            path.lineTo(arrowP3.x,arrowP3.y)
            path.lineTo(arrowP4.x,arrowP4.y)
            path.lineTo(arrowP5.x,arrowP5.y)
            path.lineTo(arrowP6.x,arrowP6.y)

            canvas.drawPath(path,paint)
            canvas.save()
        }
    }
    private fun drawCloudInClippedRect(canvas: Canvas){
        canvas.drawCircle(circlePoint1.x,circlePoint1.y,circle1Radius,paint)
        canvas.drawCircle(circlePoint2.x,circlePoint2.y,circle2Radius,paint)
        canvas.drawCircle(circlePoint3.x,circlePoint3.y,circle3Radius,paint)
        paint.color = primaryColorDark
        canvas.drawRect(x,y,viewWidth.toFloat(),viewHeight.toFloat(),paint)

        canvas.save()
        canvas.translate(circlePoint2.x,circlePoint2.y)
        canvas.clipRect(100f,100f,100f,100f)
        canvas.restore()
    }
    private fun getCircleBorderPoint(circleCenterPoint:CanvasPoint,circleRadius:Float,pointAngle:Float):CanvasPoint{
        val op = sin(pointAngle)*circleRadius
        val ad = cos(pointAngle)*circleRadius
        Log.i("BORDER_POINT", "Hypothenus: ${op/ sin(pointAngle)}")
        val px = circleCenterPoint.x - ad
        val py = circleCenterPoint.x - op
        return CanvasPoint(px,py)
    }
}
data class CanvasPoint(val x:Float,val y:Float)