package com.jsstech.woocommerceapp;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsstech.woocommerceapp.Model.Products;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;
import com.jsstech.woocommerceapp.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;



public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference productRef;
    RecyclerView recyclerView;

    private String type="";

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null)
        {
            type=getIntent().getExtras().get("Admin").toString();
        }



       // type=getIntent().getExtras().get("Admin").toString();



        Paper.init(this);

        productRef= FirebaseDatabase.getInstance().getReference().child("Products");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!type.equals("Admin"))
                {
                    Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                    startActivity(intent);

                }


//                Snackbar.make(view,"Replace with your own action",Snackbar.LENGTH_LONG)
//                        .setAction("Action",null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);

        TextView userName=headerView.findViewById(R.id.profile_name);
        CircleImageView profileImageV=headerView.findViewById(R.id.profile_image);

        if (!type.equals("Admin")){
            userName.setText(Prevalent.CurrentonlineUser.getName());
        Picasso.get().load(Prevalent.CurrentonlineUser.getImage()).placeholder(R.drawable.profileimage).into(profileImageV);

        }

//        userName.setText(Prevalent.CurrentonlineUser.getName());
//        Picasso.get().load(Prevalent.CurrentonlineUser.getImage()).placeholder(R.drawable.profileimage).into(profileImageV);

         recyclerView=findViewById(R.id.rv_menu);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
       // retrive all product in recyclerview
        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productRef,Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder,int i,@NonNull Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDes());
                        holder.  txtProductPrice.setText("Price= "+model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.pImageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (type.equals("Admin"))
                                {
                                    Intent intent=new Intent(HomeActivity.this,AdminMaintaiProductAct.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);

                                }
                                else
                                    {
                                        Intent intent=new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                        intent.putExtra("pid",model.getPid());
                                        startActivity(intent);

                                }

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent,false);
                        ProductViewHolder holder=new ProductViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            //admin cant visit navigation drawer when we use this if(!type.equals("Admin")
            //if we allow to admin then crash coz its for end users
            if (!type.equals("Admin"))
            {

                Intent intent=new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_search)
        {
            if (!type.equals("Admin"))
            {
                Intent intent=new Intent(HomeActivity.this,SearchProductActivity.class);
                startActivity(intent);

            }

        }
        else if (id == R.id.nav_categories)
        {

        }
        else if (id == R.id.nav_setting)
        {
            if (!type.equals("Admin"))
            {

                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_logout)
        {
            if (!type.equals("Admin"))
            {

                Paper.book().destroy();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    }


