package com.pp.coworkingapp.app.old_activties

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pp.coworkingapp.R
import com.pp.coworkingapp.databinding.ActivityRegistrationBinding
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class RegistrationActivity : AppCompatActivity() {

    lateinit var bindingClass : ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        //маска для ввода телефона
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        mask.isForbidInputWhenFilled = false // default value

        val formatWatcher: FormatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(bindingClass.editTextPhone)

//        val edList = arrayOf<EditText>(bindingClass.edFirstName, bindingClass.edLastName, bindingClass.editTextPhone, bindingClass.editTextPassword)
//        val textWatcher = CustomTextWatcher(edList, bindingClass.btSignOut)
//        for (editText in edList) editText.addTextChangedListener(textWatcher)

        bindingClass.btSignOut.setOnClickListener() {
            val inputFirstNameText: String = bindingClass.edFirstName.text.toString()
            val inputLastNameText: String = bindingClass.edLastName.text.toString()
            val inputPhoneText: String = bindingClass.editTextPhone.text.toString()
            val inputPassText: String = bindingClass.editTextPassword.text.toString()

            if (inputFirstNameText.isEmpty()) {
                bindingClass.edFirstName.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError1.setText(R.string.errors_empty)
                bindingClass.tvError1.isVisible = true
            } else {
                bindingClass.edFirstName.setBackgroundResource(R.drawable.rectangle_3)
                bindingClass.tvError1.isVisible = false
            }

            if (inputLastNameText.isEmpty()) {
                bindingClass.edLastName.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError2.setText(R.string.errors_empty)
                bindingClass.tvError2.isVisible = true
            } else {
                bindingClass.edLastName.setBackgroundResource(R.drawable.rectangle_3)
                bindingClass.tvError2.isVisible = false
            }

            if (inputPhoneText.isEmpty()) {
                bindingClass.editTextPhone.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError3.setText(R.string.errors_empty)
                bindingClass.tvError3.isVisible = true
            } else if (inputPhoneText.length < 18) {
                bindingClass.editTextPhone.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError3.setText(R.string.errors_exception)
                bindingClass.tvError3.isVisible = true
            } else {
                bindingClass.editTextPhone.setBackgroundResource(R.drawable.rectangle_3)
                bindingClass.tvError3.isVisible = false
            }

            if (inputPassText.isEmpty()) {
                bindingClass.editTextPassword.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError4.setText(R.string.errors_empty)
                bindingClass.tvError4.isVisible = true
            } else {
                bindingClass.editTextPassword.setBackgroundResource(R.drawable.rectangle_3)
                bindingClass.tvError4.isVisible = false
            }
        }
    }
}