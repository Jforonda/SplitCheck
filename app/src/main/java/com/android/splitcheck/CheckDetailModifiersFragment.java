package com.android.splitcheck;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.android.splitcheck.data.Modifier;

import java.util.ArrayList;
import java.util.List;

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
        mTextViewTax.setText(String.valueOf(mModifier.getTax()));
        mImageViewTax.setColorFilter(R.color.colorBlack);
        mImageViewTax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the amount, open dialog?
                Toast.makeText(getContext(), "Changing tax: " + String.valueOf(mModifier.getTipPercent()), Toast.LENGTH_SHORT).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                        "Tax", mCheckId, 0, mModifier.getTax(), mModifier.getTaxPercent());
                changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
                changeModifierFragment.show(fm, "Tax");
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
                        mModifier.setTaxPercent(false);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Dollars", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        modifier.updateTaxPercent(contentResolver, mCheckId, true);
                        mModifier.setTaxPercent(true);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Percent", Toast.LENGTH_SHORT).show();
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
        mTextViewTip.setText(String.valueOf(mModifier.getTip()));
        mImageViewTip.setColorFilter(R.color.colorBlack);
        mImageViewTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the amount, open dialog?
                Toast.makeText(getContext(), "Changing tip", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                        "Tip", mCheckId, 1, mModifier.getTip(), mModifier.getTipPercent());
                changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
                changeModifierFragment.show(fm, "Tax");
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
                        mModifier.setTipPercent(false);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Dollars", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        modifier.updateTipPercent(contentResolver, mCheckId, true);
                        mModifier.setTipPercent(true);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Percent", Toast.LENGTH_SHORT).show();
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
        mTextViewGratuity.setText(String.valueOf(mModifier.getGratuity()));
        mImageViewGratuity.setColorFilter(R.color.colorBlack);
        mImageViewGratuity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the amount, open dialog?
                Toast.makeText(getContext(), "Changing gratuity", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                        "Gratuity", mCheckId, 2, mModifier.getGratuity(), mModifier.getGratuityPercent());
                changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
                changeModifierFragment.show(fm, "Gratuity");
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
                        mModifier.setGratuityPercent(false);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Dollars", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        modifier.updateGratuityPercent(contentResolver, mCheckId, true);
                        mModifier.setGratuityPercent(true);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Percent", Toast.LENGTH_SHORT).show();
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
            mTextViewFees.setText(String.valueOf(mModifier.getFees()));
        } else {
            mTextViewFees.setText(mModifier.getFeesString());
        }
        mImageViewFees.setColorFilter(R.color.colorBlack);
        mImageViewFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the amount, open dialog?
                Toast.makeText(getContext(), "Changing fees", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                        "Fees", mCheckId, 3, mModifier.getFees(), mModifier.getFeesPercent());
                changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
                changeModifierFragment.show(fm, "Fees");
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
                        mModifier.setFeesPercent(false);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Dollars", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        modifier.updateFeesPercent(contentResolver, mCheckId, true);
                        mModifier.setFeesPercent(true);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Percent", Toast.LENGTH_SHORT).show();
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
        mTextViewDiscount.setText(String.valueOf(mModifier.getDiscount()));
        mImageViewDiscount.setColorFilter(R.color.colorBlack);
        mImageViewDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the amount, open dialog?
                Toast.makeText(getContext(), "Changing discount", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ChangeModifierFragment changeModifierFragment = ChangeModifierFragment.newInstance(
                        "Discount", mCheckId, 4, mModifier.getDiscount(), mModifier.getDiscountPercent());
                changeModifierFragment.setTargetFragment(CheckDetailModifiersFragment.this, 100);
                changeModifierFragment.show(fm, "Discount");
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
                        mModifier.setDiscountPercent(false);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Dollars", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        modifier.updateDiscountPercent(contentResolver, mCheckId, true);
                        mModifier.setDiscountPercent(true);
                        listener.onChangeModifier();
                        Toast.makeText(getContext(), "Percent", Toast.LENGTH_SHORT).show();
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
    public void onFinishChangeModifierDialog(int amount, int modifierType) {
        // Callback From Dialog, Updates text when changing amount
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
        // TODO Modifier: Fix dialog, showing old data
    }

}
