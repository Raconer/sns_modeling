package com.sns.modeling.domain.member.entity.post;

import com.sns.modeling.domain.post.entity.Post;
import com.sns.modeling.domain.post.repository.PostRepository;
import com.sns.modeling.util.PostFixtureFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.stream.IntStream;

@SpringBootTest
public class PostBulkInsertTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    public void bulkInsert() {
        var easyRandom = PostFixtureFactory.get(3L,
                                               LocalDate.of(2022, 1, 1),
                                               LocalDate.of(2022, 2, 1));


        // 객체 생성 시간(forEach() Insert)    1.7237716
        // 객체 생성 시간(stream().parallel().forEach() Insert)    1.7998228
        // 객체 생성 시간(parallelStream().forEach() Insert)     1.6954892
        // 객체 생성 시간(Bulk Insert) 	: 1.4947793
        var tenThousand  = 1000;
        var stopWatch = new StopWatch();
        stopWatch.start();
        var posts = IntStream.range(0, tenThousand * 10)
                .parallel()
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .toList();
        stopWatch.stop();

        var insertStopWatch = new StopWatch();

        // forEach() Insert 시간 	: 204.591780301
//        var insertKind = "forEach() Insert";
//        insertStopWatch.start();
//        posts.forEach(post -> this.postRepository.save(post));
//        insertStopWatch.stop();

        // stream().parallel().forEach() Insert 시간 : 32.9538523
//         var insertKind = "stream().parallel().forEach() Insert";
//         insertStopWatch.start();
//         posts.stream().parallel().forEach(post -> this.postRepository.save(post));
//         insertStopWatch.stop();

        // parallelStream().forEach() Insert 시간(s):31.9208487
//         var insertKind = "parallelStream().forEach() Insert";
//         insertStopWatch.start();
//         posts.parallelStream().forEach(post -> this.postRepository.save(post));
//         insertStopWatch.stop();

         // Bulk Insert 시간 	: 1.5614143
         var insertKind = "Bulk Insert";
         insertStopWatch.start();
         this.postRepository.bulkInsert(posts);
         insertStopWatch.stop();

        System.out.println("객체 생성 시간 \t: " + stopWatch.getTotalTimeSeconds());
        System.out.println(insertKind + " 시간 \t: " + insertStopWatch.getTotalTimeSeconds());
    }
}
