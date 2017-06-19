package com.laughingstock.ritwick.shutup;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.ArrayList;


class ScheduleContactsAdapter extends BaseAdapter
{

    Context context;

    private ArrayList<Bundle> schedinfo;
    private TextView listemptytext;
    private String number = "", name = "", photo = "", time = "", date = "", dialnumber = "";
    boolean repeatcall=false;
    private long timeinmills;
    private ArrayList<String> diffnums;


    private static LayoutInflater inflater = null;

    private AdapterView.OnItemClickListener onItemClickListener;

    void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    ScheduleContactsAdapter(Context context, ArrayList<Bundle> schedinfo, TextView listemptytext)
    {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.schedinfo = schedinfo;
        this.listemptytext = listemptytext;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row_in_schedulelist,parent,false);
        final View vitemp=vi;

        final SwipeRevealLayout swipeLayout = (SwipeRevealLayout) vi.findViewById(R.id.swipe);

        TextView schedinfotextview = (TextView) vi.findViewById(R.id.schedinfotextview);
        final ImageView schcontactphoto = (ImageView) vi.findViewById(R.id.schlistcontactphotopic);

        Bundle b = schedinfo.get(position);
        if (b != null)
        {
            name = b.getString("name");
            diffnums = b.getStringArrayList("numbers");
            dialnumber = b.getString("dialnumber");
            photo = b.getString("photo");
            time = b.getString("time");
            date = b.getString("date");
            timeinmills = b.getLong("timeinmills");
            repeatcall=b.getBoolean("repeatcall");

            String temp = "Call " + name + "\nat " + time + ((repeatcall)?(" daily"):("\nof " + date)) + "\non number " + dialnumber;
            schedinfotextview.setText(temp);

            schcontactphoto.setImageURI(Uri.parse(photo));
        }
        else vi.setVisibility(View.GONE);


        swipeLayout.close(true);
        swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener()
        {
            @Override
            public void onClosed(SwipeRevealLayout view)
            {            }

            @Override
            public void onOpened(SwipeRevealLayout view)
            {
                final Bundle temp = schedinfo.get(position);
                schedinfo.remove(position);
                notifyDataSetChanged();
                Snackbar.make(swipeLayout, "Schedule cancelled", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                schedinfo.add(position, temp);
                                notifyDataSetChanged();
                                CSFragment csFragment=new CSFragment();
                                csFragment.saveToInternalStorage(context,schedinfo);

                                SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                                //schedAlarmReciever.setAlarm(context,timeinmills,position);


                                if (schedinfo.size() == 0)
                                    listemptytext.setVisibility(View.VISIBLE);
                                else listemptytext.setVisibility(View.INVISIBLE);
                                swipeLayout.close(true);

                            }
                        })
                        .setActionTextColor(Color.parseColor("#2196F3"))
                        .show();
                if (schedinfo.size() == 0) listemptytext.setVisibility(View.VISIBLE);

                CSFragment csFragment=new CSFragment();
                csFragment.saveToInternalStorage(context,schedinfo);

                SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                //schedAlarmReciever.cancelAlarm(context,position);
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset)
            {            }
        });

        RelativeLayout mainswipe=(RelativeLayout) vi.findViewById(R.id.mainswipe);
        mainswipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(null, vitemp, position, -1);
                }
            }
        });
        return vi;
    }

    @Override
    public void notifyDataSetChanged()
    {
        Thread t=new Thread(){
            @Override
            public void run()
            {
                for(int j=0;j<=50;j++)
                {
                    SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                    schedAlarmReciever.cancelAlarm(context,j);
                }
                for(Bundle b:schedinfo)
                {
                    SchedAlarmReciever schedAlarmReciever=new SchedAlarmReciever();
                    schedAlarmReciever.setAlarm(context,b.getLong("timeinmills"),schedinfo.indexOf(b));
                }

            }
        };

        t.start();

        super.notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return schedinfo.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return schedinfo.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }
}