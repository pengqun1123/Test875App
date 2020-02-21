package com.arcFace.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcFace.R;
import com.arcFace.faceServer.CompareResult;
import com.arcFace.faceServer.FaceServer;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

/**
 * 识别成功后左上角的人脸提示
 */
public class FaceSearchResultAdapter extends RecyclerView.Adapter<FaceSearchResultAdapter.CompareResultHolder> {
    private List<CompareResult> compareResultList;
    private LayoutInflater inflater;

    public FaceSearchResultAdapter(List<CompareResult> compareResultList, Context context) {
        inflater = LayoutInflater.from(context);
        this.compareResultList = compareResultList;
    }

    @NonNull
    @Override
    public CompareResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.arc_recycler_item_search_result
                , null, false);
        CompareResultHolder compareResultHolder = new CompareResultHolder(itemView);
        compareResultHolder.textView = itemView.findViewById(R.id.tv_item_name);
        compareResultHolder.imageView = itemView.findViewById(R.id.iv_item_head_img);
        return compareResultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompareResultHolder holder, int position) {
        if (compareResultList == null) {
            return;
        }
//        File imgFile = new File(FaceServer.ROOT_PATH + File.separator +
//                FaceServer.SAVE_IMG_DIR + File.separator +
//                compareResultList.get(position).getUserName() + FaceServer.IMG_SUFFIX);
        byte[] faceHead = compareResultList.get(position).getFaceHead();
        Glide.with(holder.imageView)
                .load(faceHead)
                .into(holder.imageView);
        holder.textView.setText(compareResultList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return compareResultList == null ? 0 : compareResultList.size();
    }

    class CompareResultHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        CompareResultHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
