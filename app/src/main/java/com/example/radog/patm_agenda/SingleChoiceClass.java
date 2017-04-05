package com.example.radog.patm_agenda;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radog on 05/04/2017.
 */

public class SingleChoiceClass extends DialogFragment {

    private List<String> contacts;
    private String selection;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] items = convertListToChar();
        builder.setTitle("Select contact").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String parts[];
                try {
                    selection = (String) items[which];
                    parts = selection.split("/"); //el teléfono está en la posición 1

                    Intent objCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + parts[1]));
                } catch (SecurityException e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Make a call", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return super.onCreateDialog(savedInstanceState);
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    private CharSequence[] convertListToChar() {
        CharSequence[] items = new CharSequence[contacts.size()];
        for (int i = 0; i < contacts.size(); ++i) {
            items[i] = contacts.get(i);
        }

        return items;
    }
}
