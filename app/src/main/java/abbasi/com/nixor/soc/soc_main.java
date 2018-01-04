package abbasi.com.nixor.soc;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import abbasi.com.nixor.R;
import abbasi.com.nixor.misc.Tabs;
import abbasi.com.nixor.soc.adapters.LazyAdapter;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class soc_main extends Fragment{
    TextView soctitle;
    ListView soclist;
    TextView foldername;
    String classname;
    FirebaseDatabase db;
    ArrayList<String> classnameslist;
    ArrayList<String> classstudentslist;
    ArrayList<String> userlist;
    ArrayList<String> downloadlinks;
    ArrayList<String> imageslist;
    String classnameeeee;

    Button backbutton;
    Button bucketbutton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_soc_cloud, container, false);

foldername = (TextView)rootView.findViewById(R.id.foldername);

        soclist=(ListView)rootView.findViewById(R.id.soclist);
        soctitle=(TextView)rootView.findViewById(R.id.soctitle);
        backbutton=(Button)rootView.findViewById(R.id.backbutton);
        bucketbutton=(Button)rootView.findViewById(R.id.bucketbutton);
        Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(),"fonts/tg.otf");
        soctitle.setTypeface(typeFace);
        db = FirebaseDatabase.getInstance();
        classnameslist = new ArrayList<>();
        classstudentslist = new ArrayList<>();
        downloadlinks = new ArrayList<>();
        userlist = new ArrayList<>();
        imageslist = new ArrayList<>();
        getListOfClasses();
        bucketbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ias = new Intent(getContext(), abbasi.com.nixor.soc.socupload
                        .class);
                Log.i("classname",classnameeeee);
                ias.putExtra("Classname",classnameeeee);

                startActivity(ias);
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListOfClasses();
            }
        });

        return rootView;}
    public void getListOfClasses(){
        bucketbutton.setVisibility(View.INVISIBLE);
        classname = "Class List";
        foldername.setText("CLASS LIST");
        backbutton.setVisibility(View.INVISIBLE);
        soclist.setAdapter(new LazyAdapter(soc_main.this,classnameslist,null,null,null,classname));

        db.getReference().child("soc").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue()!=null){
                    if(!classnameslist.contains(dataSnapshot.getKey().toString())){
                        classnameslist.add(dataSnapshot.getKey());
                        soclist.setAdapter(new LazyAdapter(soc_main.this,classnameslist,null,null,null,classname));
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    public  void getListofStudents(final String Classname){
        classnameeeee=Classname;
        Log.i("classname",Classname);
        bucketbutton.setVisibility(View.VISIBLE);
        classname="Students List";
        foldername.setText("STUDENTS LIST: "+Classname);
        backbutton.setVisibility(View.VISIBLE);
        classstudentslist = new ArrayList<>();
        if(soclist!=null){
            soclist.setAdapter(new abbasi.com.nixor.soc.adapters.LazyAdapter(soc_main.this,classstudentslist,null,Classname,null,classname));
        }

        db.getReference().child("soc").child(Classname).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue()!=null){
                    if(!classstudentslist.contains(dataSnapshot.getKey().toString())&&!dataSnapshot.getKey().toString().equals(Tabs.Username)){
                        classstudentslist.add(dataSnapshot.getKey());
                        soclist.setAdapter(new abbasi.com.nixor.soc.adapters.LazyAdapter(soc_main.this,classstudentslist,null,Classname,null,classname));
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getuserlist(String bucketusername, String Classname){
        bucketbutton.setVisibility(View.INVISIBLE);

        classname="Bucket";
        foldername.setText(bucketusername.toUpperCase()+"' "+"BUCKET"+"- "+Classname);
        if(soclist!=null){
            soclist.setAdapter(new abbasi.com.nixor.soc.adapters.LazyAdapter(soc_main.this,imageslist,downloadlinks,null,null,classname));
        }

        db.getReference().child("soc").child(Classname).child(bucketusername).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("name").getValue()!=null){
                    if(!imageslist.contains(dataSnapshot.child("name").getValue().toString())){
                        imageslist.add(dataSnapshot.child("name").getValue().toString());
                        downloadlinks.add(dataSnapshot.child("url").getValue().toString());
                        soclist.setAdapter(new abbasi.com.nixor.soc.adapters.LazyAdapter(soc_main.this,imageslist,downloadlinks,null,null,classname));
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void backButton(){
        getListOfClasses();
    }

}
