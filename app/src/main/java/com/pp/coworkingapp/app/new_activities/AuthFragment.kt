package com.pp.coworkingapp.app.new_activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pp.coworkingapp.R
import com.pp.coworkingapp.app.retrofit.api.MainApi
import com.pp.coworkingapp.app.retrofit.domain.Common
import com.pp.coworkingapp.app.retrofit.domain.request.AuthRequest
import com.pp.coworkingapp.app.retrofit.domain.viewModel.AuthViewModel
import com.pp.coworkingapp.databinding.FragmentAuthBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private lateinit var mainApi: MainApi
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainApi = Common.retrofitService

        //маска для ввода телефона
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        mask.isForbidInputWhenFilled = false // default value

        val formatWatcher: FormatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(binding.editTvPhone)

        binding.signOut.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_regPageFragment)
        }

        //проверяем при нажатии введенные значения
        checkEditsText()
    }

    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.auth(authRequest.username, authRequest.password)
            val message = response.errorBody()?.string()?.let { JSONObject(it).getString("detail")}
            requireActivity().runOnUiThread {
                if (!message.equals(null)) {
                    binding.tvError1.visibility = View.VISIBLE
                    binding.tvError2.visibility = View.VISIBLE
                    binding.editTvPass.setBackgroundResource(R.drawable.stroke_red)
                    binding.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError1.text = "Неправильно набран телефон"
                    binding.tvError2.text = "Неправильно набран пароль"
                } else {
                    viewModel.token.value = response.body()?.token
                    Log.i("Token", response.body()?.token.toString())
                    binding.tvError1.visibility = View.INVISIBLE
                    binding.editTvPass.setBackgroundResource(R.drawable.rectangle_3)
                    findNavController().navigate(R.id.action_authFragment_to_mainPageFragment)
                }
            }
        }
    }

//    private fun initRetrofit() {
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.level = HttpLoggingInterceptor.Level.BODY
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(interceptor)
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://www.1506815-cq40245.tw1.ru").client(client)
//            .addConverterFactory(GsonConverterFactory.create()).build()
//        mainApi = retrofit.create(MainApi::class.java)
//    }

    private fun checkEditsText() {
        binding.btSignIn.setOnClickListener() {
            val inputPhoneText: String = binding.editTvPhone.text.toString()
            val inputPassText: String = binding.editTvPass.text.toString()
            Log.i("Length", inputPhoneText.length.toString())

            if (inputPhoneText.isEmpty() || inputPassText.isEmpty()) {
                if (inputPhoneText.isEmpty()) {
                    binding.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError1.setText(R.string.errors_empty)
                    binding.tvError1.isVisible = true
                }
                if (inputPassText.isEmpty()) {
                    binding.editTvPass.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError2.setText(R.string.errors_empty)
                    binding.tvError2.isVisible = true
                }
            } else if (inputPhoneText.length < 18 || inputPassText.length < 6) {
                if (inputPhoneText.length < 18) {
                    binding.editTvPhone.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError1.setText("Введите полностью телефон")
                    binding.tvError1.isVisible = true
                }
                if (inputPassText.length < 6) {
                    binding.editTvPass.setBackgroundResource(R.drawable.stroke_red)
                    binding.tvError2.setText("Пароль меньше 6 символов")
                    binding.tvError2.isVisible = true
                }
            } else {
                binding.editTvPhone.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError1.isVisible = false
                binding.editTvPass.setBackgroundResource(R.drawable.rectangle_3)
                binding.tvError2.isVisible = false
                //запускаем auth()

                val redactPhone: String = "8${binding.editTvPhone.text.toString().replace(Regex("[+ ()-]"), "").substring(1)}"
                Log.i("Phone", redactPhone)

                auth(
                    AuthRequest(
                        redactPhone,
                        binding.editTvPass.text.toString()

                    )
                )
            }
        }
    }
}