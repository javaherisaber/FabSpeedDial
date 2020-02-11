package ir.logicbase.fabspeeddial.app

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ir.logicbase.fabspeeddial.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnSpeedDialClickListener(
            items = speedDialItems(),
            onOptionClickListener = { _, index ->
                Toast.makeText(this, "You clicked at $index", Toast.LENGTH_SHORT).show()
            },
            textAppearance = R.style.App_TextAppearance_Caption,
            runGuard = ::shouldGuardOnDisplayOptions // false if you want speed dial to be displayed
        )
    }

    private fun shouldGuardOnDisplayOptions() = false

    private fun speedDialItems(): List<Pair<CharSequence?, Drawable>> {
        val tint = colorAt(R.color.colorAccent)
        return listOf(
            "Save".color(tint) to drawableAt(R.drawable.ic_save_black_24dp).withTint(tint),
            "Schedule".color(tint) to drawableAt(R.drawable.ic_schedule_black_24dp).withTint(tint),
            "Security".color(tint) to drawableAt(R.drawable.ic_security_black_24dp).withTint(tint)
        )
    }
}
