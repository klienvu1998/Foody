package com.example.foody.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foody.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*

@Suppress("DEPRECATION")
class LoginActivity: AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, FirebaseAuth.AuthStateListener {

    companion object {
        const val CODE_LOGIN_GOOGLE: Int = 99
        var CHECK_LOGIN = 0
    }

    private lateinit var btnGoogleLogin: SignInButton
    private lateinit var btnFbLogin: LoginButton
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.layout_loginscreen)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        LoginManager.getInstance().logOut()
        mapping()
        createClientLoginGoogle()
        loginFacebook()
    }

    private fun loginFacebook() {
        callbackManager = CallbackManager.Factory.create()
        btnFbLogin.setReadPermissions("email", "public_profile")
        btnFbLogin.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                CHECK_LOGIN = 2
                val tokenId: String = result?.accessToken!!.token
                googleCredential(tokenId)
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
            }

        })
    }

    override fun onStart() {
        super.onStart()
        btnGoogleLogin.setOnClickListener(this)
        btnFbLogin.setOnClickListener(this)
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }

    private fun mapping() {
        btnGoogleLogin = findViewById(R.id.btn_login_google)
        btnFbLogin = findViewById(R.id.btn_login_facebook)
    }

    // create client to login google
    private fun createClientLoginGoogle() {
        val googleSignInOptions: GoogleSignInOptions? = GoogleSignInOptions.Builder()
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()

        googleApiClient = googleSignInOptions?.let {
            GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, it)
                .build()
        }!!
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onClick(v: View?) {
        var id = v?.id
        when (id) {
            R.id.btn_login_google -> {
                loginWithGoogle()
            }
        }
    }

    // open form login by google
    private fun loginWithGoogle() {
        CHECK_LOGIN = 1
        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent, CODE_LOGIN_GOOGLE)
    }

    // get token ID to login firebase
    private fun googleCredential(tokenId: String) {
        if (CHECK_LOGIN == 1) {
            val authCredential = GoogleAuthProvider.getCredential(tokenId, null)
            firebaseAuth.signInWithCredential(authCredential)
        } else if (CHECK_LOGIN == 2) {
            val authCredential = FacebookAuthProvider.getCredential(tokenId)
            firebaseAuth.signInWithCredential(authCredential)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_LOGIN_GOOGLE) {
            if (resultCode == Activity.RESULT_OK) {
                val signInResult: GoogleSignInResult =
                    Auth.GoogleSignInApi.getSignInResultFromIntent(data)!!
                val account: GoogleSignInAccount = signInResult.signInAccount!!
                val tokenId: String = account.idToken!!
                googleCredential(tokenId)
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val user: FirebaseUser? = p0.currentUser
        if (user != null) {
            Toast.makeText(applicationContext, "Login Success", Toast.LENGTH_LONG).show()
        }
    }

}