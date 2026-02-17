package ru.practicum.ewm.event.service;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootTest
@EnableScheduling
public class ScheduleSpringTest {
    @SpyBean
    private EventStatsScheduler scheduler;

//    @Test
//    void shouldRunScheduler() throws InterruptedException {
//        Thread.sleep(65000);
//
//        verify(scheduler, atLeastOnce()).updateViews();
//    }
}
