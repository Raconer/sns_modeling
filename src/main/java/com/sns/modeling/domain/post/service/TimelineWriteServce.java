package com.sns.modeling.domain.post.service;

import com.sns.modeling.domain.post.entity.Timeline;
import com.sns.modeling.domain.post.repository.TimelineRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TimelineWriteServce {
    final private TimelineRepository timelineRepository;

    public void deliveryToTimeline(Long postId, List<Long> toMemberIds) {
        var timelines = toTimeline(postId, toMemberIds).toList();

        this.timelineRepository.bulkInsert(timelines);
    }

    @NotNull
    private static Stream<Timeline> toTimeline(Long postId, List<Long> toMemberIds) {
        return toMemberIds.stream()
                .map(memberId -> Timeline.builder()
                        .memberId(memberId)
                        .postId(postId)
                        .build());
    }
}
