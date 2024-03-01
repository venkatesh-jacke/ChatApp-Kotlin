package com.example.chatapp_kotlin

// Constants.kt

object Constants {
    // Firebase Database
    const val USERS_NODE = "users"
    const val CHATS_NODE = "chats"
    const val MESSAGES_NODE = "messages"

    // Intent extras
    const val EXTRA_ROOMMATE_USERNAME = "roommate_userName"
    const val EXTRA_ROOMMATE_EMAIL = "roommate_email"
    const val EXTRA_ROOMMATE_IMG = "roommate_img"
    const val EXTRA_MY_IMG = "my_img"

    // Logging
    const val TAG_FRIENDS_ACTIVITY = "FriendsActivity"
    const val TAG_PROFILE_ACTIVITY = "ProfileActivity"
    const val TAG_MESSAGE_ACTIVITY = "MessageActivity"

    // Preferences
    const val PREFS_NAME = "MyAppPreferences"
    const val PREF_MY_USER_ID = "my_user_id"
    const val PREF_MY_USER_NAME = "my_user_name"
    const val PREF_MY_PROFILE_IMAGE = "my_profile_image"

    // Permissions
    const val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
    const val PERMISSION_WRITE_EXTERNAL_STORAGE = android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    // Request codes
    const val REQUEST_CODE_CAMERA = 101
    const val REQUEST_CODE_GALLERY = 102
}
