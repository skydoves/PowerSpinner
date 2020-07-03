/*
 * Designed and developed by 2019 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.powerspinner

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.powerspinner.databinding.LayoutBodyBinding

/** A lightweight dropdown spinner, fully customizable with arrow and animations. */
@Suppress("MemberVisibilityCanBePrivate", "unused")
@SuppressLint("InflateParams")
class PowerSpinnerView : AppCompatTextView, LifecycleObserver {

  /** Main body view for composing the Spinner popup. */
  private val binding: LayoutBodyBinding =
    LayoutBodyBinding.inflate(LayoutInflater.from(context), null, false)

  /** PopupWindow for creating the spinner. */
  private val spinnerWindow: PopupWindow

  /** An adapter for composing items of the spinner. */
  private var adapter: PowerSpinnerInterface<*> = DefaultSpinnerAdapter(this)

  /** Spinner is showing or not. */
  var isShowing: Boolean = false
    private set

  /** An index of the selected item. */
  var selectedIndex: Int = NO_SELECTED_INDEX
    private set

  /** The arrow will  be animated or not when show and dismiss the spinner. */
  var arrowAnimate: Boolean = true

  /** A duration of the arrow animation when show and dismiss. */
  var arrowAnimationDuration: Long = 250L

  /** A drawable of the arrow. */
  var arrowDrawable: Drawable? = context.contextDrawable(R.drawable.arrow)?.mutate()

  /** A duration of the debounce for showOrDismiss. */
  var debounceDuration: Long = 150L
    private set

  /** A backing field of the previously debounce local time. */
  private var previousDebounceTime: Long = 0

  /** A drawable resource of the arrow. */
  @DrawableRes
  var arrowResource: Int = NO_INT_VALUE
    set(value) {
      field = value
      updateSpinnerArrow()
    }

  /** The arrow will be shown or not on the popup. */
  var showArrow: Boolean = true
    set(value) {
      field = value
      updateSpinnerArrow()
    }

  /** A gravity of the arrow. */
  var arrowGravity: SpinnerGravity = SpinnerGravity.END
    set(value) {
      field = value
      updateSpinnerArrow()
    }

  /** A padding of the arrow. */
  @Px
  var arrowPadding: Int = dp2Px(2)
    set(value) {
      field = value
      updateSpinnerArrow()
    }

  /** A tint color of the arrow. */
  @ColorInt
  var arrowTint: Int = outRangeColor
    set(value) {
      field = value
      updateSpinnerArrow()
    }

  /** A divider between items will be shown or not. */
  var showDivider: Boolean = false
    set(value) {
      field = value
      updateSpinnerWindow()
    }

  /** A width size of the divider. */
  @Px
  var dividerSize: Int = dp2Px(0.5f)
    set(value) {
      field = value
      updateSpinnerWindow()
    }

  /** A color of the divider. */
  @ColorInt
  var dividerColor: Int = Color.WHITE
    set(value) {
      field = value
      updateSpinnerWindow()
    }

  /** A background color of the spinner popup. */
  @ColorInt
  var spinnerPopupBackgroundColor: Int = outRangeColor
    set(value) {
      field = value
      updateSpinnerWindow()
    }

  /** A elevation of the spinner popup. */
  @Px
  var spinnerPopupElevation: Int = dp2Px(4)
    set(value) {
      field = value
      updateSpinnerWindow()
    }

  /** The spinner popup will be dismissed when got notified an item is selected. */
  var dismissWhenNotifiedItemSelected: Boolean = true

  /** Interface definition for a callback to be invoked when touched on outside of the spinner popup. */
  var spinnerOutsideTouchListener: OnSpinnerOutsideTouchListener? = null

  /** A collection of the spinner popup animation when show and dismiss. */
  var spinnerPopupAnimation: SpinnerAnimation = SpinnerAnimation.NORMAL

  /** A style resource for the popup animation when show and dismiss. */
  @StyleRes
  var spinnerPopupAnimationStyle: Int = NO_INT_VALUE

  /** A width size of the spinner popup. */
  var spinnerPopupWidth: Int = NO_INT_VALUE

  /** A height size of the spinner popup. */
  var spinnerPopupHeight: Int = NO_INT_VALUE

  /** A preferences name of the spinner. */
  var preferenceName: String? = null
    set(value) {
      field = value
      updateSpinnerPersistence()
    }

  /**
   * A lifecycle owner for observing the lifecycle owner's lifecycle.
   * It is recommended that this field be should be set for avoiding memory leak of the popup window.
   * [Avoid Memory leak](https://github.com/skydoves/powerspinner#avoid-memory-leak)
   */
  var lifecycleOwner: LifecycleOwner? = null
    set(value) {
      field = value
      field?.lifecycle?.addObserver(this@PowerSpinnerView)
    }

  init {
    if (adapter is RecyclerView.Adapter<*>) {
      this.binding.recyclerView.adapter = adapter as RecyclerView.Adapter<*>
    }
    this.spinnerWindow = PopupWindow(this.binding.body,
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT
    )
    this.setOnClickListener { showOrDismiss() }
    if (this.gravity == Gravity.NO_GRAVITY) {
      this.gravity = Gravity.CENTER_VERTICAL
    }
  }

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    getAttrs(attributeSet)
  }

  constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
    context,
    attributeSet,
    defStyle
  ) {
    getAttrs(attributeSet, defStyle)
  }

  private fun getAttrs(attributeSet: AttributeSet) {
    val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PowerSpinnerView)
    try {
      setTypeArray(typedArray)
    } finally {
      typedArray.recycle()
    }
  }

  private fun getAttrs(attributeSet: AttributeSet, defStyleAttr: Int) {
    val typedArray = context.obtainStyledAttributes(
      attributeSet,
      R.styleable.PowerSpinnerView,
      defStyleAttr,
      0
    )
    try {
      setTypeArray(typedArray)
    } finally {
      typedArray.recycle()
    }
  }

  private fun setTypeArray(a: TypedArray) {
    this.arrowResource =
      a.getResourceId(R.styleable.PowerSpinnerView_spinner_arrow_drawable, NO_INT_VALUE)
    this.showArrow = a.getBoolean(R.styleable.PowerSpinnerView_spinner_arrow_show, this.showArrow)
    when (a.getInteger(R.styleable.PowerSpinnerView_spinner_arrow_gravity,
      this.arrowGravity.value)) {
      SpinnerGravity.START.value -> this.arrowGravity = SpinnerGravity.START
      SpinnerGravity.TOP.value -> this.arrowGravity = SpinnerGravity.TOP
      SpinnerGravity.END.value -> this.arrowGravity = SpinnerGravity.END
      SpinnerGravity.BOTTOM.value -> this.arrowGravity = SpinnerGravity.BOTTOM
    }
    this.arrowPadding =
      a.getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_arrow_padding, this.arrowPadding)
    this.arrowTint =
      a.getColor(R.styleable.PowerSpinnerView_spinner_arrow_tint, this.arrowTint)
    this.arrowAnimate =
      a.getBoolean(R.styleable.PowerSpinnerView_spinner_arrow_animate, this.arrowAnimate)
    this.arrowAnimationDuration =
      a.getInteger(R.styleable.PowerSpinnerView_spinner_arrow_animate_duration,
        this.arrowAnimationDuration.toInt()).toLong()
    this.showDivider =
      a.getBoolean(R.styleable.PowerSpinnerView_spinner_divider_show, this.showDivider)
    this.dividerSize =
      a.getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_divider_size, this.dividerSize)
    this.dividerColor =
      a.getColor(R.styleable.PowerSpinnerView_spinner_divider_color, this.dividerColor)
    this.spinnerPopupBackgroundColor =
      a.getColor(R.styleable.PowerSpinnerView_spinner_popup_background,
        this.spinnerPopupBackgroundColor)
    when (a.getInteger(R.styleable.PowerSpinnerView_spinner_popup_animation,
      this.spinnerPopupAnimation.value)) {
      SpinnerAnimation.DROPDOWN.value -> this.spinnerPopupAnimation = SpinnerAnimation.DROPDOWN
      SpinnerAnimation.FADE.value -> this.spinnerPopupAnimation = SpinnerAnimation.FADE
      SpinnerAnimation.BOUNCE.value -> this.spinnerPopupAnimation = SpinnerAnimation.BOUNCE
    }
    this.spinnerPopupAnimationStyle =
      a.getResourceId(R.styleable.PowerSpinnerView_spinner_popup_animation_style,
        this.spinnerPopupAnimationStyle)
    this.spinnerPopupWidth =
      a.getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_width,
        this.spinnerPopupWidth)
    this.spinnerPopupHeight =
      a.getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_height,
        this.spinnerPopupHeight)
    this.spinnerPopupElevation =
      a.getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_elevation,
        this.spinnerPopupElevation)
    val itemArray = a.getResourceId(R.styleable.PowerSpinnerView_spinner_item_array, NO_INT_VALUE)
    if (itemArray != NO_INT_VALUE) {
      setItems(itemArray)
    }
    this.dismissWhenNotifiedItemSelected =
      a.getBoolean(R.styleable.PowerSpinnerView_spinner_dismiss_notified_select,
        this.dismissWhenNotifiedItemSelected)
    this.debounceDuration =
      a.getInteger(R.styleable.PowerSpinnerView_spinner_debounce_duration, debounceDuration.toInt())
        .toLong()
    this.preferenceName =
      a.getString(R.styleable.PowerSpinnerView_spinner_preference_name)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    updateSpinnerWindow()
    updateSpinnerArrow()
    updateSpinnerPersistence()
  }

  private fun updateSpinnerWindow() {
    post {
      this.spinnerWindow.apply {
        width = this@PowerSpinnerView.width
        isOutsideTouchable = true
        setTouchInterceptor(object : OnTouchListener {
          @SuppressLint("ClickableViewAccessibility")
          override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
              spinnerOutsideTouchListener?.onSpinnerOutsideTouch(view, event)
              return true
            }
            return false
          }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          elevation = spinnerPopupElevation.toFloat()
        }
      }
      binding.body.apply {
        if (this@PowerSpinnerView.spinnerPopupBackgroundColor == outRangeColor) {
          background = this@PowerSpinnerView.background
        } else {
          setBackgroundColor(this@PowerSpinnerView.spinnerPopupBackgroundColor)
        }
        setPadding(
          this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom)
        if (this@PowerSpinnerView.showDivider) {
          val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
          val shape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setSize(width, dividerSize)
            setColor(dividerColor)
          }
          decoration.setDrawable(shape)
          binding.recyclerView.addItemDecoration(decoration)
        }
      }
      if (this.spinnerPopupWidth != NO_INT_VALUE) {
        this.spinnerWindow.width = this.spinnerPopupWidth
      }
      if (this.spinnerPopupHeight != NO_INT_VALUE) {
        this.spinnerWindow.height = this.spinnerPopupHeight
      }
    }
  }

  private fun updateSpinnerArrow() {
    if (this.arrowResource != NO_INT_VALUE) {
      this.arrowDrawable = context.contextDrawable(this.arrowResource)?.mutate()
    }
    this.compoundDrawablePadding = this.arrowPadding
    updateCompoundDrawable(this.arrowDrawable)
  }

  private fun updateCompoundDrawable(drawable: Drawable?) {
    if (this.showArrow) {
      drawable?.let {
        if (this.arrowTint != outRangeColor) DrawableCompat.setTint(it, this.arrowTint)
      }
      when (this.arrowGravity) {
        SpinnerGravity.START -> setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        SpinnerGravity.TOP -> setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        SpinnerGravity.END -> setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        SpinnerGravity.BOTTOM -> setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
      }
    } else {
      setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }
  }

  private fun updateSpinnerPersistence() {
    this.preferenceName.whatIfNotNullOrEmpty {
      if (PowerSpinnerPersistence.getInstance(context).getSelectedIndex(it) != -1) {
        this.adapter.notifyItemSelected(
          PowerSpinnerPersistence.getInstance(context).getSelectedIndex(it))
      }
    }
  }

  private fun applyWindowAnimation() {
    if (this.spinnerPopupAnimationStyle == NO_INT_VALUE) {
      when (this.spinnerPopupAnimation) {
        SpinnerAnimation.DROPDOWN -> this.spinnerWindow.animationStyle = R.style.DropDown
        SpinnerAnimation.FADE -> this.spinnerWindow.animationStyle = R.style.Fade
        SpinnerAnimation.BOUNCE -> this.spinnerWindow.animationStyle = R.style.Elastic
        else -> Unit
      }
    } else {
      this.spinnerWindow.animationStyle = this.spinnerPopupAnimationStyle
    }
  }

  /** gets the spinner popup's recyclerView. */
  fun getSpinnerRecyclerView(): RecyclerView = binding.recyclerView

  /** sets an item list for setting items of the adapter. */
  @Suppress("UNCHECKED_CAST")
  fun <T> setItems(itemList: List<T>) {
    val adapter = this.adapter as PowerSpinnerInterface<T>
    adapter.setItems(itemList)
  }

  /**
   * sets a string array resource for setting items of the adapter.
   * This function only works for the [DefaultSpinnerAdapter].
   */
  fun setItems(@ArrayRes resource: Int) {
    if (this.adapter is DefaultSpinnerAdapter) {
      (this.adapter as DefaultSpinnerAdapter).setItems(
        context.resources.getStringArray(resource).toList())
    }
  }

  /** sets an adapter of the [PowerSpinnerView]. */
  fun <T> setSpinnerAdapter(powerSpinnerInterface: PowerSpinnerInterface<T>) {
    this.adapter = powerSpinnerInterface
    if (this.adapter is RecyclerView.Adapter<*>) {
      binding.recyclerView.adapter = this.adapter as RecyclerView.Adapter<*>
    }
  }

  /** gets an adapter of the [PowerSpinnerView]. */
  @Suppress("UNCHECKED_CAST")
  fun <T> getSpinnerAdapter(): PowerSpinnerInterface<T> {
    return this.adapter as PowerSpinnerInterface<T>
  }

  /** sets a [OnSpinnerItemSelectedListener] to the default adapter. */
  @Suppress("UNCHECKED_CAST")
  fun <T> setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<T>) {
    val adapter = this.adapter as PowerSpinnerInterface<T>
    adapter.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener
  }

  /** sets a [OnSpinnerItemSelectedListener] to the popup using lambda. */
  @Suppress("UNCHECKED_CAST")
  fun <T> setOnSpinnerItemSelectedListener(block: (position: Int, item: T) -> Unit) {
    val adapter = this.adapter as PowerSpinnerInterface<T>
    adapter.onSpinnerItemSelectedListener = object : OnSpinnerItemSelectedListener<T> {
      override fun onItemSelected(position: Int, item: T) {
        block(position, item)
      }
    }
  }

  /** sets a [OnSpinnerOutsideTouchListener] to the popup using lambda. */
  fun setOnSpinnerOutsideTouchListener(unit: (View, MotionEvent) -> Unit) {
    this.spinnerOutsideTouchListener = object : OnSpinnerOutsideTouchListener {
      override fun onSpinnerOutsideTouch(view: View, event: MotionEvent) {
        unit(view, event)
      }
    }
  }

  /** shows the spinner popup menu to the center. */
  @MainThread
  fun show() {
    debounceShowOrDismiss {
      if (!this.isShowing) {
        this.isShowing = true
        animateArrow(true)
        applyWindowAnimation()
        this.spinnerWindow.showAsDropDown(this)
        post {
          val spinnerWidth = if (spinnerPopupWidth != NO_INT_VALUE) {
            spinnerPopupWidth
          } else {
            width
          }
          val spinnerHeight = if (spinnerPopupHeight != NO_INT_VALUE) {
            spinnerPopupHeight
          } else {
            this.binding.body.height
          }
          this.spinnerWindow.update(spinnerWidth, spinnerHeight)
        }
      }
    }
  }

  /** dismiss the spinner popup menu. */
  @MainThread
  fun dismiss() {
    debounceShowOrDismiss {
      if (this.isShowing) {
        animateArrow(false)
        this.spinnerWindow.dismiss()
        this.isShowing = false
      }
    }
  }

  /**
   * If the popup is not showing, shows the spinner popup menu to the center.
   * If the popup is already showing, dismiss the spinner popup menu.
   */
  @MainThread
  fun showOrDismiss() {
    val adapter = getSpinnerRecyclerView().adapter ?: return
    if (!isShowing && adapter.itemCount > 0) {
      show()
    } else {
      dismiss()
    }
  }

  /** debounce for showing or dismissing spinner popup. */
  private fun debounceShowOrDismiss(action: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - previousDebounceTime > debounceDuration) {
      this.previousDebounceTime = currentTime
      action()
    }
  }

  /** select an item by index. */
  fun selectItemByIndex(index: Int) {
    this.adapter.notifyItemSelected(index)
  }

  /** notifies to [PowerSpinnerView] of changed information from [PowerSpinnerInterface]. */
  fun notifyItemSelected(index: Int, changedText: CharSequence) {
    this.selectedIndex = index
    this.text = changedText
    if (this.dismissWhenNotifiedItemSelected) {
      dismiss()
    }
    this.preferenceName.whatIfNotNullOrEmpty {
      PowerSpinnerPersistence.getInstance(context).persistSelectedIndex(it, this.selectedIndex)
    }
  }

  /** clears a selected item. */
  fun clearSelectedItem() {
    notifyItemSelected(NO_SELECTED_INDEX, "")
  }

  /** animates the arrow rotation. */
  private fun animateArrow(shouldRotateUp: Boolean) {
    if (this.arrowAnimate) {
      val start = if (shouldRotateUp) 0 else 10000
      val end = if (shouldRotateUp) 10000 else 0
      ObjectAnimator.ofInt(this.arrowDrawable, "level", start, end).apply {
        duration = this@PowerSpinnerView.arrowAnimationDuration
        start()
      }
    }
  }

  /** dismiss automatically when lifecycle owner is destroyed. */
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    dismiss()
  }

  /** Builder class for creating [PowerSpinnerView]. */
  @PowerSpinnerDsl
  class Builder(context: Context) {
    val powerSpinnerView = PowerSpinnerView(context)

    fun setArrowAnimate(value: Boolean) = apply { this.powerSpinnerView.arrowAnimate = value }
    fun setArrowAnimationDuration(value: Long) = apply {
      this.powerSpinnerView.arrowAnimationDuration = value
    }

    fun setArrowDrawableResource(@DrawableRes value: Int) = apply {
      this.powerSpinnerView.arrowResource = value
    }

    fun setShowArrow(value: Boolean) = apply { this.powerSpinnerView.showArrow = value }
    fun setArrowGravity(value: SpinnerGravity) = apply {
      this.powerSpinnerView.arrowGravity = value
    }

    fun setArrowPadding(@Px value: Int) = apply { this.powerSpinnerView.arrowPadding = value }
    fun setArrowTint(@ColorInt value: Int) = apply { this.powerSpinnerView.arrowTint = value }
    fun setShowDivider(value: Boolean) = apply { this.powerSpinnerView.showDivider = value }
    fun setDividerSize(@Px value: Int) = apply { this.powerSpinnerView.dividerSize = value }
    fun setDividerColor(@ColorInt value: Int) = apply { this.powerSpinnerView.dividerColor = value }
    fun setSpinnerPopupBackgroundColor(@ColorInt value: Int) = apply {
      this.powerSpinnerView.spinnerPopupBackgroundColor = value
    }

    fun setDismissWhenNotifiedItemSelected(value: Boolean) = apply {
      this.powerSpinnerView.dismissWhenNotifiedItemSelected = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<T>) {
      val adapter: PowerSpinnerInterface<T> =
        this.powerSpinnerView.adapter as PowerSpinnerInterface<T>
      adapter.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> setOnSpinnerItemSelectedListener(block: (position: Int, item: T) -> Unit) {
      val adapter: PowerSpinnerInterface<T> =
        this.powerSpinnerView.adapter as PowerSpinnerInterface<T>
      adapter.onSpinnerItemSelectedListener = object : OnSpinnerItemSelectedListener<T> {
        override fun onItemSelected(position: Int, item: T) {
          block(position, item)
        }
      }
    }

    fun setOnSpinnerOutsideTouchListener(value: OnSpinnerOutsideTouchListener) = apply {
      this.powerSpinnerView.spinnerOutsideTouchListener = value
    }

    fun setOnSpinnerOutsideTouchListener(unit: (View, MotionEvent) -> Unit) {
      this.powerSpinnerView.spinnerOutsideTouchListener = object : OnSpinnerOutsideTouchListener {
        override fun onSpinnerOutsideTouch(view: View, event: MotionEvent) {
          unit(view, event)
        }
      }
    }

    fun setSpinnerPopupAnimation(value: SpinnerAnimation) = apply {
      this.powerSpinnerView.spinnerPopupAnimation = value
    }

    fun setSpinnerPopupAnimationStyle(@StyleRes value: Int) = apply {
      this.powerSpinnerView.spinnerPopupAnimationStyle = value
    }

    fun setSpinnerPopupWidth(@Px value: Int) = apply {
      this.powerSpinnerView.spinnerPopupWidth = value
    }

    fun setSpinnerPopupHeight(@Px value: Int) = apply {
      this.powerSpinnerView.spinnerPopupHeight = value
    }

    fun setPreferenceName(value: String) = apply {
      this.powerSpinnerView.preferenceName = value
    }

    fun setLifecycleOwner(value: LifecycleOwner) = apply {
      this.powerSpinnerView.lifecycleOwner = value
    }

    fun build() = this.powerSpinnerView
  }
}
