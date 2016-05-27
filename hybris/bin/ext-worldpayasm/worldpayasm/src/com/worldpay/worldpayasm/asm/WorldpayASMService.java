package com.worldpay.worldpayasm.asm;

/**
 * Interface that exposes methods to handle and detect when ASM is active.
 */
public interface WorldpayASMService {

    /**
     * @return true when there is an active ASM session, false otherwise
     */
    boolean isASMEnabled();
}
