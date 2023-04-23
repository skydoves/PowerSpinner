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

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.powerspinner.databinding.PowerspinnerItemDefaultPowerBinding
import com.skydoves.powerspinner.internals.NO_INT_VALUE
import com.skydoves.powerspinner.internals.NO_SELECTED_INDEX

/** DefaultSpinnerAdapter is a default adapter composed of string items. */
public class DefaultSpinnerAdapter(
  powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<DefaultSpinnerAdapter.DefaultSpinnerViewHolder>(),
  PowerSpinnerInterface<CharSequence> {

  override var index: Int = powerSpinnerView.selectedIndex
  override val spinnerView: PowerSpinnerView = powerSpinnerView
  override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<CharSequence>? = null

  private val spinnerItems: MutableList<CharSequence> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultSpinnerViewHolder {
    val binding =
      PowerspinnerItemDefaultPowerBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    return DefaultSpinnerViewHolder(binding).apply {
      binding.root.setOnClickListener {
        val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
          ?: return@setOnClickListener
        notifyItemSelected(position)
      }
    }
  }

  override fun onBindViewHolder(holder: DefaultSpinnerViewHolder, position: Int) {
    holder.bind(spinnerView, spinnerItems[position], index == position)
  }

  override fun setItems(itemList: List<CharSequence>) {
    this.spinnerItems.clear()
    this.spinnerItems.addAll(itemList)
    this.index = NO_SELECTED_INDEX
    notifyDataSetChanged()
  }

  override fun notifyItemSelected(index: Int) {
    if (index == NO_SELECTED_INDEX) return
    val oldIndex = this.index
    this.index = index
    this.spinnerView.notifyItemSelected(index, spinnerItems[index])
    notifyDataSetChanged()
    this.onSpinnerItemSelectedListener?.onItemSelected(
      oldIndex = oldIndex,
      oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { spinnerItems[oldIndex] },
      newIndex = index,
      newItem = spinnerItems[index]
    )
  }

  override fun getItemCount(): Int = spinnerItems.size

  public class DefaultSpinnerViewHolder(private val binding: PowerspinnerItemDefaultPowerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    internal fun bind(spinnerView: PowerSpinnerView, item: CharSequence, isSelectedItem: Boolean) {
      binding.itemDefaultText.apply {
        text = item
        typeface = spinnerView.typeface
        gravity = spinnerView.gravity
        setTextSize(TypedValue.COMPLEX_UNIT_PX, spinnerView.textSize)
        setTextColor(spinnerView.currentTextColor)
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
      if (spinnerView.spinnerSelectedItemBackground != null && isSelectedItem) {
        binding.root.background = spinnerView.spinnerSelectedItemBackground
      } else {
        binding.root.background = null
      }
    }
  }
}
