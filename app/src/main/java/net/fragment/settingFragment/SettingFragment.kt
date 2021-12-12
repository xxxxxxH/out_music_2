package net.fragment.settingFragment

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.layout_fragment_setting.*
import net.basicmodel.PrivacyActivity
import net.basicmodel.R
import net.fragment.BaseFragment
import net.utils.CommonUtils
import net.utils.MMKVUtils

class SettingFragment:BaseFragment(), TimePickerDialog.OnTimeSetListener {
    override fun getLayout(): Int {
        return R.layout.layout_fragment_setting
    }

    override fun initView() {
        ll_clear_cache.setOnClickListener {
            CommonUtils.get().deleteCache(requireContext())
            Toast.makeText(context, "cache has been cleared", Toast.LENGTH_SHORT).show()
        }
        ll_check_cache.setOnClickListener {
            CommonUtils.get().initializeCache(requireContext())
        }
        rel_set_timer.setOnClickListener {
            CommonUtils.get().ShowTimePickerDialog(this, requireActivity() as AppCompatActivity)
        }
        ll_clear_history.setOnClickListener {
            MMKV.defaultMMKV()!!.remove("history")
            Toast.makeText(context, "History clear successfully", Toast.LENGTH_SHORT).show()
        }
        ll_privacy.setOnClickListener {
            val intent = Intent(context, PrivacyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {
        checkbox_time.isChecked = true
        txt_timer.text = CommonUtils.get().timeFormate(hourOfDay, minute)
    }
}