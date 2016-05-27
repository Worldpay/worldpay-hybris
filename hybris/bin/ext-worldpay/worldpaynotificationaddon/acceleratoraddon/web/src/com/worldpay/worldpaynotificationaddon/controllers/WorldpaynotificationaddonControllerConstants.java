package com.worldpay.worldpaynotificationaddon.controllers;


public interface WorldpaynotificationaddonControllerConstants {
    String ADDON_PREFIX = "addon:/worldpaynotificationaddon/";

    interface WorldpayNotificationAddon {
        interface Views {
            String WORLDPAY_RESPONSE_OK_VIEW = ADDON_PREFIX + "pages/orderNotification/worldpayResponseOkView";
        }
    }
}
