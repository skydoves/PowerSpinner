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

package com.skydoves.powerspinner.internals

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.skydoves.powerspinner.SpinnerSizeSpec

/** resize a drawable width and height size using specific pixel sizes.  */
@JvmSynthetic
internal fun Drawable.resize(context: Context, size: SpinnerSizeSpec): Drawable {
  val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  setBounds(0, 0, size.width, size.height)
  draw(canvas)
  return BitmapDrawable(context.resources, bitmap)
}
