package com.example.chatapp_kotlin

class Message {


    var sender:String?=null
    var reciever:String?=null
    var context:String?=null


    constructor(sender: String?, reciever: String?, context: String?) {
        this.sender = sender
        this.reciever = reciever
        this.context = context
    }
    constructor()
}