package com.technion.android.joblin

import com.thejuki.kformmaster.helper.FormBuildHelper

/**
 * Form Builder Extensions
 *
 * Used for Kotlin DSL to create the FormBuildHelper
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */

/** FormBuildHelper extension to add a FormCustomElement */
fun FormBuildHelper.customEx(tag: Int = -1, init: FormCustomElement.() -> Unit): FormCustomElement {
    return addFormElement(FormCustomElement(tag).apply(init))
}

/** FormBuildHelper extension to add a FormPlacesAutoCompleteElement */
fun FormBuildHelper.placesAutoComplete(tag: Int = -1, init: FormPlacesAutoCompleteElement.() -> Unit): FormPlacesAutoCompleteElement {
    return addFormElement(FormPlacesAutoCompleteElement(tag).apply(init))
}
