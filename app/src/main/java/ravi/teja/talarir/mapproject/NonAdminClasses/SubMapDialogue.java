package ravi.teja.talarir.mapproject.NonAdminClasses;

import android.app.DialogFragment;
import android.content.Context;
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

public class SubMapDialogue extends DialogFragment implements View.OnClickListener
{

    private Button subDialogYesBtn,subDialogNoBtn;
    private SubDialogToActivityInterface interfaceObject;
    private View subDialogView;

    public SubMapDialogue()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        subDialogView=inflater.inflate(R.layout.sub_map_dialog_layout,null);
        subDialogYesBtn= (Button) subDialogView.findViewById(R.id.acceptSubMapDialog);
        subDialogNoBtn= (Button) subDialogView.findViewById(R.id.declineSubMapDialog);
        subDialogYesBtn.setOnClickListener(this);
        subDialogNoBtn.setOnClickListener(this);
        getDialog().setTitle("Woah!");
        getDialog().setCancelable(false);
        return subDialogView;
    }

    @Override
    public void onClick(View v)
    {
        if (v==subDialogYesBtn)
        {
            interfaceObject.checkAcceptDecline(true);
            dismiss();
        }
        else if (v==subDialogNoBtn)
        {
            interfaceObject.checkAcceptDecline(false);
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceObject=(SubDialogToActivityInterface) getParentFragment();
    }
}
