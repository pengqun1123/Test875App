package com.testApp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baselibrary.listener.OnceClickListener;
import com.baselibrary.pojo.Manager;
import com.testApp.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By pq
 * on 2019/10/9
 */
public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.Holder> {

    private List<Manager> managers;
    private ManagerItemCallBack callBack;

    public ManagerAdapter(ManagerItemCallBack callBack) {
        managers = new ArrayList<>();
        this.callBack = callBack;
    }

    public void addData(List<Manager> managerList) {
        if (managerList.size() > 0) {
            managers.addAll(managerList);
            notifyDataSetChanged();
        }
    }

    public void addData(Manager manager) {
        if (manager != null) {
            managers.add(manager);
            notifyDataSetChanged();
        }
    }

    public void removeData(int position) {
        managers.remove(position);
        notifyDataSetChanged();
    }

    public void removeData(Manager manager) {
        if (manager != null) {
            managers.remove(manager);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        managers.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.manager_item, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.managerNo.setText(MessageFormat.format("{0}{1}",
                holder.itemView.getContext().getString(R.string.manager), i));
        holder.managerPw.setText(managers.get(i).getManage_pw());
        holder.managerItem.setOnClickListener(new OnceClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (callBack != null)
                    callBack.managerItemCallBack(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return managers.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final LinearLayout managerItem;
        private final AppCompatTextView managerNo, managerPw;

        Holder(@NonNull View itemView) {
            super(itemView);
            managerNo = itemView.findViewById(R.id.managerNo);
            managerItem = itemView.findViewById(R.id.managerItem);
            managerPw = itemView.findViewById(R.id.managerPw);

        }
    }

    public interface ManagerItemCallBack {
        void managerItemCallBack(int position);
    }
}
