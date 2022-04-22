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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.heycode.iporesult.R
import com.heycode.iporesult.adapters.BoidAdapter
import com.heycode.iporesult.databinding.ActivityMainBinding
import com.heycode.iporesult.models.CaptchaData
import com.heycode.iporesult.models.CheckData
import com.heycode.iporesult.models.CompanyShare
import com.heycode.iporesult.utils.USER_BOID_SET
import com.heycode.iporesult.utils.dataFromSharedPref
import com.heycode.iporesult.utils.editorFromSharedPref
import com.heycode.iporesult.utils.hasInternetConnection
import com.heycode.iporesult.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BoidAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private var storedBoids: MutableSet<String> = mutableSetOf()

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
                svMainContainer.visibility = View.GONE
                clError.visibility = View.VISIBLE
                btnRetryError.setOnClickListener {
                    finish()
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                }
            }
        } else {
            if (dataFromSharedPref(this@MainActivity)
                    .getStringSet(USER_BOID_SET, null) != null
            ) {
                binding.tvMiddleTitle.visibility = View.VISIBLE
                storedBoids = dataFromSharedPref(this@MainActivity)
                    .getStringSet(USER_BOID_SET, null) as MutableSet<String>

                binding.apply {
                    if (storedBoids.isNotEmpty()) {
                        binding.tvMiddleTitle.visibility = View.VISIBLE
                        rvSavedBoid.apply {
                            val myAdapter =
                                BoidAdapter(storedBoids.toTypedArray(), this@MainActivity)
                            adapter = myAdapter
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            setHasFixedSize(false)
                        }
                    } else {
                        binding.tvMiddleTitle.visibility = View.GONE
                    }
                }
            } else {
                binding.tvMiddleTitle.visibility = View.GONE
            }

            // load data
            loadCompanyNames()

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
                            binding.svMainContainer.visibility = View.GONE
                            binding.btnSubmit.visibility = View.GONE
                            homeViewModel.checkResult(json)

                            val msg = homeViewModel.message.value.toString()
                            startActivity(
                                Intent(this@MainActivity, ResultActivity::class.java)
                                    .apply {
                                        putExtra(
                                            "msg",
                                            msg
                                        )
                                        putExtra("boid", json.boid)
                                    })
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun loadCompanyNames() {
        lifecycleScope.launch {
            homeViewModel.getHome()
            homeViewModel.homeContent.observe(this@MainActivity) { data ->
                companies = data.body.companyShareList
                captchaData = data.body.captchaData
                binding.apply {
                    clError.visibility = View.GONE
                    pbLoading.visibility = View.GONE
                    btnSubmit.visibility = View.VISIBLE
                    svMainContainer.visibility = View.VISIBLE
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
    }

    override fun onItemClick(position: Int) {
//        Toast.makeText(this, "hhhhh", Toast.LENGTH_SHORT).show()
        binding.tietBoid.setText(storedBoids.toTypedArray()[position])
    }

    override fun onItemLongClick(position: Int) {
        onLongClick(position)
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

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun onLongClick(position: Int): Boolean {
        val dialogBox = AlertDialog.Builder(this@MainActivity)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete ${storedBoids.toTypedArray()[position]}?")
            .setPositiveButton(
                "Yes"
            ) { d, _ ->
                storedBoids.remove(storedBoids.elementAt(position))
                editorFromSharedPref(this@MainActivity).putStringSet(
                    USER_BOID_SET,
                    storedBoids
                )
                val ss = BoidAdapter(storedBoids.toTypedArray(), this@MainActivity)
                binding.rvSavedBoid.adapter = ss
                d.dismiss()
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> dialog.dismiss() }
            .create()
        dialogBox.show()
        return true
    }
}