package BO;

import DB.Entities.ChatMessageEntity;
import DB.Entities.UserEntity;
import ViewModel.ChatMessageViewModel;
import ViewModel.UserViewModel;


public class ModelConverter {


    private static UserViewModel convertToUserViewModel(UserEntity u){
        UserViewModel user = new UserViewModel();
        user.setUserId(u.getUserId());
        user.setUsername(u.getUsername());
        user.setPassword(u.getPassword());
        return user;
    }

    private static UserEntity convertToUserEntity(UserViewModel u){
        UserEntity user = new UserEntity();
        user.setUserId(u.getUserId());
        user.setUsername(u.getUsername());
        user.setPassword(u.getPassword());
        return user;
    }

    public static ChatMessageEntity convertToChatMessageEntity(ChatMessageViewModel c){
        ChatMessageEntity chat = new ChatMessageEntity();
        chat.setMessage(c.getMessage());
        chat.setSender(convertToUserEntity(c.getSender()));
        chat.setReceiver(convertToUserEntity(c.getReceiver()));
        chat.setSendDate(c.getSendDate());
        return chat;
    }

    static ChatMessageViewModel convertToChatMessageViewModel(ChatMessageEntity c){
        ChatMessageViewModel chat = new ChatMessageViewModel();
        chat.setMessage(c.getMessage());
        chat.setSender(convertToUserViewModel(c.getSender()));
        chat.setReceiver(convertToUserViewModel(c.getReceiver()));
        chat.setSendDate(c.getSendDate());
        return chat;
    }

}
