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
public class ManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Manager> managers;
    private ManagerItemCallBack callBack;
    private View footerView;

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (footerView != null && i == AdapterType.TYPE_FOOTER) {
            return new RvHolder(footerView);
        } else {
            return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.manager_item, viewGroup, false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (getItemViewType(i) == AdapterType.TYPE_FOOTER && holder instanceof RvHolder) {
            RvHolder footerHolder = (RvHolder) holder;
            footerHolder.showAllData.setText(footerHolder.itemView.getContext()
                    .getString(R.string.show_all_data));
            if (managers.size() > 0) {
                footerHolder.showAllData.setVisibility(View.VISIBLE);
            } else {
                footerHolder.showAllData.setVisibility(View.GONE);
            }
            return;
        }
        if (getItemViewType(i) == AdapterType.TYPE_NORMAL && holder instanceof Holder) {
            Holder normalHolder = (Holder) holder;
            normalHolder.managerNo.setText(MessageFormat.format("{0}{1}",
                    holder.itemView.getContext().getString(R.string.manager), i));
            Manager manager = managers.get(i);
            String manage_pw = manager.getManage_pw();
            normalHolder.managerPw.setText(MessageFormat.format("密码:{0}", manage_pw));
            normalHolder.managerItem.setOnClickListener(new OnceClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    if (callBack != null)
                        callBack.managerItemCallBack(holder.getAdapterPosition());
                }
            });
            normalHolder.managerItem.setOnLongClickListener(view -> {
                if (callBack != null)
                    callBack.itemLongClickListener(manager,
                            normalHolder.itemView.getContext().getString(R.string.manager) + i
                            , normalHolder.getAdapterPosition());
                return false;
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            //到底了
            return AdapterType.TYPE_FOOTER;
        }
        if (footerView == null) {
            //正常的item
            return AdapterType.TYPE_NORMAL;
        }
        return AdapterType.TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (footerView != null) {
            return managers.size() + 1;
        } else {
            return managers.size();
        }
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

    static class RvHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView showAllData;

        RvHolder(@NonNull View itemView) {
            super(itemView);
            showAllData = itemView.findViewById(R.id.showAllData);
        }
    }

    public interface ManagerItemCallBack {
        void managerItemCallBack(int position);

        void itemLongClickListener(Manager manager, String managerName, int position);
    }
}
