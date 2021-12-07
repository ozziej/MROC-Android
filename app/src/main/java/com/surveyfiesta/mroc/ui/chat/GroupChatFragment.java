package com.surveyfiesta.mroc.ui.chat;

import static android.content.Context.INPUT_METHOD_SERVICE;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.entities.Users;

import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;
import com.surveyfiesta.mroc.viewmodels.WebSocketViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupChatFragment extends Fragment {

    private Button sendChatButton;
    private EditText chatTextView;
    private LinearLayout chatLayout;
    private ScrollView chatScrollView;
    private Users currentUser;
    private GroupChat groupChat;
    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;
    private GroupChatViewModel groupChatViewModel;
    private WebSocketViewModel webSocketViewModel;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM YYYY");

    public static GroupChatFragment newInstance() {
        return new GroupChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        groupChatViewModel = new ViewModelProvider(this).get(GroupChatViewModel.class);
        webSocketViewModel = new ViewModelProvider(this).get(WebSocketViewModel.class);

        sendChatButton = view.findViewById(R.id.sendChatButton);
        chatTextView = view.findViewById(R.id.chatTextView);
        chatLayout = view.findViewById(R.id.chatLayout);
        chatScrollView = view.findViewById(R.id.chatScrollView);
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        currentUser = userViewModel.getCurrentUserData().getValue();
        String groupChatUuid = stateViewModel.getCurrentChatUuid();

        String userToken = stateViewModel.getCurrentUserToken();
        if (userToken == null || userToken.isEmpty()) {
            navController.navigate(R.id.loginFragment);
        } else {
            if (currentUser == null) {
                userViewModel.login(userToken);
            }
        }

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            } else {
                currentUser = result.getUser();
                chatLayout.removeAllViews();
                groupChatViewModel.findGroupChat(groupChatUuid);
            }
        });

        sendChatButton.setOnClickListener(l -> {
            String chatText = chatTextView.getText().toString();
            if (!chatText.isEmpty()) {
                hideKeyboard(view);
                chatTextView.setText("");
                webSocketViewModel.sendMessage(encodeMessage(groupChat.getGroupId(),currentUser.getUserId(), chatText));
            }
        });

        groupChatViewModel.getGroupChatData().observe(getViewLifecycleOwner(), l -> {
            groupChat = l.getGroupChat();
            if (groupChat != null && currentUser != null) {
                webSocketViewModel.initWebSocket(groupChat, currentUser);
                webSocketViewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), this::onChanged);
            }
            getInitialMessages();
        });
    }

    private String encodeMessage(Integer groupId, Integer userId, String messageBody) {
        String jsonText = "{\"groupId\":"+groupId+
                ",\"userId\":"+userId+
                ",\"messageBody\":\""+messageBody+"\"}";
        return jsonText;
    }

    private void onChanged(InstantNotification notification) {
        if (notification != null) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> addMessageBox(notification));
            }
        }
    }

    @Override
    public void onDestroy() {
        webSocketViewModel.closeSocket();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getInitialMessages() {
        if (groupChat != null && groupChatViewModel != null) {
            ProgressBar simpleProgressBar = getView().findViewById(R.id.loginProgressBar);
            simpleProgressBar.setVisibility(View.VISIBLE);

            groupChatViewModel.findGroupChatMessages(groupChat);
            groupChatViewModel.getNotificationLiveDate().observe(getViewLifecycleOwner(), instantNotifications -> {
                AtomicInteger previousDays = new AtomicInteger(0);
                if (instantNotifications != null) {
                    instantNotifications.forEach(i -> {
                        Long days = ChronoUnit.DAYS.between(i.getDateTime(), LocalDateTime.now());
                        if (days > 0 && previousDays.getAndSet(days.intValue()) != days) {
                            drawDayBubble(dateFormatter.format(i.getDateTime()));
                        }
                        if (days == 0 && previousDays.getAndSet(days.intValue()) != days) {
                            drawDayBubble("Today");
                        }
                        addMessageBox(i);
                    });
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void hideKeyboard(View v) {
        Context context = v.getContext();
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

    private void addMessageBox(InstantNotification notification) {
        Boolean isCurrentUser = notification.getSenderId().equals(currentUser.getUserId());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.chat_bubble,null);
        String bubbleTime;

        TextView senderName = view.findViewById(R.id.chatBubbleSenderName);
        TextView textView = view.findViewById(R.id.chatBubbleTextView);
        TextView chatTime = view.findViewById(R.id.chatTime);

        textView.setText(notification.getNotificationText());
        textView.setTextColor(Color.BLACK);

        bubbleTime = notification.getFormattedTime();
        chatTime.setText(bubbleTime);
        chatTime.setTextColor(Color.WHITE);

        senderName.setText(notification.getSenderName());
        senderName.setPadding(4,0,4,0);
        textView.setPadding(4,0,4,0);
        textView.setMinEms(4);
        textView.setMaxEms(12);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 10.0f;
        layoutParams.topMargin = 4;
        layoutParams.rightMargin = 4;
        layoutParams.leftMargin = 4;

        GradientDrawable drawable;
        drawable = new GradientDrawable();
        drawable.setStroke(1, ContextCompat.getColor(getContext(), R.color.medium_grey));
        drawable.setCornerRadius(10);

        if (isCurrentUser) {
            layoutParams.gravity = Gravity.RIGHT;
            drawable.setColor(ContextCompat.getColor(getContext(), R.color.sf_green));
        } else {
            layoutParams.gravity = Gravity.LEFT;
            drawable.setColor(ContextCompat.getColor(getContext(), R.color.light_grey));
        }
        view.setBackground(drawable);
        view.setLayoutParams(layoutParams);
        chatLayout.addView(view);
        chatScrollView.postDelayed(() -> chatScrollView.fullScroll(View.FOCUS_DOWN),200);
    }

    private void drawDayBubble(String displayText) {
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.date_bubble,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 10.0f;
        layoutParams.topMargin = 4;
        layoutParams.rightMargin = 4;
        layoutParams.leftMargin = 4;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        TextView displayTextView = view.findViewById(R.id.dateBubbleTime);
        displayTextView.setPadding(32,8,32,8);
        displayTextView.setText(displayText);
        displayTextView.setLayoutParams(layoutParams);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(1, Color.parseColor("#dddddd"));
        drawable.setCornerRadius(10);
        drawable.setColor(Color.parseColor("#cccccc"));
        view.setBackground(drawable);
        view.setLayoutParams(layoutParams);
        chatLayout.addView(view);
    }

}