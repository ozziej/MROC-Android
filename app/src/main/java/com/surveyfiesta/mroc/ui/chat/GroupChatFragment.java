package com.surveyfiesta.mroc.ui.chat;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.constants.NotificationTypes;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupChatItemRequest;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;
import com.surveyfiesta.mroc.viewmodels.WebSocketViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupChatFragment extends Fragment {

    private Button sendChatButton;
    private EditText chatTextView;
    private LinearLayout chatLayout;
    private ScrollView chatScrollView;
    private Users currentUser;
    String userToken;
    private GroupChat groupChat;
    private List<GroupUsers> groupUsersList;

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;
    private GroupChatViewModel groupChatViewModel;
    private WebSocketViewModel webSocketViewModel;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    ProgressBar simpleProgressBar;

    public static GroupChatFragment newInstance() {
        return new GroupChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.group_chat_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        groupChatViewModel = new ViewModelProvider(this).get(GroupChatViewModel.class);
        webSocketViewModel = new ViewModelProvider(this).get(WebSocketViewModel.class);

        userViewModel.getLoginResult().observe(this, result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                displayMessage(result.getResponseMessage());
            } else {
                currentUser = result.getUser();
                findChatData();
            }
        });

        groupChatViewModel.getGroupChatData().observe(this, l -> {
            if (l != null) {
                groupChat = l.getGroupChat();
                groupUsersList = l.getGroupUsers();

                if (groupChat != null && currentUser != null) {
                    boolean groupEnabled = groupChat.isGroupEnabled();
                    chatTextView.setEnabled(groupEnabled);
                    sendChatButton.setEnabled(groupEnabled);
                    webSocketViewModel.initWebSocket(groupChat, userToken);
                    webSocketViewModel.getNotificationLiveData().observe(this, this::onChanged);
                }
                getInitialMessages();
            }
        });

        groupChatViewModel.getNotificationLiveDate().observe(this, instantNotifications -> {
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
                if (simpleProgressBar != null) {
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        sendChatButton = view.findViewById(R.id.sendChatButton);
        chatTextView = view.findViewById(R.id.chatTextView);
        chatLayout = view.findViewById(R.id.chatLayout);
        chatScrollView = view.findViewById(R.id.chatScrollView);
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        currentUser = userViewModel.getCurrentUserData().getValue();

        userToken = stateViewModel.getCurrentUserToken();
        if (userToken == null || userToken.isEmpty()) {
            navController.navigate(R.id.loginFragment);
        } else {
            if (currentUser == null) {
                userViewModel.login(userToken);
            }
        }

        sendChatButton.setOnClickListener(l -> {
            String chatText = chatTextView.getText().toString();
            if (!chatText.isEmpty()) {
                hideKeyboard();
                chatTextView.setText("");
                try {
                    InstantNotification notification = new InstantNotification();
                    notification.setSenderId(currentUser.getUserId());
                    notification.setRecipientId(groupChat.getGroupId());
                    notification.setSenderName(currentUser.getOtherName());
                    notification.setNotificationTitle("Chat");
                    notification.setNotificationText(chatText);
                    webSocketViewModel.sendMessage(objectMapper.writeValueAsString(notification));
                } catch (JsonProcessingException ex) {
                    Log.d("Error : ", ex.getLocalizedMessage());
                }
            }
        });
        chatLayout.removeAllViews();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_chat_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.editGroupUsersButton) {
            showUserListFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        webSocketViewModel.closeSocket();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        chatLayout.removeAllViews();
        getInitialMessages();
        super.onResume();
    }

    private void findChatData() {
        String groupChatUuid = stateViewModel.getCurrentChatUuid();
        if (groupChatViewModel.getGroupChatData().getValue() == null) {
            GroupChat groupChat = new GroupChat();
            groupChat.setGroupUuid(groupChatUuid);
            UserGroupChatEntity chatEntity = new UserGroupChatEntity(userToken, groupChat, null);
            groupChatViewModel.findGroupChat(chatEntity);
        }
    }

    private void showUserListFragment() {
        if (groupUsersList.stream().anyMatch(i -> i.getUser().getUserId().equals(currentUser.getUserId()))) {
            Navigation.findNavController(getView()).navigate(R.id.action_groupChatFragment_to_groupUserFragment);
        } else {
            Snackbar.make(getView(), "Sorry, you don't have permission to do that.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onChanged(InstantNotification notification) {
        if (notification != null) {
            Activity activity = getActivity();
            if (activity != null && !notification.getNotificationType().equals(NotificationTypes.PONG.toString())) {
                activity.runOnUiThread(() -> addMessageBox(notification));
            }
        }
    }

    private void getInitialMessages() {
        simpleProgressBar = getView().findViewById(R.id.loginProgressBar);
        simpleProgressBar.setVisibility(View.VISIBLE);
        if (groupChat != null && groupChatViewModel != null) {
            GroupChatItemRequest itemRequest = new GroupChatItemRequest(userToken, groupChat.getGroupId(), "",10);
            groupChatViewModel.findGroupChatMessages(itemRequest);
        }
    }

    private void hideKeyboard() {
        Context context = getView().getContext();
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getApplicationWindowToken(),0);
    }

    private void addMessageBox(InstantNotification notification) {
        Boolean isCurrentUser = notification.getSenderId().equals(currentUser.getUserId());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.chat_bubble,null);
        String bubbleTime;

        TextView senderName = view.findViewById(R.id.chatBubbleSenderName);
        TextView textView = view.findViewById(R.id.chatBubbleTextView);
        TextView chatTime = view.findViewById(R.id.chatTime);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(4,0,4,0);
        textView.setMinEms(4);

        if (notification.getNotificationType().equals(NotificationTypes.SYSTEM_MESSAGE.toString())) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(notification.getNotificationTitle(), new StyleSpan(Typeface.BOLD),0);
            builder.append("\n", new StyleSpan(Typeface.NORMAL),0);
            builder.append(notification.getNotificationText(), new StyleSpan(Typeface.NORMAL),0);
            textView.setText(builder);
        } else {
            textView.setText(notification.getNotificationText());
            textView.setMaxEms(12);
        }

        bubbleTime = notification.getFormattedTime();
        chatTime.setText(bubbleTime);
        chatTime.setTextColor(Color.WHITE);

        senderName.setText(notification.getSenderName());
        senderName.setPadding(4,0,4,0);
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
            layoutParams.gravity = Gravity.END;
            drawable.setColor(ContextCompat.getColor(getContext(), R.color.sf_green));
        } else {
            layoutParams.gravity = Gravity.START;
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

    private void displayMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }
}