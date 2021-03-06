package com.example.firebasedemo.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasedemo.Domain.User;
import com.example.firebasedemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
        Log.e("Test", String.valueOf(mUsers.size()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user=mUsers.get(position);
        Log.e("Test", user.getName());
        holder.btnfollow.setVisibility(View.VISIBLE);

        holder.userName.setText(user.getUserName());
        holder.fullName.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
        isFollowed(user.getId(),holder.btnfollow); //change the text of follow button

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btnfollow.setVisibility(View.GONE);
        }
        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnfollow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").
                            child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").
                            child(firebaseUser.getUid()).setValue(true);
                }
                if(holder.btnfollow.getText().toString().equalsIgnoreCase("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").
                            child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").
                            child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    private void isFollowed(final String id, final Button btnfollow) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                child("following");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()){
                    btnfollow.setText("Following");
                }
                else {
                    btnfollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView userName;
        public TextView fullName;
        public Button btnfollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile=itemView.findViewById(R.id.image_profile);
            userName=itemView.findViewById(R.id.username);
            fullName=itemView.findViewById(R.id.full_name);
            btnfollow=itemView.findViewById(R.id.button_follow);
        }
    }
}
