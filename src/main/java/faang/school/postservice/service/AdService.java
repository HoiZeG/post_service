package faang.school.postservice.service;

import faang.school.postservice.config.context.CustomThreadExecutor;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {
    private final AdRepository adRepository;
    private final CustomThreadExecutor executor;

    public Iterable<Ad> getAllAds(){
        return adRepository.findAll();
    }

    private List<Ad> findOverdueAds(Iterable<Ad> ads){
        return StreamSupport.stream(ads.spliterator(),false)
                .filter(ad -> ad.getAppearancesLeft() <= 0
                        || ad.getEndDate().isBefore(LocalDateTime.now()))
                .toList();
    }

    @Async("asyncExecutor")
    public void deleteInactiveAds(int batchSize){
        List<Ad> ads = findOverdueAds(getAllAds());
        int count = (int) Math.ceil((double) ads.size() / batchSize);
        for (int i = 0; i < count; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, ads.size());
            List<Ad> batch = ads.subList(start, end);
            executor.asyncExecutor()
                    .submit(() ->
                            adRepository.deleteAllById(batch.stream()
                                    .map(Ad::getId)
                                    .toList()));
        }
        executor.asyncExecutor().shutdown();
    }

}
