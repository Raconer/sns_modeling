package com.sns.modeling.domain.post.service;

import com.sns.modeling.domain.post.entity.Timeline;
import com.sns.modeling.domain.post.repository.TimelineRepository;
import com.sns.modeling.util.CursorRequest;
import com.sns.modeling.util.PageCursor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TimelineReadService {
    final private TimelineRepository timelineRepository;

    public PageCursor<Timeline> getTimelines(Long memberId, CursorRequest cursorRequest) {
        var timelines = this.findAllBy(memberId, cursorRequest);
        var nextKey = timelines.stream().mapToLong(Timeline::getId).min().orElse(CursorRequest.NONE_KEY);

        return new PageCursor<>(cursorRequest.next(nextKey), timelines);
    }

    private List<Timeline> findAllBy(Long memberId, CursorRequest cursorRequest) {
        if (cursorRequest.haKey()) {
            return this.timelineRepository.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(cursorRequest.key(),
                                                                                      memberId,
                                                                                      cursorRequest.size());
        }
        return this.timelineRepository.findAllByMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
    }
}
