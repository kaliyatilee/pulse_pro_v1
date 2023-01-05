package com.algebratech.pulse_wellness;

import static com.inuker.bluetooth.library.BluetoothService.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.adapters.commentAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.models.commentModel;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.FeedCommentListner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Comment_activity extends AppCompatActivity implements FeedCommentListner {
    private Toolbar toolbarPolicy;
    private TextView send, no_comment;
    private FeedCommentListner feedCommentListner;
    private EditText editText;
    private String userId;
    private SharedPreferences sharedPreferences;
    private String feedId;
    private RequestQueue requestQueue;
    private int requestCountmain = 1;
    List<commentModel> commentModelList = new ArrayList<>();
    List<commentModel> commentModelList1 = new ArrayList<>();
    private commentAdapter commentAdapter;
    private RecyclerView.Adapter mAdapter;
    RecyclerView recyclerView;
    String selectedCommentId = "";
    String subSelectedCommentId = "";
    ImageView likeImg;
    TextView reply;
    int likeCount;
    int CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        feedId = getIntent().getStringExtra("FEEDID");
        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        editText = findViewById(R.id.commentMessage);
        send = findViewById(R.id.send);
        no_comment = findViewById(R.id.no_comment);
        recyclerView = findViewById(R.id.recycle_comment);
        setSupportActionBar(toolbarPolicy);
        setTitle("Comments");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);

        GetComments();

        if (Build.VERSION.SDK_INT >= 24) {
            //this solves android.os.FileUriExposedException
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().toString().trim().isEmpty()) {
                    send.setTextColor(getResources().getColor(R.color.gray));
                }
                if (!editText.getText().toString().trim().isEmpty()) {
                    send.setTextColor(getResources().getColor(R.color.primary));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().trim().isEmpty()) {
                    AddComment(selectedCommentId, subSelectedCommentId);
                } else
                    Toast.makeText(Comment_activity.this, "Please enter comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(CODE);
        super.onBackPressed();
    }

    private void GetComments() {
        if (CM.isConnected(Comment_activity.this)) {
            CM.showProgressLoader(Comment_activity.this);
            commentModelList.clear();
            JSONObject object = new JSONObject();
            try {
                object.put("feed_id", feedId);
                object.put("user_id", userId);
                object.put("type", "comment");
                Log.e("getComment", object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.getfeeddetails, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                CM.HideProgressLoader();
                                if (response.getString("status").equals("success")) {
                                    Log.e("comment", response.toString());
                                    JSONArray array = new JSONArray(response.getString("data"));
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String like_status = object.getString("like_status");
                                        String user_id = object.getString("user_id");
                                        String userName = object.getString("firstname") + " " + object.getString("lastname");
                                        String userComment = object.getString("comments");
                                        String comment_id = object.getString("comment_id");
                                        String tot_likes = object.getString("tot_likes");
                                        String tot_comments = object.getString("tot_comments");
                                        String profileurl = object.getString("profileurl");
                                        String created_at = object.getString("created_at");

                                        JSONArray subarray = object.getJSONArray("comments_arr");
                                        commentModelList1 = new ArrayList<>();
                                        for (int j = 0; j < subarray.length(); j++) {

                                            JSONObject subobject = subarray.getJSONObject(j);

                                            String subId = subobject.getString("id");
                                            String subLike_status = subobject.getString("like_status");
                                            String subUser_id = subobject.getString("user_id");
                                            String subUserName = subobject.getString("firstname") + " " + subobject.getString("lastname");
                                            String subUserComment = subobject.getString("comments");
                                            String subComment_id = subobject.optString("comment_id");
                                            String subTot_likes = subobject.optString("tot_likes");
                                            String subTot_comments = subobject.optString("tot_comments");
                                            String subProfileurl = subobject.getString("profileurl");
                                            String sub_created_at = subobject.getString("created_at");

                                            commentModel commentModel1 = new commentModel(subId, subUserComment, subUser_id,
                                                    subUserName, subComment_id, subTot_likes, subTot_comments, subProfileurl, sub_created_at,
                                                    subLike_status, null);
                                            commentModelList1.add(commentModel1);
                                        }


                                        commentModel commentModel = new commentModel(id, userComment, user_id, userName, comment_id, tot_likes, tot_comments, profileurl, created_at, like_status, commentModelList1);
                                        commentModelList.add(commentModel);
                                    }
                                    setDataAdapter();


                                } else {


                                }

                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());

                            }
                            if (commentModelList.size() == 0) {
                                CM.HideProgressLoader();
                                no_comment.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", error.toString());
                    CM.HideProgressLoader();
                }
            });

            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(Comment_activity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    void setDataAdapter() {
        commentAdapter = new commentAdapter(commentModelList, getContext(), this, false, 0);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = commentAdapter;
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.layer));

        recyclerView.addItemDecoration(itemDecoration);
    }

    private void AddComment(String commentId, String subCommentId) {

        if (CM.isConnected(Comment_activity.this)) {
            no_comment.setVisibility(View.GONE);
            CM.showProgressLoader(Comment_activity.this);
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("type", "comment");
                object.put("feed_id", feedId);
                if (subCommentId != null && !subCommentId.isEmpty()) {
                    object.put("comment_id", subCommentId);
                    object.put("root_comment_id", commentId);
                } else if (!commentId.isEmpty()) {
                    object.put("comment_id", commentId);
                }
                object.put("comments", editText.getText().toString());
                Log.e("params", object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.add_feed_activities, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("true")) {
                                    CM.HideProgressLoader();
                                    Log.e("status", response.getString("status"));
                                    CODE = 100;
                                    GetComments();

                                } else {

                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                CM.HideProgressLoader();
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    VolleyLog.d("Error", "Error: " + error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
            editText.setText("");
            editText.clearFocus();
            selectedCommentId = "";
        } else
            Toast.makeText(Comment_activity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onClickViewEvent(View view, int position, int subPosition) {
        reply = view.findViewById(R.id.reply);
        likeImg = view.findViewById(R.id.like_button);
        if (view == likeImg) {
            String commentId = commentModelList.get(position).getId();
            if (subPosition >= 0) {
                Log.e("COMMENT_ARR_LENGTH", "==" + commentModelList.get(position).getComments_arr());
                commentId = commentModelList.get(position).getComments_arr().get(subPosition).getId();
                if (commentModelList.get(position).getComments_arr().get(subPosition).getLike_status() != null &&
                        commentModelList.get(position).getComments_arr().get(subPosition).getLike_status().equalsIgnoreCase("no")) {
                    likeComment(commentId, "like", likeImg, position, subPosition);
                } else {
                    likeComment(commentId, "unlike", likeImg, position, subPosition);
                }
            } else {
                if (commentModelList.get(position).getLike_status() != null &&
                        commentModelList.get(position).getLike_status().equalsIgnoreCase("no")) {
                    likeComment(commentId, "like", likeImg, position, subPosition);
                } else {
                    likeComment(commentId, "unlike", likeImg, position, subPosition);
                }
            }


        } else if (view == reply) {
            selectedCommentId = commentModelList.get(position).getId();
            if (subPosition >= 0) {
                Log.e("COMMENT_ARR_LENGTH", "==" + commentModelList.get(position).getComments_arr());
                subSelectedCommentId = commentModelList.get(position).getComments_arr().get(subPosition).getId();
            }

            Log.e("Comment id", selectedCommentId);
            Log.e("Sub _ Comment id", subSelectedCommentId);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            editText.requestFocus();
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editText.getText().toString().trim().isEmpty()) {
                        AddComment(selectedCommentId, subSelectedCommentId);
                    } else
                        Toast.makeText(Comment_activity.this, "Please enter comment", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void likeComment(String commentId, String status, ImageView imageView, int position, int subposition) {

        if (CM.isConnected(Comment_activity.this)) {

            JSONObject object = new JSONObject();
            try {

                object.put("user_id", userId);
                object.put("type", status);
                object.put("feed_id", feedId);
                object.put("comment_id", commentId);
                object.put("comments", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("PARAMETERS", object.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.add_feed_activities, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("LIKECOMMENT", String.valueOf(response));
                            Log.d("LIKECOMMENTAPI", Api.add_feed_activities);
                            Log.d("LIKECOMMENTPARAMS", object.toString());
                            try {
                                if (response.getString("status").equals("true")) {


                                    if (subposition > -1) {

                                        int totalLikes = 0;

                                        if (!commentModelList.get(position).getComments_arr().get(subposition).getTot_likes().isEmpty()) {
                                            totalLikes = Integer.parseInt(commentModelList.get(position).getComments_arr().get(subposition).getTot_likes());
                                        }

                                        if (status.equalsIgnoreCase("like")) {
                                            commentModelList.get(position).getComments_arr().get(subposition).setLike_status("Yes");
                                            totalLikes = totalLikes + 1;
                                            commentModelList.get(position).getComments_arr().get(subposition).setTot_likes("" + totalLikes);
                                        } else {
                                            commentModelList.get(position).getComments_arr().get(subposition).setLike_status("No");
                                            totalLikes = totalLikes - 1;
                                            commentModelList.get(position).getComments_arr().get(subposition).setTot_likes("" + totalLikes);
                                        }

                                    } else {
                                        int totalLikes = 0;

                                        if (!commentModelList.get(position).getTot_likes().isEmpty()) {
                                            totalLikes = Integer.parseInt(commentModelList.get(position).getTot_likes());
                                        }

                                        if (status.equals("like")) {
                                            commentModelList.get(position).setLike_status("Yes");
                                            totalLikes = totalLikes + 1;
                                            commentModelList.get(position).setTot_likes("" + totalLikes);
                                        } else {
                                            commentModelList.get(position).setLike_status("No");
                                            totalLikes = totalLikes - 1;
                                            commentModelList.get(position).setTot_likes("" + totalLikes);
                                        }

                                    }


                                    commentAdapter.notifyItemChanged(position);

                                } else {

//                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("ERROR_EXCEPTION", e.getMessage());
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LIKECOMMENTPARAMS", object.toString());
                    VolleyLog.d("LIKECOMMENT", "Error: " + error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(Comment_activity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();


    }
}

