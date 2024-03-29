package com.sns.modeling.util;

import com.sns.modeling.domain.post.entity.Post;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.time.LocalDate;

import static org.jeasy.random.FieldPredicates.*;

public class PostFixtureFactory {

    static public EasyRandom get(Long memberId,
                                 LocalDate firstDate,
                                 LocalDate lastDate) {
        var idPredicate = named("id")
                                .and(ofType(Long.class))
                                .and(inClass(Post.class));
        var memberIdPredicate = named("memberId")
                                    .and(ofType(Long.class))
                                    .and(inClass(Post.class));
        var param = new EasyRandomParameters()
                        .excludeField(idPredicate)
                        .dateRange(firstDate, lastDate)
                        .randomize(memberIdPredicate, () -> memberId);

        return new EasyRandom(param);
    }
}