package com.technion.android.joblin

import android.widget.Spinner
import com.github.vivchar.rendererrecyclerviewadapter.ViewHolder
import com.thejuki.kformmaster.state.BaseFormViewState
import com.thejuki.kformmaster.widget.ClearableEditText

class FormCustomViewState(holder: ViewHolder) : BaseFormViewState(holder) {

    private var value: String? = null
    init {
        val editText = holder.viewFinder.find(R.id.formElementValue) as ClearableEditText
        val units = holder.viewFinder.find(R.id.formElementUnits) as Spinner
        value = editText.text.toString()
    }

    override fun restore(holder: ViewHolder) {
        super.restore(holder)
        holder.viewFinder.setText(R.id.formElementValue, value)
    }
}