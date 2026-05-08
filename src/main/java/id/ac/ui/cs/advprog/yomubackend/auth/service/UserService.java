package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.AddIdentityRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.UpdateProfileRequest;

public interface UserService {
    void updateProfile(String currentUsername, UpdateProfileRequest request);
    void addIdentity(String currentUsername, AddIdentityRequest request);
    void deleteAccount(String currentUsername);
}