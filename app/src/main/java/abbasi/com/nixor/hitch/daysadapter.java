package abbasi.com.nixor.hitch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import abbasi.com.nixor.R;


public class daysadapter extends BaseAdapter{
    ArrayList days;
    ArrayList hours;
    ArrayList mins;
    Context context;

    private static LayoutInflater inflater=null;
    public daysadapter(ArrayList d, ArrayList h, ArrayList m, Context w) {
  days=d;
  hours= h;
  mins =m;
  context = w;

        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }



    public class Holder
    {
        TextView hour;
        TextView day;
RelativeLayout thumbnail;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.dayrow, null);
        holder.day = (TextView) rowView.findViewById(R.id.but);
        holder.thumbnail = (RelativeLayout) rowView.findViewById(R.id.thumbnail);
        holder.hour = (TextView) rowView.findViewById(R.id.Hour);
        if(days!=null){

            Log.i("yoy",days.get(position).toString());

            switch (days.get(position).toString()){

                case "0":holder.day.setText("Sunday");break;
                case "1":holder.day.setText("Monday");break;
                case "2":holder.day.setText("Tuesday");break;
                case "3":holder.day.setText("Wednesday");break;
                case "4":holder.day.setText("Thursday");break;
                case "5":holder.day.setText("Friday");break;
                case "6":holder.day.setText("Saturday");break;

            }
            if((int)hours.get(position)==0){

                holder.hour.setText("day not selected");
                holder.thumbnail.setAlpha(0.6f);
            }else {
                if ((int) hours.get(position) > 12) {
                    int hour = (int) hours.get(position) - 12;
                    holder.hour.setText(hour + ":" + mins.get(position) + "PM");


                } else {
                    int hour = (int) hours.get(position);
                    holder.hour.setText(hour + ":" + mins.get(position) + "AM");
                }

            }


        }
        return rowView;


    }





}