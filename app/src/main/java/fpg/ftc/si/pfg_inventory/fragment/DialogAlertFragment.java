/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.pfg_inventory.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import fpg.ftc.si.pfg_inventory.R;

/**
 * ref:http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * Created by MarlinJoe on 2014/5/6.
 */
public class DialogAlertFragment extends DialogFragment {

    private DialogInterface.OnClickListener mListener;

    public static DialogAlertFragment newInstance(String message) {
        DialogAlertFragment frag = new DialogAlertFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    public void addListener(DialogInterface.OnClickListener listener){
        mListener = listener;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setTitle(getResources().getString(R.string.system_dialog_title));
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(R.string.action_ok,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mListener!=null) {
                    mListener.onClick(dialog, which);
                }
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }

    //TODO not work
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }



}
