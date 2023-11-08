package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.request.AuthRequest
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.databinding.FragmentAuthBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var mainApi: MainApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //маска для ввода телефона
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        mask.isForbidInputWhenFilled = false // default value

        val formatWatcher: FormatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(binding.editTvPhone)

        binding.signOut.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_regPageFragment)
        }

        binding.btSignIn.setOnClickListener() {
            val inputPhoneText: String = binding.editTvPhone.text.toString()
            val inputPassText: String = binding.editTvPass.text.toString()

            if (inputPhoneText.isEmpty()) {
                binding.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                binding.tvError1.setText(R.string.errors_empty)
                binding.tvError1.isVisible = true
            } else if (inputPhoneText.length < 18) {
                binding.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                binding.tvError1.setText(R.string.errors_exception)
                binding.tvError1.isVisible = true
            } else {
                binding.editTvPhone.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError1.isVisible = false
            }

            if (inputPassText.isEmpty()) {
                binding.editTvPass.setBackgroundResource(R.drawable.stroke_red)
                binding.tvError2.setText(R.string.errors_empty)
                binding.tvError2.isVisible = true
            } else {
                binding.editTvPass.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError2.isVisible = false
                viewModel.token.value = "gjfjdl"
                findNavController().navigate(R.id.action_authFragment_to_mainPageFragment)

//                //запускаем auth()
//                auth(
//                    AuthRequest(
//                        binding.editTvPhone.text.toString(),
//                        binding.editTvPass.text.toString()
//                    )
//                )
            }
        }
    }

    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = mainApi.auth(authRequest)
            val message = token.errorBody()?.string()?.let { JSONObject(it).getString("message") }
//            requireActivity().runOnUiThread {
//                viewModel.token.value = token.toString()
//                binding.error.text = message
//                val user = response.body()
//                if(user != null) {
//                    Picasso.get().load(user.image).into(binding.imView)
//                    binding.name.text = user.firstName
//                    binding.bNext.visibility = View.VISIBLE
//                    viewModel.token.value = user.token
//                }
//            }
        }
    }
}