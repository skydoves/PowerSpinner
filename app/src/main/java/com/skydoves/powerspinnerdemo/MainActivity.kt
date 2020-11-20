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

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinnerdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.spinnerView.apply {
      lifecycleOwner = this@MainActivity
      setOnSpinnerItemSelectedListener<String> { _, text ->
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
      }
    }
    binding.spinnerView.setIsFocusable(true)
    binding.spinnerView.selectItemByIndex(3)
    binding.spinnerView1.apply {
      lifecycleOwner = this@MainActivity
      setOnSpinnerItemSelectedListener<String> { index, _ ->
        when (index) {
          0 -> setBackgroundColor(getContextColor(R.color.colorPrimary))
          1 -> setBackgroundColor(getContextColor(R.color.md_orange_200))
          2 -> setBackgroundColor(getContextColor(R.color.md_yellow_200))
          3 -> setBackgroundColor(getContextColor(R.color.md_green_200))
          4 -> setBackgroundColor(getContextColor(R.color.md_blue_200))
          5 -> setBackgroundColor(getContextColor(R.color.md_purple_200))
        }
      }
    }

    val adapter = IconSpinnerAdapter(binding.spinnerView2)
    binding.spinnerView2.setOnSpinnerItemSelectedListener(
      OnSpinnerItemSelectedListener<IconSpinnerItem> { _, item ->
        Toast.makeText(applicationContext, item.text, Toast.LENGTH_SHORT).show()
      }
    )
    adapter.setItems(
      arrayListOf(
        IconSpinnerItem(
          ContextCompat.getDrawable(this, R.drawable.ic_dashboard_white_24dp),
          "Item0"
        ),
        IconSpinnerItem(
          ContextCompat.getDrawable(this, R.drawable.ic_dashboard_white_24dp),
          "Item1"
        ),
        IconSpinnerItem(
          ContextCompat.getDrawable(this, R.drawable.ic_dashboard_white_24dp),
          "Item2"
        ),
        IconSpinnerItem(
          ContextCompat.getDrawable(this, R.drawable.ic_dashboard_white_24dp),
          "Item3"
        ),
        IconSpinnerItem(
          ContextCompat.getDrawable(this, R.drawable.ic_dashboard_white_24dp),
          "Item4"
        ),
        IconSpinnerItem(
          ContextCompat.getDrawable(this, R.drawable.ic_dashboard_white_24dp),
          "Item5"
        )
      )
    )
    binding.spinnerView2.apply {
      lifecycleOwner = this@MainActivity
      getSpinnerRecyclerView().adapter = adapter
    }
  }

  private fun getContextColor(@ColorRes resource: Int): Int {
    return ContextCompat.getColor(this@MainActivity, resource)
  }
}
