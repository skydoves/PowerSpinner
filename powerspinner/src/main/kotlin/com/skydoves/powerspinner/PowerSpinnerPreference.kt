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

@file:Suppress("unused")

package com.skydoves.powerspinner

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.skydoves.powerspinner.internals.dp2Px

/** PowerSpinnerPreference is a Preference for showing [PowerSpinnerView] in the preferences inflation from XML. */
public class PowerSpinnerPreference @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null,
  defStyle: Int = androidx.preference.R.attr.preferenceStyle
) : Preference(context, attributeSet, defStyle) {

  public val powerSpinnerView: PowerSpinnerView = PowerSpinnerView(context)
  private var defaultValue: Int = 0

  init {
    this.layoutResource = R.layout.powerspinner_layout_preference

    when {
      attributeSet != null && defStyle != androidx.preference.R.attr.preferenceStyle ->
        getAttrs(attributeSet, defStyle)
      attributeSet != null -> getAttrs(attributeSet)
    }
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
    this.powerSpinnerView.showArrow =
      a.getBoolean(
        R.styleable.PowerSpinnerView_spinner_arrow_show,
        this.powerSpinnerView.showArrow
      )
    when (
      a.getInteger(
        R.styleable.PowerSpinnerView_spinner_arrow_gravity,
        this.powerSpinnerView.arrowGravity.value
      )
    ) {
      SpinnerGravity.START.value -> this.powerSpinnerView.arrowGravity = SpinnerGravity.START
      SpinnerGravity.TOP.value -> this.powerSpinnerView.arrowGravity = SpinnerGravity.TOP
      SpinnerGravity.END.value -> this.powerSpinnerView.arrowGravity = SpinnerGravity.END
      SpinnerGravity.BOTTOM.value ->
        this.powerSpinnerView.arrowGravity =
          SpinnerGravity.BOTTOM
    }
    this.powerSpinnerView.arrowPadding =
      a.getDimensionPixelSize(
        R.styleable.PowerSpinnerView_spinner_arrow_padding,
        this.powerSpinnerView.arrowPadding
      )
    this.powerSpinnerView.arrowAnimate =
      a.getBoolean(
        R.styleable.PowerSpinnerView_spinner_arrow_animate,
        this.powerSpinnerView.arrowAnimate
      )
    this.powerSpinnerView.arrowAnimationDuration =
      a.getInteger(
        R.styleable.PowerSpinnerView_spinner_arrow_animate_duration,
        this.powerSpinnerView.arrowAnimationDuration.toInt()
      ).toLong()
    this.powerSpinnerView.showDivider =
      a.getBoolean(
        R.styleable.PowerSpinnerView_spinner_divider_show,
        this.powerSpinnerView.showDivider
      )
    this.powerSpinnerView.dividerSize =
      a.getDimensionPixelSize(
        R.styleable.PowerSpinnerView_spinner_divider_size,
        this.powerSpinnerView.dividerSize
      )
    this.powerSpinnerView.dividerColor =
      a.getColor(
        R.styleable.PowerSpinnerView_spinner_divider_color,
        this.powerSpinnerView.dividerColor
      )
    this.powerSpinnerView.spinnerPopupBackground =
      a.getDrawable(
        R.styleable.PowerSpinnerView_spinner_popup_background
      )
    when (
      a.getInteger(
        R.styleable.PowerSpinnerView_spinner_popup_animation,
        this.powerSpinnerView.spinnerPopupAnimation.value
      )
    ) {
      SpinnerAnimation.DROPDOWN.value ->
        this.powerSpinnerView.spinnerPopupAnimation =
          SpinnerAnimation.DROPDOWN
      SpinnerAnimation.FADE.value ->
        this.powerSpinnerView.spinnerPopupAnimation =
          SpinnerAnimation.FADE
      SpinnerAnimation.BOUNCE.value ->
        this.powerSpinnerView.spinnerPopupAnimation =
          SpinnerAnimation.BOUNCE
    }
    this.powerSpinnerView.spinnerPopupAnimationStyle =
      a.getResourceId(
        R.styleable.PowerSpinnerView_spinner_popup_animation_style,
        this.powerSpinnerView.spinnerPopupAnimationStyle
      )
    this.powerSpinnerView.spinnerPopupWidth =
      a.getDimensionPixelSize(
        R.styleable.PowerSpinnerView_spinner_popup_width,
        this.powerSpinnerView.spinnerPopupWidth
      )
    this.powerSpinnerView.spinnerPopupHeight =
      a.getDimensionPixelSize(
        R.styleable.PowerSpinnerView_spinner_popup_height,
        this.powerSpinnerView.spinnerPopupHeight
      )
    this.powerSpinnerView.spinnerPopupElevation =
      a.getDimensionPixelSize(
        R.styleable.PowerSpinnerView_spinner_popup_elevation,
        this.powerSpinnerView.spinnerPopupElevation
      )
    val itemArray = a.getResourceId(R.styleable.PowerSpinnerView_spinner_item_array, -1)
    if (itemArray != -1) {
      this.powerSpinnerView.setItems(itemArray)
    }
    this.powerSpinnerView.dismissWhenNotifiedItemSelected =
      a.getBoolean(
        R.styleable.PowerSpinnerView_spinner_dismiss_notified_select,
        this.powerSpinnerView.dismissWhenNotifiedItemSelected
      )
  }

  override fun onBindViewHolder(holder: PreferenceViewHolder) {
    this.powerSpinnerView.apply {
      selectItemByIndex(getPersistedInt(defaultValue))
      if (getSpinnerAdapter<Any>().onSpinnerItemSelectedListener == null) {
        setOnSpinnerItemSelectedListener<Any> { _, _, newIndex, _ ->
          persistInt(newIndex)
        }
      }
    }
    val preference = holder.findViewById(R.id.powerSpinner_preference) as ViewGroup
    preference.addView(
      this.powerSpinnerView,
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.WRAP_CONTENT
    )
    val titleTextView = holder.findViewById(R.id.preference_title) as TextView
    titleTextView.text = title
    val titleParams = titleTextView.layoutParams as ViewGroup.MarginLayoutParams
    this.powerSpinnerView.setPadding(
      titleParams.marginStart,
      context.dp2Px(10),
      titleParams.marginEnd,
      context.dp2Px(10)
    )
  }

  /** sets a [OnSpinnerItemSelectedListener] to the default adapter. */
  public fun <T> setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<T>) {
    this.powerSpinnerView.setOnSpinnerItemSelectedListener<T> { oldIndex, oldItem, newIndex, newItem ->
      onSpinnerItemSelectedListener.onItemSelected(oldIndex, oldItem, newIndex, newItem)
      persistInt(newIndex)
    }
  }

  /** sets a [OnSpinnerItemSelectedListener] to the popup using lambda. */
  @JvmSynthetic
  public fun <T> setOnSpinnerItemSelectedListener(block: (oldIndex: Int, oldItem: T?, newIndex: Int, newItem: T) -> Unit) {
    this.powerSpinnerView.setOnSpinnerItemSelectedListener<T> { oldIndex, oldItem, newIndex, newItem ->
      block(oldIndex, oldItem, newIndex, newItem)
      persistInt(newIndex)
    }
  }

  override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
    return a.getInt(index, 0)
  }

  override fun onSetInitialValue(defaultValue: Any?) {
    super.onSetInitialValue(defaultValue)
    if (defaultValue is Int) {
      this.defaultValue = defaultValue
    }
  }
}
