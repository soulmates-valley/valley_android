package com.soulmates.valley.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*


@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(context: Context?, private val recyclerView: RecyclerView) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private lateinit var buttons: MutableList<UnderlayButton>
    private lateinit var gestureDetector: GestureDetector
    private var swipedPosition = -1
    private var swipeThreshold = 0.5f
    private val buttonWidth = 200
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>> = mutableMapOf()
    private val recoverQueue = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            if (contains(element)) return false
            return super.add(element)
        }
    }
    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            for (button in buttons) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { _, e ->
        if (swipedPosition < 0) return@OnTouchListener false
        val point = Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPosition)
        val swipedItem = swipedViewHolder!!.itemView
        val rect = Rect()
        swipedItem.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) gestureDetector.onTouchEvent(e) else {
                recoverQueue.add(swipedPosition)
                swipedPosition = -1
                recoverSwipedItem()
            }
        }
        false
    }

    init {
        buttons = ArrayList()
        gestureDetector = GestureDetector(context, gestureListener)
        recyclerView.setOnTouchListener(onTouchListener)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.layoutPosition
        if (swipedPosition != pos) recoverQueue.add(swipedPosition)
        swipedPosition = pos

        if (buttonsBuffer.containsKey(swipedPosition)) buttons = buttonsBuffer[swipedPosition]!!
        else buttons.clear()

        buttonsBuffer.clear()
        swipeThreshold = 0.5f * buttons.size * buttonWidth
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.layoutPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<UnderlayButton>? =
                    ArrayList()
                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer)
                    buttonsBuffer[pos] = buffer!!
                } else {
                    buffer = buttonsBuffer[pos]
                }
                translationX =
                    dX * buffer!!.size * buttonWidth / itemView.width
                drawButtons(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val position = recoverQueue.poll()
            if (position > -1) {
                recyclerView.adapter!!.notifyItemChanged(position)
            }
        }
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>?,
        position: Int,
        dX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer!!.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                position
            )
            right = left
        }
    }

    abstract fun instantiateUnderlayButton(
        viewHolder: RecyclerView.ViewHolder?,
        underlayButtons: MutableList<UnderlayButton>?
    )

    class UnderlayButton(
        private val text: String?,
        private val imageResId: Drawable?,
        private val buttonBackgroundColor: Int,
        private val textColor: Int?,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var position = 0
        private var clickRegion: RectF? = null
        fun onClick(x: Float, y: Float): Boolean {
            clickRegion?.let {
                if (clickRegion!!.contains(x, y)) {
                    clickListener.onClick(position)
                    return true
                }
            }
            return false
        }

        fun onDraw(canvas: Canvas, rect: RectF, pos: Int) {
            val p = Paint()
            p.color = buttonBackgroundColor
            canvas.drawRect(rect, p)
            // 텍스트 있을 때
            if (text != null) {
                // Draw Text
                p.color = textColor!!
                p.textSize = 40f
                val r = Rect()
                val cHeight = rect.height()
                val cWidth = rect.width()
                p.textAlign = Paint.Align.LEFT
                p.getTextBounds(text, 0, text.length, r)
                val x = cWidth / 2f - r.width() / 2f - r.left
                val y = cHeight / 2f + r.height() / 2f - r.bottom - 40
                canvas.drawText(text, rect.left + x, rect.top + y, p)
            }
            // 이미지 있을 때 (텍스트도 있는 경우 주석으로 크기 수정 필요)
            if (imageResId != null) {
                imageResId.setBounds(
                    (rect.left + 70).toInt(),
//                        (rect.top + cHeight / 2f).toInt(),
                    (rect.top + 70).toInt(),
                    (rect.right - 70).toInt(),
//                        (rect.bottom - cHeight / 10f).toInt()
                    (rect.bottom - 70).toInt()
                )
                imageResId.draw(canvas)
            }
            clickRegion = rect
            this.position = pos
        }

    }

    interface UnderlayButtonClickListener {
        fun onClick(position: Int)
    }
}