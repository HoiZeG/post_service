package faang.school.postservice.service.resource;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.resources.ImageProcessor;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private static final int BYTES_PER_MB = 1024 * 1024;

    @Setter
    @Value("${resource.max-size}")
    private int maxFileSize;

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ImageProcessor imageProcessor;

    @Override
    public List<Resource> addResourcesToPost(List<MultipartFile> files, Post post) {
        checkFilesSizes(files);
        List<byte[]> images = files.stream()
                .map(imageProcessor::processImage)
                .toList();
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileKey = generateFileKey(post.getId(), file.getOriginalFilename());

            resources.add(initResource(
                    fileKey, file.getSize(), file.getOriginalFilename(), file.getContentType(), post));
            s3Service.uploadFile(images.get(i), file.getContentType(), fileKey);
        }
        resourceRepository.saveAll(resources);
        log.info("Added {} resources to post with id {}", files.size(), post.getId());
        return resources;
    }

    @Override
    @Transactional
    public void deleteResourcesFromPost(List<Long> resourcesIds, Post post) {
        List<Resource> resourcesToDelete = resourceRepository.findAllById(resourcesIds);
        if (resourcesToDelete.size() != resourcesIds.size()) {
            List<String> notFoundedIds = resourcesIds.stream()
                    .filter(id -> resourcesToDelete.stream()
                            .map(Resource::getId)
                            .noneMatch(existingId -> existingId.equals(id)))
                    .map(String::valueOf)
                    .toList();
            throw new DataValidationException("Resources with ids %s not found"
                    .formatted(String.join(", ", notFoundedIds)));
        }
        resourceRepository.deleteAll(resourcesToDelete);
        resourcesToDelete.forEach(resource -> s3Service.deleteFile(resource.getKey()));
        log.info("Deleted {} resources from post with id {}", resourcesIds.size(), post.getId());
    }

    private void checkFilesSizes(List<MultipartFile> files) {
        List<String> sizeExceededMessages = files.stream()
                .filter(file -> file.getSize() > maxFileSize)
                .map(file -> "File %s is too big. Max size is %dMB"
                        .formatted(file.getName(), maxFileSize / BYTES_PER_MB))
                .toList();
        if (sizeExceededMessages.size() > 0) {
            throw new DataValidationException(String.join("\n", sizeExceededMessages));
        }
    }

    private String generateFileKey(Long postId, String fileName) {
        return "posts/%d/%s-%d".formatted(postId, fileName, System.currentTimeMillis());
    }

    private Resource initResource(String fileKey, Long size, String name, String type, Post post) {
        return Resource.builder()
                .key(fileKey)
                .size(size)
                .name(name)
                .type(type)
                .post(post)
                .build();
    }
}
