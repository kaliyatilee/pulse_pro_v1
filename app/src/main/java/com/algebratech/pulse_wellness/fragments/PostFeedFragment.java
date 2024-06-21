package com.algebratech.pulse_wellness.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.activities.Camera2Activity;
import com.algebratech.pulse_wellness.activities.CreateProfileOneActivity;
import com.algebratech.pulse_wellness.activities.LoginActivity;
import com.algebratech.pulse_wellness.activities.MainActivity;
import com.algebratech.pulse_wellness.activities.RegisterActivity;
import com.algebratech.pulse_wellness.api.Api;
import com.algebratech.pulse_wellness.network.VolleyMultipartRequest;
import com.algebratech.pulse_wellness.utils.BitmapUtils;
import com.algebratech.pulse_wellness.utils.CM;
import com.algebratech.pulse_wellness.utils.Constants;
import com.algebratech.pulse_wellness.utils.IntentUtils;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PostFeedFragment extends Fragment {

    private View root;
    private RelativeLayout capture;
    private Button btnPost;
    private ImageView imgPreview;
    private Context context;
    //  private Spinner category;
    private EditText ed_post_desc;

    private SharedPreferences sharedPreferences;

    private ArrayList<HashMap<String, String>> arraylist;

    FragmentManager FM;
    FragmentTransaction FT;

    private static final int CAMERA_REQUEST = 9514;
    private ArrayList<String> returnValue = new ArrayList<String>();
    private Bitmap bitmap;
    private boolean image, video = false;
    String post_data = "";
    String postcategory = "";
    String post_type = "text";
    String post_desc, userId, post_privacy, privacy;
    String extension;
    RelativeLayout progressBar;
    //  RadioGroup radioGroup;
    RadioButton selectedPrivacy;
    private RequestQueue requestQueue;
    String outputSource;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_post_feed, container, false);

        context = getActivity();

        sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", null);

        imgPreview = root.findViewById(R.id.imgPreview);
        capture = root.findViewById(R.id.capture);
        btnPost = root.findViewById(R.id.btnPost);
        //   category = root.findViewById(R.id.section);
        ed_post_desc = root.findViewById(R.id.post);
        progressBar = root.findViewById(R.id.progressBar);
        //   radioGroup = root.findViewById(R.id.radio_group);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Camera2Activity.class);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      postcategory = category.getSelectedItem().toString();

                if (!validatePostType()) {
                    return;
                }
                post_desc = ed_post_desc.getText().toString();
//
//                if (radioGroup.getCheckedRadioButtonId() == -1) {
//                    post_privacy = "Friends Only";
//                } else {
//                    selectedPrivacy = root.findViewById(radioGroup.getCheckedRadioButtonId());
//                    privacy = selectedPrivacy.getText().toString();
//                    post_privacy = "Friends Only";
//                }
//                Log.d(Constants.TAG + "Post", postcategory + " : " + post_privacy);


                if (video) {
                    post_type = "video";
                }

                if (image) {
                    post_type = "image";
                }

                if (!image && !video) {
                    post_type = "text";
                }


                progressBar.setVisibility(View.VISIBLE);
                if (!post_type.equals("text")) {

                    Log.d(Constants.TAG + "DATA", Uri.fromFile(new File(post_data)).toString());

                    Uri uri = Uri.fromFile(new File(post_data));

                    String displayName = String.valueOf(Calendar.getInstance().getTimeInMillis() + "." + extension);
                    Log.d("ooooooo", displayName);
                    uploadPost(displayName, uri);

                    //Toast.makeText(context,post_type + " : "  + post_data,Toast.LENGTH_LONG).show();
                } else {
                    createPost();
                    //Toast.makeText(context,post_type + " : "  + post_desc,Toast.LENGTH_LONG).show();
                }
            }
        });


        return root;

    }

    private void createPost() {
        if (CM.isConnected(getActivity())) {
            JSONObject object = new JSONObject();
            try {
                //input your API parameters
                object.put("user_id", userId);
                object.put("post_type", post_type);
                object.put("post_category", postcategory);
                object.put("post_desc", post_desc);
                object.put("post_privacy", "Friends Only");

                System.out.println(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Api.add_feed, object,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, String.valueOf(response));

                            progressBar.setVisibility(View.GONE);

                            try {

                                if (response.getString("status").equals("true")) {

                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();

                                    FM = getActivity().getSupportFragmentManager();
                                    FT = FM.beginTransaction();
                                    FT.replace(R.id.content_frame, new NewsFeedFragment()).commit();
                                    FM.popBackStack();

                                } else {

                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    VolleyLog.d("Error", "Error: " + error.toString());
                    try {
                        if (error.getMessage().contains(Api.baseurl)) {
                            Toast.makeText(context, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.d(Constants.TAG, e.getMessage());
                    }
                }
            });
            requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

        } else
            Toast.makeText(getActivity(), R.string.noInternet, Toast.LENGTH_SHORT).show();


    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // path = data.getStringExtra(IntentUtils.EXTRA_PATH_RESULT);
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);


        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
            //Toast.makeText(context,"Please use a image , not a video.",Toast.LENGTH_LONG).show();
        }


        try {

            if (!returnValue.isEmpty()) {

                if (!StaticMethods.sizeLimitUpload(returnValue.get(0))) {
                    Log.d(Constants.TAG + "PATH", returnValue.get(0).toString());
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
                        Log.d(Constants.TAG + "File", f.getAbsolutePath().toString() + video);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            bitmap = ThumbnailUtils.createVideoThumbnail(f, new Size(500, 500), null);
                        } else {
                            bitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                        }
                        // Picasso.with(context).load(f.getAbsolutePath()).error(R.drawable.post_bg_lines).into(imgPreview);
                        Glide.with(context).load(bitmap).into(imgPreview);

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
                        Log.d(Constants.TAG + "File", f.getAbsolutePath().toString());
                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                        Log.d(Constants.TAG + "Bitmap", BitmapUtils.decodeImage(bitmap));
                        //Glide.with(context).load(StaticMethods.RotateBitmap(bitmap,-90)).into(imgPreview);
                        Picasso.with(context).load("file:" + f.getAbsolutePath()).error(R.drawable.ic_insert_photo).into(imgPreview);
                        post_data = f.getAbsolutePath();

                    }

                } else {
                    Toast.makeText(context, "File too large , please choose a file below 150MB", Toast.LENGTH_LONG).show();
                }

            }

        } catch (Exception e) {
            Log.d(Constants.TAG + "PATH_ERROR", e.getMessage());
            Toast.makeText(context, "Please try again.", Toast.LENGTH_LONG).show();
        }

    }

//    private void compressVideo(String absolutePath) {
//        Uri uri = Uri.parse(absolutePath);
//
//        VideoCompressor.INSTANCE.start(
//                context, // => This is required if srcUri is provided. If not, pass null.
//                uri, // => Source can be provided as content uri, it requires context.
//                null, // => This could be null if srcUri and context are provided.
//                outputSource + "/1.mp4",
//                new CompressionListener() {
//                    @Override
//                    public void onStart() {
//                        // Compression start
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        // On Compression success
//                    }
//
//                    @Override
//                    public void onFailure(String failureMessage) {
//                        // On Failure
//                    }
//
//                    @Override
//                    public void onProgress(float v) {
//                        // Update UI with progress value
//                        getActivity().runOnUiThread(new Runnable() {
//                            @SuppressLint("LongLogTag")
//                            public void run() {
////                                progress.setText(progressPercent + "%");
////                                progressBar.setProgress((int) progressPercent);
//                                // Toast.makeText(context,v + "%",Toast.LENGTH_LONG).show();
//                                Log.d(Constants.TAG + "FileCompressed", v + "%");
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled() {
//                        // On Cancelled
//                    }
//                }, new Configuration(
//                        VideoQuality.MEDIUM,
//                        false,
//                        false,
//                        null /*videoHeight: double, or null*/,
//                        null /*videoWidth: double, or null*/,
//                        null /*videoBitrate: int, or null*/
//                )
//        );
//    }

    private void uploadPost(final String pdfname, Uri pdffile) {
        if (CM.isConnected(getActivity())) {
            InputStream iStream = null;
            try {

                iStream = context.getContentResolver().openInputStream(pdffile);
                final byte[] inputData = getBytes(iStream);

                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Api.add_feed,
                        new Response.Listener<NetworkResponse>() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onResponse(NetworkResponse response) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("ressssssoo", new String(response.data));
                                requestQueue.getCache().clear();
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(response.data));

                                    if (jsonObject.getString("status").equals("true")) {
                                        Log.d("ressssssoocome::: >>>  ", "yessssss");
                                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        FM = getActivity().getSupportFragmentManager();
                                        FT = FM.beginTransaction();
                                        FT.replace(R.id.content_frame, new NewsFeedFragment()).commit();
                                        FM.popBackStack();

                                    } else {
                                        Toast.makeText(context, "Failed to create post...Please try again", Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressBar.setVisibility(View.GONE);
                                try {
                                    if (error.getMessage().contains(Api.baseurl)) {
                                        Toast.makeText(context, "No internet connection available!!!.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception e) {
                                    Log.d(Constants.TAG, e.getMessage());
                                }
                            }
                        }) {

                    /*
                     * If you want to add more parameters with the image
                     * you can do it here
                     * here we have only one parameter with the image
                     * which is tags
                     * */
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", userId);
                        params.put("post_type", post_type);
                        params.put("post_category", postcategory);
                        params.put("post_desc", post_desc);
                        params.put("post_privacy", "Friends Only");

                        // params.put("tags", "ccccc");  add string parameters
                        Log.d(Constants.TAG + "PostData", params.toString());
                        return params;
                    }

                    /*
                     *pass files using below method
                     * */
                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();

                        params.put("uploaded_file", new DataPart(pdfname, inputData));
                        return params;
                    }
                };


                volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(volleyMultipartRequest);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
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


    private boolean validatePostType() {
        String val = postcategory;
        if (ed_post_desc.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter your thoughts", Toast.LENGTH_LONG).show();
            return false;
        } else if (val.equals("Select post type")) {
            Toast.makeText(getContext(), "Please select post type", Toast.LENGTH_LONG).show();
            return false;
        } else {

            return true;
        }
    }


}
