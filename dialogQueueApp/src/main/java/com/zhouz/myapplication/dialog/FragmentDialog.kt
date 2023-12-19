package com.zhouz.myapplication.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.zhouz.myapplication.R
import com.zhouz.myapplication.databinding.FragmentDialogBinding


/**
 * @author:zhouz
 * @date: 2023/12/18 10:30
 * description：fragmentDialog
 */
class FragmentDialog : DialogFragment() {

    companion object {
        fun newInstance(extra: String): FragmentDialog {
            val args = Bundle()
            args.putString("extra", extra)
            val fragment = FragmentDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private var mBinding: FragmentDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dialog, null)
        mBinding = DataBindingUtil.bind(view)
        return view
    }

}