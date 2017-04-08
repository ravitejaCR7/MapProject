package ravi.teja.talarir.mapproject.AdminClasses;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ravi.teja.talarir.mapproject.R;

/**
 * Created by talarir on 24/02/2017.
 */

public class InputSubGroupDialogClass extends DialogFragment implements View.OnClickListener
{

    Button yes,no;
    EditText editTextDialogFrag;
    CommunicateWithMainMethod communicateWithMainMethod;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicateWithMainMethod=(CommunicateWithMainMethod) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.frag_dialog_layout,null);

        yes= (Button) view.findViewById(R.id.buttonYesSubGroupFragDialog);
        no = (Button) view.findViewById(R.id.buttonNoSubGroupFragDialog);
        editTextDialogFrag= (EditText) view.findViewById(R.id.editTextSubGroupFragDialog);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.buttonYesSubGroupFragDialog)
        {
            communicateWithMainMethod.sendTheSubCategoryToAdminActivity(editTextDialogFrag.getText().toString());
            dismiss();
        }
        if (v.getId()==R.id.buttonNoSubGroupFragDialog)
        {
            communicateWithMainMethod.sendTheSubCategoryToAdminActivity("haha");
            dismiss();
        }
    }

    public interface CommunicateWithMainMethod
    {
        public void sendTheSubCategoryToAdminActivity(String subCategoryName);
    }
}
