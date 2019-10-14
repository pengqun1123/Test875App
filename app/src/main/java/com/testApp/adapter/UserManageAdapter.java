package com.testApp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baselibrary.pojo.User;
import com.baselibrary.util.glidUtils.GlideUtil;

import com.testApp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By pq
 * on 2019/10/8
 */
public class UserManageAdapter extends RecyclerView.Adapter<UserManageAdapter.Holder> {

    private List<User> users;

    public UserManageAdapter() {
        users = new ArrayList<>();
    }

    public void addData(User user) {
        if (user != null) {
            this.users.add(user);
            notifyDataSetChanged();
        }
    }

    public void addData(List<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void setData(List<User> users){
        this.users=users;
        notifyDataSetChanged();
    }
    public void removeData(User user) {
        this.users.remove(user);
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        this.users.remove(position);
        notifyDataSetChanged();
    }

    public List<User> getUsers() {
        return this.users;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.manae_item, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        User user = users.get(i);
       // String imagePath = user.getFace().getImagePath();
        String imagePath=null;
        GlideUtil.loadCircleImage(holder.itemView.getContext(), R.drawable.ic_default_user_avatar,
                imagePath, holder.userAvatar);
        holder.userName.setText(user.getName());
        holder.userSex.setText(user.getSex());
        holder.userAge.setText(user.getAge());
        holder.userPhone.setText(user.getPhone());
        holder.userSection.setText(user.getSection());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AppCompatImageView userAvatar;
        private final AppCompatTextView userName;
        private final AppCompatTextView userSex;
        private final AppCompatTextView userAge;
        private final AppCompatTextView userPhone;
        private final AppCompatTextView userSection;

        Holder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userSex = itemView.findViewById(R.id.userSex);
            userAge = itemView.findViewById(R.id.userAge);
            userPhone = itemView.findViewById(R.id.userPhone);
            userSection = itemView.findViewById(R.id.userSection);

        }
    }
}
