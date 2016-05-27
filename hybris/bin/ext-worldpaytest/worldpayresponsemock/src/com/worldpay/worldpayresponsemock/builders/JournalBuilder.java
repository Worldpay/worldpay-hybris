package com.worldpay.worldpayresponsemock.builders;

import com.worldpay.internal.model.AccountTx;
import com.worldpay.internal.model.Amount;
import com.worldpay.internal.model.BookingDate;
import com.worldpay.internal.model.Date;
import com.worldpay.internal.model.Journal;

import java.time.LocalDate;

public final class JournalBuilder {

    private static final String IN_PROCESS_AUTHORISED = "inProcessAuthorised";
    private static final String BATCH_ID = "19";
    private static final LocalDate DATE = LocalDate.now();

    private String journalType;
    private Date journalBookingDate;
    private Amount amount;

    private JournalBuilder() {
    }

    public static JournalBuilder aJournalBuilder() {
        return new JournalBuilder();
    }

    public JournalBuilder withBookingDate(String day, String month, String year) {
        journalBookingDate = buildDate(day, month, year);
        return this;
    }

    public JournalBuilder withJournalType(String journalType) {
        this.journalType = journalType;
        return this;
    }

    public JournalBuilder withAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public Journal build() {
        final Journal journal = new Journal();

        final BookingDate bookingDate = new BookingDate();

        if (journalBookingDate == null) {
            journalBookingDate = buildDate(String.valueOf(DATE.getDayOfMonth()), String.valueOf(DATE.getMonthValue()), String.valueOf(DATE.getYear()));
        }

        bookingDate.setDate(journalBookingDate);
        journal.setJournalType(journalType);
        journal.setBookingDate(bookingDate);

        final AccountTx accountTx = new AccountTx();
        accountTx.setAccountType(IN_PROCESS_AUTHORISED);
        accountTx.setAmount(amount);
        accountTx.setBatchId(BATCH_ID);
        journal.getAccountTx().add(accountTx);

        return journal;
    }

    private Date buildDate(String day, String month, String year) {
        Date date = new Date();
        date.setDayOfMonth(day);
        date.setMonth(month);
        date.setYear(year);
        return date;
    }
}
