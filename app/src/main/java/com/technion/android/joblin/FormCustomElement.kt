package com.technion.android.joblin

import com.thejuki.kformmaster.model.BaseFormElement

/**
 * Form Custom Element
 *
 * Form element for AppCompatEditText
 *
 * @author **TheJuki** ([GitHub](https://github.com/TheJuki))
 * @version 1.0
 */
class FormCustomElement(tag: Int = -1) : BaseFormElement<String>(tag) {
    var unit: Int = 0
    private var timesAweek = arrayOf("Once a week", "Twice a week", "3 Times a week", "4 Times a week", "Full Time")
    private var precentage = arrayOf("20-30%","20-30%", "40-50%", "40-50%", "60-70%", "60-70%", "80-90%", "80-90%", "Full Time")

    fun setUnit(unit: Any): BaseFormElement<String> {
        this.unit = unit as Int
        return this
    }

    fun getFinalValue(): String? {
        if(unit == 0)
            return timesAweek[value.toString().toInt()-1]
        return precentage[value.toString().toInt()/10-2]
    }

    override val isValid: Boolean
        get() = validityCheck()

    var validityCheck: () -> Boolean = {
        ((unit == 0) && (value.toString().toInt() <7) && (value.toString().toInt() >0))
        ||
        ((unit == 1) && (value.toString().toInt() <=100) && (value.toString().toInt() >=20))
    }
}