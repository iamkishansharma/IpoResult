package com.heycode.iporesult.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.heycode.iporesult.R
import com.heycode.iporesult.databinding.ActivityMainBinding
import com.heycode.iporesult.models.CaptchaData
import com.heycode.iporesult.models.CheckData
import com.heycode.iporesult.models.CompanyShare
import com.heycode.iporesult.utils.hasInternetConnection
import com.heycode.iporesult.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var companies: List<CompanyShare>
    private var compNames: ArrayList<String> = ArrayList()
    private var compHash = HashMap<String, String>()
    private lateinit var captchaData: CaptchaData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_IpoResult)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setLogo()
//        window.statusBarColor = resources.getColor(android.R.color.transparent, null)
        supportActionBar?.title = "IPO Result"
        if (!hasInternetConnection(this@MainActivity)) {

            binding.apply {
                pbLoading.visibility = View.GONE
                btnSubmit.visibility = View.GONE
                cvMainContainer.visibility = View.GONE
                clError.visibility = View.VISIBLE
                btnRetryError.setOnClickListener {
                    finish()
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
        } else {
            lifecycleScope.launch {
                homeViewModel.getHome()
                homeViewModel.homeContent.observe(this@MainActivity) { data ->
                    companies = data.body.companyShareList
                    captchaData = data.body.captchaData
                    binding.apply {
                        clError.visibility = View.GONE
                        pbLoading.visibility = View.GONE
                        btnSubmit.visibility = View.VISIBLE
                        cvMainContainer.visibility = View.VISIBLE
                    }

                    // get names
                    companies.forEach {
                        compNames.add(it.name)
                        compHash[it.name] = it.id.toString()
                    }
                    val ad = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        compNames.asReversed()
                    )
                    binding.actMainSelect.apply {
                        setAdapter(ad)
                    }

                    // set captcha
                    setCaptcha(captchaData.captcha)
                }

            }

            binding.apply {
                btnSubmit.setOnClickListener {
                    if (!hasError(actMainSelect, tietBoid, tietCaptchaCode)) {
                        // hide keyboard
                        it.hideKeyboard()

                        val json = CheckData(
                            compHash[actMainSelect.text.toString()].toString(),
                            tietBoid.text.toString(),
                            tietCaptchaCode.text.toString(),
                            captchaData.captchaIdentifier
                        )
                        Log.d("JsonData", json.toString())
                        lifecycleScope.launch {
                            binding.pbLoading.visibility = View.VISIBLE
                            binding.cvMainContainer.visibility = View.GONE
                            binding.btnSubmit.visibility = View.GONE
                            homeViewModel.checkResult(json)

                            showAlertDialog(
                                this@MainActivity,
                                homeViewModel.message.value.toString()
                            )

                        }
                    }
                }
            }
        }


    }

    private fun hasError(
        t1: AutoCompleteTextView,
        t2: TextInputEditText,
        t3: TextInputEditText
    ): Boolean {
        if (t1.text.trim().isEmpty()) {
            t1.error = "Required"
            return true
        }
        if (t2.text!!.trim().isEmpty()) {
            t2.error = "Required"
            return true
        }
        if (t2.text.toString().length != 16) {
            binding.tilBox2.apply {
                helperText = "BOID must be 16 digit starting with 130"
                setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.red, null)))
            }
            return true
        }
        if (!t2.text.toString().matches(Regex("^(130).*$"))) {
            t2.error = "BOID is wrong"
            return true
        }

        if (t3.text!!.trim().isEmpty()) {
            t3.error = "Required"
            return true
        }
        if (t3.text.toString().length > 5 || t3.text.toString().length < 5) {
            binding.tilBox3.apply {
                helperText = "Wrong captcha"
                setHelperTextColor(ColorStateList.valueOf(resources.getColor(R.color.red, null)))
            }
            return true
        }
        return false
    }

    private fun showAlertDialog(context: Context, message: String) {
        binding.pbLoading.visibility = View.GONE
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Result")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing negative action
        builder.setNegativeButton("OK") { _, _ ->
            finish()
//            dialogInterface.dismiss()
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    private fun setCaptcha(imageString: String) {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.ivCaptcha.setImageBitmap(decodedImage)
    }

    override fun onBackPressed() {
        val diaBox = askOption()
        diaBox.show()
    }

    private fun askOption(): AlertDialog {
        return AlertDialog.Builder(this@MainActivity)
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton(
                "Yes"
            ) { _, _ -> finish() }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> dialog.dismiss() }
            .create()
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}