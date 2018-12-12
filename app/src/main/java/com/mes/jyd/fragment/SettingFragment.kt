package com.mes.jyd.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import com.mes.jyd.R
import org.jetbrains.anko.ctx

/**
 *
 * Created by pandanxin on 2018/1/11.
 */
class SettingFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 加载xml资源文件
        addPreferencesFromResource(R.xml.settings)
        preferenceManager.sharedPreferencesName = "settings"
    }

    override fun onResume() {
        super.onResume()

        sharedPreferences = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)

        // we want to watch the preference values' changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val preferencesMap = sharedPreferences.all
        // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
        for (preferenceEntry in preferencesMap.entries) {
            val preference=findPreference(preferenceEntry.key)
            if (preference is EditTextPreference) {
                updateSummary(preference,preferenceEntry.value.toString())
            }else if (preference is SwitchPreference){
                preference.isChecked=preferenceEntry.value.toString().toBoolean()
            }else if (preference is ListPreference){
                preference.value=preferenceEntry.value.toString()
            }
        }
    }

    override fun onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                           key: String) {
        val preferencesMap = sharedPreferences.all

        // get the preference that has been changed
        val changedPreference = findPreference(key)
        // and if it's an instance of EditTextPreference class, update its summary
        if (changedPreference is EditTextPreference) {
            updateSummary(changedPreference,preferencesMap[key].toString())
        } else{
            if(changedPreference is ListPreference){
              //  changedPreference.text=text
                changedPreference.value=preferencesMap[key].toString()
                changedPreference.summary = preferencesMap[key].toString()
            }
        }
    }

    private fun updateSummary(preference: EditTextPreference, text:String) {
        // set the EditTextPreference's summary value to its current text
        preference.text=text
        preference.summary = text
    }


}
