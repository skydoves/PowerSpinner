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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_default.view.item_default_text

/** IconSpinnerAdapter is a custom adapter composed of [IconSpinnerItem] items. */
class IconSpinnerAdapter(
  powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<IconSpinnerAdapter.IconSpinnerViewHolder>(),
  PowerSpinnerInterface<IconSpinnerItem> {

  override val spinnerView: PowerSpinnerView = powerSpinnerView
  override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<IconSpinnerItem>? = null

  private val compoundPadding: Int = 12
  private val spinnerItems: MutableList<IconSpinnerItem> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconSpinnerViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    return IconSpinnerViewHolder(layoutInflater.inflate(R.layout.item_default, parent, false))
  }

  init {
    this.spinnerView.compoundDrawablePadding = compoundPadding
  }

  override fun onBindViewHolder(holder: IconSpinnerViewHolder, position: Int) {
    val item = this.spinnerItems[position]
    holder.itemView.apply {
      item_default_text.apply {
        text = item.text
        typeface = spinnerView.typeface
        gravity = spinnerView.gravity
        setTextSize(TypedValue.COMPLEX_UNIT_PX, spinnerView.textSize)
        setTextColor(spinnerView.currentTextColor)
        compoundDrawablePadding = compoundPadding
        setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null, null)
      }
      setPadding(spinnerView.paddingLeft, spinnerView.paddingTop, spinnerView.paddingRight,
        spinnerView.paddingBottom)
      setOnClickListener { notifyItemSelected(position) }
    }
  }

  override fun setItems(itemList: List<IconSpinnerItem>) {
    this.spinnerItems.clear()
    this.spinnerItems.addAll(itemList)
    notifyDataSetChanged()
  }

  override fun notifyItemSelected(index: Int) {
    this.spinnerView.setCompoundDrawablesWithIntrinsicBounds(this.spinnerItems[index].icon, null,
      spinnerView.arrowDrawable, null)
    this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].text)
    this.onSpinnerItemSelectedListener?.onItemSelected(index, this.spinnerItems[index])
  }

  override fun getItemCount() = this.spinnerItems.size

  class IconSpinnerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
