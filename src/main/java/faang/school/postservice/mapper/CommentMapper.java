package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDTO(Comment comment);

    @Mapping(target = "post", ignore = true)
    Comment toEntity(CommentDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    List<CommentDto> toDtoList(List<Comment> mentorshipEntity);
}
