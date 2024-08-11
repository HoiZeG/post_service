package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("likes")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersThatLikedPost(@PathVariable Long postId) {
        return likeService.getUsersThatLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersThatLikedComment(@PathVariable Long commentId) {
        return likeService.getUsersThatLikedComment(commentId);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    public UserDto deleteLikeFromPost(@PathVariable Long postId, @PathVariable Long userId) {
        return likeService.deleteLikeFromPost(postId, userId);
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    public UserDto deleteLikeFromComment(@PathVariable Long commentId, @PathVariable Long userId) {
        return likeService.deleteLikeFromComment(commentId, userId);
    }

}
