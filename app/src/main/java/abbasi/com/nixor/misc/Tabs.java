package abbasi.com.nixor.misc;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import abbasi.com.nixor.R;
import abbasi.com.nixor.hitch.Welcome;
import abbasi.com.nixor.soc.soc_main;
import de.hdodenhof.circleimageview.CircleImageView;


public class Tabs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
     ViewPager viewPager;
    TextView nav_user;
    String studentid;
    String nameofstudent;
    private DrawerLayout drawer;
   public static String Username;
    FirebaseDatabase db;
    public static  String photourl;
    CircleImageView dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random);
        db = FirebaseDatabase.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        hView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(Tabs.this,myprofile.class);
                profile.putExtra("studentid",studentid);
                profile.putExtra("Name",nameofstudent);
                profile.putExtra("username",Username);


                startActivity(profile);
            }
        });
         dp = (CircleImageView)hView.findViewById(R.id.dp);

        nav_user = (TextView)hView.findViewById(R.id.nav_name);

        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab().setText("CHAT"));
        tabLayout.addTab(tabLayout.newTab().setText("NSP"));
        tabLayout.addTab(tabLayout.newTab().setText("SOC"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


   viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        checkusername();

    }



    public void checkusername(){

        db.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    if (dataSnapshot.child("username").getValue()==null||
                            dataSnapshot.child("Name").getValue()==null||
                            dataSnapshot.child("StudentID").getValue()==null){
                    Intent id = new Intent(Tabs.this, abbasi.com.nixor.portal.nsp_weblogin.class);
                            id.putExtra("getusername",true);
                    startActivity(id);
                    finish();
                }else{
                    Username = dataSnapshot.child("username").getValue().toString();
                        nameofstudent=  dataSnapshot.child("Name").getValue().toString();
                        studentid=  dataSnapshot.child("StudentID").getValue().toString();

                        nav_user.setText(nameofstudent+"\n"+studentid+"\n"+"("+Username+")");
                }
if(dataSnapshot.child("photourl").getValue()!=null){

                                photourl= dataSnapshot.getValue().toString();
                                Glide.with(Tabs.this)
                                        .load(dataSnapshot.getValue().toString())
                                        .error(R.drawable.profile) // will be displayed if the image cannot be loaded
                                        .crossFade()
                                .into(dp);


}


            }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
        if(viewPager!=null){
        viewPager.setCurrentItem(1);
    }}}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


       if (id == R.id.go) {
            Intent intent = new Intent(this, Welcome.class);
            intent.putExtra("string", "Go to other Activity by NavigationView item cliked!");
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}