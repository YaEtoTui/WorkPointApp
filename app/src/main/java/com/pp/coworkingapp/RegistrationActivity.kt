package com.pp.coworkingapp

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.pp.coworkingapp.app.CustomTextWatcher
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

        val edList = arrayOf<EditText>(bindingClass.edFirstName, bindingClass.edLastName, bindingClass.editTextPhone, bindingClass.editTextPassword)
        val textWatcher = CustomTextWatcher(edList, bindingClass.btSignOut)
        for (editText in edList) editText.addTextChangedListener(textWatcher)
    }
}