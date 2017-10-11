package ch.ethz.inf.vs.a1.forstesa.ble;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Christian on 08.10.17.
 */

public class PermissionDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.give_permission)
                .setTitle(R.string.permission_disabled)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
                    }
                });

        return builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getActivity(), R.string.permission_disabled, Toast.LENGTH_SHORT).show();
                }
            }
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    Toast.makeText(getActivity(), R.string.permission_disabled, Toast.LENGTH_SHORT).show();
                }
            }

        }
        return;
    }
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100;
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 200;
}
