package com.laughingstock.ritwick.shutup;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    Switch masterswitch;
    SharedPreferences preferences;
    TextView welcome1,welcome2;
    BroadcastReceiver phonestaterecevier;
    NotificationManager notificationManager;
    RelativeLayout fragmentcontainer;
    OptionsFragment settingsFragment;

    boolean checktel=false,checkdnd=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        masterswitch=(Switch) findViewById(R.id.masterswitch);
        welcome1=(TextView) findViewById(R.id.welcome1);
        welcome2=(TextView) findViewById(R.id.welcome2);

        fragmentcontainer=(RelativeLayout) findViewById(R.id.fragmentcontainer);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        if (findViewById(R.id.fragmentcontainer) != null)
        {
            if(savedInstanceState == null)
            {
                settingsFragment = new OptionsFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, settingsFragment,"settingsFragment").commit();
            }
            else
            {
                settingsFragment = (OptionsFragment) getFragmentManager().findFragmentByTag("settingsFragment");
            }
        }
        phonestaterecevier = new PhoneStateReceiver();
    }

    protected void onPause()
    {
        super.onPause();
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("switchstate",masterswitch.isChecked());
        editor.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        preferences = getSharedPreferences("switchstatepref",MODE_PRIVATE);
        masterswitch.setChecked(preferences.getBoolean("switchstate",false));
        masterswitch.setBackgroundColor(Color.parseColor(masterswitch.isChecked()?"#26A69A":"#EF5350"));
        fragmentmanage();
    }


    public void MasterSwitchClicked(View v)
    {

        if(permissionmanage())
        {
            ComponentName component = new ComponentName(getApplication(), PhoneStateReceiver.class);
            preferences = getSharedPreferences("switchstatepref", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("switchstate", masterswitch.isChecked());

            getPackageManager().setComponentEnabledSetting(component,
                    masterswitch.isChecked()?PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            editor.apply();

            fragmentmanage();
            masterswitch.setBackgroundColor(Color.parseColor((masterswitch.isChecked())?"#26A69A":"#EF5350"));

        }
         else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()
                || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED)
        {
            masterswitch.toggle();
        }

    }



    public boolean permissionmanage()
    {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS}, 0);

        return checktel && (checkdnd || (Build.VERSION.SDK_INT < 23));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && requestCode==0)
        {
            checktel=true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted())
            {

                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                Toast.makeText(this,"Won't misuse. Pinky promise.",Toast.LENGTH_SHORT).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted() )
                checkdnd=true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getTitle().equals("Settings"))
        {
            if(masterswitch.isChecked())
            {
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,"Master switch is off",Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getTitle().equals("Help"))
        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.help_dialog);
            dialog.setTitle("Instructions:");
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            Button dialogButton = (Button) dialog.findViewById(R.id.helpclosebutton);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else if(item.getTitle().equals("Rate"))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id="+getPackageName()));
            startActivity(intent);
            //Toast.makeText(this,"Thank you!",Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(MainActivity.this,"Thank you!",Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        }
        return super.onOptionsItemSelected(item);
    }


    public void fragmentmanage()
    {
        if(masterswitch.isChecked())
        {
            fragmentcontainer.setVisibility(View.VISIBLE);
            welcome2.setVisibility(View.INVISIBLE);
            welcome1.setVisibility(View.INVISIBLE);
        }
        else
        {
            fragmentcontainer.setVisibility(View.GONE);
            welcome2.setVisibility(View.VISIBLE);
            welcome1.setVisibility(View.VISIBLE);
        }

    }


    public void silentonpickcheckboxclicked(View v)
    {
        settingsFragment.silentonpickcheckboxclicked(v);
    }

    public void speakeroncheckboxclicked(View v)
    {
        settingsFragment.speakeroncheckboxclicked(v);
    }

}