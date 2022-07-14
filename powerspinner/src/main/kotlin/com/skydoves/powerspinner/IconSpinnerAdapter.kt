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
        LayoutInflater.from(parent.context), parent,
        false
      )
    return IconSpinnerViewHolder(binding).apply {
      binding.root.setOnClickListener {
        val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
          ?: return@setOnClickListener
        if (position == NO_SELECTED_INDEX) return@setOnClickListener
        val oldIndex = index
        val selected = onSpinnerItemSelectedListener?.onItemSelected(
          oldIndex = oldIndex,
          oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { spinnerItems[oldIndex] },
          newIndex = position,
          newItem = spinnerItems[position]
        )
        if (true == selected) {
          notifyItemSelected(position)
        }
      }
    }
  }

  override fun onBindViewHolder(holder: IconSpinnerViewHolder, position: Int) {
    holder.bind(spinnerItems[position], spinnerView)
  }

  override fun setItems(itemList: List<IconSpinnerItem>) {
    this.spinnerItems.clear()
    this.spinnerItems.addAll(itemList)
    this.index = NO_SELECTED_INDEX
    notifyDataSetChanged()
  }

  override fun notifyItemSelected(index: Int) {
    val item = spinnerItems[index]
    spinnerView.compoundDrawablePadding = item.iconPadding ?: spinnerView.compoundDrawablePadding
    val icon = item.iconRes?.let {
      ResourcesCompat.getDrawable(spinnerView.resources, it, null)
    } ?: item.icon
    when (item.iconGravity) {
      Gravity.START ->
        spinnerView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
      Gravity.END ->
        spinnerView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
      Gravity.TOP ->
        spinnerView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
      Gravity.BOTTOM ->
        spinnerView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon)
    }
    this.spinnerView.notifyItemSelected(index, item.text)
  }

  override fun getItemCount(): Int = this.spinnerItems.size

  public class IconSpinnerViewHolder(private val binding: PowerspinnerItemDefaultPowerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    internal fun bind(item: IconSpinnerItem, spinnerView: PowerSpinnerView) {
      binding.itemDefaultText.apply {
        text = item.text
        item.textTypeface?.let { typeface = it } ?: setTypeface(typeface, item.textStyle)
        gravity = item.gravity ?: spinnerView.gravity
        setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize ?: spinnerView.textSize)
        setTextColor(item.textColor ?: spinnerView.currentTextColor)
        compoundDrawablePadding = item.iconPadding ?: spinnerView.compoundDrawablePadding
        val icon = item.iconRes?.let {
          ResourcesCompat.getDrawable(spinnerView.resources, it, null)
        } ?: item.icon
        when (item.iconGravity) {
          Gravity.START ->
            setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
          Gravity.END ->
            setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
          Gravity.TOP ->
            setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
          Gravity.BOTTOM ->
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, icon)
        }
      }
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
  }
}
