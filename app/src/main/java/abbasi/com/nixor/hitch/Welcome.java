package abbasi.com.nixor.hitch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedaytimepicker.SlideDayTimeListener;
import com.github.jjobes.slidedaytimepicker.SlideDayTimePicker;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import abbasi.com.nixor.R;
import abbasi.com.nixor.misc.Tabs;

public class Welcome extends AppCompatActivity implements View.OnClickListener {
Button car;
Button seat;
Button add;
Button remove;
Button Next;
Button Back;
Button tofrom;
Button daytimepicker;
ImageView cartopool;
TextView count;
int numofseats =0;
ArrayList days;
ArrayList hours;
ArrayList mins;
ListView dayandtime;
     SlideDayTimeListener listener;
    FirebaseDatabase ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ref= FirebaseDatabase.getInstance();

        dayandtime = (ListView)findViewById(R.id.dayandtime);
        days = new ArrayList();
        hours = new ArrayList();
        mins = new ArrayList();
        car = (Button)findViewById(R.id.car);
        seat = (Button)findViewById(R.id.seat);
        add = (Button)findViewById(R.id.add);
        remove = (Button)findViewById(R.id.remove);
        Next = (Button)findViewById(R.id.Next);
        Back = (Button)findViewById(R.id.Back);
        tofrom = (Button)findViewById(R.id.tofrom);
        daytimepicker = (Button)findViewById(R.id.daypicker);
        count = (TextView)findViewById(R.id.count);
        cartopool = (ImageView) findViewById(R.id.cartopool);

    add.setOnClickListener(this);
    remove.setOnClickListener(this);
    car.setOnClickListener(this);
    seat.setOnClickListener(this);
    Next.setOnClickListener(this);
    Back.setOnClickListener(this);
    tofrom.setOnClickListener(this);


       days.add(0,0);
       days.add(1,1);
       days.add(2,2);
       days.add(3,3);
       days.add(4,4);
       days.add(5,5);
       days.add(6,6);


        hours.add(0,0);
        hours.add(1,0);
        hours.add(2,0);
        hours.add(3,0);
        hours.add(4,0);
        hours.add(5,0);
        hours.add(6,0);


        mins.add(0,0);
        mins.add(1,0);
        mins.add(2,0);
        mins.add(3,0);
        mins.add(4,0);
        mins.add(5,0);
        mins.add(6,0);


        dayandtime.setAdapter(new daysadapter(days,hours,mins,Welcome.this));
        listener = new SlideDayTimeListener() {

            @Override
            public void onDayTimeSet(int day, int hour, int minute)
            {
                int dayyyy = day-1;
                if(days.contains(dayyyy)) {
                    int a = days.indexOf(dayyyy);
                    days.set(a,dayyyy);
                    hours.set(a,hour);
                    mins.set(a,minute);
                }else {
                    days.set(dayyyy,dayyyy);
                   hours.set(dayyyy,hour);
                   mins.set(dayyyy,minute);



                }

                dayandtime.setAdapter(new daysadapter(days,hours,mins,Welcome.this));

            }

            @Override
            public void onDayTimeCancel()
            {
            }
        };



    }

  @Override
    public void onClick(View view){
  if(view.getId()== R.id.car){
      car.setVisibility(View.INVISIBLE);
      seat.setVisibility(View.INVISIBLE);
      cartopool.setVisibility(View.VISIBLE);
      count.setVisibility(View.VISIBLE);
      add.setVisibility(View.VISIBLE);
      remove.setVisibility(View.VISIBLE);
      Next.setVisibility(View.VISIBLE);
      Back.setVisibility(View.VISIBLE);


  }else if (view.getId()==R.id.add){
      numofseats++;
      count.setText("Available Seats: "+numofseats);



  }else if(view.getId()==R.id.remove){
      if(numofseats>0) {
          numofseats--;
          count.setText("Available Seats: " + numofseats);
      }


  }else if(view.getId()==R.id.Back){
          car.setVisibility(View.VISIBLE);
          seat.setVisibility(View.VISIBLE);
          cartopool.setVisibility(View.INVISIBLE);
          count.setVisibility(View.INVISIBLE);
          add.setVisibility(View.INVISIBLE);
          remove.setVisibility(View.INVISIBLE);
          Next.setVisibility(View.INVISIBLE);
          Back.setVisibility(View.INVISIBLE);



      }else if(view.getId()==R.id.Next){
          if(numofseats==0){
              Toast.makeText(this,"Please add at least one available seat to proceed",Toast.LENGTH_SHORT).show();
          }else if(numofseats>5){
              Toast.makeText(this,"The maximum people you can take with you is 4",Toast.LENGTH_SHORT).show();
          }else {
              count.setVisibility(View.INVISIBLE);
              add.setVisibility(View.INVISIBLE);
              remove.setVisibility(View.INVISIBLE);
            dayandtime.setVisibility(View.VISIBLE);
            daytimepicker.setVisibility(View.VISIBLE);
            cartopool.setVisibility(View.INVISIBLE);
          tofrom.setVisibility(View.VISIBLE);
          }

      }
      else if (view.getId()==R.id.tofrom){

  }

  }
  public  void openDayPicker(View view){
      new SlideDayTimePicker.Builder(getSupportFragmentManager())
              .setListener(listener)
              .setInitialDay(1)
              .setInitialHour(13)
              .setInitialMinute(30)
              .setIs24HourTime(false)
              .build()

              .show();
  }

}
