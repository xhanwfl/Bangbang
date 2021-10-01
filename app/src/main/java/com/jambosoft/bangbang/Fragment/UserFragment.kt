package com.jambosoft.bangbang.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.*
import com.jambosoft.bangbang.model.UserInfoDTO
import com.nhn.android.naverlogin.OAuthLogin
import net.daum.mf.map.api.MapView

class UserFragment : Fragment() {
    var rootView: View? = null
    lateinit var mContext: Context
    lateinit var user: FirebaseUser
    lateinit var db: FirebaseFirestore
    lateinit var userInfoDTO: UserInfoDTO
    lateinit var nameTextView: TextView
    lateinit var alramImageView : ImageView
    val TAG = "!UserFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_user, container, false)
        user = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()
        mContext = requireContext()
        nameTextView = rootView!!.findViewById(R.id.user_name)

        //프로필설정
        setProfile()

        //알람 설정
        alramImageView = rootView!!.findViewById(R.id.frag_user_alram_imageview)
        alramImageView.setOnClickListener{
            val intent = Intent(requireContext(), MyContentActivity::class.java)
            intent.putExtra("kind", "rooms")
            startActivity(intent)
        }


        //프로필수정 버튼
        val profileModifyButton = rootView?.findViewById<Button>(R.id.user_profile_modify_btn)
        profileModifyButton?.setOnClickListener {
            startActivityForResult(Intent(requireContext(), ProfileModifyActivity::class.java), 200)
        }

        //로그아웃 버튼
        val logoutButton = rootView?.findViewById<TextView>(R.id.frag_user_logout_textview)
        logoutButton?.setOnClickListener {
            when(userInfoDTO.tokenType){
                "n" ->{
                    val mOAuthInstance = OAuthLogin.getInstance()
                    mOAuthInstance.logout(requireContext())

                    mOAuthInstance.logoutAndDeleteToken(requireContext())

                    Log.e(TAG, "네이버 로그아웃")
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    activity?.finish()
                }
                "f" -> {
                    LoginManager.getInstance().logOut()
                    Log.d(TAG,"페이스북로그아웃")
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    activity?.finish()
                }
            }
            /*if (userInfoDTO.tokenType.equals("n")){
                var mOAuthInstance = OAuthLogin.getInstance()
                mOAuthInstance.logout(requireContext())

                mOAuthInstance.logoutAndDeleteToken(requireContext())

                Log.e("logout", "클릭함")
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }else if(userInfoDTO.tokenType.equals("f")){

            }*/
        }

        //문의한방 버튼
        val myInquireButton = rootView?.findViewById<Button>(R.id.frag_user_myinquire_btn)
        myInquireButton?.setOnClickListener {
            var intent = Intent(mContext,MyInquireActivity::class.java)
            startActivity(intent)
        }

        //방 내놓기 버튼
        val putUpRoomButton = rootView?.findViewById<TextView>(R.id.frag_user_putuproom_textview)
        putUpRoomButton?.setOnClickListener {
            if (!userInfoDTO.hpAuth) { //전화번호 인증이 안되었을경우
                var intent = Intent(requireContext(), HpAuthActivity::class.java)
                startActivityForResult(intent, 300)
            } else { //인증 완료
                if(userInfoDTO.name.equals("이름을 변경해주세요")) { //아직 이름을 변경하지 않음
                    Toast.makeText(requireContext(),"이름을 먼저 변경해주세요.",Toast.LENGTH_SHORT).show()
                }else{ //아무이상없을때
                    var intent = Intent(requireContext(), PutUpRoomActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        //내가 쓴 글 버튼
        val myContentButton = rootView?.findViewById<TextView>(R.id.frag_user_mycontent_textview)
        myContentButton?.setOnClickListener {
            var intent = Intent(requireContext(), MyContentActivity::class.java)
            intent.putExtra("kind", "contents")
            startActivity(intent)
        }
        //내놓은 방 버튼
        val myRoomButton = rootView?.findViewById<TextView>(R.id.frag_user_myroom_textview)
        myRoomButton?.setOnClickListener {
            var intent = Intent(requireContext(), MyContentActivity::class.java)
            intent.putExtra("kind", "rooms")
            startActivity(intent)
        }
        //설정 버튼
        val settingButton = rootView?.findViewById<TextView>(R.id.frag_user_setting_textview)
        settingButton?.setOnClickListener {
            Toast.makeText(requireContext(),"준비중입니다.",Toast.LENGTH_SHORT).show()
        }


        // Inflate the layout for this fragment
        return rootView!!
    }


    //프로필 가져오기
    fun setProfile() {

        db.collection("userInfo").document(user!!.uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                Log.e("프로필", "가져오기 성공")
                userInfoDTO = document.toObject<UserInfoDTO>()!!
                if (userInfoDTO!!.name.equals("이름을 변경해주세요")) { //이름이 null일경우 이름수정이벤트
                    startActivityForResult(Intent(mContext, ProfileModifyActivity::class.java), 200)
                } else { //이름이 null이 아닐경우

                    //email, profile 설정
                    val emailTextView = rootView?.findViewById<TextView>(R.id.user_email)
                    val profileImageView =
                        rootView?.findViewById<ImageView>(R.id.frag_user_profile_imageview)

                    nameTextView?.text = userInfoDTO.name
                    emailTextView?.text = userInfoDTO.email
                    Log.e("url profileUrl", userInfoDTO.profileUrl)

                    if(userInfoDTO.alramCount>0){//알람이 있을경우 알람 아이콘 변경
                        alramImageView.setImageResource(R.drawable.ic_favorite)
                    }

                    //프로필이미지 가져오기
                    if(!userInfoDTO.profileUrl.equals("")){
                        Glide.with(mContext).load(userInfoDTO.profileUrl.toUri()).thumbnail(0.1f).apply(
                            RequestOptions().centerCrop()
                        ).into(profileImageView!!)
                    }
                }
            } else {
                Log.e("프로필", "프로필이 없습니다.")
            }
        }.addOnFailureListener {
            Log.e("프로필", "가져오기 실패")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        //프로필 수정
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 200) {
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            var name = data?.getStringExtra("name")
            var uri = data?.getStringExtra("uri")
            val email = data?.getStringExtra("email")

            db.collection("userInfo").document(user!!.uid).update("name", name)
                .addOnSuccessListener {
                    nameTextView.text = name
                }
            if(!email.equals("")){ //이메일이 입력됐을경우 업데이트
                db.collection("userInfo").document(user!!.uid).update("email", email)
                    .addOnSuccessListener {
                        nameTextView.text = name
                    }
            }

            if (!uri.equals("")) { //uri가 있을경우 이미지 업로드
                val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${user!!.uid}")
                storageRef.putFile(uri!!.toUri()).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener {
                        db.collection("userInfo").document(user.uid)
                            .update("profileUrl", it.toString())
                            .addOnSuccessListener {
                                Log.e("!userFragment", "url업로드 성공~~")
                                Toast.makeText(requireContext(),"프로필사진 변경 완료",Toast.LENGTH_SHORT).show()
                                setProfile()
                            }
                    }
                }
            }
        }
        //전화번호인증
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 300) {
            userInfoDTO.hpAuth = true
            Toast.makeText(requireContext(),"전화번호 인증 완료.",Toast.LENGTH_SHORT).show()
        }
    }
}