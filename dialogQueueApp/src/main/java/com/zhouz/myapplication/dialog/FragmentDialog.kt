package com.zhouz.myapplication.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.zhouz.dialogqueue.IDialogQ
import com.zhouz.myapplication.R
import com.zhouz.myapplication.databinding.FragmentDialogBinding


/**
 * @author:zhouz
 * @date: 2023/12/18 10:30
 * description：fragmentDialog
 */
class FragmentDialog : DialogFragment(), IDialogQ {

    companion object {
        fun newInstance(extra: String, content: String): FragmentDialog {
            val args = Bundle()
            args.putString("extra", extra)
            args.putString("content", content)
            val fragment = FragmentDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private val title get() = arguments?.getString("title") ?: "FragmentDialog"
    private val content get() = arguments?.getString("content")


    private var mBinding: FragmentDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dialog, null)
        mBinding = DataBindingUtil.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.mDialog?.mTittleTv?.text = title
        mBinding?.mDialog?.mContentTv?.text = content

        mBinding?.mDialog?.mCancelTv?.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}