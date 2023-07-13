package com.algebratech.pulse_wellness.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algebratech.pulse_wellness.Comment_activity;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.Camera2Activity;
import com.algebratech.pulse_wellness.activities.NewsFeedDetailActivity;
import com.algebratech.pulse_wellness.activities.NewsLikeActivity;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.adapters.NewsFeedAdapter;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.fragment_tab_newsfeed_dashboard;
import com.algebratech.pulse_wellness.models.NewsFeedModel;
import com.algebratech.pulse_wellness.network.VolleyMultipartRequest;
import com.algebratech.pulse_wellness.utils.BitmapUtils;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.NewsFeedListner;
import com.algebratech.pulse_wellness.utils.StaticMethods;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressLint("NewApi")
public class NewsFeedFragment extends Fragment implements RecyclerView.OnScrollChangeListener, NewsFeedListner {

    View root;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    List<NewsFeedModel> news = new ArrayList<>();
    NewsFeedListner newsFeedListner;
    private RecyclerView.Adapter mAdapter;
    private int requestCountmain = 1;
    private SharedPreferences sharedPreferences;
    private String userId;
    private String my_name;
    private Bitmap bitmap;
    String outputSource;
    private FragmentManager FM;
    private FragmentTransaction FT;
    private RequestQueue requestQueue;
    private NewsFeedAdapter newsFeedAdapter;
    ImageView likeImg;
    TextView likeText, no_news;
    private boolean image, video = false;
    String post_type = "text";
    private static final int CAMERA_REQUEST = 9514;
    private ArrayList<String> returnValue = new ArrayList<String>();
    String post_desc, post_data, extension;
    ImageView imgPreview;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_news_feed, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    init();
                    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    getNewsFeeds(requestCountmain);
                }
            }
        }, 500);

    }

    public void init() {

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);
        my_name = sharedPreferences.getString("fullname", null);
        fab = root.findViewById(R.id.fab);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setOnScrollChangeListener(this);
        no_news = root.findViewById(R.id.no_newsfeed);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FM = getActivity().getSupportFragmentManager();
                FT = FM.beginTransaction();
                showBottomSheetDialog();
            }
        });

    }


    private void getNewsFeeds(int requestCount) {
        if (CM.isConnected(getActivity())) {
            CM.showProgressLoader(getActivity());
            JSONObject object = new JSONObject();
            try {
                object.put("page", requestCount);
                object.put("user_id", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.allfeeds, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            news.clear();
                            try {

                                if (response.getString("status").equals("true")) {
                                    Log.e("NEWS RESPONSE",response.getString("data"));
                                    JSONArray array = new JSONArray(response.getString("data"));
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String id = object.getString("id");
                                        String category = object.getString("category");
                                        String headline = object.getString("headline");
                                        String post = object.getString("post");
                                        String imageurl = object.getString("imageurl");
                                        String postedby = object.getString("postedby");
                                        String partnerid = object.getString("partnerid");
                                        String created_at = object.getString("created_at");
                                        String username = object.getString("firstname") + " " + object.getString("lastname");
                                        String profliepic = object.getString("profileurl");
                                        String type = object.getString("type");
                                        String total_likes = object.optString("tot_likes");
                                        String like_status = object.optString("like_status");
                                        String tot_comment_count = object.optString("tot_comment_count");
                                        String points = object.optString("loyaltpoints");

                                        NewsFeedModel newsfeed = new NewsFeedModel(id, category, headline, post, imageurl, postedby, partnerid, created_at, username, profliepic, type, total_likes, like_status, false, tot_comment_count, points);
                                        news.add(newsfeed);
                                    }
                                }
                            } catch (Exception e) {

                                Log.e(Constants.TAG, e.getMessage());
                                Log.e("CATCH", e.getMessage());
                                CM.HideProgressLoader();
                                no_news.setVisibility(View.VISIBLE);
                            }

                            if (news.size() == 0) {
                                CM.HideProgressLoader();
                                Log.e("hide 2", "hide 2");
                                no_news.setVisibility(View.VISIBLE);
                            } else
                                setdataAdapter();


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    Log.e("Eroooorrr22", error.toString());
                    //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            });

//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    10000,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);
            requestCountmain++;
        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();

    }


    void setdataAdapter() {
        Log.e("NEWSFEED", news.toString());
        recyclerView.setVisibility(View.VISIBLE);
        no_news.setVisibility(View.GONE);
        newsFeedAdapter = new NewsFeedAdapter(getContext(), news, this);
        mAdapter = newsFeedAdapter;
        recyclerView.setAdapter(mAdapter);
        CM.HideProgressLoader();
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

//        if (isLastItemDisplaying(recyclerView)) {
//            try {
//                getNewsFeeds(requestCountmain);
//            } catch (Exception e) {
//
//            }
//
//        }

    }


    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            newsFeedAdapter.releasePlayer();
            requestQueue.stop();
            requestQueue.getCache().clear();
        } catch (Exception e) {

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            requestQueue.stop();
            requestQueue.getCache().clear();
        } catch (Exception ignored) {

        }

    }

    @Override
    public void onClickViewEvent(View view, int position) {

        RelativeLayout relativeLayoutLikes = view.findViewById(R.id.relativeLayoutLikes);
        LinearLayout relativeLayoutClick = view.findViewById(R.id.vidimg);
        LinearLayout linearComments = view.findViewById(R.id.linearComments);
        LinearLayout data = view.findViewById(R.id.data);
        LinearLayout share = view.findViewById(R.id.share);
        TextView likeCount = view.findViewById(R.id.LikeCount);
        likeImg = view.findViewById(R.id.likeImg);
        likeText = view.findViewById(R.id.likeText);

        if (view == relativeLayoutLikes) {
            if (!news.get(position).getLike_status().isEmpty() && news.get(position).getLike_status().equals("like")) {
                Like(news.get(position).getId(), "unlike", likeImg, position);
            } else {
                Like(news.get(position).getId(), "like", likeImg, position);
            }
        }

        if (view == data) {
            OpenFeed(news.get(position));
        }

        if (view == relativeLayoutClick) {
            OpenFeed(news.get(position));
        }


        if (view == linearComments) {
            Intent intent = new Intent(getActivity(), Comment_activity.class);
            intent.putExtra("FEEDID", news.get(position).getId());
            startActivityForResult(intent, 100);
        }
        if (view == share) {
            share(news.get(position));
        }
        if (view == likeCount) {
            Intent intent = new Intent(getActivity(), NewsLikeActivity.class);
            intent.putExtra("FEEDID", news.get(position).getId());
            startActivity(intent);
        }
    }

    private void Like(String feedId, String status, ImageView imageView, int pos) {

        if (CM.isConnected(getActivity())) {
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("type", status);
                object.put("feed_id", feedId);
                Log.e("FEEDID", feedId);
                Log.e("LIKE_POSITION", "" + pos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.add_feed_activities, object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("LIKEEE", String.valueOf(response));
                            Log.e("TYPE", news.get(pos).getType());

                            try {
                                if (response.getString("status").equals("true")) {
                                    if (status.equals("unlike")) {
                                        news.get(pos).setLike_status(status);
                                        int likeCount = 0;
                                        if (news.get(pos).getTotal_likes() != null &&
                                                !news.get(pos).getTotal_likes().isEmpty()) {
                                            likeCount = Integer.parseInt(news.get(pos).getTotal_likes());
                                        }
                                        if (likeCount > 0) {
                                            likeCount = likeCount - 1;
                                        }
                                        news.get(pos).setTotal_likes("" + likeCount);

                                    } else {
                                        news.get(pos).setLike_status(status);
                                        int likeCount = 0;
                                        if (news.get(pos).getTotal_likes() != null &&
                                                !news.get(pos).getTotal_likes().isEmpty()) {
                                            likeCount = Integer.parseInt(news.get(pos).getTotal_likes());
                                        }
                                        likeCount = likeCount + 1;
                                        news.get(pos).setTotal_likes("" + likeCount);
                                    }
                                    Log.e("POSITION", String.valueOf(pos));
                                    mAdapter.notifyItemChanged(pos, news.get(pos));
                                } else {
                                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.toString());
                    Toast.makeText(getActivity(), "Please check internet and try again!", Toast.LENGTH_LONG).show();

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    private void OpenFeed(NewsFeedModel position) {
        getActivity().startActivity(new Intent(getActivity(), NewsFeedDetailActivity.class)
                .putExtra("headline", position.getHeadline())
                .putExtra("post", position.getPost())
                .putExtra("type", position.getType())
                .putExtra("data", position.getImageurl())
                .putExtra("profile", position.getProfile_pic())
                .putExtra("username", position.getUserName())
                .putExtra("date", position.getCreatedAt())
        );
    }

    private void share(NewsFeedModel position) {
        Intent shareIntent = new Intent();
        String headline = position.getHeadline();
        String post = position.getPost();
        if (position.getType().equals("video")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "Posted a feed on Pulse News Feed."
                    + "\n \n" + position.getImageurl()
                    + "\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            try {
                URL url = new URL(position.getImageurl());
                shareIntent.putExtra(Intent.EXTRA_ORIGINATING_URI, url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            shareIntent.setType("video/*");

            getActivity().startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

        }
        if (position.getType().equals("image")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "\n"
                    + truncate(post, 50)
                    + "\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("image/*");


            try {
                URL url = new URL(position.getImageurl());
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                shareIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(image));
                getActivity().startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if (position.getType().equals("text")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "\n"
                    + truncate(post, 50)
                    + "'\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("text/plain");
            getActivity().startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

        }
        if (position.getType().equals("youtube")) {

            String sharetext = position.getUserName()
                    + " on Pulse Helath \n\n "
                    + "Posted a feed on Pulse News Feed."
                    + "\n \n Join Pulse Health for your wellness and community like " + my_name
                    + ".\n Download now Pulse Health : https://play.google.com/store/apps/details?id=com.algebratech.pulse_wellness ";

            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE, "Pulse Health App");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pulse Community");
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
            shareIntent.setType("*/*");
            getActivity().startActivity(Intent.createChooser(shareIntent, "Share Pulse Post"));

        }
    }

    public static String truncate(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len) + "...";
        } else {
            Log.e("news", str);
            return str;
        }
    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_post_feed, null);
        bottomSheetDialog.setContentView(view);
        EditText post = view.findViewById(R.id.post);
        RelativeLayout capture = view.findViewById(R.id.capture);
        Button btnPost = view.findViewById(R.id.btnPost);
        post.setImeOptions(EditorInfo.IME_ACTION_DONE);
        post.setRawInputType(InputType.TYPE_CLASS_TEXT);

        imgPreview = view.findViewById(R.id.imgPreview);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Camera2Activity.class);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (post.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter your thoughts", Toast.LENGTH_LONG).show();
                } else {
                    post_desc = post.getText().toString();
                    if (video) {
                        post_type = "video";
                    }
                    if (image) {
                        post_type = "image";
                    }

                    if (!image && !video) {
                        post_type = "text";
                    }


                    if (!post_type.equals("text")) {

                        Log.d(Constants.TAG + "DATA", Uri.fromFile(new File(post_data)).toString());

                        Uri uri = Uri.fromFile(new File(post_data));

                        String displayName = Calendar.getInstance().getTimeInMillis() + "." + extension;

                        uploadPost(displayName, uri);

//                        createPost(displayName, uri, true);

                        //Toast.makeText(context,post_type + " : "  + post_data,Toast.LENGTH_LONG).show();
                    } else {
                        createPost();
                        //Toast.makeText(context,post_type + " : "  + post_desc,Toast.LENGTH_LONG).show();
                    }
                    bottomSheetDialog.dismiss();
                }
            }

        });
    }


    private void createPost() {

        if (CM.isConnected(getActivity())) {
            CM.showProgressLoader(getActivity());
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", userId);
                object.put("post_type", post_type);
                object.put("post_category", "");
                object.put("post_desc", post_desc);
                object.put("post_privacy", "Friends Only");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.add_feed, object,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(JSONObject response) {
                            CM.HideProgressLoader();
                            try {

                                if (response.getString("status").equals("true")) {
                                    requestCountmain = 1;
                                    getNewsFeeds(requestCountmain);
                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                } else {

                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CM.HideProgressLoader();
                    Log.e("hide 6", "hide 6");
                    try {
                        if (error.getMessage().contains(Api.baseurl)) {
                            Toast.makeText(getContext(), "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.d(Constants.TAG, e.getMessage());
                    }
                }
            });
            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);
        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    private void uploadPost(final String pdfname, Uri pdffile) {
        if (CM.isConnected(getActivity())) {
            CM.showProgressLoader(getActivity());
            InputStream iStream = null;
            try {

                iStream = getContext().getContentResolver().openInputStream(pdffile);
                final byte[] inputData = getBytes(iStream);

                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Api.add_feed,
                        new Response.Listener<NetworkResponse>() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onResponse(NetworkResponse response) {
                                CM.HideProgressLoader();
                                requestQueue.getCache().clear();
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(response.data));

                                    if (jsonObject.getString("status").equals("true")) {
                                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        FM = getActivity().getSupportFragmentManager();
                                        FT = FM.beginTransaction();
                                        FT.replace(R.id.content_frame, new fragment_tab_newsfeed_dashboard()).commit();
                                        FM.popBackStack();

                                    } else {
                                        Toast.makeText(getContext(), "Failed to create post...Please try again", Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    CM.HideProgressLoader();
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                CM.HideProgressLoader();
                                try {
                                    if (error.getMessage().contains(Api.baseurl)) {
                                        Toast.makeText(getContext(), "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Log.d(Constants.TAG, e.getMessage());
                                }
                            }
                        }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", userId);
                        params.put("post_type", post_type);
                        params.put("post_category", "");
                        //here
                        params.put("post_desc", post_desc);
                        params.put("post_privacy", "Friends Only");

                        Log.e("POSTTT", params.toString());
                        return params;
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();

                        params.put("uploaded_file", new DataPart(pdfname, inputData));
                        Log.e("POSTTT", params.toString());
                        return params;
                    }
                };


                volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(volleyMultipartRequest);


            } catch (FileNotFoundException e) {
                CM.HideProgressLoader();
                e.printStackTrace();
            } catch (IOException e) {
                CM.HideProgressLoader();
                e.printStackTrace();
            }
        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100 && resultCode == 100) {
                requestCountmain = 1;
                getNewsFeeds(requestCountmain);
            }

        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
        }


        if (requestCode == CAMERA_REQUEST) {

            try {
                returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                if (!returnValue.isEmpty()) {

                    if (!StaticMethods.sizeLimitUpload(returnValue.get(0))) {
                        Log.d(Constants.TAG + "PATH", returnValue.get(0));
                        Log.d(Constants.TAG + "DATA", Uri.fromFile(new File(returnValue.get(0))).toString());

                        File f = new File(returnValue.get(0));
                        int len = f.getAbsolutePath().length();
                        extension = String.valueOf(f.getAbsolutePath().subSequence(len - 3, len));

                        Log.d(Constants.TAG + "Extension", extension);
                        String base64 = StaticMethods.fileToBase64(returnValue.get(0));

                        Log.d(Constants.TAG + "Base64", base64);

                        if (extension.equals("mp4") | extension.equals("mkv")) {

                            video = true;
                            image = false;
                            Log.d(Constants.TAG + "File", f.getAbsolutePath() + video);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                bitmap = ThumbnailUtils.createVideoThumbnail(f, new Size(500, 500), null);
                            } else {
                                bitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            }
                            // Picasso.with(context).load(f.getAbsolutePath()).error(R.drawable.post_bg_lines).into(imgPreview);
                            Glide.with(getContext()).load(bitmap).into(imgPreview);

                            outputSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PulseHealth" + "/" + ".tmp/";
                            File dir = new File(outputSource);
                            if (!dir.exists()) {
                                dir.mkdirs();
                                Log.d(Constants.TAG + "FileFolder", "Ndagadzira");
                            } else {
                                Log.d(Constants.TAG + "FileFolder", "Ndiripo");
                            }
                            //compressVideo(f.getAbsolutePath());
                            post_data = f.getAbsolutePath();
                            //String filePath = String.valueOf(new VideoCompressAsyncTask(context).execute("false", f.getAbsolutePath(), outputSource));
                            //String filePath = SiliCompressor.with(context).compressVideo(f.getAbsolutePath(), outputSource);
                            //Log.d(Constants.TAG + "FileCompressed", filePath);


                        }

                        if (extension.equals("jpg") | extension.equals("png")) {

                            image = true;
                            video = false;
                            Log.d(Constants.TAG + "File", f.getAbsolutePath());
                            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                            Log.d(Constants.TAG + "Bitmap", BitmapUtils.decodeImage(bitmap));
                            //Glide.with(context).load(StaticMethods.RotateBitmap(bitmap,-90)).into(imgPreview);
                            Picasso.get().load("file:" + f.getAbsolutePath()).error(R.drawable.ic_insert_photo).into(imgPreview);
                            post_data = f.getAbsolutePath();

                        }

                    } else {
                        Toast.makeText(getContext(), "File too large , please choose a file below 150MB", Toast.LENGTH_LONG).show();
                    }

                }

            } catch
            (Exception e) {
                Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
                Toast.makeText(getContext(), "Please try again.", Toast.LENGTH_LONG).show();
            }

        }

    }


    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;

        try {
            File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;


    }


}
