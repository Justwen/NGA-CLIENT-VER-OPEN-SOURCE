package gov.anzong.androidnga.activity.compose

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.justwen.androidnga.ui.compose.BaseComposeFragment

class TemplateComposeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentStr = intent.getStringExtra("fragment")
        if (fragmentStr != null) {
            commitFragment(fragmentStr)
        }
    }

    private fun commitFragment(fragmentStr: String) {
        try {
            val fragment :BaseComposeFragment = Class.forName(fragmentStr).getDeclaredConstructor().newInstance() as BaseComposeFragment
            fragment.arguments = intent.extras
            supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

}