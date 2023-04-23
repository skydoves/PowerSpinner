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

import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.powerspinner.databinding.PowerspinnerItemDefaultPowerBinding
import com.skydoves.powerspinner.internals.NO_INT_VALUE
import com.skydoves.powerspinner.internals.NO_SELECTED_INDEX

/** IconSpinnerAdapter is a custom adapter composed of [IconSpinnerItem] items. */
public class IconSpinnerAdapter(
  powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<IconSpinnerAdapter.IconSpinnerViewHolder>(),
  PowerSpinnerInterface<IconSpinnerItem> {

  override var index: Int = powerSpinnerView.selectedIndex
  override val spinnerView: PowerSpinnerView = powerSpinnerView
  override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<IconSpinnerItem>? = null

  private val compoundPadding: Int = 12
  private val spinnerItems: MutableList<IconSpinnerItem> = arrayListOf()

  init {
    this.spinnerView.compoundDrawablePadding = compoundPadding
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconSpinnerViewHolder {
    val binding =
      PowerspinnerItemDefaultPowerBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    return IconSpinnerViewHolder(binding).apply {
      binding.root.setOnClickListener {
        val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
          ?: return@setOnClickListener
        notifyItemSelected(position)
      }
    }
  }

  override fun onBindViewHolder(holder: IconSpinnerViewHolder, position: Int) {
    holder.bind(spinnerView, spinnerItems[position], index == position)
  }

  override fun setItems(itemList: List<IconSpinnerItem>) {
    this.spinnerItems.clear()
    this.spinnerItems.addAll(itemList)
    this.index = NO_SELECTED_INDEX
    notifyDataSetChanged()
  }

  override fun notifyItemSelected(index: Int) {
    if (index == NO_SELECTED_INDEX) return
    val item = spinnerItems[index]
    spinnerView.compoundDrawablePadding = item.iconPadding ?: spinnerView.compoundDrawablePadding
    spinnerView.applyCompoundDrawables(spinnerView, item)
    val oldIndex = this.index
    this.index = index
    this.spinnerView.notifyItemSelected(index, item.text)
    notifyDataSetChanged()
    this.onSpinnerItemSelectedListener?.onItemSelected(
      oldIndex = oldIndex,
      oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { spinnerItems[oldIndex] },
      newIndex = index,
      newItem = item
    )
  }

  override fun getItemCount(): Int = this.spinnerItems.size

  public inner class IconSpinnerViewHolder(private val binding: PowerspinnerItemDefaultPowerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    internal fun bind(spinnerView: PowerSpinnerView, item: IconSpinnerItem, isSelectedItem: Boolean) {
      binding.itemDefaultText.apply {
        text = item.text
        item.textTypeface?.let { typeface = it } ?: setTypeface(typeface, item.textStyle)
        gravity = item.gravity ?: spinnerView.gravity
        setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize ?: spinnerView.textSize)
        setTextColor(item.textColor ?: spinnerView.currentTextColor)
        compoundDrawablePadding = item.iconPadding ?: spinnerView.compoundDrawablePadding
        applyCompoundDrawables(spinnerView, item)
        binding.root.setPadding(
          spinnerView.paddingLeft,
          spinnerView.paddingTop,
          spinnerView.paddingRight,
          spinnerView.paddingBottom
        )
        if (spinnerView.spinnerItemHeight != NO_INT_VALUE) {
          binding.root.height = spinnerView.spinnerItemHeight
        }
      }
      if (spinnerView.spinnerSelectedItemBackground != null && isSelectedItem) {
        binding.root.background = spinnerView.spinnerSelectedItemBackground
      } else {
        binding.root.background = null
      }
    }
  }

  @JvmSynthetic
  internal fun AppCompatTextView.applyCompoundDrawables(
    spinnerView: PowerSpinnerView,
    item: IconSpinnerItem
  ) {
    val icon = item.iconRes?.let {
      ResourcesCompat.getDrawable(spinnerView.resources, it, null)
    } ?: item.icon
    val start = compoundDrawablesRelative[0]
    val top = compoundDrawablesRelative[1]
    val end = compoundDrawablesRelative[2]
    val bottom = compoundDrawablesRelative[3]
    when (item.iconGravity) {
      Gravity.START ->
        setCompoundDrawablesRelativeWithIntrinsicBounds(icon, top, end, bottom)
      Gravity.END ->
        setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, icon, bottom)
      Gravity.TOP ->
        setCompoundDrawablesRelativeWithIntrinsicBounds(start, icon, end, bottom)
      Gravity.BOTTOM ->
        setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, icon)
    }
  }
}
