package com.mes.jyd.delegate

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.mes.jyd.R

/**
 * 播放提示音
 * Created by pandanxin on 2017/11/25.
 */
class SoundUtil{

    private lateinit var sp:SoundPool
    private lateinit var soundMap:Map<Int,Int>
    lateinit var context:Context

    @Suppress("DEPRECATION")
    fun initSoundPool(ct:Context){
        context=ct
//        sp= SoundPool(1,AudioManager.STREAM_MUSIC,1)
        sp= SoundPool(10,AudioManager.STREAM_ALARM,0)
        soundMap=mapOf(1 to sp.load(context, R.raw.msg,1))
    }

    fun play(sound:Int,number:Int){
        val am:AudioManager=context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val audioMaxVolume:Float=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val audioCurrentVolume:Float=am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volumnRatio:Float=audioCurrentVolume/audioMaxVolume

        sp.play(soundMap[sound]!!,audioCurrentVolume,audioCurrentVolume,number,number,volumnRatio)
        //sp.play(soundMap[1]!!,1f,1f,0,0,1f)
    }
}