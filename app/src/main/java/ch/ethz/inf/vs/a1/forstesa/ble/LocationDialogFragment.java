package ch.ethz.inf.vs.a1.forstesa.ble;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Christian on 08.10.17.
 */

public class LocationDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.should_enable_GPS)
                .setTitle(R.string.GPS_disabled)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.myBluetoothAdapter.enable();
                        Toast.makeText(getActivity(), R.string.GPS_enabled, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), R.string.GPS_disabled, Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}