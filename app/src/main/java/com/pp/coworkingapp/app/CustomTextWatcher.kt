package com.pp.coworkingapp.app

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText


class CustomTextWatcher(edList: Array<EditText>, v: Button) :
    TextWatcher {
    var v: View
    var edList: Array<EditText>

    init {
        this.v = v
        this.edList = edList
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        for (editText in edList) {
            if (editText.text.toString().trim { it <= ' ' }.length <= 0) {
                v.isEnabled = false
                break
            } else v.isEnabled = true
        }
    }
}