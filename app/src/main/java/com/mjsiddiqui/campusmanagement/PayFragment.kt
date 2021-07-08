package com.mjsiddiqui.campusmanagement


import Activity.FailedActivity
import Activity.SuccessActivity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.shreyaspatil.easyupipayment.EasyUpiPayment
import com.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import com.shreyaspatil.easyupipayment.model.PaymentApp
import com.shreyaspatil.easyupipayment.model.TransactionDetails
import com.shreyaspatil.easyupipayment.model.TransactionStatus
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_pay.*

class PayFragment : Fragment(){
    private lateinit var easyUpiPayment:EasyUpiPayment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pay, container, false)
        val button:Button = view.findViewById(R.id.pay_btn)
        val mContext = activity
        val transId = "TID" + System.currentTimeMillis()
        val nameEditText:EditText = view.findViewById(R.id.user_Name)
        val upiEditText:EditText = view.findViewById(R.id.upi)
        val desEditText:EditText = view.findViewById(R.id.description)
        val amountEditText:EditText = view.findViewById(R.id.amount)

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bill_Name.text = s
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        upiEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bill_Upi.text = s
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        amountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bill_Amount.text = s
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        button.setOnClickListener {
            if (nameEditText.text.toString().isNotEmpty() && amountEditText.text.toString().isNotEmpty() && upiEditText.text.toString().isNotEmpty())
            {
                val uName = nameEditText.text.toString()
                val upi = upiEditText.text.toString()
                val des = desEditText.text.toString()
                val uAmount = amountEditText.text.toString()
                val pay = PaymentApp.ALL

                try {
                    easyUpiPayment = EasyUpiPayment(mContext!!){
                        paymentApp = pay
                        description = des
                        payeeVpa = upi
                        payeeName = uName
                        transactionId = transId
                        transactionRefId = transId
                        amount = uAmount
                    }
                    easyUpiPayment.startPayment()
                    easyUpiPayment.setPaymentStatusListener(object : PaymentStatusListener {
                        override fun onTransactionCancelled() {
                            Toast.makeText(mContext, "Transaction Canceled", Toast.LENGTH_SHORT).show()
                        }

                        override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
                            when (transactionDetails.transactionStatus) {
                                TransactionStatus.SUCCESS -> {
                                    val obj = Intent(context, SuccessActivity::class.java)
                                    obj.putExtra("Mj5","Transaction Complete")
                                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@PayFragment)?.commit()
                                    startActivity(obj)
                                }
                                TransactionStatus.FAILURE -> {
                                    val obj = Intent(context, FailedActivity::class.java)
                                    obj.putExtra("Mj5","Transaction Failed")
                                    startActivity(obj)
                                }
                            }
                        }
                    })
                }catch (e:Exception){
                    Toast.makeText(mContext,"${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(mContext,"Please fill all details",Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}