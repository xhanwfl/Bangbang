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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.*
import com.jambosoft.bangbang.model.UserInfoDTO
import com.nhn.android.naverlogin.OAuthLogin
import net.daum.mf.map.api.MapView

class UserFragment : Fragment() {
    var rootView : View ? = null
    lateinit var user : FirebaseUser
    lateinit var db : FirebaseFirestore
    lateinit var userInfoDTO : UserInfoDTO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_user, container, false)
        user = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()

        //프로필설정
        setProfile(requireContext(),rootView!!)

        //프로필수정 버튼
        val profileModifyButton = rootView?.findViewById<Button>(R.id.user_profile_modify_btn)
        profileModifyButton?.setOnClickListener {
            startActivityForResult(Intent(requireContext(),ProfileModifyActivity::class.java),200)
        }

        //로그아웃 버튼
        val logoutButton = rootView?.findViewById<TextView>(R.id.user_logout_textview)
        logoutButton?.setOnClickListener {
            var mOAuthInstance = OAuthLogin.getInstance()
            mOAuthInstance.logout(requireContext())

            mOAuthInstance.logoutAndDeleteToken(requireContext())

            Log.e("logout","클릭함")
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        //방 내놓기 버튼
        val putUpRoomButton = rootView?.findViewById<TextView>(R.id.frag_user_putuproom_textview)
        putUpRoomButton?.setOnClickListener {
            if(userInfoDTO.hp.equals("")){
                Toast.makeText(requireContext(),"전화번호를 먼저 등록해주세요",Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(requireContext(), PutUpRoomActivity::class.java))
            }
        }

        //내가 쓴 글 버튼
        val myContentButton = rootView?.findViewById<TextView>(R.id.frag_user_mycontent_textview)
        myContentButton?.setOnClickListener {
            var intent = Intent(requireContext(),MyContentActivity::class.java)
            intent.putExtra("kind","contents")
            startActivity(intent)
        }
        //내놓은 방 버튼
        val myRoomButton = rootView?.findViewById<TextView>(R.id.frag_user_myroom_textview)
        myRoomButton?.setOnClickListener {
            var intent = Intent(requireContext(),MyContentActivity::class.java)
            intent.putExtra("kind","rooms")
            startActivity(intent)
        }



        // Inflate the layout for this fragment
        return rootView!!
    }


    //프로필 가져오기
    fun setProfile(context : Context, rootView : View){

        db.collection("userInfo").document(user!!.uid).get().addOnSuccessListener { document ->
            if(document!=null){
                Log.e("프로필","가져오기 성공")
                userInfoDTO = document.toObject<UserInfoDTO>()!!
                if(userInfoDTO!!.name.equals("")){ //이름이 null일경우
                    startActivityForResult(Intent(context, ProfileModifyActivity::class.java),200)
                }else{ //이름이 null이 아닐경우
                    val nameTextView = rootView.findViewById<TextView>(R.id.user_name)
                    val emailTextView = rootView.findViewById<TextView>(R.id.user_email)
                    val profileImageView = rootView.findViewById<ImageView>(R.id.frag_user_profile_imageview)

                    nameTextView.text = userInfoDTO.name
                    emailTextView.text = userInfoDTO.email
                    Log.e("url profileUrl",userInfoDTO.profileUrl)



                    //프로필이미지 가져오기
                    val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${user!!.uid}")
                    storageRef.downloadUrl.addOnSuccessListener {  //storage에 이미지를 저장해둔경우
                        Glide.with(context).load(it).thumbnail(0.1f).apply(
                            RequestOptions().centerCrop()).into(profileImageView)
                    }.addOnFailureListener{ //storage에 없을경우 네이버프로필을 가져옴
                        Glide.with(context).load(userInfoDTO.profileUrl.toUri()).thumbnail(0.1f).apply(
                            RequestOptions().centerCrop()).into(profileImageView)
                    }


                }
            }else{
                Log.e("프로필", "프로필이 없습니다.")
            }

        }.addOnFailureListener {
            Log.e("프로필","가져오기 실패")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        //프로필 수정
        if(resultCode== AppCompatActivity.RESULT_OK &&requestCode==200){
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            var name = data?.getStringExtra("name")
            var hp = data?.getStringExtra("hp")
            var uri = data?.getStringExtra("uri")

            db.collection("userInfo").document(user!!.uid).update("name",name)
            db.collection("userInfo").document(user!!.uid).update("hp",hp)

            if(!uri.equals("")){
                val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${user!!.uid}")
                val uploadTask = storageRef.putFile(uri!!.toUri()).addOnSuccessListener {
                    db.collection("userInfo").document(user.uid).update("profileUrl",storageRef.downloadUrl.toString())
                    Log.e("url업로드 성공","제발~~")

                }
            }
        }
    }

}