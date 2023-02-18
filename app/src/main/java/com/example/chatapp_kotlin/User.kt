package com.example.chatapp_kotlin

//data class User(val userName:String,val email:String,val profile_image:String)

class User{
    var userName:String? = null
    var email:String? = null
    var profile_image:String? = null

    constructor(userName: String?, email: String?, profile_image: String?) {
        this.userName = userName
        this.email = email
        this.profile_image = profile_image
    }

    constructor()

}