package com.heycode.iporesult.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.heycode.iporesult.R
import com.heycode.iporesult.databinding.ActivityResultBinding
import com.heycode.iporesult.utils.USER_BOID_SET
import com.heycode.iporesult.utils.dataFromSharedPref
import com.heycode.iporesult.utils.editorFromSharedPref
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_IpoResult)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bundle: Bundle? = intent.extras
        // checking if intent has sent any data
        // if sent then show edit else show add
        if (bundle != null) {
            val msg = bundle.getString("msg", "error")
            val gotBoid = bundle.getString("boid", null)

            // set tool bar
            supportActionBar?.apply {
                title = "BOID: $gotBoid"
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            }
            // back press
            onBackPressedDispatcher.addCallback(this) {
                // Handle the back button event
                finish()
                startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            }
            // config animation
            val party = Party(
                colors = listOf(Color.YELLOW, Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA),
                angle = 0,
                spread = 360,
                speed = 1f,
                maxSpeed = 10f,
                fadeOutEnabled = true,
                timeToLive = 3000L,
                shapes = listOf(Shape.Square, Shape.Circle),
                size = listOf(Size(12)),
                position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
                emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(300)
            )

            binding.apply {
                if (msg.lowercase().contains("allot") && !msg.lowercase().contains("sorry")) {
                    resultCelebrate.start(party)
                    tvResultMsg.setOnClickListener { resultCelebrate.start(party) }
                }
                tvResultMsg.text = msg

                // get boids from saved
                val prevSet =
                    dataFromSharedPref(this@ResultActivity)
                        .getStringSet(USER_BOID_SET, null)
                        ?.toTypedArray()

                val new = ArrayList<String>()
                prevSet?.forEach {
                    if (!it.isNullOrBlank()) {
                        new.add(it)
                    }
                }
                btnSave.setOnClickListener {
                    Toast.makeText(this@ResultActivity, "Your BOID is saved.", Toast.LENGTH_SHORT)
                        .show()
                    new.add(gotBoid)
                    editorFromSharedPref(this@ResultActivity).putStringSet(
                        USER_BOID_SET,
                        new.toMutableSet()
                    ).apply()

                    startActivity(Intent(this@ResultActivity, MainActivity::class.java))
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Sorry no data found", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}