package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.AuthorMapper;
import faang.school.postservice.model.redis.AuthorRedisCache;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceClient userServiceClient;
    private final AuthorMapper authorMapper;

    @Override
    @Retryable(retryFor = { FeignException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public UserDto getUserById(long userId) {

        return userServiceClient.getUser(userId);
    }

    @Override
    @Retryable(retryFor = { FeignException.class }, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public AuthorRedisCache getUserAuthorCacheById(long userId) {

        UserDto userDto = userServiceClient.getUser(userId);
        return authorMapper.toAuthorCache(userDto);
    }
}