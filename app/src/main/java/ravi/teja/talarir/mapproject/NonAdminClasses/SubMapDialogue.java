package ravi.teja.talarir.mapproject.NonAdminClasses;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ravi.teja.talarir.mapproject.R;

/**
 * Created by talarir on 14/03/2017.
 */

public class SubMapDialogue extends DialogFragment
{

    private Button subDialogYesBtn,subDialogNoBtn;
    private SubDialogToActivityInterface interfaceObject;
    private View subDialogView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        subDialogView=inflater.inflate(R.layout.sub_map_dialog_layout,container,false);

        getDialog().setTitle("Woah!");
        getDialog().setCancelable(false);
        return subDialogView;
    }

    private void initializeSubDialogueView(View subDialogView)
    {
        subDialogYesBtn= (Button) subDialogView.findViewById(R.id.acceptSubMapDialog);
        subDialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                interfaceObject.checkAcceptDecline(true);
                dismiss();
            }
        });
        subDialogNoBtn= (Button) subDialogView.findViewById(R.id.declineSubMapDialog);
        subDialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                interfaceObject.checkAcceptDecline(false);
                dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSubDialogueView(subDialogView);
        interfaceObject=(SubDialogToActivityInterface) getActivity();
    }
}
