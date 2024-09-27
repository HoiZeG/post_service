package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.CommentCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCacheRepository extends CrudRepository<CommentCache, Long> {}