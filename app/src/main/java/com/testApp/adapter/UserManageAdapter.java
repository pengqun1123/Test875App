package com.testApp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baselibrary.pojo.Face;
import com.baselibrary.pojo.Finger6;
import com.baselibrary.pojo.IdCard;
import com.baselibrary.pojo.Manager;
import com.baselibrary.pojo.Pw;
import com.baselibrary.pojo.User;
import com.baselibrary.util.glidUtils.GlideUtil;

import com.orhanobut.logger.Logger;
import com.testApp.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By pq
 * on 2019/10/8
 */
public class UserManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0; //说明是带有Header的
    public static final int TYPE_FOOTER = 1; //说明是带有Footer的
    public static final int TYPE_NORMAL = 2; //说明是不带有header和footer的

    private List<User> users;
    private UserItemCallBack callBack;
    private View headerView;
    private View footerView;

    public UserManageAdapter() {
        users = new ArrayList<>();
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyItemInserted(0);
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyItemInserted(getItemCount() - 1);
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (headerView != null && i == TYPE_HEADER) {
            return new RvHolder(headerView);
        }
        if (footerView != null && i == TYPE_FOOTER) {
            return new RvHolder(footerView);
        }
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.manae_item, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (getItemViewType(i) == TYPE_HEADER && holder instanceof RvHolder) {
            //RvHolder headHolder = (RvHolder) holder;
            return;
        }
        if (getItemViewType(i) == TYPE_FOOTER && holder instanceof RvHolder) {
            RvHolder footerHolder = (RvHolder) holder;
            footerHolder.showAllData.setText(footerHolder.itemView.getContext()
                    .getString(R.string.show_all_data));
            if (users.size() > 0) {
                footerHolder.showAllData.setVisibility(View.VISIBLE);
            } else {
                footerHolder.showAllData.setVisibility(View.GONE);
            }
            return;
        }
        if (getItemViewType(i) == TYPE_NORMAL && holder instanceof Holder) {
            Holder normalHolder = (Holder) holder;
            User user = users.get(i);
            Face face = user.getFace();
            Finger6 finger6 = user.getFinger6();
            Pw pw = user.getPw();
            IdCard idCard = user.getIdCard();
            String imagePath = null;
            if (face != null) {
                imagePath = face.getImagePath();
                normalHolder.faceLabel.setVisibility(View.VISIBLE);
            } else {
                normalHolder.faceLabel.setVisibility(View.GONE);
            }
            if (finger6 == null) {
                normalHolder.fingerLabel.setVisibility(View.GONE);
            } else {
                normalHolder.fingerLabel.setVisibility(View.VISIBLE);
            }
            if (pw == null) {
                normalHolder.pwLabel.setVisibility(View.GONE);
            } else {
                normalHolder.pwLabel.setVisibility(View.VISIBLE);
            }
            if (idCard == null) {
                normalHolder.cardLabel.setVisibility(View.GONE);
            } else {
                normalHolder.cardLabel.setVisibility(View.VISIBLE);
            }
            GlideUtil.loadCircleImage(normalHolder.itemView.getContext(), R.drawable.ic_default_user_avatar,
                    imagePath, normalHolder.userAvatar);
            normalHolder.userName.setText(user.getName());
            normalHolder.userSex.setText(user.getSex());
            normalHolder.userAge.setText(user.getAge());
            normalHolder.userNo.setText(MessageFormat.format("编号:{0}", user.getWorkNum()));
            normalHolder.userPhone.setText(MessageFormat.format("手机号:{0}", user.getPhone()));
            normalHolder.userSection.setText(MessageFormat.format("部门:{0}", user.getSection()));
            normalHolder.userCompany.setText(MessageFormat.format("公司:{0}", user.getOrganizName()));
            normalHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (callBack != null)
                        callBack.itemLongClickListener(user, null, normalHolder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView == null && footerView == null) {
            return TYPE_NORMAL;
        }
        if (headerView != null && position == 0) {
            return TYPE_HEADER;
        }
        if (footerView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (headerView == null && footerView == null) {
            return users.size();
        } else if (headerView != null && footerView == null) {
            return users.size() + 1;
        } else if (headerView == null && footerView != null) {
            return users.size() + 1;
        } else {
            return users.size() + 2;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final AppCompatImageView userAvatar;
        private final AppCompatTextView userName;
        private final AppCompatTextView userSex;
        private final AppCompatTextView userAge;
        private final AppCompatTextView userPhone, fingerLabel, pwLabel, faceLabel, cardLabel;
        private final AppCompatTextView userSection, userCompany, userNo;

        Holder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            userSex = itemView.findViewById(R.id.userSex);
            userAge = itemView.findViewById(R.id.userAge);
            userPhone = itemView.findViewById(R.id.userPhone);
            userSection = itemView.findViewById(R.id.userSection);
            userCompany = itemView.findViewById(R.id.userCompany);
            userNo = itemView.findViewById(R.id.userNo);
            pwLabel = itemView.findViewById(R.id.pwLabel);
            cardLabel = itemView.findViewById(R.id.cardLabel);
            faceLabel = itemView.findViewById(R.id.faceLabel);
            fingerLabel = itemView.findViewById(R.id.fingerLabel);

        }
    }

    static class RvHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView showAllData;

        RvHolder(@NonNull View itemView) {
            super(itemView);
            showAllData = itemView.findViewById(R.id.showAllData);
        }
    }

    public interface UserItemCallBack {
        void userItemCallBack(int position);

        void itemLongClickListener(User user, String managerName, int position);
    }
}
