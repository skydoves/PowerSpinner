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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinnerdemo.databinding.ActivityCustomBinding

class CustomActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityCustomBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.spinnerView.apply {
      setSpinnerAdapter(IconSpinnerAdapter(this))
      setItems(
        arrayListOf(
          IconSpinnerItem(iconRes = R.drawable.unitedstates, text = "USA"),
          IconSpinnerItem(iconRes = R.drawable.unitedkingdom, text = "UK"),
          IconSpinnerItem(iconRes = R.drawable.france, text = "France"),
          IconSpinnerItem(icon = contextDrawable(R.drawable.canada), text = "Canada"),
          IconSpinnerItem(icon = contextDrawable(R.drawable.southkorea), text = "South Korea"),
          IconSpinnerItem(icon = contextDrawable(R.drawable.germany), text = "Germany"),
          IconSpinnerItem(icon = contextDrawable(R.drawable.spain), text = "Spain"),
          IconSpinnerItem(icon = contextDrawable(R.drawable.china), text = "China")
        )
      )
      setOnSpinnerItemSelectedListener<IconSpinnerItem> { _, _, _, item ->
        Toast.makeText(applicationContext, item.text, Toast.LENGTH_SHORT).show()
      }
      getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 2)
      selectItemByIndex(4)
      preferenceName = "country"
    }

    binding.spinnerView1.apply {
      setOnSpinnerItemSelectedListener<String> { _, _, _, item ->
        binding.spinnerView2.hint = item
        Toast.makeText(applicationContext, item, Toast.LENGTH_SHORT).show()
      }
      preferenceName = "question1"
    }

    binding.spinnerView2.preferenceName = "question2"

    binding.spinnerView3.preferenceName = "year"

    binding.spinnerView4.preferenceName = "month"

    binding.spinnerView5.preferenceName = "day"
  }

  private fun contextDrawable(@DrawableRes resource: Int): Drawable? {
    return ContextCompat.getDrawable(this@CustomActivity, resource)
  }
}
