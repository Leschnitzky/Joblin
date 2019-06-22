package com.technion.android.joblin


import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.github.vivchar.rendererrecyclerviewadapter.ViewState
import com.github.vivchar.rendererrecyclerviewadapter.ViewStateProvider
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.view.BaseFormViewBinder

class CustomViewBinder(private val context: Context, private val formBuilder: FormBuildHelper, @LayoutRes private val layoutID: Int?) : BaseFormViewBinder() {
    var viewBinder = ViewBinder(layoutID
            ?: R.layout.form_element_custom, FormCustomElement::class.java, { model, finder, _ ->
        val textViewTitle = finder.find(R.id.formElementTitle) as AppCompatTextView
        val mainViewLayout = finder.find(R.id.formElementMainLayout) as? LinearLayout
        val textViewError = finder.find(R.id.formElementError) as AppCompatTextView
        val dividerView = finder.find(R.id.formElementDivider) as? View
        val itemView = finder.getRootView() as View
        baseSetup(model, dividerView, textViewTitle, textViewError, itemView, mainViewLayout)
        val editTextValue = finder.find(R.id.formElementValue) as com.thejuki.kformmaster.widget.ClearableEditText
        val units = finder.find(R.id.formElementUnits) as Spinner
        editTextValue.setText(model.valueAsString)
        editTextValue.hint = model.hint ?: ""
        model.editView = editTextValue
        units.setSelection(model.unit)
        var check = 0
        units.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(++check>1) {
                    if (!editTextValue.text?.isEmpty()!!) {
                        if (position == 0)
                            editTextValue.setText(Math.round((editTextValue.text.toString().toDouble() / 100) * 20 / 4).toString())
                        else
                            editTextValue.setText(((Math.round((editTextValue.text.toString().toDouble() * 4 / 20) * 100) + 9) / 10 * 10).toString())
                    }
                    model.unit = position
                }
            }
        }
        // Initially use 4 lines
        // unless a different number was provided
        if (model.maxLines == 1) {
            model.maxLines = 4
        }

        // If an InputType is provided, use it instead
        model.inputType?.let { editTextValue.setRawInputType(it) }

        // If imeOptions are provided, use them instead of creating a new line
        model.imeOptions?.let { editTextValue.imeOptions = it }

        setEditTextFocusEnabled(editTextValue, itemView)
        setOnFocusChangeListener(context, model, formBuilder)
        addTextChangedListener(model, formBuilder)
        setOnEditorActionListener(model, formBuilder)

    }, object : ViewStateProvider<FormCustomElement, ViewHolder> {

        override fun createViewStateID(model: FormCustomElement): Int {

            return model.id

        }

        override fun createViewState(holder: ViewHolder): ViewState<ViewHolder> {

            return FormCustomViewState(holder)

        }

    })



    private fun setEditTextFocusEnabled(editTextValue: AppCompatEditText, itemView: View) {

        itemView.setOnClickListener {

            editTextValue.requestFocus()

            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            editTextValue.setSelection(editTextValue.text?.length ?: 0)

            imm.showSoftInput(editTextValue, InputMethodManager.SHOW_IMPLICIT)

        }

    }

}