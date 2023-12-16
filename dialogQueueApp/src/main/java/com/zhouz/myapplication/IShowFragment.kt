package com.zhouz.myapplication

import androidx.fragment.app.FragmentActivity
import com.zhouz.myapplication.fragment.FirstFragment
import com.zhouz.myapplication.fragment.SecondFragment

interface IShowFragment {

    var firstFragment: FirstFragment?

    var secondFragment: SecondFragment?

    fun showFirstFragment(activity: FragmentActivity) {
        if (firstFragment == null) {
            firstFragment = FirstFragment(R.layout.fragment_main)
            val bt = activity.supportFragmentManager.beginTransaction()
            bt.add(R.id.container, firstFragment!!)
            bt.commitAllowingStateLoss()
        } else {
            val bt = activity.supportFragmentManager.beginTransaction()
            bt.show(firstFragment!!)
        }
    }

    fun showSecondFragment(activity: FragmentActivity) {
        if (secondFragment == null) {
            secondFragment = SecondFragment(R.layout.fragment_main_2)
            val bt = activity.supportFragmentManager.beginTransaction()
            bt.add(R.id.container, secondFragment!!)
            bt.commitAllowingStateLoss()
        } else {
            val bt = activity.supportFragmentManager.beginTransaction()
            bt.show(secondFragment!!)
        }
    }

    fun back(activity: FragmentActivity): Boolean {
        if (firstFragment != null) {
            val bt = activity.supportFragmentManager.beginTransaction()

            bt.remove(firstFragment!!)
            bt.commitAllowingStateLoss()
        }
        return false
    }
}