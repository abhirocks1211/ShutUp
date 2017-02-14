package com.laughingstock.ritwick.shutup;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class CCandAFragment extends Fragment
{

    SharedPreferences preferences;
    Spinner singlelist,doublelist,flipdownlist;
    TextView singlewave,doublewave,flipdowntext,blacklistwhitelisttext1,blacklistwhitelisttext2;
    ArrayAdapter<CharSequence> waveadapter,flipdownadapter;
    Context context;
    CheckBox silentonpick,speakeron;
    Switch blacklistwhitelistswitch;
    View imptechview,looksniceview;
    RelativeLayout.LayoutParams params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_ccanda,container,false);
        context=getActivity();

        singlelist=(Spinner) view.findViewById(R.id.singlelist);
        doublelist=(Spinner) view.findViewById(R.id.doublelist);
        flipdownlist=(Spinner) view.findViewById(R.id.flipdownlist);
        singlewave=(TextView) view.findViewById(R.id.singlewave);
        doublewave=(TextView) view.findViewById(R.id.doublewave);
        flipdowntext=(TextView) view.findViewById(R.id.flipdowntext);
        silentonpick=(CheckBox) view.findViewById(R.id.silentonpickcheckbox);
        speakeron=(CheckBox) view.findViewById(R.id.speakeroncheckbox);
        blacklistwhitelistswitch=(Switch) view.findViewById(R.id.blacklistwhitelistswitch);
        imptechview= view.findViewById(R.id.viewview);
        looksniceview=view.findViewById(R.id.looksniceview);
        blacklistwhitelisttext1=(TextView) view.findViewById(R.id.blacklistwhitelisttext1);
        blacklistwhitelisttext2=(TextView) view.findViewById(R.id.blacklistwhitelisttext2);

        params = (RelativeLayout.LayoutParams) imptechview.getLayoutParams();

        waveadapter = ArrayAdapter.createFromResource(context,R.array.wavelistoptions,  R.layout.spinner_item);//.R.layout.simple_spinner_item);
        waveadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);//android.R.layout.simple_spinner_dropdown_item);

        flipdownadapter=ArrayAdapter.createFromResource(context,R.array.facedownlistoptions,R.layout.spinner_item);
        flipdownadapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        singlelist.setAdapter(waveadapter);
        doublelist.setAdapter(waveadapter);

        flipdownlist.setAdapter(flipdownadapter);


        singlelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("singlewaveselection",parent.getItemAtPosition(position).toString());

                if(parent.getItemAtPosition(position).toString().equals("End call") || parent.getItemAtPosition(position).toString().equals("Answer call"))
                {
                    editor.putString("doublewaveselection","lol");
                    doublelist.setVisibility(View.INVISIBLE);
                    doublewave.setText("Double wave function not available");
                    if(parent.getItemAtPosition(position).toString().equals("Answer call"))
                    {
                        singlewave.setText("Single wave or :\nPut on ear");
                    }
                    else    singlewave.setText(R.string.single_wave);
                }
                else
                {
                    editor.putString("doublewaveselection",doublelist.getSelectedItem().toString());
                    doublelist.setVisibility(View.VISIBLE);
                    doublewave.setText(R.string.double_wave);
                }

                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(context,"Choice not changed",Toast.LENGTH_SHORT).show();
            }

        });

        doublelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("doublewaveselection",parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(context,"Choice not changed",Toast.LENGTH_SHORT).show();
            }
        });


        flipdownlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("flipdownlistselection",parent.getItemAtPosition(position).toString());
                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(context,"Choice not changed",Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        singlelist.setSelection(waveadapter.getPosition(preferences.getString("singlewaveselection","")));
        doublelist.setSelection(waveadapter.getPosition(preferences.getString("doublewaveselection","")));
        silentonpick.setChecked(preferences.getBoolean("silentonpickcheckboxstate",false));
        speakeron.setChecked(preferences.getBoolean("speakeroncheckboxstate",false));
        blacklistwhitelistswitch.setChecked(preferences.getBoolean("blacklistwhitelistswitchstate",false));
        flipdownlist.setSelection(flipdownadapter.getPosition(preferences.getString("flipdownlistselection","")));
        checkspeakeronchecked();
    }


    public void silentonpickcheckboxclicked(View v)
    {
        preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("silentonpickcheckboxstate", silentonpick.isChecked());
        editor.apply();
    }

    public void speakeroncheckboxclicked(View v)
    {
        preferences = context.getSharedPreferences("switchstatepref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("speakeroncheckboxstate", speakeron.isChecked());
        editor.apply();
        checkspeakeronchecked();
    }


    public void blacklistwhitelisttextclicked(View v)
    {
        if(blacklistwhitelistswitch.isChecked())
        {
            Intent i = new Intent(context, BlacklistWhitelist.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(context,"Switch is off",Toast.LENGTH_SHORT).show();
        }
    }

    public void blacklistwhitelistswitchclicked(View v)
    {
        preferences = context.getSharedPreferences("switchstatepref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("blacklistwhitelistswitchstate",blacklistwhitelistswitch.isChecked());
        editor.apply();
    }

    public void checkspeakeronchecked()
    {
        if(speakeron.isChecked())
        {
            blacklistwhitelisttext1.setVisibility(View.VISIBLE);
            blacklistwhitelisttext2.setVisibility(View.VISIBLE);
            blacklistwhitelistswitch.setVisibility(View.VISIBLE);
            looksniceview.setVisibility(View.VISIBLE);
            params.addRule(RelativeLayout.BELOW, R.id.blacklistwhitelisttext1);
            imptechview.setLayoutParams(params);
        }
        else
        {
            blacklistwhitelisttext1.setVisibility(View.GONE);
            blacklistwhitelisttext2.setVisibility(View.GONE);
            blacklistwhitelistswitch.setVisibility(View.GONE);
            looksniceview.setVisibility(View.GONE);
            params.addRule(RelativeLayout.BELOW, R.id.speakeroncheckbox);
            imptechview.setLayoutParams(params);
        }
    }
}