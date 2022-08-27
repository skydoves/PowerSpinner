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

/**
 * ☔ WhatIf is kotlin extensions for expressing a single if-else statement, nullable and boolean.
 *
 * [WhatIf](https://github.com/skydoves/WhatIf)
 *
 */

/** An expression for invoking [whatIf] when the [String] is not null and not empty. */
@JvmSynthetic
internal inline fun String?.whatIfNotNullOrEmpty(
  whatIf: (String) -> Unit
) {
  this.whatIfNotNullOrEmpty(
    whatIf = whatIf,
    whatIfNot = { }
  )
}

/**
 * An expression for invoking [whatIf] when the [String] is not null and not empty.
 * If the array is null or empty, [whatIfNot] will be invoked instead of the [whatIf].
 */
@JvmSynthetic
internal inline fun String?.whatIfNotNullOrEmpty(
  whatIf: (String) -> Unit,
  whatIfNot: () -> Unit
) {
  if (!this.isNullOrEmpty()) {
    whatIf(this)
  } else {
    whatIfNot()
  }
}
