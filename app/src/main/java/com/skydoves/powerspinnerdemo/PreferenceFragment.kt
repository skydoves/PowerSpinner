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

package com.skydoves.powerspinnerdemo

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.PowerSpinnerPreference

class PreferenceFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    addPreferencesFromResource(R.xml.settings)
    setHasOptionsMenu(true)

    val countySpinnerPreference = findPreference<PowerSpinnerPreference>("country")
    countySpinnerPreference?.let {
      it.powerSpinnerView.apply {
        setSpinnerAdapter(IconSpinnerAdapter(this))
        setItems(
          arrayListOf(
            IconSpinnerItem(icon = contextDrawable(R.drawable.unitedstates), text = "USA"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.unitedkingdom), text = "UK"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.france), text = "France"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.canada), text = "Canada"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.southkorea), text = "South Korea"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.germany), text = "Germany"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.spain), text = "Spain"),
            IconSpinnerItem(icon = contextDrawable(R.drawable.china), text = "China")
          )
        )
        getSpinnerRecyclerView().layoutManager = GridLayoutManager(requireContext(), 2)
      }
      it.setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
        Toast.makeText(requireContext(), item.text, Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun contextDrawable(@DrawableRes resource: Int): Drawable? {
    return ContextCompat.getDrawable(requireContext(), resource)
  }
}
