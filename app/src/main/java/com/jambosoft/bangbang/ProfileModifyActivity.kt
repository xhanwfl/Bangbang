package com.jambosoft.bangbang

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.net.toUri
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.model.UserInfoDTO
import java.util.concurrent.TimeUnit

class ProfileModifyActivity : AppCompatActivity() {
    var imageUri = ""
    lateinit var auth : FirebaseAuth
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    //lateinit var callbacks : PhoneAuthProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)

        auth = FirebaseAuth.getInstance()
        //프로필 이미지버튼
        val profileImageView = findViewById<ImageView>(R.id.profile_modify_profile_imageview)
        profileImageView.setOnClickListener {
            setImage()
        }


        //닫기 버튼
        val closeButton = findViewById<Button>(R.id.profile_modify_close_btn)
        closeButton.setOnClickListener {
            finish()
        }

        //이름
        val nameEditText = findViewById<EditText>(R.id.profile_modify_name_edittext)

        //핸드폰번호
        val hpEditText = findViewById<EditText>(R.id.profile_modify_hp_edittext)

        //인증레이아웃
        val authLayout = findViewById<LinearLayout>(R.id.profile_modify_auth_layout)

        //인증번호
        val authNumberEditText = findViewById<EditText>(R.id.profile_modify_auth_number)

        //인증완료버튼
        val authButton = findViewById<Button>(R.id.profile_modify_auth_btn)

        //인증요청버튼
        val requireAuthTextView = findViewById<TextView>(R.id.profile_modify_requireauth_textview)
        requireAuthTextView.setOnClickListener {
            authLayout.visibility = View.VISIBLE
            val number="+${hpEditText.text}"
            Toast.makeText(applicationContext,"${number}",Toast.LENGTH_SHORT).show()
            sendAuthNumber(number)

        }

        //변경하기 버튼
        val modifyButton = findViewById<Button>(R.id.profile_modify_modify_btn)
        modifyButton.setOnClickListener {

            val name = nameEditText.text.toString()

            val hp =hpEditText.text.toString()


            if(name.equals("")||hp.equals("")){ //내용을 입력하지 않을경우
                Toast.makeText(this,"내용을 모두 입력하세요",Toast.LENGTH_SHORT).show()
            }else{ //내용전부 입력할경우

                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("hp",hp)
                intent.putExtra("uri",imageUri)


                Log.e("uri putExtra부분",imageUri)

                setResult(RESULT_OK,intent)
                finish()
            }
        }


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //번호인증이 끝난 상태로 따로 인증번호를 입력할 필요없는 상태
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Toast.makeText(applicationContext,"인증성공",Toast.LENGTH_SHORT).show()
            }

            //번호인증이 실패한 상태 (없는전화번호)
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext,"인증실패",Toast.LENGTH_SHORT).show()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }



            //번호는 확인 되었으나 인증코드를 입력해야 하는 상태
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                authButton.setOnClickListener {
                    var number = authNumberEditText.text.toString()
                    var sms = PhoneAuthProvider.getCredential(verificationId, number).smsCode

                    Toast.makeText(applicationContext,sms,Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(applicationContext,"인증이에요인증",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun sendAuthNumber(phoneNumber : String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun setImage(){
        var intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        startActivityForResult(intent, 500)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== RESULT_OK&&requestCode==500){
            imageUri = data?.data.toString()
            Log.e("uri2",imageUri)

            val profileImageView = findViewById<ImageView>(R.id.profile_modify_profile_imageview)
            profileImageView.setImageURI(imageUri.toUri())

        }
    }
}