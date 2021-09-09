package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.model.UserInfoDTO
import java.util.concurrent.TimeUnit
import kotlin.math.sign

class HpAuthActivity : AppCompatActivity() {
    lateinit var user : FirebaseUser
    lateinit var auth : FirebaseAuth
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hp_auth)

        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

        //전화번호
        val hpEditText = findViewById<EditText>(R.id.hpauth_hp_edittext)
        //인증요청버튼
        val requireAuthButton = findViewById<TextView>(R.id.hpauth_requireauth_textview)
        //인증번호
        val authNumberEditText = findViewById<EditText>(R.id.hpauth_authnumber_edittext)
        //인증하기버튼
        val authButton = findViewById<Button>(R.id.hpauth_auth_btn)

        requireAuthButton.setOnClickListener {
            var hpText = hpEditText.text.toString()
            if(hpText.equals("")){
                Toast.makeText(this,"전화번호를 입력해주세요",Toast.LENGTH_SHORT).show()
            }else{
                var hp = "+8210${hpText}"
                val options = PhoneAuthOptions.newBuilder()
                    .setPhoneNumber(hp)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this@HpAuthActivity)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)

                Log.d("!HpAuthActivity","인증요청 보냄 ${hp}")
            }
        }


        //콜백
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //이미 로그인된 상태
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("!HpAuthActivity", "onVerificationCompleted:$credential")
                //signInWithPhoneAuthCredential(credential)
            }

            //핸드폰번호가 존재하지 않을경우
            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("!HpAuthActivity", "인증실패", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            //인증번호 전송된 상태
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("!HpAuthActivity", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                authButton.setOnClickListener {
                    var code = authNumberEditText.text.toString()
                    val credential = PhoneAuthProvider.getCredential(verificationId,code)
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { //인증번호 일치
                    Log.d("!HpAuthActivity", "로그인 인증 성공")
                    val db = FirebaseFirestore.getInstance()
                    db.collection("userInfo").document(user.uid).get().addOnSuccessListener {
                        val dto = it.toObject(UserInfoDTO::class.java)
                        auth.signInWithEmailAndPassword(dto!!.email,dto.token).addOnSuccessListener {
                            dto.hpAuth = true
                            db.collection("userInfo").document(user.uid).set(dto)
                            Log.d("!HpAuthActivity","재로그인 성공 | hpAuth = ${dto.hpAuth} ")
                        }
                    }

                    //
                } else { //인증번호 불일치
                    Log.w("!HpAuthActivity", "로그인 인증 실패", task.exception)
                    Toast.makeText(this,"인증번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }




}