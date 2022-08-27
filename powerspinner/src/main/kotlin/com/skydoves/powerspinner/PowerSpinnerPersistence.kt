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
import android.content.SharedPreferences
import com.skydoves.powerspinner.internals.NO_SELECTED_INDEX

/** PowerSpinnerPreferences helps to persist selected item status. */
internal class PowerSpinnerPersistence private constructor() {

  /** gets selected index from the preference. */
  fun getSelectedIndex(name: String): Int = sharedPreferenceManager.getInt(
    INDEX + name,
    NO_SELECTED_INDEX
  )

  /** puts selected index from the preference. */
  fun persistSelectedIndex(name: String, index: Int) =
    sharedPreferenceManager.edit().putInt(INDEX + name, index).apply()

  /** removes a selected index data from the preference. */
  fun removePersistedData(name: String) =
    sharedPreferenceManager.edit().remove(name).apply()

  /** clears all of [PowerSpinnerView] on the application. */
  fun clearAllPersistedData() = sharedPreferenceManager.edit().clear().apply()

  companion object {
    @Volatile
    private var instance: PowerSpinnerPersistence? = null
    private lateinit var sharedPreferenceManager: SharedPreferences
    private const val INDEX = "INDEX"

    @JvmStatic
    fun getInstance(context: Context): PowerSpinnerPersistence =
      instance ?: synchronized(this) {
        instance ?: PowerSpinnerPersistence().also {
          instance = it
          sharedPreferenceManager =
            context.getSharedPreferences("com.skydoves.powerspinenr", Context.MODE_PRIVATE)
        }
      }
  }
}
