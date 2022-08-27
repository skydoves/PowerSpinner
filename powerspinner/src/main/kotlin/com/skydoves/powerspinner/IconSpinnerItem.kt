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

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px

/**
 * IconSpinnerItem is an item class for constructing [IconSpinnerAdapter].
 *
 * @param text A text for constructing the spinner item.
 * @param icon An icon of the spinner item.
 * @param iconRes An icon resource of the spinner item.
 * @param iconPadding A padding between icon and text.
 * @param iconGravity A gravity of the icon.
 * @param textStyle A typeface for the text.
 * @param gravity A gravity of the text.
 * @param textSize A size of the text.
 * @param textColor A color of the text.
 */
public data class IconSpinnerItem @JvmOverloads constructor(
  val text: CharSequence,
  val icon: Drawable? = null,
  @DrawableRes val iconRes: Int? = null,
  @Px val iconPadding: Int? = null,
  val iconGravity: Int = Gravity.START,
  val textStyle: Int = Typeface.NORMAL,
  val textTypeface: Typeface? = null,
  val gravity: Int? = null,
  val textSize: Float? = null,
  @ColorInt val textColor: Int? = null
)
