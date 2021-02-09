package com.example.amrgamal.weartracker.fragments;

/**
 * Created by amrga on 01/02/2018.
 */

        import android.content.Context;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.annotation.RequiresApi;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.DefaultItemAnimator;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Toast;

        import com.example.amrgamal.weartracker.GetTimeAgo;
        import com.example.amrgamal.weartracker.R;
        import com.example.amrgamal.weartracker.adapters.User_Adapter;
        import com.example.amrgamal.weartracker.models.User_Model;
        import com.example.amrgamal.weartracker.models.WearUser;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;

public class User_Fragment extends Fragment {

    OnListFragmentInteractionListener mListener;

    private List<WearUser> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private User_Adapter mAdapter;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth auth;
    String currentUserId;
    private DatabaseReference mGetData;

    public User_Fragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.users_fragment, container, false);
        auth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.users_recycle_view);


//        userList.add(new User_Model("عمرو","منوف","5:20"));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);

        currentUserId = auth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Child");

        mGetData = FirebaseDatabase.getInstance().getReference().child("");

        return view;
    }



    private void collectUserData(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();
        ArrayList<WearUser> list = new ArrayList<>();
        WearUser wearUser;
        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            wearUser = new WearUser();
            wearUser.setName(singleUser.get("name").toString());
            wearUser.setAdress(singleUser.get("adress").toString());

            wearUser.setKey(entry.getKey());
            lastTime = Long.parseLong(singleUser.get("time").toString());

            wearUser.setTime(getTimeAgo.getTimeAgo(lastTime, getContext()));
            list.add(wearUser);
            //Get phone field and append to list
            phoneNumbers.add(singleUser.get("name").toString());


        }

        mAdapter = new User_Adapter(list, getContext(), mListener,currentUserId);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        System.out.println(phoneNumbers.toString());
        System.out.println("*********************");
        System.out.println(list.get(0).getName());
        System.out.println(list.get(0).getAdress());
        System.out.println(list.get(0).getTime());


    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Child").child(currentUserId);
            reference.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            try {
                                collectUserData((Map<String, Object>) dataSnapshot.getValue());
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }catch (Exception e){
            Toast.makeText(getContext()," لا يوجد مستخدمين حاليا  ",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

    }

    public interface OnListFragmentInteractionListener {

        boolean onListClick(View v,String k);
    }

}
