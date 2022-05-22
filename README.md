<h1 align="center">PowerSpinner</h1></br>

<p align="center">
🌀 A lightweight dropdown popup spinner, fully customizable with an arrow and animations for Android.
</p> <br>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=17"><img alt="API" src="https://img.shields.io/badge/API-17%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/PowerSpinner/actions"><img alt="Build Status" src="https://github.com/skydoves/PowerSpinner/workflows/Android%20CI/badge.svg"/></a>
  <a href="https://androidweekly.net/issues/issue-395"><img alt="Android Weekly" src="https://skydoves.github.io/badges/android-weekly.svg"/></a>
  <a href="https://skydoves.medium.com/customizing-android-popup-spinner-dropdown-list-with-animations-4fef68110c53"><img alt="Medium" src="https://skydoves.github.io/badges/Story-Medium.svg"/></a>
  <a href="https://github.com/skydoves"><img alt="Profile" src="https://skydoves.github.io/badges/skydoves.svg"/></a>
  <a href="https://skydoves.github.io/libraries/powerspinner/javadoc/powerspinner/com.skydoves.powerspinner/index.html"><img alt="Javadoc" src="https://skydoves.github.io/badges/javadoc-powerspinner.svg"/></a>
</p> <br>

<p align="center">
<img src="https://user-images.githubusercontent.com/24237865/71962685-534a6600-323d-11ea-9e1e-df1f68cb2181.gif" width="280"/>
<img src="https://user-images.githubusercontent.com/24237865/100536652-4f646f80-3265-11eb-9617-07911a8b2865.gif" width="266"/>
<img src="https://user-images.githubusercontent.com/24237865/71613264-b8241180-2be8-11ea-8e0a-85b5b250cc75.gif" width="280"/>
</p>


## Including in your project
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/powerspinner.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22powerspinner%22)

### Gradle
Add the dependency below to your module's build.gradle file:

```gradle
dependencies {
    implementation "com.github.skydoves:powerspinner:1.2.1"
}
```

## SNAPSHOT 
[![PowerSpinner](https://img.shields.io/static/v1?label=snapshot&message=powerspinner&logo=apache%20maven&color=C71A36)](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/powerspinner/) <br>
Snapshots of the current development version of PowerSpinner are available, which track [the latest versions](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/powerspinner/).

```Gradle
repositories {
   maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```

## Usage
Add following XML namespace inside your XML layout file.

```gradle
xmlns:app="http://schemas.android.com/apk/res-auto"
```

### PowerSpinnerView in XML
You can implement `PowerSpinnerView` in your XML layout as the below example. You can use `PowerSpinnerView` same as `TextView`. For instance, you can set the default text with the `hint` and `textColorHint` attributes..

```gradle
<com.skydoves.powerspinner.PowerSpinnerView
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:background="@color/md_blue_200"
  android:gravity="center"
  android:hint="Question 1"
  android:padding="10dp"
  android:textColor="@color/white_93"
  android:textColorHint="@color/white_70"
  android:textSize="14.5sp"
  app:spinner_arrow_gravity="end"
  app:spinner_arrow_padding="8dp"
  app:spinner_divider_color="@color/white_70"
  app:spinner_divider_show="true"
  app:spinner_divider_size="0.4dp"
  app:spinner_item_array="@array/questions"
  app:spinner_item_height="46dp"
  app:spinner_popup_animation="dropdown"
  app:spinner_popup_background="@color/background800"
  app:spinner_popup_elevation="14dp" />
```

### Create PowerSpinner with Kotlin extension
You can also create the `PowerSpinnerView` programmatically with the Kotlin extension class.
```gradle
val mySpinnerView = createPowerSpinnerView(this) {
  setSpinnerPopupWidth(300)
  setSpinnerPopupHeight(350)
  setArrowPadding(6)
  setArrowAnimate(true)
  setArrowAnimationDuration(200L)
  setArrowGravity(SpinnerGravity.START)
  setArrowTint(ContextCompat.getColor(this@MainActivity, R.color.md_blue_200))
  setSpinnerPopupAnimation(SpinnerAnimation.BOUNCE)
  setShowDivider(true)
  setDividerColor(Color.WHITE)
  setDividerSize(2)
  setLifecycleOwner(this@MainActivity)
}
```

> **Note**: It's highly recommended to set the height size of the item with the `spinner_item_height` attribute or the entire height size of the popup with the `spinner_popup_height` to implement the correct behaviors of your spinner.

### Show and Dismiss
By default, the spinner popup will be displayed when you click the `PowerSpinnerView`, and it will be dismissed when you select an item. You can also show and dismiss manually with the methods below:

```kotlin
powerSpinnerView.show() // show the spinner popup.
powerSpinnerView.dismiss() // dismiss the spinner popup.

// If the popup is not showing, shows the spinner popup menu.
// If the popup is already showing, dismiss the spinner popup menu.
powerSpinnerView.showOrDismiss()
```

You can customize the default behaviours of the spinner with the method and property below:

```kotlin
// the spinner popup will not be shown when clicked.
powerSpinnerView.setOnClickListener { }

// the spinner popup will not be dismissed when item selected.
powerSpinnerView.dismissWhenNotifiedItemSelected = false
```

### OnSpinnerItemSelectedListener

You can listen the selection of the spinner items with the `setOnSpinnerItemSelectedListener` method below:

```kotlin
powerSpinnerView.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
   toast("$text selected!")
}
```

If you use Java, see the example below:

```java
powerSpinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
  @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
    toast(item + " selected!");
  }
});
```

### Select an Item by an Index
You can select an item manually/initially with the method below:<br>

```kotlin
powerSpinnerView.selectItemByIndex(4)
```

> Note: `selectItemByIndex` must be invoked after setting items with the `setItems` method.

### Store and Restore a selected Position

You can store and restore the selected position automatically and it will be re-selected automatically when the `PowerSpinnerView` is inflated with the property below:

```kotlin
powerSpinnerView.preferenceName = "country"
```

You can also set the property above with the attribute below in your XML layout:

```gradle
app:spinner_preference_name="country"
```

You can remove or clear the stored position data with the methods below:

```kotlin
spinnerView.removePersistedData("country")
spinnerView.clearAllPersistedData()
```

### SpinnerAnimation

You can set an animation when you display and dismiss the spinner with the method below:

```kotlin
app:spinner_popup_animation="normal"
```

This library supports the four animations below:

```kotlin
SpinnerAnimation.NORMAL
SpinnerAnimation.DROPDOWN
SpinnerAnimation.FADE
SpinnerAnimation.BOUNCE
```

| NORMAL | DROPDOWN | FADE | BOUNCE |
| :---------------: | :---------------: | :---------------: | :---------------: |
| <img src="https://user-images.githubusercontent.com/24237865/71888721-14a4a500-3184-11ea-9d47-a744229577f2.gif" align="center" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/71888722-14a4a500-3184-11ea-9142-e5a594fc6909.gif" align="center" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/71888724-153d3b80-3184-11ea-9f02-4da4c8482302.gif" align="center" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/71888720-14a4a500-3184-11ea-8c31-949da8517e7f.gif" align="center" width="100%"/> |

### IconSpinnerAdapter

You can also check out the dafult custom adapter, `IconSpinnerAdapter` with the `setItems` and `IconSpinnerItem` methods below:

```kotlin
spinnerView.apply {
  setSpinnerAdapter(IconSpinnerAdapter(this))
  setItems(
    arrayListOf(
        IconSpinnerItem(text = "Item1", iconRes = R.drawable.unitedstates),
        IconSpinnerItem(text = "Item2", iconRes = R.drawable.southkorea)))
  getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 2)
  selectItemByIndex(0) // select a default item.
  lifecycleOwner = this@MainActivity
}
```

> Note: You can get the `RecyclerView` of the spinner with the `getSpinnerRecyclerView()` method.

If you use Java, see the example below:

```java
List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
iconSpinnerItems.add(new IconSpinnerItem("item1", contextDrawable(R.drawable.unitedstates)));

IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(spinnerView);
spinnerView.setSpinnerAdapter(iconSpinnerAdapter);
spinnerView.setItems(iconSpinnerItems);
spinnerView.selectItemByIndex(0);
spinnerView.setLifecycleOwner(this);
```

#### Custom Spinner Adapter
You can also implement your own custom adapter and bind to the `PowerSpinnerView`. Firstly, create a new adapter and viewHolder, which extend each `RecyclerView.Adapter` and `PowerSpinnerInterface<T>` below: <br>

```kotlin
class MySpinnerAdapter(
  powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<MySpinnerAdapter.MySpinnerViewHolder>(),
  PowerSpinnerInterface<MySpinnerItem> {

  override var index: Int = powerSpinnerView.selectedIndex
  override val spinnerView: PowerSpinnerView = powerSpinnerView
  override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<MySpinnerItem>? = null
```

With the custom spinner adapter, you can use your own custom spinner item, which includes information of the spinner item.

> Note: You shoud override the `spinnerView`, `onSpinnerItemSelectedListener` properties and `setItems`, `notifyItemSelected` methods.

Next, you must call `spinnerView.notifyItemSelected` method when your item is clicked or the spinner item should be changed:

```kotlin
override fun onBindViewHolder(holder: MySpinnerViewHolder, position: Int) {
  holder.itemView.setOnClickListener {
    notifyItemSelected(position)
  }
}

// You must call the `spinnerView.notifyItemSelected` method to let `PowerSpinnerView` know the item is changed.
override fun notifyItemSelected(index: Int) {
  if (index == NO_SELECTED_INDEX) return
  val oldIndex = this.index
  this.index = index
  this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].text)
  this.onSpinnerItemSelectedListener?.onItemSelected(
      oldIndex = oldIndex,
      oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { spinnerItems[oldIndex] },
      newIndex = index,
      newItem = item
    )
}
```

Lastly, you can  

```kotlin
spinnerView.setOnSpinnerItemSelectedListener<MySpinnerItem> { 
  oldIndex, oldItem, newIndex, newItem -> toast(newItem.text) 
}
```

### PowerSpinnerPreference

You can use `PowerSpinnerView` in your `PreferenceScreen` XML for building preferences screens. Add the dependency below to your **module**'s `build.gradle` file:

```gradle
dependencies {
    implementation "androidx.preference:preference-ktx:1.2.0"
}
```

<img src="https://user-images.githubusercontent.com/24237865/71613264-b8241180-2be8-11ea-8e0a-85b5b250cc75.gif" align="right" width="300">

You can implement the spinner preference with the `PowerSpinnerPreference` in your XML file below:

```gradle
<?xml version="1.0" encoding="utf-8"?>
<androidx.preference.PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto">

  <androidx.preference.Preference
    android:title="Account preferences"
    app:iconSpaceReserved="false" />

  <com.skydoves.powerspinner.PowerSpinnerPreference
    android:key="question1"
    android:title="Question1"
    app:spinner_arrow_gravity="end"
    app:spinner_arrow_padding="8dp"
    app:spinner_divider_color="@color/white_70"
    app:spinner_divider_show="true"
    app:spinner_divider_size="0.2dp"
    app:spinner_item_array="@array/questions1"
    app:spinner_popup_animation="dropdown"
    app:spinner_popup_background="@color/background900"
    app:spinner_popup_elevation="14dp" />
```

You don't need to set `preferenceName` attribute, and `OnSpinnerItemSelectedListener` should be set on `PowerSpinnerPreference`. You can reference [this sample codes](https://github.com/skydoves/PowerSpinner/tree/master/app/src/main/java/com/skydoves/powerspinnerdemo/PreferenceFragment.kt).

```kotlin
val countySpinnerPreference = findPreference<PowerSpinnerPreference>("country")
countySpinnerPreference?.setOnSpinnerItemSelectedListener<IconSpinnerItem> { oldIndex, oldItem, newIndex, newItem ->
  Toast.makeText(requireContext(), newItem.text, Toast.LENGTH_SHORT).show()
}
```

### Avoid Memory Leak
Dialog, PopupWindow and etc.. have memory leak issue if not dismissed before activity or fragment are destroyed. But Lifecycles are now integrated with the Support Library since Architecture Components 1.0 Stable released. So you can solve the memory leak issue simply by setting the lifecycle owner with the method below:<br>

```kotlin
.setLifecycleOwner(lifecycleOwner)
```

By setting the lifecycle owner, the `dismiss()` method will be invoked automatically before destroying your activity or fragment.

## PowerSpinnerView Attributes

Attributes | Type | Default | Description
--- | --- | --- | ---
spinner_arrow_drawable | Drawable | arrow | arrow drawable.
spinner_arrow_show | Boolean | true | sets the visibility of the arrow.
spinner_arrow_gravity | SpinnerGravity | end | the gravity of the arrow.
spinner_arrow_padding | Dimension | 2dp | padding of the arrow.
spinner_arrow_tint | Color | None | tint color of the arrow.
spinner_arrow_animate | Boolean | true | show arrow rotation animation when showing.
spinner_arrow_animate_duration | Integer | 250 | the duration of the arrow animation.
spinner_divider_show | Boolean | true | show the divider of the popup items.
spinner_divider_size | Dimension | 0.5dp | sets the height of the divider.
spinner_divider_color | Color | White | sets the color of the divider.
spinner_popup_width | Dimension | spinnerView's width | the width of the popup.
spinner_popup_height | Dimension | WRAP_CONTENT | the height of the popup.
spinner_item_height | Dimension | WRAP_CONTENT | a fixed item height of the popup.
spinner_popup_background | Color | spinnerView's background | the background color of the popup.
spinner_popup_animation | SpinnerAnimation | NORMAL | the spinner animation when showing.
spinner_popup_animation_style | Style Resource | -1 | sets the customized animation style.
spinner_popup_elevation | Dimension | 4dp | the elevation size of the popup.
spinner_item_array | String Array Resource | null | sets the items of the popup.
spinner_dismiss_notified_select | Boolean | true | sets dismiss when the popup item is selected.
spinner_debounce_duration | Integer | 150 | A duration of the debounce for showOrDismiss.
spinner_preference_name | String | null | saves and restores automatically the selected position.

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/PowerSpinner/stargazers)__ for this repository. :star:<br>
And __[follow](https://github.com/skydoves)__ me for my next creations! 🤩

# License
```xml
Copyright 2019 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the L
