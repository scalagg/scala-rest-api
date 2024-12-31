package gg.scala.restapi.forums.category;

import gg.scala.restapi.forums.forums.Forum;

import java.util.List;

public record ForumCategoryResponse(
        ForumCategory category,
        List<Forum> forums
) { }
