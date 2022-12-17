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
import android.view.View
import android.view.ViewGroup
import com.skydoves.powerspinner.internals.PowerSpinnerDsl

/** creates an instance of [PowerSpinnerView] by [PowerSpinnerView.Builder] using kotlin dsl. */
@JvmSynthetic
@PowerSpinnerDsl
public inline fun createPowerSpinnerView(
  context: Context,
  builder: PowerSpinnerView.Builder.() -> Unit
): PowerSpinnerView =
  PowerSpinnerView.Builder(context).apply(builder).build()

/**
 * Extension function from [View] class (@NULLABLE).
 * Should be used in cases when the [PowerSpinnerView.dismiss] does not seem to work as expected.
 *
 * Iterates all the Parent view's children, If a power spinner can be found
 * Then dismiss it.
 *
 * Usage -> parentView?.dismissPowerSpinner()
 */
public fun View?.dismissPowerSpinner() {
  this?.getAllChildren()?.forEach {
    if (it is PowerSpinnerView) {
      it.dismiss()
      return
    }
  }
}

// Returns all the View's children view
private fun View.getAllChildren(): List<View> {
  val viewList = ArrayList<View>()
  if (this !is ViewGroup) {
    // Has no children
    viewList.add(this)
  } else {
    // Iterate all the view children, with recursion
    for (index in 0 until this.childCount) {
      val child = this.getChildAt(index)
      viewList.addAll(child.getAllChildren())
    }
  }
  return viewList
}
