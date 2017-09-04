package com.android.splitcheck;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.splitcheck.data.Modifier;

import java.math.BigDecimal;
import java.text.NumberFormat;

import butterknife.ButterKnife;

public class CheckDetailModifiersFragment extends Fragment implements ChangeModifierFragment.CreateModifierDialogListener {

    ModifierChangeListener listener;

    TextView mTextViewTax, mTextViewTip, mTextViewGratuity, mTextViewFees, mTextViewDiscount;
    ImageView mImageViewTax, mImageViewTip, mImageViewGratuity, mImageViewFees, mImageViewDiscount;
    Spinner mSpinnerTaxPercent, mSpinnerTipPercent, mSpinnerGratuityPercent, mSpinnerFeesPercent,
            mSpinnerDiscountPercent;

    static int mCheckId;
    static Modifier mModifier;

    public CheckDetailModifiersFragment() {

    }

    public static CheckDetailModifiersFragment newInstance(int checkId) {
        CheckDetailModifiersFragment fragment = new CheckDetailModifiersFragment();
        mCheckId = checkId;
        return fragment;
    }

    public interface ModifierChangeListener {
        void onChangeModifier();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ModifierChangeListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_detail_modifiers, container,
                false);

        mCheckId = getArguments().getInt("checkId");
        final Modifier modifier = new Modifier();
        final ContentResolver contentResolver = getActivity().getContentResolver();
        mModifier = modifier.getModifierFromId(contentResolver, mCheckId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.modifierPercent, R.layout.support_simple_spinner_dropdown_item);

        initializeViewItems(rootView);

        setUpTaxView(modifier, contentResolver, adapter);
        setUpTipView(modifier, contentResolver, adapter);
        setUpGratuityView(modifier, contentResolver, adapter);
        setUpFeesView(modifier, contentResolver, adapter);
        setUpDiscountView(modifier, contentResolver, adapter);

        return rootView;
    }

    private void initializeViewItems(View rootView) {
        // Tax View
        mTextViewTax = ButterKnife.findById(rootView, R.id.text_view_tax);
        mImageViewTax = ButterKnife.findById(rootView, R.id.image_view_tax);
        mSpinnerTaxPercent = ButterKnife.findById(rootView, R.id.spinner_tax);
        // Tip View
        mTextViewTip = ButterKnife.findById(rootView, R.id.text_view_tip);
        mImageViewTip = ButterKnife.findById(rootView, R.id.image_view_tip);
        mSpinnerTipPercent = ButterKnife.findById(rootView, R.id.spinner_tip);
        // Gratuity View
        mTextViewGratuity = ButterKnife.findById(rootView, R.id.text_view_gratuity);
        mImageViewGratuity = ButterKnife.findById(rootView, R.id.image_view_gratuity);
        mSpinnerGratuityPercent = ButterKnife.findById(rootView, R.id.spinner_gratuity);
        // Fees View
        mTextViewFees = ButterKnife.findById(rootView, R.id.text_view_fees);
        mImageViewFees = ButterKnife.findById(rootView, R.id.image_view_fees);
        mSpinnerFeesPercent = ButterKnife.findById(rootView, R.id.spinner_fees);
        //Discount View
        mTextViewDiscount = ButterKnife.findById(rootView, R.id.text_view_discount);
        mImageViewDiscount = ButterKnife.findById(rootView, R.id.image_view_discount);
        mSpinnerDiscountPercent = ButterKnife.findById(rootView, R.id.spinner_discount);
    }

    private void setUpTaxView(final Modifier modifier, final ContentResolver contentResolver, ArrayAdapter<CharSequence> adapter) {
        boolean isPercent = mModifier.getTaxPercent();
        if (isPercent) {
            String tax = String.valueOf(mModifier.getTax());
            mTextViewTax.setText(percentAsString(tax));
        } else {
            mTextViewTax.setText(mModifier.getTaxString());
        }
        mImageViewTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaxDialog();
            }
        });
        mTextViewTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaxDialog();
            }
        });
        mSpinnerTaxPercent.setAdapter(adapter);
        mSpinnerTaxPercent.setSelection(isPercent ? 1 : 0, false);//getTaxPercent
        mSpinnerTaxPercent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        modifier.updateTaxPercent(contentResolver, mCheckId, false);
                        modifier.updateTax(contentResolver, mCheckId, 0);
                        mModifier.setTaxPercent(false);
                        listener.onChangeModifier();
                        break;
                    case 1:
                        modifier.updateTaxPercent(contentResolver, mCheckId, true);
                        modifier.updateTax(contentResolver, mCheckId, 0);
                        mModifier.setTaxPercent(true);
                        listener.onChangeModifier();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpTipView(final Modifier modifier, final ContentResolver contentResolver, ArrayAdapter<CharSequence> adapter) {
        boolean isPercent = mModifier.getTipPercent();
        if (isPercent) {
            String tip = String.valueOf(mModifier.getTip());
            mTextViewTip.setText(percentAsString(tip));
        } else {
            mTextViewTip.setText(mModifier.getTipString());
        }
        mImageViewTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTipDialog();
            }
        });
        mTextViewTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTipDialog();
            }
        });
        mSpinnerTipPercent.setAdapter(adapter);
        mSpinnerTipPercent.setSelection(isPercent ? 1 : 0, false);
        mSpinnerTipPercent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        modifier.updateTipPercent(contentResolver, mCheckId, false);
                        modifier.updateTip(contentResolver, mCheckId, 0);
                        mModifier.setTipPercent(false);
                        listener.onChangeModifier();
                        break;
                    case 1:
                        modifier.updateTipPercent(contentResolver, mCheckId, true);
                        modifier.updateTip(contentResolver, mCheckId, 0);
                        mModifier.setTipPercent(true);
                        listener.onChangeModifier();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpGratuityView(final Modifier modifier, final ContentResolver contentResolver, ArrayAdapter<CharSequence> adapter) {
        boolean isPercent = mModifier.getGratuityPercent();
        if (isPercent) {
            String gratuity = String.valueOf(mModifier.getGratuity());
            mTextViewGratuity.setText(percentAsString(gratuity));
        } else {
            mTextViewGratuity.setText(mModifier.getGratuityString());
        }
        mImageViewGratuity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGratuityDialog();
            }
        });
        mTextViewGratuity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGratuityDialog();
            }
        });
        mSpinnerGratuityPercent.setAdapter(adapter);
        mSpinnerGratuityPercent.setSelection(isPercent ? 1 : 0, false);
        mSpinnerGratuityPercent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        modifier.updateGratuityPercent(contentResolver, mCheckId, false);
                        modifier.updateGratuity(contentResolver, mCheckId, 0);
                        mModifier.setGratuityPercent(false);
                        listener.onChangeModifier();
                        break;
                    case 1:
                        modifier.updateGratuityPercent(contentResolver, mCheckId, true);
                        modifier.updateGratuity(contentResolver, mCheckId, 0);
                        mModifier.setGratuityPercent(true);
                        listener.onChangeModifier();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpFeesView(final Modifier modifier, final ContentResolver contentResolver, ArrayAdapter<CharSequence> adapter) {
        boolean isPercent = mModifier.getFeesPercent();
        if (isPercent) {
            String fees = String.valueOf(mModifier.getFees());
            mTextViewFees.setText(percentAsString(fees));
        } else {
            mTextViewFees.setText(mModifier.getFeesString());
        }
        mImageViewFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeesDialog();
            }
        });
        mTextViewFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFeesDialog();
            }
        });
        mSpinnerFeesPercent.setAdapter(adapter);
        mSpinnerFeesPercent.setSelection(isPercent ? 1 : 0, false);
        mSpinnerFeesPercent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        modifier.updateFeesPercent(contentResolver, mCheckId, false);
                        modifier.updateFees(contentResolver, mCheckId, 0);
                        mModifier.setFeesPercent(false);
                        listener.onChangeModifier();
                        break;
                    case 1:
                        modifier.updateFeesPercent(contentResolver, mCheckId, true);
                        modifier.updateFees(contentResolver, mCheckId, 0);
                        mModifier.setFeesPercent(true);
                        listener.onChangeModifier();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpDiscountView(final Modifier modifier, final ContentResolver contentResolver, ArrayAdapter<CharSequence> adapter) {
        boolean isPercent = mModifier.getDiscountPercent();
        if (isPercent) {
            String discount = String.valueOf(mModifier.getDiscount());
            mTextViewDiscount.setText(percentAsString(discount));
        } else {
            mTextViewDiscount.setText(mModifier.getDiscountString());
        }
        mImageViewDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscountDialog();
            }
        });
        mTextViewDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscountDialog();
            }
        });
        mTextViewDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscountDialog();
            }
        });
        mSpinnerDiscountPercent.setAdapter(adapter);
        mSpinnerDiscountPercent.setSelection(isPercent ? 1 : 0, false);
        mSpinnerDiscountPercent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        modifier.updateDiscountPercent(contentResolver, mCheckId, false);
                        modifier.updateDiscount(contentResolver, mCheckId, 0);
                        mModifier.setDiscountPercent(false);
                        listener.onChangeModifier();
                        break;
                    case 1:
                        modifier.updateDiscountPercent(contentResolver, mCheckId, true);
                        modifier.updateDiscount(contentResolver, mCheckId, 0);
                        mModifier.setDiscountPercent(true);
                        listener.onChangeModifier();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onFinishChangeModifierDialog(int amount, int modifierType, boolean isPercent) {
        // Callback From Dialog, Updates text when changing amount
        if (isPercent) {
            switch (modifierType) {
                case 0:
                    mTextViewTax.setText(String.valueOf(amount));
                    break;
                case 1:
                    mTextViewTip.setText(String.valueOf(amount));
                    break;
                case 2:
                    mTextViewGratuity.setText(String.valueOf(amount));
                    break;
                case 3:
                    mTextViewFees.setText(String.valueOf(amount));
                    break;
                case 4:
                    mTextViewDiscount.setText(String.valueOf(amount));
                    break;
                default:
                    break;
            }
            listener.onChangeModifier();
        } else {
            BigDecimal newAmount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
            switch (modifierType) {
                case 0:
                    mTextViewTax.setText(NumberFormat.getCurrencyInstance().format(newAmount));
                    break;
                case 1:
                    mTextViewTip.setText(NumberFormat.getCurrencyInstance().format(newAmount));
                    break;
                case 2:
                    mTextViewGratuity.setText(NumberFormat.getCurrencyInstance().format(newAmount));
                    break;
                case 3:
                    mTextViewFees.setText(NumberFormat.getCurrencyInstance().format(newAmount));
                    break;
                case 4:
                    mTextViewDiscount.setText(NumberFormat.getCurrencyInstance().format(newAmount));
                    break;
                default:
                    break;
            }
            listener.onChangeModifier();
        }
    }

    private String percentAsString(String percent) {
        if (percent.length() == 0) {
            return "0";
        } else if (percent.length() == 1) {
            if (percent.contains("0")) {
                return "0";
            } else {
                return ".0" + percent;
            }
        } else if (percent.length() == 2) {
            return "." + percent;
        } else if (percent.length() > 2) {
            String beforeDecimal = percent.substring(0, percent.length()-2);
            String afterDecimal = percent.substring(percent.length()-2, percent.length());
            if (afterDecimal.endsWith("00")) {
                return beforeDecimal;
            } else if (afterDecimal.endsWith("0")) {
                return beforeDecimal + "." + afterDecimal.charAt(0);
            } else {
                return beforeDecimal + "." + afterDecimal;
            }
        }
        return "";
    }

    private void startTaxDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                getString(R.string.modifier_tax), mCheckId, 0, mModifier.getTax(), mModifier.getTaxPercent());
        changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
        changeModifierFragment.show(fm, getString(R.string.modifier_tax));
    }

    private void startTipDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                getString(R.string.modifier_tip), mCheckId, 1, mModifier.getTip(), mModifier.getTipPercent());
        changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
        changeModifierFragment.show(fm, getString(R.string.modifier_tip));
    }

    private void startGratuityDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                getString(R.string.modifier_gratuity), mCheckId, 2, mModifier.getGratuity(), mModifier.getGratuityPercent());
        changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
        changeModifierFragment.show(fm, getString(R.string.modifier_gratuity));
    }

    private void startFeesDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                getString(R.string.modifier_fees), mCheckId, 3, mModifier.getFees(), mModifier.getFeesPercent());
        changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
        changeModifierFragment.show(fm, getString(R.string.modifier_fees));
    }

    private void startDiscountDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                getString(R.string.modifier_discount), mCheckId, 4, mModifier.getDiscount(), mModifier.getDiscountPercent());
        changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
        changeModifierFragment.show(fm, getString(R.string.modifier_discount));
    }
}
