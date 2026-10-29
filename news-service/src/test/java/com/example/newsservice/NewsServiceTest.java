package com.example.newsservice;

import com.example.newsservice.model.NewsModel;
import com.example.newsservice.repository.NewsRepository;
import com.example.newsservice.service.NewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    @Mock NewsRepository repository;
    @InjectMocks NewsService service;

    @Test
    void getAllReturnsNewestFirstRepositoryResults() {
        List<NewsModel> expected = List.of(new NewsModel());
        when(repository.findAllByOrderByPublishDateDescIdDesc()).thenReturn(expected);
        assertSame(expected, service.getAll());
    }

    @Test
    void getByIdReturnsNewsWhenPresent() {
        NewsModel expected = new NewsModel();
        when(repository.findById(1)).thenReturn(Optional.of(expected));
        assertSame(expected, service.getById(1));
    }

    @Test
    void getByIdReturnsNotFoundWhenMissing() {
        when(repository.findById(404)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getById(404));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void saveClearsClientSuppliedId() {
        NewsModel news = validNews();
        news.setId(100);
        when(repository.save(any(NewsModel.class))).thenAnswer(inv -> inv.getArgument(0));
        NewsModel saved = service.save(news);
        assertNull(saved.getId());
        verify(repository).save(news);
    }

    @Test
    void updateChangesFieldsButKeepsExistingId() {
        NewsModel existing = validNews();
        existing.setId(3);
        NewsModel changed = validNews();
        changed.setTitle("Обновлённая новость");
        when(repository.findById(3)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        NewsModel result = service.update(3, changed);

        assertEquals(3, result.getId());
        assertEquals("Обновлённая новость", result.getTitle());
        verify(repository).save(existing);
    }

    @Test
    void deleteMissingNewsReturnsNotFound() {
        when(repository.existsById(9)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> service.delete(9));
        verify(repository, never()).deleteById(anyInt());
    }

    private NewsModel validNews() {
        NewsModel n = new NewsModel();
        n.setTitle("Важная новость");
        n.setContent("Подробное содержание новости для тестирования");
        n.setImage("news.jpg");
        n.setPublishDate(LocalDate.of(2026, 1, 1));
        n.setAuthorId(1);
        return n;
    }
}
