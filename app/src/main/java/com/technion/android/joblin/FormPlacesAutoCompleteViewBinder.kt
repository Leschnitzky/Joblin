package com.technion.android.joblin

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.AppCompatTextView
import android.text.InputType
import android.view.View
import android.widget.LinearLayout
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.state.FormEditTextViewState
import com.thejuki.kformmaster.view.BaseFormViewBinder

/**
 * Form Custom ViewBinder
 *
 * View Binder for [FormPlacesAutoCompleteElement]
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormPlacesAutoCompleteViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?, private val fragment: Fragment? = null) : BaseFormViewBinder() {
    var viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element, FormPlacesAutoCompleteElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout)

        val editTextValue = finder.find(R.id.formElementValue) as com.thejuki.kformmaster.widget.ClearableEditText

        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""
        editTextValue.alwaysShowClear = true

        model.editView = editTextValue

        editTextValue.setRawInputType(InputType.TYPE_NULL)
        editTextValue.isFocusable = false

        setClearableListener(model)

        val listener = View.OnClickListener {
            context.let { activity ->
                if (activity is FragmentActivity) {
                    val intent = Autocomplete.IntentBuilder(
                            model.autocompleteActivityMode, model.placeFields)
                            .setTypeFilter(TypeFilter.CITIES).setCountry("IL")
                            .build(activity)
                    if (fragment != null) {
                        fragment.startActivityForResult(intent, model.tag)
                    } else {
                        activity.startActivityForResult(intent, model.tag)
                    }
                }
            }
        }

        itemView.setOnClickListener(listener)
        editTextValue.setOnClickListener(listener)

    }, object : ViewStateProvider<FormPlacesAutoCompleteElement, ViewHolder> {
        override fun createViewStateID(model: FormPlacesAutoCompleteElement): Int {
            return model.id
        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {
            return FormEditTextViewState(holder)
        }
    })
}
