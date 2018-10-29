package hk.gogotech.ribs_poc.storage

import hk.gogotech.ribs_poc.model.OrderItem
import hk.gogotech.ribs_poc.model.SendBirdUser

class LocalStorage(mUser: SendBirdUser, mOrder: OrderItem) {
    private var user: SendBirdUser = mUser
    private var order: OrderItem = mOrder
    fun getUser(): SendBirdUser {
        return user
    }
    fun getOrder(): OrderItem {
        return order
    }
}