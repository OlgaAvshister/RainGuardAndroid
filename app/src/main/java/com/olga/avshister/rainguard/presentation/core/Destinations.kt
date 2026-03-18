package com.olga.avshister.rainguard.presentation.core


//region Nav arguments
const val NAV_ARGUMENT_PHONE_NUMBER = "phoneNumber"
const val NAV_ARGUMENT_RENT_POINT_ID = "rentPointId"
const val NAV_ARGUMENT_FILTER = "filter"
const val NAV_ARGUMENT_SELECTED_ARTICLES = "selectedArticles"

//endregion


//region Routes

// Экран загрузки
const val SPLASH_SCREEN = "SPLASH_SCREEN"

// Экран ввода номера телефона
const val AUTH_PHONE_SCREEN = "AUTH_PHONE_SCREEN"

// Экран с картой (выбор точки аренды/выбора пункта возврата)
const val MAP_SCREEN = "MAP_SCREEN"

const val SMS_CODE_SCREEN = "SMS_CODE_SCREEN/{$NAV_ARGUMENT_PHONE_NUMBER}"

// Каталог - экран выбора цвета зонта/дождевика
const val CATALOG_SCREEN = "CATALOG_SCREEN/{$NAV_ARGUMENT_FILTER}"

const val RENT_POINT_SCREEN = "RENT_POINT_SCREEN"

// Корзина
const val CART_SCREEN = "CART_SCREEN"

// Экран для выбора пользователем конкретных товаров (вместо сканирования QR-кода)
const val SELECT_IDS_SCREEN = "SELECT_IDS_SCREEN"

// Экран оформления заказа (и выбора тарифа)
const val CHECKOUT_SCREEN = "CHECKOUT_SCREEN"

// Экран выбора карты для оплаты
const val CARDS_SCREEN = "CARDS_SCREEN"

// Экран состояния теущей аренды
const val RENT_SCREEN = "RENT_SCREEN"

// Экран деталей точки аренда/пункта возврата
const val RENT_POINT_SCREEN_PATH = "$RENT_POINT_SCREEN/{$NAV_ARGUMENT_RENT_POINT_ID}"

// Информация о профиле
const val PROFILE_SCREEN = "PROFILE_SCREEN"

/////////////////////////////////
// Экраны сотрудника
/////////////////////////////////

// Экран ввода инвентарного номера товара для проверки персоналом (продавцом)
const val SELECT_PRODUCT_TO_CHECK_SCREEN = "SELECT_PRODUCT_TO_CHECK_SCREEN"

//endregion