package com.karthikb351.vitinfo2.fragments.WelcomeScreens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karthikb351.vitinfo2.Home;
import com.karthikb351.vitinfo2.NewUser;
import com.karthikb351.vitinfo2.ParseAPI;
import com.karthikb351.vitinfo2.R;
import com.karthikb351.vitinfo2.VITxAPI;
import com.karthikb351.vitinfo2.objects.DataHandler;
import com.karthikb351.vitinfo2.objects.OnParseFinished;
import com.karthikb351.vitinfo2.objects.OnTaskComplete;
import com.parse.ParseException;

;

/**
 * Created by saurabh on 4/27/14.
 */
public class Screen3 extends Fragment {

    private boolean ATTENDANCE_LOAD = false;
    private boolean TT_LOAD = false;
    private boolean PARSE_LOGIN = false;

    VITxAPI api_tt;
    VITxAPI api_att;
    ParseAPI api_login;

    private TextView txt_done;
    private Button btn_go;
    private ProgressBar prg;
    private ImageView applogo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_newuser_3,container, false);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Finalizing");

        txt_done = (TextView) view.findViewById(R.id.lbl_parse_data);
        btn_go = (Button) view.findViewById(R.id.btn_start_using);
        prg = (ProgressBar) view.findViewById(R.id.prg_indeterminate);
        applogo = (ImageView)view.findViewById(R.id.app_logo);

        ((TextView) view.findViewById(R.id.lbl_save_data)).setTextColor(Color.parseColor("#008000"));

        final TextView txt_att = (TextView) view.findViewById(R.id.lbl_att_load);
        final TextView txt_tt = (TextView) view.findViewById(R.id.lbl_tt_load);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), Home.class));
            }
        });

        api_login = new ParseAPI(getActivity());
        api_login.registerUser(new OnParseFinished() {
            @Override
            public void onTaskCompleted(ParseException e) {
                if(e == null || e.getCode() == ParseException.USERNAME_TAKEN)
                {
                    api_login.loginUser(new OnParseFinished() {
                        @Override
                        public void onTaskCompleted(ParseException e) {
                            if(e == null)
                            {
                                PARSE_LOGIN = true;
                                checkIfDone();
                            }
                        }
                    });
                }
                else{
                    e.printStackTrace();
                    onError(new Exception("Could not register! Please check your network and try again!"));
                }

            }
        });

        api_att = new VITxAPI(getActivity(), new OnTaskComplete() {
            @Override
            public void onTaskCompleted(Exception e, Object result) {
                if(e!= null){
                    onError(e);
                }
                else{
                    txt_att.setTextColor(Color.parseColor("#008000"));
                    txt_att.setText("Attendance Saved");

                    api_att.changeListner(new OnTaskComplete() {
                        @Override
                        public void onTaskCompleted(Exception e, Object result) {
                            if(e!=null)
                                onError(e);
                            else {
                                ATTENDANCE_LOAD = true;
                                txt_done.setText("Data Saved");
                                txt_done.setTextColor(Color.parseColor("#008000"));
                                checkIfDone();
                            }
                        }
                    });

                    api_att.loadMarks();
                }
            }
        });

        api_tt = new VITxAPI(getActivity(), new OnTaskComplete() {
            @Override
            public void onTaskCompleted(Exception e, Object result) {
                if(e!= null){
                    onError(e);
                }
                else{
                    TT_LOAD = true;
                    txt_tt.setText("Time Table Saved");
                    txt_tt.setTextColor(Color.parseColor("#008000"));
                    checkIfDone();
                }
            }
        });

        api_att.loadAttendanceWithRegistrationNumber();
        api_tt.loadTimeTable();
        return view;
    }

    private void onError(Exception e){
        try{
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            ((NewUser) getActivity()).changeScreen(1);

        }catch (Exception e1){e1.printStackTrace();}
    }

    private void checkIfDone(){
        if(ATTENDANCE_LOAD && TT_LOAD && PARSE_LOGIN){
            DataHandler.getInstance(getActivity()).setNewUser(false);
            btn_go.setEnabled(true);
            btn_go.setBackgroundResource(R.drawable.round_shape_green);
            prg.setVisibility(View.GONE);
            applogo.setVisibility(View.VISIBLE);
            txt_done.setTextColor(Color.parseColor("#008000"));
        }
    }
}