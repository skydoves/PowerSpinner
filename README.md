# PowerSpinner
<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=17"><img alt="API" src="https://img.shields.io/badge/API-17%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://travis-ci.com/skydoves/PowerSpinner"><img alt="Build Status" src="https://travis-ci.com/skydoves/PowerSpinner.svg?branch=master"/></a>
    <a href="https://androidweekly.net/issues/issue-395"><img alt="Android Weekly" src="https://img.shields.io/badge/Android%20Weekly-%23395-orange"/></a>
  <a href="https://skydoves.github.io/libraries/powerspinner/javadoc/powerspinner/com.skydoves.powerspinner/index.html"><img alt="Javadoc" src="https://img.shields.io/badge/Javadoc-PowerSpinner-yellow"/></a>
</p>

<p align="center">
🌀 A lightweight dropdown popup spinner with an arrow and animations.
</p>

<p align="center">
<img src="https://user-images.githubusercontent.com/24237865/71557629-fa840c00-2a8b-11ea-86ac-310ae2e3a5b8.gif" width="32%"/>
<img src="https://user-images.githubusercontent.com/24237865/71558150-3cfd1700-2a93-11ea-9809-ce33309ebc96.gif" width="32%"/>
<img src="https://user-images.githubusercontent.com/24237865/71613264-b8241180-2be8-11ea-8e0a-85b5b250cc75.gif" width="32%"/>
</p>


## Including in your project
[![Download](https://api.bintray.com/packages/devmagician/maven/powerspinner/images/download.svg) ](https://bintray.com/devmagician/maven/powerspinner/_latestVersion)
[![JitPack](https://jitpack.io/v/skydoves/PowerSpinner.svg)](https://jitpack.io/#skydoves/PowerSpinner) <br>

### Gradle 
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
    repositories {
        jcenter()
    }
}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:powerspinner:1.0.2"
}
```

## Usage
Add following XML namespace inside your XML layout file.

```gradle
xmlns:app="http://schemas.android.com/apk/res-auto"
```

### PowerSpinnerView
Here is a basic example of implementing `PowerSpinnerView`. </br>
Basically the `PowerSpinnerView` extends `TextView`, so we can use it like a `TextView`.</br>
You can set the unselected text using `hint` and `textColorHint` attributes.

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
  app:spinner_popup_animation="dropdown"
  app:spinner_popup_background="@color/background800"
  app:spinner_popup_elevation="14dp" />
```

### Create using builder class
We can create an instance of `PowerSpinnerView` using the builder class.
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

### Show and dismiss
Basically, when the `PowerSpinnerView` is clicked, the spinner popup will be showed and </br>
when an item is selected, the spinner popup will be dismissed.</br>
But you can show and dismiss manually using methods.

```kotlin
powerSpinnerView.show() // show the spinner popup
powerSpinnerView.dismiss() // dismiss the spinner popup

// If the popup is not showing, shows the spinner popup menu.
// If the popup is already showing, dismiss the spinner popup menu.
powerSpinnerView.showOrDismiss()
```

And you can set the show and dismiss actions using some listener and attributes.
```kotlin
// the spinner popup will not be shown when clicked.
powerSpinnerView.setOnClickListener { }

// the spinner popup will not be dismissed when item selected.
powerSpinnerView.dismissWhenNotifiedItemSelected = false
```

### OnSpinnerItemSelectedListener
Interface definition for a callback to be invoked when selected item on the spinner popup.
```kotlin
setOnSpinnerItemSelectedListener<String> { index, text ->
   toast("$text selected!")
}
```
Here is the java way.
```java
powerSpinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
  @Override public void onItemSelected(int position, String item) {
    toast(item + " selected!")
  }
});
```

### selectItemByIndex
We can select an item manually or initially using the below method.
```kotlin
spinnerView.selectItemByIndex(4)
```

### Persist a selected position
We can save and restore the selected postion automatically.<br>
If you select an item, the same position will be selected automatically on the next inflation.<br>
Just use the below method or attribute.

```kotlin
spinnerView.preferenceName = "country"
```

Or you can set it on xml.

```gradle
app:spinner_preference_name="country"
```
You can remove the persisted position data on an item or clear all of the data on your application.

```kotlin
spinnerView.removePersistedData("country")
spinnerView.clearAllPersistedData()
```

### SpinnerAnimation
We can customize the showing and dimsmiss animation.
```kotlin
SpinnerAnimation.DROPDOWN
SpinnerAnimation.FADE
SpinnerAnimation.BOUNCE
```

Dropdown | Fade | Bounce
| :---------------: | :---------------: | :---------------: |
| <img src="https://user-images.githubusercontent.com/24237865/71556493-b9d0c680-2a7c-11ea-9843-338451cc855a.gif" align="center" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/71556494-ba695d00-2a7c-11ea-840d-dc3a0aa0babc.gif" align="center" width="100%"/> | <img src="https://user-images.githubusercontent.com/24237865/71556492-b9d0c680-2a7c-11ea-8b59-c9a29a0387ba.gif" align="center" width="100%"/>

### Customized adapter
We can use our customized adapter and binds to the `PowerSpinnerView`.</br>
The `PowerSpinnerView` provides the spinner popup's recyclerview via `getSpinnerRecyclerView` method.

Here is a sample of the customized adapter.
```kotlin
val adapter = IconSpinnerAdapter(spinnerView)
spinnerView.setSpinnerAdapter(adapter)
spinnerView.getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 2)
```

#### IconSpinnerAdapter
Basically, this library provides a customized adapter.</br>
We should create an instance of the `IconSpinnerAdapter` and call `setItems` using a list of `IconSpinnerItem`.
```kotlin
spinnerView.apply {
  setSpinnerAdapter(IconSpinnerAdapter(this))
  setItems(
    arrayListOf(
        IconSpinnerItem(contextDrawable(R.drawable.unitedstates), "Item1")))
  getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 2)
  selectItemByIndex(0) // select an item initially.
  lifecycleOwner = this@MainActivity
}
```
Here is the java way.
```java
List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
iconSpinnerItems.add(new IconSpinnerItem(contextDrawable(R.drawable.unitedstates), "item1"));

IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(spinnerView);
spinnerView.setSpinnerAdapter(iconSpinnerAdapter);
spinnerView.setItems(iconSpinnerItems);
spinnerView.selectItemByIndex(0);
spinnerView.setLifecycleOwner(this);
```

#### Customized adapter
Here is a way to customize your adapter for binding the `PowerSpinnerView`.<br>
Firstly, create a new adapter and viewHolder extending `RecyclerView.Adapter` and `PowerSpinnerInterface<T>`.<br>
You shoud override `spinnerView`, `onSpinnerItemSelectedListener` fields and `setItems`, `notifyItemSelected` methods.

```kotlin
class MySpinnerAdapter(
  powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<MySpinnerAdapter.MySpinnerViewHolder>(),
  PowerSpinnerInterface<MySpinnerItem> {

  override val spinnerView: PowerSpinnerView = powerSpinnerView
  override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<MySpinnerItem>? = null
```
On the customized adapter, you must call `spinnerView.notifyItemSelected` method when your item is clicked or the spinner item should be changed.

```kotlin
override fun onBindViewHolder(holder: MySpinnerViewHolder, position: Int) {
  holder.itemView.setOnClickListener {
    notifyItemSelected(position)
  }
}

// we must call the spinnerView.notifyItemSelected method to let PowerSpinnerView know about changed information.
override fun notifyItemSelected(index: Int) {
  this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].text)
  this.onSpinnerItemSelectedListener?.onItemSelected(index, this.spinnerItems[index])
}
```

And we can listen to the selected item's information.

```kotlin
spinnerView.setOnSpinnerItemSelectedListener<MySpinnerItem> { index, item ->  toast(item.text) }
```

### PowerSpinnerPreference
We can use PowerSpinner on the `PreferenceScreen` xml for implementing setting screens.

And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "androidx.preference:preference:1.1.0"
}
```

<img src="https://user-images.githubusercontent.com/24237865/71613264-b8241180-2be8-11ea-8e0a-85b5b250cc75.gif" align="right" width="30%">

And create your preference xml file like below.

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
countySpinnerPreference?.setOnSpinnerItemSelectedListener<IconSpinnerItem> { index, item ->
  Toast.makeText(requireContext(), item.text, Toast.LENGTH_SHORT).show()
}
```

### Avoid Memory leak
Dialog, PopupWindow and etc.. have memory leak issue if not dismissed before activity or fragment are destroyed.<br>
But Lifecycles are now integrated with the Support Library since Architecture Components 1.0 Stable released.<br>
So we can solve the memory leak issue so easily.<br>

Just use `setLifecycleOwner` method. Then `dismiss` method will be called automatically before activity or fragment would be destroyed.
```java
.setLifecycleOwner(lifecycleOwner)
```

## PowerSpinnerView Attributes
Attributes | Type | Default | Description
--- | --- | --- | ---
spinner_arrow_drawable | Drawable | arrow | arrow drawable.
spinner_arrow_show | Boolean | true | sets the visibility of the arrow.
spinner_arrow_gravity | SpinnerGravity | end | the gravity of the arrow.
spinner_arrow_padding | Dimension | 2dp | padding of the arrow.
spinner_arrow_animate | Boolean | true | show arrow rotation animation when showing.
spinner_arrow_animate_duration | integer | 250 | the duration of the arrow animation.
spinner_divider_show | Boolean | true | show the divider of the popup items.
spinner_divider_size | Dimension | 0.5dp | sets the height of the divider.
spinner_divider_color | Color | White | sets the color of the divider.
spinner_popup_width | Dimension | spinnerView's width | the width of the popup.
spinner_popup_height | Dimension | WRAP_CONTENT | the height of the popup.
spinner_popup_background | Color | spinnerView's background | the background color of the popup.
spinner_popup_animation | SpinnerAnimation | Dropdown | the spinner animation when showing.
spinner_popup_animation_style | Style Resource | -1 | sets the customized animation style.
spinner_popup_elevation | Dimension | 4dp | the elevation size of the popup.
spinner_item_array | String Array Resource | null | sets the items of the popup.
spinner_dismiss_notified_select | Boolean | true | sets dismiss when the popup item is selected.
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
