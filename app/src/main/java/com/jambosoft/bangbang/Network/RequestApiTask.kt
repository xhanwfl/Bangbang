package com.jambosoft.bangbang.Network

import android.content.Context
import org.json.JSONException

import android.widget.Toast

import org.json.JSONObject

import com.nhn.android.naverlogin.OAuthLogin

import android.os.AsyncTask
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.UserInfoDTO
import android.content.Intent
import com.jambosoft.bangbang.LoginActivity
import com.jambosoft.bangbang.MainActivity


class RequestApiTask(mContext: Context, mOAuthLoginModule: OAuthLogin) : AsyncTask<Void?, Void?, String>() {
    private val mContext: Context
    private val mOAuthLoginModule: OAuthLogin
    val TAG = "!RequestApiTask"
    override fun onPreExecute() {}

    override fun onPostExecute(content: String) {
        try {
            val loginResult = JSONObject(content)
            if (loginResult.getString("resultcode") == "00") {
                val response = loginResult.getJSONObject("response")
                val id = response.getString("id")
                val email = response.getString("email")
                val profileUrl = response.getString("profile_image")

                var auth = FirebaseAuth.getInstance()
                createAndLoginEmail(auth,email,id,profileUrl) //네이버 고유 식별id값을 패스워드로 사용



            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    init {
        this.mContext = mContext
        this.mOAuthLoginModule = mOAuthLoginModule
    }

    override fun doInBackground(vararg params: Void?): String {
        val url = "https://openapi.naver.com/v1/nid/me"
        val at = mOAuthLoginModule.getAccessToken(mContext)
        return mOAuthLoginModule.requestApi(mContext, at, url)
    }

    //아이디 생성 또는 로그인
    fun createAndLoginEmail(auth : FirebaseAuth, email : String, password : String, profileUrl : String){

        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //아이디 생성이 성공했을 경우
                        Log.d(TAG,"회원가입 성공")
                    uploadUserProfile(auth,email,profileUrl,password)

                    //다음페이지 호출
                    val intent = Intent(mContext,MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티 스택제거

                } else if (task.exception?.message.isNullOrEmpty()) {
                    //회원가입 에러가 발생했을 경우
                    Log.d(TAG,"회원가입 실패")

                } else {
                    //아이디 생성도 안되고 에러도 발생되지 않았을 경우 로그인
                    signinEmail(auth,email,password)
                    Log.d(TAG,"Login 성공")
                }
            }
    }

    //로그인 메소드
    fun signinEmail(auth : FirebaseAuth, email : String, password : String) {

        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //로그인 성공 및 다음페이지 호출
                    val intent = Intent(mContext,MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) //액티비티 스택제거
                    mContext.startActivity(intent)
                } else {
                    //로그인 실패
                    Log.e("Login","로그인실패")
                }
            }
    }

    fun uploadUserProfile(auth: FirebaseAuth, email : String, profileUrl : String, token : String){
        val db = FirebaseFirestore.getInstance()
        db.collection("userInfo").document(auth.currentUser!!.uid).set(UserInfoDTO(email,profileUrl,"이름을 변경해주세요","",false,token)).addOnSuccessListener {
            Log.e("!profile","등록성공")
        }.addOnFailureListener{
            Log.e("!profile","등록실패")
        }
    }

}