package ru.practicum.ewm.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private Comment comment;
    private Event event;
    private User user;
    private Category category;
    private Random random = new Random();

    @BeforeEach
    void setup() {
        user = userRepository.save(
                User.builder().name("Adam").email(random.nextInt() + "email" + random.nextInt() + "@yandex.ru")
                        .build());
        category = categoryRepository.save(Category.builder().name("Category #" + random.nextInt()).build());
        event = eventRepository.save(Event.builder().createdOn(LocalDateTime.now()).initiator(user).title("Graduation")
                .annotation("Finish of Java course")
                .description("The day when we get certificates, finish studying and start work-life routine")
                .eventDate(LocalDateTime.of(2026, 3, 11, 12, 0)).category(category).paid(false).participantLimit(0)
                .publishedOn(LocalDateTime.now()).requestModeration(true).confirmedRequests(0)
                .state(EventState.PUBLISHED).location(new Location(20D, 20D)).views(0L).build());
        comment = commentRepository.save(
                Comment.builder().event(event).user(user).text("Comment").created(LocalDateTime.now()).build());
    }

    @Test
    void shouldReturnComment() {
        Comment result = commentRepository.findById(comment.getId()).get();

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getUser().getId(), result.getUser().getId());
        assertEquals(comment.getEvent().getId(), result.getEvent().getId());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    void shouldReturnCommentByEventId() {
        List<Comment> resultList = commentRepository.findAllByEventId(event.getId());
        Comment result = resultList.getFirst();

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getUser().getId(), result.getUser().getId());
        assertEquals(comment.getEvent().getId(), result.getEvent().getId());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    void shouldReturnCommentByUserId() {
        List<Comment> resultList = commentRepository.findAllByUserId(user.getId());
        Comment result = resultList.getFirst();

        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getUser().getId(), result.getUser().getId());
        assertEquals(comment.getEvent().getId(), result.getEvent().getId());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    void shouldUpdate() {
        Comment newComment = Comment.builder().id(comment.getId()).event(comment.getEvent()).user(comment.getUser())
                .text("new " + "text").created(comment.getCreated()).build();

        Comment updated = commentRepository.save(newComment);
        assertEquals(comment.getId(), updated.getId());
        assertEquals(comment.getUser().getId(), updated.getUser().getId());
        assertEquals(comment.getEvent().getId(), updated.getEvent().getId());
        assertEquals(newComment.getText(), updated.getText());
    }

    @Test
    void shouldReturnNullAfterDelete() {
        commentRepository.deleteById(comment.getId());
        Optional<Comment> result = commentRepository.findById(comment.getId());

        assertFalse(result.isPresent());
    }
}
