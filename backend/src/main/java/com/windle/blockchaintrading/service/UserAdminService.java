package com.windle.blockchaintrading.service;

import com.windle.blockchaintrading.common.PageResponse;
import com.windle.blockchaintrading.dto.UserAdminDTO;
import com.windle.blockchaintrading.dto.UserDetailDTO;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserAdminService {

    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PageResponse<UserAdminDTO> list(int page, int size, String search) {
        Page<User> result = userRepository.search(
            search,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return PageResponse.of(result.map(UserAdminDTO::from));
    }

    public UserDetailDTO getDetail(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return UserDetailDTO.from(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
