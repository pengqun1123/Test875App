package com.testApp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baselibrary.pojo.Face;
import com.baselibrary.pojo.Manager;
import com.baselibrary.pojo.User;
import com.baselibrary.util.glidUtils.GlideUtil;

import com.testApp.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By pq
 * on 2019/10/8
 */
public class UserManageAdapter extends RecyclerView.Adapter<UserManageAdapter.Holder> {

    private List<User> users;
    private UserItemCallBack callBack;

    public UserManageAdapter() {
        users = new ArrayList<>();
    }

    public void setCallBack(UserItemCallBack callBack) {
        this.callBack = callBack;
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

    public void setData(List<User> users) {
        this.users = users;
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
        Face face = user.getFace();
        String imagePath = null;
        if (face != null) {
            imagePath = face.getImagePath();
        }
        GlideUtil.loadCircleImage(holder.itemView.getContext(), R.drawable.ic_default_user_avatar,
                imagePath, holder.userAvatar);
        holder.userName.setText(user.getName());
        holder.userSex.setText(user.getSex());
        holder.userAge.setText(user.getAge());
        holder.userPhone.setText(MessageFormat.format("手机号:{0}", user.getPhone()));
        holder.userSection.setText(MessageFormat.format("部门:{0}", user.getSection()));
        holder.userCompany.setText(MessageFormat.format("公司:{0}", user.getOrganizName()));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (callBack != null)
                    callBack.itemLongClickListener(user, null, holder.getAdapterPosition());
                return false;
            }
        });

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
        private final AppCompatTextView userSection, userCompany;

        Holder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userSex = itemView.findViewById(R.id.userSex);
            userAge = itemView.findViewById(R.id.userAge);
            userPhone = itemView.findViewById(R.id.userPhone);
            userSection = itemView.findViewById(R.id.userSection);
            userCompany = itemView.findViewById(R.id.userCompany);

        }
    }

    public interface UserItemCallBack {
        void userItemCallBack(int position);

        void itemLongClickListener(User user, String managerName, int position);
    }
}
