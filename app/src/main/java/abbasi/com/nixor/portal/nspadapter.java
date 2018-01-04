package abbasi.com.nixor.portal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import abbasi.com.nixor.R;


public class nspadapter extends BaseAdapter{
    ArrayList days;

    portal_main context;

    private static LayoutInflater inflater=null;
    public nspadapter(ArrayList d, portal_main w) {
  days=d;

  context = w;

        inflater = (LayoutInflater)context.getContext().
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
        rowView = inflater.inflate(R.layout.nsprow, null);
        holder.day = (TextView)rowView.findViewById(R.id.but);
        holder.day.setTag(days.get(position).toString());
        holder.day.setText(days.get(position).toString());
        TypedValue outValue = new TypedValue();

        holder.day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onItemClick(view.getTag().toString(), context.getActivity());

            }
        });
        return rowView;


    }





}


