package com.example.faceexpression

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class ResultDialog: DialogFragment() {
    private lateinit var btnDone: Button
    private lateinit var tvResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resultdialog,
            container, false)

        var resultText = ""
        btnDone = view.findViewById(R.id.btnResult)
        tvResult = view.findViewById(R.id.tvResult)

        val bundle = arguments
        resultText = bundle!!.getString(
            FaceDetection.RESULT_TEXT
        )!!
        tvResult.text = resultText

        btnDone.setOnClickListener(
            View.OnClickListener { dismiss() })

        return view

    }

}