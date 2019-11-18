package com.example.myapplication.presentation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.myapplication.R;


public class FieldDialogFragment extends DialogFragment
{
    @NonNull
    int mNUM;
    public static String FRAGTAG;


    EditText mSiteName;public static String BSiteName ="SiteName";
    EditText mHostIp;public static String BHostAddress ="HostAddress";
    EditText mUsername; public static  String BUsername ="bDayIndecat";
    EditText mProtocol;public static String BProtocol = "bFirstLeter";
    EditText fPassward; public static String BPassward = "bStartTime";

    //callback part, which is used to send message from fragment to host
    public interface FieldDialogCallback
    {
        void doPositiveClick(Bundle bundle);
        void doNegativeClick();
    }

    FieldDialogCallback host;

    public static FieldDialogFragment newInstance(int num) {
        FieldDialogFragment f = new FieldDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }
//xxxx

    protected void init(View v){
        if(v!= null) {
            mSiteName =v.findViewById(R.id.fSiteName);
            mHostIp =v.findViewById(R.id.fHostIp);
            mUsername =v.findViewById(R.id.fUserName);
            mProtocol =v.findViewById(R.id.fProtocol);
            fPassward =v.findViewById(R.id.fPassward);

        }else{
            throw new ClassCastException(getContext().toString()
                    + "crash at the Dialog xml file loading: null");
        }
    }
    protected Bundle packageInfo(){
        Bundle formInfo = new Bundle();
        formInfo.putString(BSiteName, mSiteName.getText().toString());
        formInfo.putString(BHostAddress, mHostIp.getText().toString());
        formInfo.putString(BUsername, mUsername.getText().toString());
        formInfo.putString(BPassward, fPassward.getText().toString());
        formInfo.putString(BProtocol, mProtocol.getText().toString());

        return formInfo;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        builder.setView( inflater.inflate(R.layout.dialog_field,null));
        View v =inflater.inflate(R.layout.dialog_field,null);
        this.init(v);

        builder.setView(v);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add field info into it
                dismiss();
                host.doPositiveClick(packageInfo());
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
                host.doNegativeClick();
            }
        });

        //...this block for setting the dialog windows attribute
        {
            AlertDialog fieldDialog = builder.create();
            Window window = fieldDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.7f;
            //dimAmount在0.0f和1.0f之间，0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0.0f); //0 for no dim to 1 for full dim
            window.setAttributes(lp);

            return fieldDialog;
        }
    }

    //Call back part
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FieldDialogCallback) {
            host = (FieldDialogCallback) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}


//....
//here is some comment about how to use this fragment in an activity
//     1.change the builder.setview can change the apperance of the dialog except the title and bottom button
//     2.The activity implementing the class has to implements the two interface of FieldDialogCallBack. Which transfer information from
//          fragment to the activity(Information is The declared inputs in this class)
//      3.The activity implementing this class should create it via new instance to input data for information transfer from activity to this
 //       4.show a Dialogfragment in activity is the code  below
//    void showFieldDialog(){
//    // DialogFragment.show() will take care of adding the fragment
//    // in a transaction.  We also want to remove any currently showing
//    FragmentManager fragmentManager =getSupportFragmentManager();
//    FragmentTransaction ft = fragmentManager.beginTransaction();
//    Fragment prev = fragmentManager.findFragmentByTag(FieldDialogFragment.FRAGTAG);
//    if(prev!=null) {
//        ft.remove(prev);
//    }else{
//        ft.addToBackStack(null);
//    }
//    Log.d("Main","fuck");
//    FieldDialogFragment.newInstance(1).show(getSupportFragmentManager(),FieldDialogFragment.FRAGTAG);
//
//
//}
