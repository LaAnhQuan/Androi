package com.shopping.app.utils

object Constants {

    const val BASE_URL = "https://fakestoreapi.com/"

    const val DATABASE_USERS_TABLE = "users"
    const val DATABASE_BASKET_TABLE = "basket"
    const val DATABASE_PRODUCTS_TABLE = "products"
    const val DATABASE_CATEGORIES_TABLE = "categories"
    const val DATABASE_BANNERS_TABLE = "banners"
    const val DATABASE_REVIEWS_TABLE = "reviews"
    const val DATABASE_PRODUCTS_TABLE_PIECE_FIELD = "piece"

    const val DATABASE_FIELD_USERNAME = "username"
    const val DATABASE_FIELD_UID = "uid"
    const val DATABASE_FIELD_ROLE = "role"

    // User roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_SELLER = "seller"
    const val ROLE_CUSTOMER = "customer"

    const val DATA_STORE_USER = "data_store_user"
    const val PRODUCT_MODEL_NAME = "product_model"
    const val EDIT_PRODUCT_JSON = "edit_product_json"

    // Orders
    const val DATABASE_ORDERS_TABLE = "orders"

    // Presence (online status)
    const val DATABASE_STATUS_TABLE = "status"

    // Notifications
    const val DATABASE_NOTIFICATIONS_TABLE = "notifications"
    const val NOTIFICATION_TYPE_MESSAGE = "message"
    const val NOTIFICATION_TYPE_ORDER = "order"              // seller: new order
    const val NOTIFICATION_TYPE_ORDER_PLACED = "order_placed" // buyer: order placed

    // Chat
    const val DATABASE_CHATS_TABLE = "chats"
    const val DATABASE_MESSAGES_TABLE = "messages"
    const val CHAT_OTHER_UID = "chat_other_uid"
    const val CHAT_OTHER_NAME = "chat_other_name"
    const val CHAT_PRODUCT_JSON = "chat_product_json"

}