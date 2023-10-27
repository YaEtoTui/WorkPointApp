package com.pp.coworkingapp

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pp.coworkingapp.app.CustomTextWatcher
import com.pp.coworkingapp.databinding.ActivityAuthorizationBinding
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class AuthorizationActivity : AppCompatActivity() {

    lateinit var bindingClass : ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        //маска для ввода телефона
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        mask.isForbidInputWhenFilled = false // default value

        val formatWatcher: FormatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(bindingClass.editTvPhone)

        //блок кнопки при не заполнении edit'ов
//        val edList = arrayOf<EditText>(bindingClass.editTvPhone, bindingClass.editTvPass)
//        val textWatcher = CustomTextWatcher(edList, bindingClass.button)
//        for (editText in edList) editText.addTextChangedListener(textWatcher)

        bindingClass.button.setOnClickListener() {
            val inputPhoneText: String = bindingClass.editTvPhone.text.toString()
            val inputPassText: String = bindingClass.editTvPass.text.toString()

            if (inputPhoneText.isEmpty()) {
                bindingClass.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError1.setText(R.string.errors_empty)
                bindingClass.tvError1.isVisible = true
            } else if (inputPhoneText.length < 18) {
                bindingClass.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError1.setText(R.string.errors_exception)
                bindingClass.tvError1.isVisible = true
            } else {
                bindingClass.editTvPhone.setBackgroundResource(R.drawable.rectangle_3)
                bindingClass.tvError1.isVisible = false
            }

            if (inputPassText.isEmpty()) {
                bindingClass.editTvPass.setBackgroundResource(R.drawable.stroke_red)
                bindingClass.tvError2.setText(R.string.errors_empty)
                bindingClass.tvError2.isVisible = true
            } else {
                bindingClass.editTvPass.setBackgroundResource(R.drawable.rectangle_3)
                bindingClass.tvError2.isVisible = false
            }
        }
    }
    fun setOnClickReg(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}