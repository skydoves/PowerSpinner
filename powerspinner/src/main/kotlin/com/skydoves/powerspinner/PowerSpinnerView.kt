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
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.powerspinner.databinding.PowerspinnerLayoutBodyBinding
import com.skydoves.powerspinner.internals.NO_INT_VALUE
import com.skydoves.powerspinner.internals.NO_SELECTED_INDEX
import com.skydoves.powerspinner.internals.PowerSpinnerDsl
import com.skydoves.powerspinner.internals.contextDrawable
import com.skydoves.powerspinner.internals.dp2Px
import com.skydoves.powerspinner.internals.resize
import com.skydoves.powerspinner.internals.whatIfNotNullOrEmpty

/** A lightweight dropdown spinner, fully customizable with arrow and animations. */
@Suppress("MemberVisibilityCanBePrivate", "unused")
public class PowerSpinnerView : AppCompatTextView, DefaultLifecycleObserver {

  /** Main body view for composing the Spinner popup. */
  private val binding: PowerspinnerLayoutBodyBinding =
    PowerspinnerLayoutBodyBinding.inflate(LayoutInflater.from(context), null, false)

  /** PopupWindow for creating the spinner. */
  public val spinnerWindow: PopupWindow

  /** Spinner is showing or not. */
  public var isShowing: Boolean = false
    private set

  /** An index of the selected item. */
  public var selectedIndex: Int = NO_SELECTED_INDEX
    private set

  /** An adapter for composing items of the spinner. */
  private var adapter: PowerSpinnerInterface<*> = DefaultSpinnerAdapter(this)

  /** A padding values for the content of the spinner. */
  private val padding: PowerSpinnerPaddings = PowerSpinnerPaddings()

  /** The arrow will  be animated or not when show and dismiss the spinner. */
  public var arrowAnimate: Boolean = true

  /** A duration of the arrow animation when show and dismiss. */
  public var arrowAnimationDuration: Long = 250L

  /** A drawable of the arrow. */
  public var arrowDrawable: Drawable? =
    context.contextDrawable(R.drawable.powerspinner_arrow)?.mutate()

  /** A duration of the debounce for showOrDismiss. */
  public var debounceDuration: Long = 150L
    private set

  /** Disable changing text automatically when an item selection notified. */
  @JvmField
  public var disableChangeTextWhenNotified: Boolean = false

  /** A backing field of the previously debounce local time. */
  private var previousDebounceTime: Long = 0

  @DrawableRes
  private var _arrowResource: Int = NO_INT_VALUE

  /** A drawable resource of the arrow. */
  public var arrowResource: Int
    @DrawableRes get() = _arrowResource
    set(@DrawableRes value) {
      _arrowResource = value
      updateSpinnerArrow()
    }

  private var _showArrow: Boolean = true

  /** The arrow will be shown or not on the popup. */
  public var showArrow: Boolean
    get() = _showArrow
    set(value) {
      _showArrow = value
      updateSpinnerArrow()
    }

  private var _arrowGravity: SpinnerGravity = SpinnerGravity.END

  /** A gravity of the arrow. */
  public var arrowGravity: SpinnerGravity
    get() = _arrowGravity
    set(value) {
      _arrowGravity = value
      updateSpinnerArrow()
    }

  private var _arrowSize: SpinnerSizeSpec? = null

  /** A size spec of the arrow. */
  public var arrowSize: SpinnerSizeSpec?
    get() = _arrowSize
    set(value) {
      _arrowSize = value
      updateSpinnerArrow()
    }

  @Px
  private var _arrowPadding: Int = 0

  /** A padding of the arrow. */
  public var arrowPadding: Int
    @Px get() = _arrowPadding
    set(@Px value) {
      _arrowPadding = value
      updateSpinnerArrow()
    }

  @ColorInt
  private var _arrowTint: Int = NO_INT_VALUE

  /** A tint color of the arrow. */
  public var arrowTint: Int
    @ColorInt get() = _arrowTint
    set(@ColorInt value) {
      _arrowTint = value
      updateSpinnerArrow()
    }

  private var _showDivider: Boolean = false

  /** A divider between items will be shown or not. */
  public var showDivider: Boolean
    get() = _showDivider
    set(value) {
      _showDivider = value
      updateSpinnerWindow()
    }

  @Px
  private var _dividerSize: Int = dp2Px(0.5f)

  /** A width size of the divider. */
  public var dividerSize: Int
    @Px get() = _dividerSize
    set(@Px value) {
      _dividerSize = value
      updateSpinnerWindow()
    }

  @ColorInt
  private var _dividerColor: Int = Color.WHITE

  /** A color of the divider. */
  public var dividerColor: Int
    @ColorInt get() = _dividerColor
    set(@ColorInt value) {
      _dividerColor = value
      updateSpinnerWindow()
    }

  private var _spinnerPopupBackground: Drawable? = null

  /** A background of the spinner popup. */
  public var spinnerPopupBackground: Drawable?
    get() = _spinnerPopupBackground
    set(value) {
      _spinnerPopupBackground = value
      updateSpinnerWindow()
    }

  @Px
  private var _spinnerPopupElevation: Int = dp2Px(4)

  /** A elevation of the spinner popup. */
  public var spinnerPopupElevation: Int
    @Px get() = _spinnerPopupElevation
    set(@Px value) {
      _spinnerPopupElevation = value
      updateSpinnerWindow()
    }

  /** A style resource for the popup animation when show and dismiss. */
  @StyleRes
  public var spinnerPopupAnimationStyle: Int = NO_INT_VALUE

  /** A width size of the spinner popup. */
  public var spinnerPopupWidth: Int = NO_INT_VALUE

  /** A height size of the spinner popup. */
  public var spinnerPopupHeight: Int = NO_INT_VALUE

  /** A max height size of the spinner popup. */
  public var spinnerPopupMaxHeight: Int = NO_INT_VALUE

  /** A fixed item height size of the spinner popup. */
  public var spinnerItemHeight: Int = NO_INT_VALUE

  /** A background of the selected item. */
  public var spinnerSelectedItemBackground: Drawable? = null

  /** The spinner popup will be dismissed when got notified an item is selected. */
  public var dismissWhenNotifiedItemSelected: Boolean = true

  /** Interface definition for a callback to be invoked when touched on outside of the spinner popup. */
  public var spinnerOutsideTouchListener: OnSpinnerOutsideTouchListener? = null

  /** Interface definition for a callback to be invoked when spinner popup is dismissed. */
  public var onSpinnerDismissListener: OnSpinnerDismissListener? = null

  /** A collection of the spinner popup animation when show and dismiss. */
  public var spinnerPopupAnimation: SpinnerAnimation = SpinnerAnimation.NORMAL

  /** A preferences name of the spinner. */
  public var preferenceName: String? = null
    set(value) {
      field = value
      updateSpinnerPersistence()
    }

  /**
   * A lifecycle owner for observing the lifecycle owner's lifecycle.
   * It is recommended that this field be should be set for avoiding memory leak of the popup window.
   * [Avoid Memory leak](https://github.com/skydoves/powerspinner#avoid-memory-leak)
   */
  public var lifecycleOwner: LifecycleOwner? = null
    set(value) {
      field?.lifecycle?.removeObserver(this@PowerSpinnerView)
      field = value
      field?.lifecycle?.addObserver(this@PowerSpinnerView)
    }

  init {
    if (adapter is RecyclerView.Adapter<*>) {
      getSpinnerRecyclerView().adapter = adapter as RecyclerView.Adapter<*>
    }
    this.spinnerWindow = PopupWindow(
      this.binding.body,
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.WRAP_CONTENT
    )
    this.setOnClickListener { showOrDismiss() }
    if (this.gravity == Gravity.NO_GRAVITY) {
      this.gravity = Gravity.CENTER_VERTICAL
    }
    val viewContext = context
    if (lifecycleOwner == null && viewContext is LifecycleOwner) {
      lifecycleOwner = viewContext
    }
  }

  public constructor(context: Context) : super(context)

  public constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    getAttrs(attributeSet)
  }

  public constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
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
    a.apply {
      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_drawable)) {
        _arrowResource =
          getResourceId(R.styleable.PowerSpinnerView_spinner_arrow_drawable, _arrowResource)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_show)) {
        _showArrow = a.getBoolean(R.styleable.PowerSpinnerView_spinner_arrow_show, _showArrow)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_gravity)) {
        _arrowGravity = when (
          getInteger(
            R.styleable.PowerSpinnerView_spinner_arrow_gravity,
            _arrowGravity.value
          )
        ) {
          SpinnerGravity.START.value -> SpinnerGravity.START
          SpinnerGravity.TOP.value -> SpinnerGravity.TOP
          SpinnerGravity.END.value -> SpinnerGravity.END
          SpinnerGravity.BOTTOM.value -> SpinnerGravity.BOTTOM
          else -> throw IllegalArgumentException("unknown argument: spinner_arrow_gravity")
        }
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_top_padding)) {
        padding.top =
          getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_top_padding, 0)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_end_padding)) {
        padding.end =
          getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_end_padding, 0)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_bottom_padding)) {
        padding.bottom =
          getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_bottom_padding, 0)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_start_padding)) {
        padding.start =
          getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_start_padding, 0)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_padding)) {
        val value = getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_popup_padding, 0)
        padding.apply {
          top = value
          end = value
          bottom = value
          start = value
        }
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_padding)) {
        _arrowPadding =
          getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_arrow_padding, _arrowPadding)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_tint)) {
        _arrowTint =
          getColor(R.styleable.PowerSpinnerView_spinner_arrow_tint, _arrowTint)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_animate)) {
        arrowAnimate =
          getBoolean(R.styleable.PowerSpinnerView_spinner_arrow_animate, arrowAnimate)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_arrow_animate_duration)) {
        arrowAnimationDuration =
          getInteger(
            R.styleable.PowerSpinnerView_spinner_arrow_animate_duration,
            arrowAnimationDuration.toInt()
          ).toLong()
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_divider_show)) {
        _showDivider =
          getBoolean(R.styleable.PowerSpinnerView_spinner_divider_show, _showDivider)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_divider_size)) {
        _dividerSize =
          getDimensionPixelSize(R.styleable.PowerSpinnerView_spinner_divider_size, _dividerSize)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_divider_color)) {
        _dividerColor =
          getColor(R.styleable.PowerSpinnerView_spinner_divider_color, _dividerColor)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_background)) {
        _spinnerPopupBackground =
          getDrawable(
            R.styleable.PowerSpinnerView_spinner_popup_background
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_animation)) {
        spinnerPopupAnimation = when (
          getInteger(
            R.styleable.PowerSpinnerView_spinner_popup_animation,
            spinnerPopupAnimation.value
          )
        ) {
          SpinnerAnimation.DROPDOWN.value -> SpinnerAnimation.DROPDOWN
          SpinnerAnimation.FADE.value -> SpinnerAnimation.FADE
          SpinnerAnimation.BOUNCE.value -> SpinnerAnimation.BOUNCE
          SpinnerAnimation.NORMAL.value -> SpinnerAnimation.NORMAL
          else -> throw IllegalArgumentException("unknown argument: spinner_popup_animation")
        }
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_animation_style)) {
        spinnerPopupAnimationStyle =
          getResourceId(
            R.styleable.PowerSpinnerView_spinner_popup_animation_style,
            spinnerPopupAnimationStyle
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_width)) {
        spinnerPopupWidth =
          getDimensionPixelSize(
            R.styleable.PowerSpinnerView_spinner_popup_width,
            spinnerPopupWidth
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_height)) {
        spinnerPopupHeight =
          getDimensionPixelSize(
            R.styleable.PowerSpinnerView_spinner_popup_height,
            spinnerPopupHeight
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_max_height)) {
        spinnerPopupMaxHeight =
          getDimensionPixelSize(
            R.styleable.PowerSpinnerView_spinner_popup_max_height,
            spinnerPopupMaxHeight
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_item_height)) {
        spinnerItemHeight =
          getDimensionPixelSize(
            R.styleable.PowerSpinnerView_spinner_item_height,
            spinnerItemHeight
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_selected_item_background)) {
        spinnerSelectedItemBackground = getDrawable(R.styleable.PowerSpinnerView_spinner_selected_item_background)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_elevation)) {
        _spinnerPopupElevation =
          getDimensionPixelSize(
            R.styleable.PowerSpinnerView_spinner_popup_elevation,
            _spinnerPopupElevation
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_item_array)) {
        val itemArray =
          getResourceId(R.styleable.PowerSpinnerView_spinner_item_array, NO_INT_VALUE)
        if (itemArray != NO_INT_VALUE) {
          setItems(itemArray)
        }
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_dismiss_notified_select)) {
        dismissWhenNotifiedItemSelected =
          getBoolean(
            R.styleable.PowerSpinnerView_spinner_dismiss_notified_select,
            dismissWhenNotifiedItemSelected
          )
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_debounce_duration)) {
        debounceDuration =
          getInteger(
            R.styleable.PowerSpinnerView_spinner_debounce_duration,
            debounceDuration.toInt()
          )
            .toLong()
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_preference_name)) {
        preferenceName =
          getString(R.styleable.PowerSpinnerView_spinner_preference_name)
      }

      if (hasValue(R.styleable.PowerSpinnerView_spinner_popup_focusable)) {
        setIsFocusable(getBoolean(R.styleable.PowerSpinnerView_spinner_popup_focusable, false))
      }
    }
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
        setOnDismissListener { onSpinnerDismissListener?.onDismiss() }
        setTouchInterceptor(
          object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View, event: MotionEvent): Boolean {
              if (event.action == MotionEvent.ACTION_OUTSIDE) {
                spinnerOutsideTouchListener?.onSpinnerOutsideTouch(view, event)
                return true
              }
              return false
            }
          }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          elevation = spinnerPopupElevation.toFloat()
        }
      }
      binding.body.apply {
        background = if (this@PowerSpinnerView.spinnerPopupBackground == null) {
          this@PowerSpinnerView.background
        } else {
          this@PowerSpinnerView.spinnerPopupBackground
        }
        setPaddingRelative(padding.start, padding.top, padding.end, padding.bottom)
        if (this@PowerSpinnerView.showDivider) {
          val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
          val shape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setSize(width, dividerSize)
            setColor(dividerColor)
          }
          decoration.setDrawable(shape)
          getSpinnerRecyclerView().addItemDecoration(decoration)
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
    this.arrowSize?.let {
      arrowDrawable = arrowDrawable?.resize(context, it)
    }
    updateCompoundDrawable(this.arrowDrawable)
  }

  private fun updateCompoundDrawable(drawable: Drawable?) {
    if (this.showArrow) {
      drawable?.let {
        val wrappedDrawable = DrawableCompat.wrap(it).mutate()
        wrappedDrawable.invalidateSelf()
        if (arrowTint != NO_INT_VALUE) {
          DrawableCompat.setTint(wrappedDrawable, arrowTint)
        }
      }
      when (this.arrowGravity) {
        SpinnerGravity.START -> setCompoundDrawablesRelativeWithIntrinsicBounds(
          drawable,
          null,
          null,
          null
        )
        SpinnerGravity.TOP -> setCompoundDrawablesRelativeWithIntrinsicBounds(
          null,
          drawable,
          null,
          null
        )
        SpinnerGravity.END -> setCompoundDrawablesRelativeWithIntrinsicBounds(
          null,
          null,
          drawable,
          null
        )
        SpinnerGravity.BOTTOM -> setCompoundDrawablesRelativeWithIntrinsicBounds(
          null,
          null,
          null,
          drawable
        )
      }
    } else {
      setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }
  }

  private fun updateSpinnerPersistence() {
    if (adapter.getItemCount() > 0) {
      this.preferenceName.whatIfNotNullOrEmpty {
        if (PowerSpinnerPersistence.getInstance(context)
          .getSelectedIndex(it) != NO_SELECTED_INDEX
        ) {
          this.adapter.notifyItemSelected(
            PowerSpinnerPersistence.getInstance(context).getSelectedIndex(it)
          )
        }
      }
    }
  }

  private fun applyWindowAnimation() {
    if (this.spinnerPopupAnimationStyle == NO_INT_VALUE) {
      when (this.spinnerPopupAnimation) {
        SpinnerAnimation.DROPDOWN ->
          this.spinnerWindow.animationStyle = R.style.PowerSpinner_DropDown
        SpinnerAnimation.FADE -> this.spinnerWindow.animationStyle = R.style.PowerSpinner_Fade
        SpinnerAnimation.BOUNCE -> this.spinnerWindow.animationStyle = R.style.PowerSpinner_Elastic
        else -> Unit
      }
    } else {
      this.spinnerWindow.animationStyle = this.spinnerPopupAnimationStyle
    }
  }

  /** gets the spinner popup's recyclerView. */
  public fun getSpinnerRecyclerView(): RecyclerView = binding.recyclerView

  /** calculates the height size of the popup window. */
  internal fun calculateSpinnerHeight(): Int {
    val itemSize = getSpinnerAdapter<Any>().getItemCount()
    val layoutManager = getSpinnerRecyclerView().layoutManager
    if (layoutManager is GridLayoutManager) {
      return itemSize * (spinnerItemHeight + dividerSize) / layoutManager.spanCount
    }
    return itemSize * (spinnerItemHeight + dividerSize)
  }

  /** gets the spinner popup's body. */
  public fun getSpinnerBodyView(): FrameLayout = binding.body

  /**
   * Sets an item list for setting items of the adapter.
   *
   * @param itemList An item list for the [RecyclerView.Adapter] which extends the [PowerSpinnerInterface].
   */
  @Suppress("UNCHECKED_CAST")
  public fun <T> setItems(itemList: List<T>) {
    (adapter as PowerSpinnerInterface<T>).setItems(itemList)
  }

  /**
   * Sets a string array resource for setting items of the adapter.
   * This function only works for the [DefaultSpinnerAdapter].
   *
   * @param resource A resource of the string array.
   */
  public fun setItems(@ArrayRes resource: Int) {
    if (adapter is DefaultSpinnerAdapter) {
      setItems(context.resources.getStringArray(resource).toList())
    }
  }

  /** sets an adapter of the [PowerSpinnerView]. */
  public fun <T> setSpinnerAdapter(powerSpinnerInterface: PowerSpinnerInterface<T>) {
    adapter = powerSpinnerInterface
    if (adapter is RecyclerView.Adapter<*>) {
      getSpinnerRecyclerView().adapter = adapter as RecyclerView.Adapter<*>
    }
  }

  /** gets an adapter of the [PowerSpinnerView]. */
  @Suppress("UNCHECKED_CAST")
  public fun <T> getSpinnerAdapter(): PowerSpinnerInterface<T> {
    return adapter as PowerSpinnerInterface<T>
  }

  /** sets a [OnSpinnerItemSelectedListener] to the default adapter. */
  @Suppress("UNCHECKED_CAST")
  public fun <T> setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<T>) {
    val adapter = adapter as PowerSpinnerInterface<T>
    adapter.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener
  }

  /** sets a [OnSpinnerItemSelectedListener] to the popup using lambda. */
  @Suppress("UNCHECKED_CAST")
  @JvmSynthetic
  public fun <T> setOnSpinnerItemSelectedListener(block: (oldIndex: Int, oldItem: T?, newIndex: Int, newItem: T) -> Unit) {
    val adapter = adapter as PowerSpinnerInterface<T>
    adapter.onSpinnerItemSelectedListener =
      OnSpinnerItemSelectedListener { oldIndex, oldItem, newIndex, newItem ->
        block(oldIndex, oldItem, newIndex, newItem)
      }
  }

  /** sets a [OnSpinnerOutsideTouchListener] to the popup using lambda. */
  @JvmSynthetic
  public fun setOnSpinnerOutsideTouchListener(block: (View, MotionEvent) -> Unit) {
    this.spinnerOutsideTouchListener =
      OnSpinnerOutsideTouchListener { view, event -> block(view, event) }
  }

  /** sets a [OnSpinnerDismissListener] to the popup using lambda. */
  @JvmSynthetic
  public fun setOnSpinnerDismissListener(block: () -> Unit) {
    this.onSpinnerDismissListener = OnSpinnerDismissListener {
      block()
    }
  }

  /** shows the spinner popup menu to the center. */
  @MainThread
  @JvmOverloads
  public fun show(xOff: Int = 0, yOff: Int = 0) {
    debounceShowOrDismiss {
      if (!isShowing) {
        isShowing = true
        animateArrow(true)
        applyWindowAnimation()
        spinnerWindow.width = getSpinnerWidth()
        if (getSpinnerHeight() != 0) {
          spinnerWindow.height = getSpinnerHeight()
        }
        spinnerWindow.showAsDropDown(this, xOff, yOff)
        post {
          spinnerWindow.update(getSpinnerWidth(), getSpinnerHeight())
        }
      }
    }
  }

  private fun getSpinnerWidth(): Int {
    return if (spinnerPopupWidth != NO_INT_VALUE) {
      spinnerPopupWidth
    } else {
      width
    }
  }

  /** Returns the window height size of the spinner. */
  public fun getSpinnerHeight(): Int {
    val height = when {
      spinnerPopupHeight != NO_INT_VALUE -> spinnerPopupHeight
      spinnerItemHeight != NO_INT_VALUE -> calculateSpinnerHeight()
      else -> getSpinnerRecyclerView().height
    }
    return when {
      spinnerPopupMaxHeight == NO_INT_VALUE -> height
      spinnerPopupMaxHeight > height -> height
      else -> spinnerPopupMaxHeight
    }
  }

  /** dismiss the spinner popup menu. */
  @MainThread
  public fun dismiss() {
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
  @JvmOverloads
  public fun showOrDismiss(xOff: Int = 0, yOff: Int = 0) {
    val adapter = getSpinnerRecyclerView().adapter ?: return
    if (!isShowing && adapter.itemCount > 0) {
      show(xOff, yOff)
    } else {
      dismiss()
    }
  }

  /** Disable changing text automatically when an item selection notified. */
  public fun setDisableChangeTextWhenNotified(value: Boolean) {
    this.disableChangeTextWhenNotified = value
  }

  /**
   * sets isFocusable of the spinner popup.
   * The spinner popup will be got a focus and [onSpinnerDismissListener] will be replaced.
   */
  public fun setIsFocusable(isFocusable: Boolean) {
    this.spinnerWindow.isFocusable = isFocusable
    this.onSpinnerDismissListener = OnSpinnerDismissListener { dismiss() }
  }

  /** debounce for showing or dismissing spinner popup. */
  private fun debounceShowOrDismiss(action: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - previousDebounceTime > debounceDuration) {
      this.previousDebounceTime = currentTime
      action()
    }
  }

  /**
   * Select an item by index.
   * This method must be invoked after adapter items are set using the [setItems].
   *
   * @param index An index should be selected.
   */
  public fun selectItemByIndex(index: Int) {
    this.adapter.notifyItemSelected(index)
  }

  /** notifies to [PowerSpinnerView] of changed information from [PowerSpinnerInterface]. */
  public fun notifyItemSelected(index: Int, changedText: CharSequence) {
    this.selectedIndex = index
    if (!disableChangeTextWhenNotified) {
      this.text = changedText
    }
    if (this.dismissWhenNotifiedItemSelected) {
      dismiss()
    }
    this.preferenceName.whatIfNotNullOrEmpty {
      PowerSpinnerPersistence.getInstance(context).persistSelectedIndex(it, this.selectedIndex)
    }
  }

  /** clears a selected item. */
  public fun clearSelectedItem() {
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
  override fun onDestroy(owner: LifecycleOwner) {
    super.onDestroy(owner)
    dismiss()
    lifecycleOwner?.lifecycle?.removeObserver(this@PowerSpinnerView)
  }

  /** Builder class for creating [PowerSpinnerView]. */
  @PowerSpinnerDsl
  public class Builder(context: Context) {
    private val powerSpinnerView = PowerSpinnerView(context)

    public fun setArrowAnimate(value: Boolean): Builder =
      apply { this.powerSpinnerView.arrowAnimate = value }

    public fun setArrowAnimationDuration(value: Long): Builder = apply {
      this.powerSpinnerView.arrowAnimationDuration = value
    }

    public fun setArrowDrawableResource(@DrawableRes value: Int): Builder = apply {
      this.powerSpinnerView.arrowResource = value
    }

    public fun setShowArrow(value: Boolean): Builder =
      apply { this.powerSpinnerView.showArrow = value }

    public fun setArrowGravity(value: SpinnerGravity): Builder = apply {
      this.powerSpinnerView.arrowGravity = value
    }

    public fun setArrowPadding(@Px value: Int): Builder =
      apply { this.powerSpinnerView.arrowPadding = value }

    public fun setArrowTint(@ColorInt value: Int): Builder =
      apply { this.powerSpinnerView.arrowTint = value }

    public fun setShowDivider(value: Boolean): Builder =
      apply { this.powerSpinnerView.showDivider = value }

    public fun setDividerSize(@Px value: Int): Builder =
      apply { this.powerSpinnerView.dividerSize = value }

    public fun setDividerColor(@ColorInt value: Int): Builder =
      apply { this.powerSpinnerView.dividerColor = value }

    public fun setSpinnerPopupBackground(value: Drawable): Builder = apply {
      this.powerSpinnerView.spinnerPopupBackground = value
    }

    public fun setSpinnerPopupBackgroundResource(@DrawableRes value: Int): Builder = apply {
      this.powerSpinnerView.setBackgroundResource(value)
    }

    public fun setDismissWhenNotifiedItemSelected(value: Boolean): Builder = apply {
      this.powerSpinnerView.dismissWhenNotifiedItemSelected = value
    }

    @Suppress("UNCHECKED_CAST")
    public fun <T> setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<T>): Builder =
      apply {
        val adapter: PowerSpinnerInterface<T> =
          this.powerSpinnerView.adapter as PowerSpinnerInterface<T>
        adapter.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener
      }

    @Suppress("UNCHECKED_CAST")
    @JvmSynthetic
    public fun <T> setOnSpinnerItemSelectedListener(block: (oldIndex: Int, oldItem: T?, newIndex: Int, newItem: T) -> Unit): Builder =
      apply {
        val adapter: PowerSpinnerInterface<T> =
          this.powerSpinnerView.adapter as PowerSpinnerInterface<T>
        adapter.onSpinnerItemSelectedListener =
          OnSpinnerItemSelectedListener { oldIndex, oldItem, newIndex, newItem ->
            block(oldIndex, oldItem, newIndex, newItem)
          }
      }

    public fun setOnSpinnerOutsideTouchListener(value: OnSpinnerOutsideTouchListener): Builder =
      apply {
        this.powerSpinnerView.spinnerOutsideTouchListener = value
      }

    @JvmSynthetic
    public fun setOnSpinnerOutsideTouchListener(unit: (View, MotionEvent) -> Unit): Builder =
      apply {
        this.powerSpinnerView.spinnerOutsideTouchListener =
          OnSpinnerOutsideTouchListener { view, event -> unit(view, event) }
      }

    public fun setOnSpinnerDismissListener(value: OnSpinnerDismissListener): Builder = apply {
      this.powerSpinnerView.onSpinnerDismissListener = value
    }

    @JvmSynthetic
    public fun setOnSpinnerDismissListener(block: () -> Unit): Builder = apply {
      this.powerSpinnerView.onSpinnerDismissListener = OnSpinnerDismissListener {
        block()
      }
    }

    public fun setDisableChangeTextWhenNotified(value: Boolean): Builder = apply {
      this.powerSpinnerView.disableChangeTextWhenNotified = value
    }

    public fun setSpinnerPopupAnimation(value: SpinnerAnimation): Builder = apply {
      this.powerSpinnerView.spinnerPopupAnimation = value
    }

    public fun setSpinnerPopupAnimationStyle(@StyleRes value: Int): Builder = apply {
      this.powerSpinnerView.spinnerPopupAnimationStyle = value
    }

    public fun setSpinnerPopupWidth(@Px value: Int): Builder = apply {
      this.powerSpinnerView.spinnerPopupWidth = value
    }

    public fun setSpinnerPopupHeight(@Px value: Int): Builder = apply {
      this.powerSpinnerView.spinnerPopupHeight = value
    }

    public fun setSpinnerPopupMaxHeight(@Px value: Int): Builder = apply {
      this.powerSpinnerView.spinnerPopupMaxHeight = value
    }

    public fun setSpinnerItemHeight(@Px value: Int): Builder = apply {
      this.powerSpinnerView.spinnerItemHeight = value
    }

    public fun setSpinnerSelectedItemBackground(value: Drawable): Builder = apply {
      this.powerSpinnerView.spinnerSelectedItemBackground = value
    }

    public fun setPreferenceName(value: String): Builder = apply {
      this.powerSpinnerView.preferenceName = value
    }

    public fun setLifecycleOwner(value: LifecycleOwner): Builder = apply {
      this.powerSpinnerView.lifecycleOwner = value
    }

    public fun build(): PowerSpinnerView = this.powerSpinnerView
  }
}
