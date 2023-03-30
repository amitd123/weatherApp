package com.amit.weatherapp.model.data_class

class User {

    lateinit var username:String
    lateinit var email:String
    lateinit var password:String
    lateinit var bio:String
    lateinit var profileImageUrl:String

    //Default constructor required for calls to
    //DataSnapshot.getValue(User.class)
    constructor(){

    }

    constructor(username:String,email:String,password:String,bio:String,profileImageUrl:String){
        this.username=username
        this.email=email
        this.bio=bio
        this.profileImageUrl=profileImageUrl
        this.password=password
    }
}