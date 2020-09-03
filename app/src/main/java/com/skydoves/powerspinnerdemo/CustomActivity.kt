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
import kotlinx.android.synthetic.main.activity_custom.spinnerView
import kotlinx.android.synthetic.main.activity_custom.spinnerView1
import kotlinx.android.synthetic.main.activity_custom.spinnerView3
import kotlinx.android.synthetic.main.activity_custom.spinnerView4
import kotlinx.android.synthetic.main.activity_custom.spinnerView5
import kotlinx.android.synthetic.main.activity_main.spinnerView2

class CustomActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_custom)

    spinnerView.apply {
      setSpinnerAdapter(IconSpinnerAdapter(this))
      setItems(
        arrayListOf(
          IconSpinnerItem(contextDrawable(R.drawable.unitedstates), "USA"),
          IconSpinnerItem(contextDrawable(R.drawable.unitedkingdom), "UK"),
          IconSpinnerItem(contextDrawable(R.drawable.france), "France"),
          IconSpinnerItem(contextDrawable(R.drawable.canada), "Canada"),
          IconSpinnerItem(contextDrawable(R.drawable.southkorea), "South Korea"),
          IconSpinnerItem(contextDrawable(R.drawable.germany), "Germany"),
          IconSpinnerItem(contextDrawable(R.drawable.spain), "Spain"),
          IconSpinnerItem(contextDrawable(R.drawable.china), "China")
        )
      )
      setOnSpinnerItemSelectedListener<IconSpinnerItem> { index, item ->
        Toast.makeText(applicationContext, item.text, Toast.LENGTH_SHORT).show()
      }
      getSpinnerRecyclerView().layoutManager = GridLayoutManager(baseContext, 2)
      selectItemByIndex(4)
      preferenceName = "country"
      lifecycleOwner = this@CustomActivity
    }

    spinnerView1.apply {
      setOnSpinnerItemSelectedListener<String> { position, item ->
        spinnerView2.hint = item
        Toast.makeText(applicationContext, item, Toast.LENGTH_SHORT).show()
      }
      lifecycleOwner = this@CustomActivity
      preferenceName = "question1"
    }

    spinnerView2.apply {
      lifecycleOwner = this@CustomActivity
      preferenceName = "question2"
    }

    spinnerView3.apply {
      lifecycleOwner = this@CustomActivity
      preferenceName = "year"
    }

    spinnerView4.apply {
      lifecycleOwner = this@CustomActivity
      preferenceName = "month"
    }

    spinnerView5.apply {
      lifecycleOwner = this@CustomActivity
      preferenceName = "day"
    }
  }

  private fun contextDrawable(@DrawableRes resource: Int): Drawable? {
    return ContextCompat.getDrawable(this@CustomActivity, resource)
  }
}
