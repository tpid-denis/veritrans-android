package com.midtrans.sdk.uikit.abstracts;

import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.midtrans.sdk.corekit.core.PaymentType;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.uikit.R;
import com.midtrans.sdk.uikit.adapters.InstructionPagerAdapter;
import com.midtrans.sdk.uikit.constants.AnalyticsEventName;
import com.midtrans.sdk.uikit.views.banktransfer.status.BankTransferStatusPresenter;
import com.midtrans.sdk.uikit.widgets.DefaultTextView;
import com.midtrans.sdk.uikit.widgets.FancyButton;
import com.midtrans.sdk.uikit.widgets.MagicViewPager;

/**
 * Created by ziahaqi on 8/15/17.
 */

public abstract class BaseVaPaymentStatusActivity extends BasePaymentActivity {

    public static final String EXTRA_PAYMENT_RESULT = "bank.payment.result";
    public static final String EXTRA_BANK_TYPE = "bank.type";


    private static final int PAGE_MARGIN = 20;
    private static final int CURRENT_POSITION = -1;

    protected TabLayout tabInstruction;
    protected MagicViewPager pagerInstruction;
    private DefaultTextView textTitle;
    protected FancyButton buttonCompletePayment;

    protected BankTransferStatusPresenter presenter;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initProperties();
        initPagerInstruction();
        initTabInstruction();
        initCompletePaymentButton();
        setTitle();
    }

    @Override
    public void bindViews() {
        textTitle = (DefaultTextView) findViewById(R.id.text_page_title);
        buttonCompletePayment = (FancyButton) findViewById(R.id.button_complete_payment);

        tabInstruction = (TabLayout) findViewById(R.id.tab_instructions);
        pagerInstruction = (MagicViewPager) findViewById(R.id.pager_instruction);
    }

    @Override
    public void initTheme() {
        tabInstruction.setSelectedTabIndicatorColor(getPrimaryColor());
    }

    private void initProperties() {
        String bankType = getIntent().getStringExtra(EXTRA_BANK_TYPE);
        TransactionResponse response = (TransactionResponse) getIntent().getSerializableExtra(EXTRA_PAYMENT_RESULT);
        presenter = new BankTransferStatusPresenter(response, bankType == null ? "" : bankType);
    }

    private void initCompletePaymentButton() {
        buttonCompletePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initPagerInstruction() {
        pagerInstruction.setPageMargin(PAGE_MARGIN);
        int pageNumber;
        switch (presenter.getBankType()) {
            case PaymentType.BCA_VA:
                pageNumber = 3;
                //track page bca va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_BCA_VA_OVERVIEW);

                break;
            case PaymentType.PERMATA_VA:
                pageNumber = 2;

                //track page permata va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_PERMATA_VA_OVERVIEW);
                break;
            case PaymentType.BNI_VA:
                pageNumber = 3;

                //track page permata va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_BNI_VA_OVERVIEW);
                break;
            case PaymentType.E_CHANNEL:
                pageNumber = 2;

                //track page mandiri bill overview
                presenter.trackEvent(AnalyticsEventName.PAGE_MANDIRI_BILL_OVERVIEW);
                break;
            case PaymentType.ALL_VA:
                pageNumber = 3;

                //track page other bank va overview
                presenter.trackEvent(AnalyticsEventName.PAGE_OTHER_BANK_VA_OVERVIEW);
                break;
            default:
                pageNumber = 0;
                break;
        }
        InstructionPagerAdapter adapter = new InstructionPagerAdapter(this, presenter.getBankType(), getSupportFragmentManager(), pageNumber);
        pagerInstruction.setAdapter(adapter);

        if (CURRENT_POSITION > -1) {
            pagerInstruction.setCurrentItem(CURRENT_POSITION);
        }

        pagerInstruction.clearOnPageChangeListeners();
        pagerInstruction.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeConfirmationButtonLable(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTabInstruction() {
        tabInstruction.setupWithViewPager(pagerInstruction);
        tabInstruction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                pagerInstruction.setCurrentItem(position);
                changeConfirmationButtonLable(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private void changeConfirmationButtonLable(int position) {
        switch (presenter.getBankType()) {

            case PaymentType.BCA_VA:
                if (position == 0) {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_at_atm));
                } else if (position == 1) {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_at_klik_bca_va));
                } else {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_at_mobile_bca));
                }
                break;

            case PaymentType.PERMATA_VA:
                buttonCompletePayment.setText(getString(R.string.complete_payment_at_atm));
                break;

            case PaymentType.E_CHANNEL:
                if (position == 0) {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_at_atm));
                } else {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_via_internet_banking));
                }
                break;

            case PaymentType.BNI_VA:
                if (position == 0) {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_at_atm));
                } else if (position == 1) {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_va_bni_mobile));
                } else {
                    buttonCompletePayment.setText(getString(R.string.complete_payment_via_internet_banking));
                }

                break;

            default:
                buttonCompletePayment.setText(getString(R.string.complete_payment_at_atm));
                break;
        }
    }

    protected void setTitle() {
        switch (presenter.getBankType()) {
            case PaymentType.BCA_VA:
                setPageTitle(getString(R.string.bank_bca_transfer));
                break;
            case PaymentType.PERMATA_VA:
                setPageTitle(getString(R.string.bank_permata_transfer));
                break;
            case PaymentType.ALL_VA:
                setPageTitle(getString(R.string.other_bank_transfer));
                break;
            case PaymentType.BNI_VA:
                setPageTitle(getString(R.string.bank_bni_transfer));

            case PaymentType.E_CHANNEL:
                setPageTitle(getString(R.string.mandiri_bill_transfer));
                break;
        }
    }

    public void setPageTitle(String pageTitle) {
        this.textTitle.setText(pageTitle);
    }
}
