package com.pp.coworkingapp.app.new_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.RegisterRequest
import com.pp.coworkingapp.databinding.FragmentRegBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class RegFragment : Fragment() {

    private lateinit var binding: FragmentRegBinding
    private lateinit var mainApi: MainApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApi = Common.retrofitService

        //маска для ввода телефона
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        mask.isForbidInputWhenFilled = false // default value

        val formatWatcher: FormatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(binding.editTextPhone)

        checkEditsText()
    }

    private fun registration(registerRequest: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = mainApi.register(registerRequest)
            val message = user.errorBody()?.string()?.let {
                JSONObject(it).getString("detail")
            }
            requireActivity().runOnUiThread {
                if (!message.equals(null)) {
                    binding.tvError1.visibility = View.VISIBLE
                    binding.tvError1.text = message
                } else {
                    Log.i("User", user.body().toString())
                    findNavController().navigate(R.id.action_regFragment_to_authFragment)
                }
            }
        }
    }

    private fun checkEditsText() {
        binding.btSignOut.setOnClickListener() {
            val inputFirstNameText: String = binding.edFirstName.text.toString()
            val inputLastNameText: String = binding.edLastName.text.toString()
            val inputPhoneText: String = binding.editTextPhone.text.toString()
            val inputPassText: String = binding.editTextPassword.text.toString()

            var roleId: String = "1"
            if (!binding.btRadio1.isChecked)
                roleId = "2"

            if (inputFirstNameText.isEmpty() || inputLastNameText.isEmpty() || inputPhoneText.isEmpty() || inputPassText.isEmpty()) {
                if (inputFirstNameText.isEmpty()) {
                    binding.edFirstName.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError1.setText(R.string.errors_empty)
                    binding.tvError1.isVisible = true
                }
                if (inputLastNameText.isEmpty()) {
                    binding.edLastName.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError2.setText(R.string.errors_empty)
                    binding.tvError2.isVisible = true
                }
                if (inputPhoneText.isEmpty()) {
                    binding.editTextPhone.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError3.setText(R.string.errors_empty)
                    binding.tvError3.isVisible = true
                }
                if (inputPassText.isEmpty()) {
                    binding.editTextPassword.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError4.setText(R.string.errors_empty)
                    binding.tvError4.isVisible = true
                }
            } else if (inputPhoneText.length < 18 || inputPassText.length < 6) {
                if (inputPhoneText.length < 18) {
                    binding.editTextPhone.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError3.setText("Введите полностью телефон")
                    binding.tvError3.isVisible = true
                }
                if (inputPassText.length < 6) {
                    binding.editTextPassword.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError4.setText("Пароль меньше 6 символов")
                    binding.tvError4.isVisible = true
                }
            } else {
                binding.edFirstName.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError1.isVisible = false
                binding.edLastName.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError2.isVisible = false
                binding.editTextPhone.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError3.isVisible = false
                binding.editTextPassword.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError4.isVisible = false

                val redactPhone = "8${inputPhoneText.replace(Regex("[+ ()-]"), "").substring(1)}"
                Log.i("Phone", redactPhone)

                try {
                    registration(
                        RegisterRequest(
                            redactPhone,
                            inputFirstNameText,
                            inputLastNameText,
                            roleId,
                            "https://s3.timeweb.com/38cfe289-workpoint/грустный котик.jpeg",
                            inputPassText
                        )
                    )
                } catch (exc: Exception) {
                    exc.message
                }
            }
        }
    }
}