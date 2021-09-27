package com.jambosoft.bangbang

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jambosoft.bangbang.Network.RequestApiTask
import com.jambosoft.bangbang.model.UserInfoDTO
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import org.json.JSONObject
import java.util.*

class LoginActivity : AppCompatActivity() {
    lateinit var mOAuthLoginInstance: OAuthLogin
    lateinit var mContext: Context
    lateinit var callbackManager : CallbackManager
    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore
    val TAG = "!LoginActivity"
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //  네이버 아이디로 로그인
        val naver_client_id = getString(R.string.naver_client_id)
        val naver_client_secret = getString(R.string.naver_client_secret)
        val naver_client_name = getString(R.string.naver_client_name)
        mContext = this
        mOAuthLoginInstance = OAuthLogin.getInstance()


        //로그아웃
        /*
        mOAuthLoginInstance.logout(this)
        mOAuthLoginInstance.logoutAndDeleteToken(this)*/


        //로그인
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)

        //네이버아이디로 로그인 버튼
        val buttonOAuthLoginImg = findViewById<OAuthLoginButton>(R.id.buttonOAuthLoginImg)
        buttonOAuthLoginImg.setOAuthLoginHandler(mOAuthLoginHandler)

        //페이스북으로 로그인
        callbackManager = CallbackManager.Factory.create()
        val facebookLoginButton = findViewById<Button>(R.id.facebook_login_btn)
        facebookLoginButton.setOnClickListener {
            facebookLogin()
        }

    }

    fun facebookLogin(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this,Arrays.asList("public_profile","email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(applicationContext,"네트워크에 접속할 수 없습니다.",Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun handleFacebookAccessToken(token : AccessToken?){
        val credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth.signInWithCredential(credential).addOnSuccessListener { result ->
            Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show()

            db.collection("userInfo").document(result.user!!.uid).get().addOnSuccessListener { document ->
                if(!document.exists()){ //문서가 존재하지 않을 경우

                    val userInfoDTO = UserInfoDTO()
                    userInfoDTO.email = result.user!!.email.toString()
                    userInfoDTO.token = token.token
                    userInfoDTO.tokenType = "f"

                    db.collection("userInfo").document(result.user!!.uid).set(userInfoDTO).addOnSuccessListener {
                        Toast.makeText(this,"유저등록 성공",Toast.LENGTH_SHORT).show()
                        requestPermission() //permission check 후 MainActivity 실행
                    }.addOnFailureListener {
                        Toast.makeText(this,"유저등록 실패",Toast.LENGTH_SHORT).show()
                    }
                }else{// 문서가 존재할 경우
                    requestPermission()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }






    //네이버로그인 핸들러
    val mOAuthLoginHandler: OAuthLoginHandler = @SuppressLint("HandlerLeak")
    object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
//                val accessToken: String = mOAuthLoginModule.getAccessToken(baseContext)
//                val refreshToken: String = mOAuthLoginModule.getRefreshToken(baseContext)
//                val expiresAt: Long = mOAuthLoginModule.getExpiresAt(baseContext)
//                val tokenType: String = mOAuthLoginModule.getTokenType(baseContext)
//                var intent = Intent(this, )
                val accessToken = mOAuthLoginInstance.getAccessToken(baseContext)
                Log.d("Token",accessToken)

                //api통신 , 여기서 firebase아이디를 생성하거나 로그인함
                var requestApiTask = RequestApiTask(mContext,mOAuthLoginInstance).execute()

                requestPermission()
            } else {
                val errorCode: String = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)

                Toast.makeText(
                    baseContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    //권한 요청 후 mainActivity 실행
    private fun requestPermission(){
        var permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            //설명이 필요한지
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                //설명 필요 (사용자가 요청을 거부한 적이 있음)
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            }else{
                //설명 필요하지 않음
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
            }
        }else{
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
            //권한 허용
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSIONS_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                }else{
                   Toast.makeText(this,"위치 권한을 허용으로 설정해주세요",Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


}