package com.algebratech.pulse_wellness.activities;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.algebratech.pulse_wellness.DetailActivityScreen;
import com.algebratech.pulse_wellness.R;
import com.algebratech.pulse_wellness.fragments.ActivitySummaryFragment;
import com.algebratech.pulse_wellness.fragments.ProfileActivity;
import com.algebratech.pulse_wellness.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingFragment extends Fragment {
    private Toolbar toolbarPolicy;
    private Button select_disease, subscription_plans, friend_profile, detail_activity;
    private LinearLayout my_post, myWellnessPlan, all_transactions, notification, givefeedback, signout, profile, myReport, subscription;
    private LinearLayout layout;
    Context context;
    String sub_token;
    private SharedPreferences sharedPreferences;
    JSONObject json_sub_token;
    String subStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.activity_setting, container, false);
        context = getContext();

        return layout;
    }


    void init() {
        if(layout!=null) {
            select_disease = layout.findViewById(R.id.select_disease);
            my_post = layout.findViewById(R.id.my_post);
            subscription = layout.findViewById(R.id.subscription);
            myWellnessPlan = layout.findViewById(R.id.myWellnessPlan);
            all_transactions = layout.findViewById(R.id.all_transactions);
            subscription_plans = layout.findViewById(R.id.subscription_plans);
            friend_profile = layout.findViewById(R.id.friend_profile);
            givefeedback = layout.findViewById(R.id.givefeedback);
            detail_activity = layout.findViewById(R.id.detail_activity);
            notification = layout.findViewById(R.id.notification);
            signout = layout.findViewById(R.id.signout);
            profile = layout.findViewById(R.id.profile);
            myReport = layout.findViewById(R.id.myReport);
            sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            sub_token = sharedPreferences.getString("sub_token", "");
            try {
                json_sub_token = new JSONObject(sub_token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!json_sub_token.toString().equals("{}")) {
                try {
                    subStatus = json_sub_token.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                subscription.setVisibility(View.GONE);
            }
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    startActivityForResult(intent, 100);
                }
            });

            subscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SubscriptionPlansListingScreen.class);
                    startActivityForResult(intent, 200);
                }
            });

            myReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivitySummaryFragment.class);
                    startActivity(intent);
                }
            });

            select_disease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SelectDisease.class);
                    startActivity(intent);
                }
            });

            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NotificationActivity.class);
                    intent.putExtra("fcm","");startActivity(intent);
                }
            });

            givefeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(context, GiveFeedBackActivity.class));
                }
            });

            my_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MyNewsFeedActivity.class);
                    startActivity(intent);
                }
            });

            signout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit().clear().apply();
                    Intent intent4 = new Intent(context, LoginActivity.class);
                    startActivity(intent4);
                    new MainActivity().finish();
                }
            });

            myWellnessPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MyWellnesPlanScreen.class);
                    startActivity(intent);
                }
            });

            all_transactions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AllTransaction.class);
                    startActivity(intent);
                }
            });

            subscription_plans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SubscriptionPlansListingScreen.class);
                    startActivity(intent);
                }
            });

            friend_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FriendProfileActivity.class);
                    startActivity(intent);
                }
            });

            detail_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivityScreen.class);
                    startActivity(intent);
                }
            });

            detail_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivityScreen.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == 200) {
            subscription.setVisibility(View.GONE);
        }

        if (requestCode == 100 && resultCode == 100) {
            subscription.setVisibility(View.VISIBLE);
        }
    }
}
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
//
//        toolbarPolicy = findViewById(R.id.toolbarpolicy);
//        setSupportActionBar(toolbarPolicy);
//        setTitle("SETTINGS");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbarPolicy.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        select_disease = findViewById(R.id.select_disease);
//        setGoals = findViewById(R.id.setGoals);
//        my_post = findViewById(R.id.my_post);
//        myWellnessPlan = findViewById(R.id.myWellnessPlan);
//        all_transactions = findViewById(R.id.all_transactions);
//        subscription_plans = findViewById(R.id.subscription_plans);
//        friend_profile = findViewById(R.id.friend_profile);
//        givefeedback = findViewById(R.id.givefeedback);
//        detail_activity = findViewById(R.id.detail_activity);
//        notification = findViewById(R.id.notification);
//        signout = findViewById(R.id.signout);
//        profile = findViewById(R.id.profile);
//
//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//        select_disease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SelectDisease.class);
//                startActivity(intent);
//            }
//        });
//
//
//        setGoals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), AddUserGoals.class);
//                startActivity(intent);
//            }
//        });
//
//        notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        givefeedback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(getApplicationContext(), GiveFeedBackActivity.class));
//            }
//        });
//
//        my_post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), MyNewsFeedActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//        signout.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("NewApi")
//            @Override
//            public void onClick(View v) {
//                getApplicationContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit().clear().apply();
//                Intent intent4 = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent4);
//                new MainActivity().finish();
//                finish();
//            }
//        });
//
//        myWellnessPlan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), MyWellnesPlanScreen.class);
//                startActivity(intent);
//            }
//        });
//
//        all_transactions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), AllTransaction.class);
//                startActivity(intent);
//            }
//        });
//
//        subscription_plans.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SubscriptionPlansListingScreen.class);
//                startActivity(intent);
//            }
//        });
//
//        friend_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), FriendProfileActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//        detail_activity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), DetailActivityScreen.class);
//                startActivity(intent);
//            }
//        });
//
//        detail_activity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), DetailActivityScreen.class);
//                startActivity(intent);
//            }
//        });
//
//    }
//}