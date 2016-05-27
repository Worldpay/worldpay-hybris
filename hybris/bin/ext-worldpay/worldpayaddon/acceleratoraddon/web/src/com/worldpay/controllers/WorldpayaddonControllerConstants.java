package com.worldpay.controllers;

public interface WorldpayaddonControllerConstants {
    String ADDON_PREFIX = "addon:/worldpayaddon/";

    /**
     * Class with view name constants
     */
    interface Views {

        interface Pages {

            interface MultiStepCheckout {
                String CheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/worldpayCheckoutSummaryPage";
                String CSEPaymentDetailsPage = ADDON_PREFIX + "pages/checkout/multi/worldpayCSEPaymentPage";
                String HostedOrderPostPage = ADDON_PREFIX + "pages/checkout/multi/hostedOrderPostPage";
                String AutoSubmit3DSecure = ADDON_PREFIX + "pages/checkout/multi/autoSubmit3DSecure";
            }
        }

        interface Fragments {

            interface Checkout {
                String BillingAddressForm = ADDON_PREFIX + "fragments/checkout/worldpayBillingAddressForm";
                String BillingAddressInPaymentForm = ADDON_PREFIX + "fragments/checkout/worldpayBillingAddressInPaymentForm";
            }

            interface Common {
                String GlobalErrorsFragment = ADDON_PREFIX + "fragments/common/globalMessages";
            }
        }
    }
}
