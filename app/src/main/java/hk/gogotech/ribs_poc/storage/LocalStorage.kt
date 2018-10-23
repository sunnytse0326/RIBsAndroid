package hk.gogotech.ribs_poc.storage

import hk.gogotech.ribs_poc.model.SendBirdUser

class LocalStorage(mUser: SendBirdUser) {
    private var user: SendBirdUser = mUser
    fun getUser(): SendBirdUser {
        return user
    }
}