package com.face.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baselibrary.base.BaseApplication;
import com.baselibrary.dao.db.DBUtil;
import com.baselibrary.dao.db.FaceDao;
import com.baselibrary.pojo.Face;
import com.face.R;
import com.orhanobut.logger.Logger;
import com.zqzn.android.face.exceptions.FaceException;
import com.zqzn.android.face.model.FaceSearchLibrary;
import com.face.common.FaceConfig;
import com.face.db.User;
import com.face.db.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息列表显示示例
 */
public class UserListActivity extends AppCompatActivity {

    private static final String TAG = UserListActivity.class.getSimpleName();
    private RecyclerView rvUserList;
    private Button btnAddUser;
    private Button btnLoadMore;
    private UserListAdapter userListAdapter;
    private UserManager userManager;
    private int offset = 0;
    private static final int LIMIT = 10;
    private final List<Face> faces  = new ArrayList<>();
    private FaceSearchLibrary faceSearchLibrary;
    private DBUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity_user_list);
        rvUserList = (RecyclerView) findViewById(R.id.rv_userlist);
        btnAddUser = (Button) findViewById(R.id.btn_adduser);
        btnLoadMore = (Button) findViewById(R.id.btn_loadmore);
        rvUserList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        rvUserList.setItemAnimator(new DefaultItemAnimator());
        userListAdapter = new UserListAdapter();
        rvUserList.setAdapter(userListAdapter);
        dbUtil = BaseApplication.getDbUtil();
        initFaceSDKApi();
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load more
                List<Face> moreUsers = dbUtil.getDaoSession().queryBuilder(Face.class).offset(offset).limit(LIMIT).build().list();
              //  List<User> moreUsers = userManager.find(LIMIT, offset);
                if (moreUsers != null && !moreUsers.isEmpty()) {
                    offset += moreUsers.size();
                    faces.addAll(moreUsers);
                    userListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserListActivity.this, "没有更多的数据了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i(TAG, "点击新增人员按钮");
                startActivity(new Intent(UserListActivity.this, UserAddActivity.class));
            }
        });
    }

    private void initFaceSDKApi() {
        userManager = FaceConfig.getInstance().getUserManager();
        faceSearchLibrary = FaceConfig.getInstance().getFaceSDK().getFaceSearchLibrary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        userListAdapter.notifyDataSetChanged();
    }

    private void initData() {
        faces.clear();
        offset = 0;

        List<Face> userList = dbUtil.getDaoSession().queryBuilder(Face.class).offset(offset).limit(LIMIT).build().list();
      //  List<User> userList = userManager.find(LIMIT, offset);
        if (userList != null && !userList.isEmpty()) {
            offset += userList.size();
            faces.addAll(userList);
        }
    }

    class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserItemViewHolder> {

        @NonNull
        @Override
        public UserItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new UserItemViewHolder(LayoutInflater.from(UserListActivity.this).inflate(R.layout.face_useritem, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserItemViewHolder userItemViewHolder, int i) {
            Face face = faces.get(i);
            userItemViewHolder.head.setImageBitmap(BitmapFactory.decodeFile(face.getImagePath()));
            userItemViewHolder.name.setText(face.getName());
            userItemViewHolder.del.setTag(face);
        }

        @Override
        public int getItemCount() {
            return faces.size();
        }

        class UserItemViewHolder extends RecyclerView.ViewHolder {

            private final ImageView head;
            private final TextView name;
            private final Button del;

            public UserItemViewHolder(@NonNull View itemView) {
                super(itemView);
                head = itemView.findViewById(R.id.iv_head);
                name = itemView.findViewById(R.id.tv_name);
                del = itemView.findViewById(R.id.btn_del);
                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Face face = (Face) v.getTag();
                       // boolean delRet = userManager.deleteOne(id);
                            try {
                                dbUtil.deleteById(Face.class,face.getUId());
                                File file = new File(face.getImagePath());
                                file.delete();
                                //从数据库中删除成功后，从搜索缓存中删除
                                faceSearchLibrary.removePersons(new long[]{face.getUId()});
                            } catch (FaceException e) {
                                Logger.e(TAG, "将用户从离线1：N搜索库中移除失败. " + face.getUId(), e);
                                Toast.makeText(UserListActivity.this, "将用户从离线1：N搜索库中移除失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            int index = -1;
                            for (int i = 0; i < faces.size(); i++) {
                                if (faces.get(i).getUId() == face.getUId()) {
                                    index = i;
                                }
                            }
                            if (index >= 0) {
                                faces.remove(index);
                                UserListAdapter.this.notifyItemRemoved(index);
                            }
                            Toast.makeText(UserListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            return;

                      //  Toast.makeText(UserListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


}
